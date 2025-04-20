package gui;

import handler.ReceiptHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import rent.Receipt;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Klasa za dodatnu funkcionalnost, odnosno racunanje najprofitabilnijih vozila za svaku vrstu
 * Ova klasa je kontroler prozora Top earning vehicles
 */
public class BestController {

    @FXML
    private Button serializeButton;

    @FXML
    private Button deserializeButton;

    @FXML
    private TextArea topVehiclesTextArea;

    private static final String FILE_PATH = "src/main/java/resources/best/topVehicles.dat";

    /**
     *  Metoda koja obavlja serijalizaciju podataka prilikom klika na dugme Serialize
     *  Serijalizuje podatke i upisuje ih u binarni fajl na odredjenoj lokaciji src/main/java/resources/best
     * @throws IOException u slucaju greske prilikom serijalizacije
     */
    @FXML
    private void handleSerialize() throws IOException {
        System.out.println("Serialize button clicked!");

        // Dobavljanje podataka o najprofitabilnijim vozilima
        List<Receipt> allReceipts = ReceiptHandler.getReceipts();
        Map<String, String> vehicleTypes = MainController.getVehicleTypes();

        Map<String, Map.Entry<String, Double>> topRevenueVehicles = ReceiptHandler.getTopRevenueVehiclesByType(allReceipts, vehicleTypes);

        // Serijalizacija u binarni fajl
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(topRevenueVehicles);
            System.out.println("Top revenue vehicles successfully serialized.");
        } catch (IOException e) {
            System.err.println("Error during serialization: " + e.getMessage());
        }
    }

    /**
     * Metoda koja obavlja deserijalizaciju podataka prilikom klika na dugme Deserialize
     * Deserijalizuje podatke iz binarnog fajla i upisuje ih i prikazuje u prozoru Top earning vehicles
     * @throws IOException u slucaju greske prilikom deserijalizacije
     */
    @FXML
    private void handleDeserialize() throws IOException {
        System.out.println("Deserialize button clicked!");

        // Deserijalizacija podataka iz binarnog fajla
        Map<String, Map.Entry<String, Double>> topRevenueVehicles = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            topRevenueVehicles = (Map<String, Map.Entry<String, Double>>) ois.readObject();
            System.out.println("Top revenue vehicles successfully deserialized.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during deserialization: " + e.getMessage());
        }

        // Formatiraj i ispisi podatke u TextArea
        if (topRevenueVehicles != null && !topRevenueVehicles.isEmpty()) {
            StringBuilder displayText = new StringBuilder();
            for (Map.Entry<String, Map.Entry<String, Double>> entry : topRevenueVehicles.entrySet()) {
                String vehicleType = entry.getKey();
                String vehicleId = entry.getValue().getKey();
                Double earnings = entry.getValue().getValue();
                displayText.append(String.format("ID: %s, Type: %s, Earnings: %.2f%n", vehicleId, vehicleType, earnings));
            }
            topVehiclesTextArea.setText(displayText.toString());
        } else {
            topVehiclesTextArea.setText("No data found or error during deserialization.");
        }
    }
}
