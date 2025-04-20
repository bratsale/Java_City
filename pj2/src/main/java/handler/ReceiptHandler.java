package handler;

import rent.Receipt;
import rent.Rental;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Klasa ReceiptHandler sluzi da s lakse ucitaju i kreiraju racuni za iznajmljivanja
 * Ovo je samo handler, ne treba je zbuniti za klasu Receipt koja je dalje u projektu napravljena
 */
public class ReceiptHandler {
    private static final List<Receipt> receipts = new ArrayList<>(); // Lista svih računa kao objekata

    /**
     * Metoda koja generise racun, kreira instancu Receipt (zbog kasnijih upotreba vezanih za rezultate poslovanja) i
     * upisuje racune u fajlove koje smjesta u poseban direktorijum
     * @param rental - iznajmljivanje za koje ce biti generisan racun
     * @param idNumber - identifikator racuna
     * @param driverLicense - vozacka dozvola
     */
    public static void generateReceipt(Rental rental, String idNumber, String driverLicense) {
        String folderPath = "src/main/java/resources/receipts";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = folderPath + "/receipt_" + rental.getVehicle().getId() + "_" + LocalDateTime.now() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            StringBuilder receiptContent = new StringBuilder();
            receiptContent.append("RECEIPT\n");
            receiptContent.append("User ID: ").append(rental.getUserId()).append("\n");
            receiptContent.append("Personal ID: ").append(idNumber).append("\n");
            receiptContent.append("Driver License: ").append(driverLicense).append("\n");
            receiptContent.append("Vehicle ID: ").append(rental.getVehicle().getId()).append("\n");
            receiptContent.append("Start Location: ").append(rental.getStartLocation()).append("\n");
            receiptContent.append("Destination: ").append(rental.getEndLocation()).append("\n");
            receiptContent.append("Start Time: ").append(rental.getStartTime()).append("\n");
            receiptContent.append("End Time: ").append(rental.getEndTime()).append("\n");
            receiptContent.append("Price: ").append(rental.getTotalPrice()).append("\n");
            receiptContent.append("Promotion Applied: ").append(rental.getPromotion()).append("\n");
            receiptContent.append("Fault Occurred: ").append(rental.getFault()).append("\n");
            receiptContent.append("\nThank you for using our service!");

            writer.write(receiptContent.toString());

            // Kreiranje Receipt objekta i dodavanje u listu
            Receipt receipt = new Receipt(
                    rental.getUserId(),
                    idNumber,
                    driverLicense,
                    rental.getVehicle().getId(),
                    rental.getStartLocation(),
                    rental.getEndLocation(),
                    rental.getStartTime(),
                    rental.getEndTime(),
                    rental.getTotalPrice(),
                    rental.getPromotion(),
                    rental.getFault()
            );
            receipts.add(receipt);
            System.out.println("Receipt generated: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda koja dobija listu svih racuna
     * @return vraca tu listu
     */
    public static List<Receipt> getReceipts() {
        return receipts;
    }

    /**
     * Metoda koja trazi i vraca po jedno vozilo iz svakog tipa vozila koje je najvise prihoda donijelo kompaniji
     * ova metoda je napravljena u sklopu dodatne funkcionalnosti koju smo morali implementirati na osnovu indeksa (1153/20)
     * @param receipts - lista svih racuna
     * @param vehicleTypes - tipovi vozila
     * @return vraca mapu sa vozilima koji su bili najprofitabilniji za kompaniju
     */
    public static Map<String, Map.Entry<String, Double>> getTopRevenueVehiclesByType(List<Receipt> receipts, Map<String, String> vehicleTypes) {
        // Map za cuvanje ukupnog prihoda po vozilu
        Map<String, Double> revenueByVehicle = receipts.stream()
                .collect(Collectors.groupingBy(receipt -> receipt.getVehicleId(), Collectors.summingDouble(Receipt::getTotalPrice)));

        // Map za cuvanje najprofitabilnijeg vozila po tipu
        Map<String, Map.Entry<String, Double>> topRevenueVehicles = new HashMap<>();

        // Grupisemo vozila prema tipu
        revenueByVehicle.forEach((vehicleId, revenue) -> {
            String vehicleType = vehicleTypes.get(vehicleId);
            if (vehicleType != null) {
                topRevenueVehicles.compute(vehicleType, (type, currentBest) -> {
                    // Ako jos nemamo vozila za taj tip, postavljamo ovo
                    if (currentBest == null || revenue > currentBest.getValue()) {
                        return new AbstractMap.SimpleEntry<>(vehicleId, revenue);
                    }
                    return currentBest;
                });
            }
        });

        return topRevenueVehicles;
    }

    /**
     * Metoda za ispis najprofitabilnijih vozila
     * @param topRevenueVehicles - najprofitabilnija vozila
     */
    public static void printTopRevenueVehicles(Map<String, Map.Entry<String, Double>> topRevenueVehicles) {
        System.out.println("Top revenue vehicles by type:");
        topRevenueVehicles.forEach((type, vehicleEntry) -> {
            System.out.println("Type: " + type);
            System.out.println("    Vehicle ID: " + vehicleEntry.getKey() + " | Revenue: " + vehicleEntry.getValue());
        });
    }

}
