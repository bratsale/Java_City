package handler;

import model.ECar;
import model.EBike;
import model.EScooter;
import model.Vehicle;
import rent.Rental; // Pretpostavljamo da imamo ovu klasu Rental

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVHandler {

    // Metoda za učitavanje vozila
    public static List<Vehicle> loadVehicles(String filePath) {
        List<Vehicle> vehicles = new ArrayList<>();
        Set<String> loadedIds = new HashSet<>();  // Set za praćenje već učitavanih ID-ova


        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Preskočimo header (prvi red)
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                // Preskakanje praznih linija
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                if (values.length < 8) {  // Ako nedostaju obavezni podaci, preskoči red
                    System.out.println("Invalid data: " + line);
                    continue;
                }

                String id = values[0].trim();
                //provjera da li je id vec ucitan
                if (loadedIds.contains(id)) {
                    System.out.println("Duplicate ID: " + id + ", skipping this vehicle.");
                    continue;
                }
                String manufacturer = values[1].trim();
                String model = values[2].trim();

                // Provera da li je cena i nivo baterije validan
                double price = parseDoubleSafe(values[4].trim(), 0.0);
                String type = values[8].trim();  // Tip vozila (automobil, bicikl, trotinet)

                // Ispis vrednosti tipa vozila radi debagovanja
                System.out.println("Detected vehicle type: " + type);

                // Datum nabavke, samo za Cars
                String carDate = null;
                if ("automobil".equalsIgnoreCase(type)) {
                    carDate = values[3].trim();  // Datum nabavke je na poziciji 3
                }

                // Kreiraj vozilo na osnovu tipa
                Vehicle vehicle = null;

                // Provera validnosti tipa vozila
                switch (type.toLowerCase()) {
                    case "automobil":
                        if (values.length > 7) {
                            String carDescription = values[7].trim();
                            vehicle = new ECar(id, manufacturer, model, price, carDescription, carDate);
                        }
                        break;
                    case "bicikl":
                        if (values.length > 7) {
                            double rangePerCharge = parseDoubleSafe(values[5].trim(), 0.0);
                            vehicle = new EBike(id, manufacturer, model, price, rangePerCharge);
                            System.out.println("range per charge: " + rangePerCharge);
                        }
                        break;
                    case "trotinet":
                        if (values.length > 6) {
                            double maxSpeed = parseDoubleSafe(values[6].trim(), 0.0);
                            vehicle = new EScooter(id, manufacturer, model, price, maxSpeed);
                        }
                        break;
                    default:
                        // Dodaj log koji prikazuje nevalidan tip vozila
                        System.out.println("Invalid vehicle type detected: " + type);
                        break;
                }

                // Dodaj vozilo u listu ako je validno
                if (vehicle != null) {
                    vehicles.add(vehicle);
                    loadedIds.add(id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vehicles;
    }

    // Metoda za učitavanje podataka o iznajmljivanjima
    public static List<Rental> loadRentals(String filePath, List<Vehicle> vehicles) {
        List<Rental> rentals = new ArrayList<>();
        String csvPattern = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"; // Regex za razdvajanje zareza izvan navodnika
        Set<String> processedRentals = new HashSet<>(); // Set za praćenje unikatnih parova (vehicleId, date)

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Preskočimo header (prvi red)
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                // Preskakanje praznih linija
                if (line.trim().isEmpty()) continue;

                // Razdvajanje linije pomoću regexa
                String[] values = line.split(csvPattern, -1);

                if (values.length != 8)  {  // Ako nedostaju obavezni podaci, preskoči red
                    System.out.println("Invalid data: " + line);
                    continue;
                }

                // Uklanjanje navodnika iz podataka ako postoje
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim().replaceAll("^\"|\"$", ""); // Skidanje navodnika s početka i kraja
                }

                // Učitavamo podatke iz CSV fajla
                String date = values[0];
                String user = values[1];
                String vehicleId = values[2];
                String startLocation = values[3];
                String endLocation = values[4];
                int duration = parseIntSafe(values[5], 0);
                String breakdown = values[6];
                String promotion = values[7];

                // Kreiramo ključ za proveru unikatnosti
                String rentalKey = vehicleId + "_" + date;

                // Provera da li je par (vehicleId, date) već obrađen
                if (processedRentals.contains(rentalKey)) {
                    System.out.println("Duplicate rental ignored: " + line);
                    continue;
                }

                // Dodajemo ključ u set obrađenih unikatnih iznajmljivanja
                processedRentals.add(rentalKey);

                // Pronalaženje vozila prema ID-u
                Vehicle rentedVehicle = null;
                for (Vehicle vehicle : vehicles) {
                    if (vehicle.getId().equalsIgnoreCase(vehicleId)) {
                        rentedVehicle = vehicle;
                        break;
                    }
                }

                if (rentedVehicle != null) {
                    // Kreiraj objekat Rental
                    Rental rental = new Rental(date, user, rentedVehicle, startLocation, endLocation, duration, breakdown, promotion);
                    rentals.add(rental);
                } else {
                    // Ako vozilo nije pronađeno, ispisuj poruku
                    System.out.println("Vehicle with ID " + vehicleId + " not found.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        rentals.sort(Comparator.comparing(Rental::getStartTime));

        return rentals;
    }




    // Pomoćna metoda za pronalaženje vozila prema ID-u
    private static Vehicle findVehicleById(List<Vehicle> vehicles, String vehicleId) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equalsIgnoreCase(vehicleId)) {
                return vehicle;
            }
        }
        return null;
    }

    // Metoda za sigurno parsiranje double vrednosti
    private static double parseDoubleSafe(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Metoda za sigurno parsiranje int vrednosti
    private static int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
