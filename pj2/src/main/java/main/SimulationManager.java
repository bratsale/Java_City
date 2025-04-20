package main;

import handler.*;
import model.*;
import rent.*;
import gui.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Klasa SimulationManager se bavi citavom simulacijom programa
 * Obuhvata ucitavanje podataka, simulaciju, racunanje, niti itd
 */
public class SimulationManager {
    private static List<Vehicle> vehiclesAfterSimulation = new ArrayList<>();
    private static List<Rental> allRentals = new ArrayList<>(); // Dodano

    /**
     * Metoda koja je preuzeta iz CSVHandler klase, ucitava vozila
     *
     * @param vehiclesFilePath - relativna putanja CSV fajla za vozila
     * @return vraca ucitana vozila
     */
    public static List<Vehicle> loadVehicles(String vehiclesFilePath) {
        return CSVHandler.loadVehicles(vehiclesFilePath);
    }

    /**
     * Metoda koja je preuzeta iz CSVHandler klase, ucitava iznajmljivanja
     *
     * @param rentalsFilePath - relativna putanja CSV fajla za iznajmljivanja
     * @param vehicles        - lista vozila
     * @return vraca ucitana iznajmljivanja
     */
    public static List<Rental> loadRentals(String rentalsFilePath, List<Vehicle> vehicles) {
        return CSVHandler.loadRentals(rentalsFilePath, vehicles);
    }

    /**
     * Metoda koja grupise sva iznajmljivanja po vremenu iznajmljivanja
     *
     * @param rentals - iznajmljivanja
     * @return vraca mapu iznajmljivanja grupisana po vremenu
     */
    public static Map<LocalDateTime, List<Rental>> groupRentalsByTime(List<Rental> rentals) {
        Map<LocalDateTime, List<Rental>> groupedRentals = new TreeMap<>();
        for (Rental rental : rentals) {
            LocalDateTime startTime = rental.getStartTime();
            groupedRentals.computeIfAbsent(startTime, k -> new ArrayList<>()).add(rental);
        }
        return groupedRentals;
    }

    /**
     * Metoda koja cuva nivoe baterije nakon simulacije
     *
     * @param vehicles - lista vozila
     */
    public static void saveBatteryLevels(List<Vehicle> vehicles) {
        vehiclesAfterSimulation.clear();
        for (Vehicle vehicle : vehicles) {
            // Smjesti kopiju vozila sa azuriranim nivoom baterije
            vehiclesAfterSimulation.add(vehicle);
        }
    }

    /**
     * Metoda koja vraca listu vozila nakon sto je simulacija zavrsena
     *
     * @return vraca tu listu vozila
     */
    public static List<Vehicle> getVehiclesAfterSimulation() {
        return vehiclesAfterSimulation;
    }

    /**
     * Metoda runSimulations je glavna metoda za pokretanje simulacije. Obuhvata kreiranje korisnickih dokumenata,
     * kreiranja liste svih racuna, samu simulaciju i niti (pojedinacne za svako iznajmljivanje i citavu simulaciju),
     * racunanje rezultata poslovanja i njihovo prosljedjivanje kontrolerima i upisivanje u tabelu itd.
     *
     * @param groupedRentals - iznajmljivanja grupisana prema vremenu pocetka iznajmljivanja
     * @param mainController - instanca glavnog kontrolera GUI-ja
     * @throws IOException u slucaju greske prilikom obradjivanja niti
     */
    public static void runSimulations(Map<LocalDateTime, List<Rental>> groupedRentals, MainController mainController) throws IOException {
        UserDocumentHandler.generateUserDocuments();
        allRentals.clear();
        List<Receipt> allReceipts = new ArrayList<>(); // Lista za prikupljanje svih racuna
        Map<String, String> vehicleTypes = mainController.getVehicles().stream()
                .collect(Collectors.toMap(Vehicle::getId, Vehicle::getType)); // Mapa ID vozila na tip vozila

        for (Map.Entry<LocalDateTime, List<Rental>> entry : groupedRentals.entrySet()) {
            LocalDateTime rentalTime = entry.getKey();
            List<Rental> rentalGroup = entry.getValue();

            System.out.println("Starting simulations for time: " + rentalTime);

            List<Thread> threads = new ArrayList<>();
            for (Rental rental : rentalGroup) {
                allRentals.add(rental); // Dodajemo sve najmove u listu
                Thread thread = new Thread(new RentalSimulation(rental, mainController));
                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Main thread was interrupted during join: " + e.getMessage());
                }
            }

            // Prikupljanje racuna iz trenutne grupe simulacija
            rentalGroup.forEach(rental -> {
                Receipt receipt = rental.getReceipt();
                if (receipt != null) {
                    allReceipts.add(receipt);
                }
            });

            // Cuvamo nivo baterije za sva vozila u mainController-u
            saveBatteryLevels(mainController.getVehicles());

            // Pauza izmedju razlicitih vremenskih grupa iznajmljivanja
            System.out.println("Pausing for 5 seconds before next time group...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread was interrupted during sleep: " + e.getMessage());
            }
        }

        System.out.println("All simulations completed.");

        // Racunanje rezultata poslovanja
        ResultsController resultsController = mainController.getResultsController();
        Results results = new Results(resultsController); // Prosljedjujemo vec gore inicijalizovanom kontroleru
        results.calculateRentalValues(allRentals);
        results.calculateDailyValues(allRentals);
        results.calculateSummaryValuesAndAddToTable(allRentals);
        mainController.setSummaryResults(List.of(results)); //
        mainController.setDailyResults(List.of(results)); // U ovoj i gornjoj liniji cuvamo rezultate u mainController-u, da ih ne bismo izgubili prilikom inicijalizacije prozora klikom na dugme (tad bi se rezultati svi vratili na nulu)

        //Azuriramo rezultate i cuvamo ih u resultsController-u
        if (resultsController != null) {
            List<Results> resultsList = List.of(results); // Koristimo obicnu listu
            resultsController.setResults(resultsList); // Ovo ce automatski konvertovati u ObservableList u setResults() metodi
        } else {
            System.err.println("ResultsController instance not available.");
        }

        // Pozivanje i ispis najprofitabilnijih vozila
        Map<String, Map.Entry<String, Double>> topRevenueVehicles = ReceiptHandler.getTopRevenueVehiclesByType(allReceipts, vehicleTypes);
        ReceiptHandler.printTopRevenueVehicles(topRevenueVehicles);
    }
}