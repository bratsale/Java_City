package gui;

import handler.CSVHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import model.Vehicle;
import rent.Rental;
import main.SimulationManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rent.Results;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa koja predstavlja glavni kontroler GUI-ja
 * Upravljace simulacijom uglavnom i sluzice kao prozor za biranje drugih opcija u GUI-ju
 */
public class MainController {

    private VehiclesController vehiclesController;
    private ResultsController resultsController;
    private List<Results> summaryResults = new ArrayList<>();
    private List<Results> dailyResults = new ArrayList<>();


    private Stage resultsStage;
    private Parent resultsRoot; // cuvanje ucitanog fxml-a roditeljskog cvora

    @FXML
    private Label welcomeText;

    @FXML
    private GridPane gridWithLabels;

    @FXML
    private Button startButton;

    @FXML
    private Button vehiclesButton;

    @FXML
    private Button faultsButton;

    @FXML
    private Button resultsButton;

    @FXML
    private Button bestButton;

    // relativne putanje
    private static final String VEHICLES_FILE_PATH = "src/main/java/resources/vozila.csv";
    private static final String RENTALS_FILE_PATH = "src/main/java/resources/iznajmljivanja.csv";

    public static final int GRID_SIZE = 20; // 20x20 grid
    public static final int CELL_SIZE = 30;

    private static List<Vehicle> vehicles;
    private List<Rental> rentals;

    private Map<String, Rectangle> vehicleRectangles = new HashMap<>();
    private Map<String, Label> vehicleLabels = new HashMap<>(); // Mapa za čuvanje Label-ova

    /**
     * Metoda koja inicijalizuje prvi prozor u GUI-ju (osnovni) iz njega mozemo dalje birati opcije
     * @throws IOException u slucaju da se ResultsView.fxml ne ucita
     */
    @FXML
    public void initialize() throws IOException {
        resultsButton.setVisible(false);
        bestButton.setVisible(false);
        vehicles = CSVHandler.loadVehicles(VEHICLES_FILE_PATH);
        rentals = CSVHandler.loadRentals(RENTALS_FILE_PATH, vehicles);

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles loaded!");
        } else {
            System.out.println("Loaded " + vehicles.size() + " vehicles.");
        }

        if (rentals.isEmpty()) {
            System.out.println("No rentals loaded!");
        } else {
            System.out.println("Loaded " + rentals.size() + " rentals.");
        }

        // Ucitavamo ResultsView unaprijed da bismo mogli po zavrsetku simulacije automatski izracunati rezultate poslovanja
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/ResultsView.fxml"));
            resultsRoot = loader.load();
            resultsController = loader.getController();

            if (resultsController != null) {
                System.out.println("ResultsController successfully initialized.");
            } else {
                System.err.println("Failed to initialize ResultsController.");
            }

        } catch (IOException e) {
            System.err.println("Failed to load Results.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Metoda koja zapocinje samu simulaciju
     * Povezana je sa dugmetom "Start simulation"
     * Inicijalizuje mrezu, pokrece niti i samu metodu runSimulations
     */
    @FXML
    protected void onStartSimulationClick() {
        startButton.setText("Simulating...");

        resultsButton.setVisible(false);
        bestButton.setVisible(false);

        // Prikazivanje mreze
        welcomeText.setText("Java City");
        gridWithLabels.setVisible(true);
        initializeGrid();

        // Pokretanje simulacije u posebnoj niti
        new Thread(() -> {
            Map<LocalDateTime, List<Rental>> groupedRentals = SimulationManager.groupRentalsByTime(rentals);
            try {
                SimulationManager.runSimulations(groupedRentals, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Kada simulacija zavrsi, azuriramo gui i dodajemo dugmad za rezultate i dodatnu funkcionalnost
            Platform.runLater(() -> {
                startButton.setText("Simulation Finished");

                resultsButton.setVisible(true);
                bestButton.setVisible(true);

                // Uklanjamo sva vozila s mape
                for (Vehicle vehicle : vehicles) {
                    removeVehicleMarker(vehicle.getId());
                }
            });
        }).start();
    }

    /**
     * Metoda za kreiranje 20x20 mreze
     */
    private void initializeGrid() {
        gridWithLabels.getChildren().clear();

        for (int i = 0; i < GRID_SIZE; i++) {
            Label columnLabel = new Label(String.valueOf(i));
            columnLabel.setAlignment(Pos.CENTER);
            gridWithLabels.add(columnLabel, i + 1, 0);

            Label rowLabel = new Label(String.valueOf(i));
            rowLabel.setAlignment(Pos.CENTER);
            gridWithLabels.add(rowLabel, 0, i + 1);
        }

        // dodajemo 20x20 mrezu
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);


                if (row >= 5 && row < 15 && col >= 5 && col < 15) {
                    cell.setFill(Color.LIGHTBLUE); // Uzi dio grada
                } else {
                    cell.setFill(Color.WHITE); // Siri dio grada
                }

                cell.setStroke(Color.BLACK);
                gridWithLabels.add(cell, col + 1, row + 1);
            }
        }
    }

    /**
     * Metoda koja azurira poziciju vozila na mapi
     * Takodje azurira i nivo baterije vozila
     * @param vehicleId
     * @param row
     * @param col
     * @param finalRow
     * @param finalCol
     * @param isSimulationFinished
     */
    public void updateVehiclePosition(String vehicleId, int row, int col, int finalRow, int finalCol, boolean isSimulationFinished) {
        Platform.runLater(() -> {
            Rectangle vehicleRect = vehicleRectangles.get(vehicleId);
            Label vehicleLabel = vehicleLabels.get(vehicleId);

            if (vehicleRect == null) {
                vehicleRect = new Rectangle(CELL_SIZE, CELL_SIZE, Color.RED); // Crvena boja za vozilo
                vehicleRect.setStroke(Color.BLACK); // Crna ivica
                vehicleRectangles.put(vehicleId, vehicleRect);
                gridWithLabels.add(vehicleRect, col + 1, row + 1); // Dodaj u mrežu

                // Label identifikatora vozila i nivo baterije
                Vehicle vehicle = getVehicleById(vehicleId);
                if (vehicle != null) {
                    vehicleLabel = new Label(vehicleId + "\n" + Math.round(vehicle.getBatteryLevel()) + "%");
                    vehicleLabel.setWrapText(true);
                    vehicleLabel.setMaxWidth(CELL_SIZE);
                    vehicleLabel.setAlignment(Pos.CENTER);
                    vehicleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: black;");
                    vehicleLabels.put(vehicleId, vehicleLabel);

                    gridWithLabels.add(vehicleLabel, col + 1, row);
                }
            } else {
                // Ako vozilo postoji, azuriramo poziciju
                GridPane.setColumnIndex(vehicleRect, col + 1);
                GridPane.setRowIndex(vehicleRect, row + 1);

                GridPane.setColumnIndex(vehicleLabel, col + 1);
                GridPane.setRowIndex(vehicleLabel, row); //

                // Azuriramo label zbog novog nivoa baterije (-1)
                Vehicle vehicle = getVehicleById(vehicleId);
                if (vehicle != null) {
                    vehicleLabel.setText(vehicleId + "\n" + Math.round(vehicle.getBatteryLevel()) + "%");

                }
            }

            if (isSimulationFinished) {
                removeVehicleMarker(vehicleId);
            }
        });
    }

    /**
     * Metoda za uklanjanje markera vozila sa mape
     * @param vehicleId - identifikator vozila
     */
    public void removeVehicleMarker(String vehicleId) {
        Platform.runLater(() -> {
            Rectangle vehicleRect = vehicleRectangles.remove(vehicleId);
            Label vehicleLabel = vehicleLabels.remove(vehicleId);

            if (vehicleRect != null) {
                gridWithLabels.getChildren().remove(vehicleRect);
            }
            if (vehicleLabel != null) {
                gridWithLabels.getChildren().remove(vehicleLabel);
            }
        });
    }

    /**
     * Metoda za dobijanje vozila prema njegovom identifikatoru
     * @param vehicleId - identifikator vozila
     * @return vraca vozilo prema identifikatoru
     */
    private Vehicle getVehicleById(String vehicleId) {
        return vehicles.stream()
                .filter(vehicle -> vehicle.getId().equals(vehicleId))
                .findFirst()
                .orElse(null);
    }


    /**
     * Metoda za dobijanje objekta VehiclesController
     * @return vraca taj objekat
     */
    public VehiclesController getVehiclesController() {
        return vehiclesController;
    }

    /**
     * Metoda za otvaranje novog prozora prilikom klika na dugme Vehicles
     */
    @FXML
    private void onVehiclesClick() {
        openNewWindow("VehiclesView.fxml", "Vehicles");
    }

    /**
     * Metoda za otvaranje novog prozora prilikom klika na dugme Faults
     */
    @FXML
    private void onFaultsClick() {
        openNewWindow("FaultsView.fxml", "Faults");
    }

    /**
     * Metoda za otvaranje novog prozora prilikom klika na dugme Top earning vehicles
     */
    @FXML
    private void onBestClick() { openNewWindow("BestView.fxml", "Top Earning Vehicles"); }

    /**
     * Metoda za otvaranje prozora prilikom klika na dugme Results
     * @throws IOException - u slucaju da se ne otvori/ucita prozor Results
     */
    @FXML
    private void onResultsClick() throws IOException {
        if (resultsStage == null) {
            try {
                // Kreiramo novi stage koristeci vec ucitan root
                resultsStage = new Stage();
                resultsStage.setTitle("Results");
                resultsStage.setScene(new Scene(resultsRoot)); // resultsRoot je vec ucitan u metodi initialize()

                resultsStage.setOnCloseRequest(event -> resultsStage = null);

                resultsStage.show();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to open Results window.");
            }
        } else {
            resultsStage.toFront(); // Ako prozor već postoji, dovedi ga u fokus
        }
    }

    /**
     * Metoda za otvaranje novog prozora, koristi se za prozor Vehicles, Faults, Top earning vehicles
     * Kod results je drugacije zbog obracuna koji se rade po zavrsetku simulacije
     * @param fxmlFile
     * @param title
     */
    private void openNewWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/main/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to open window: " + title);
        }
    }

    /**
     * Metoda za dobijanje liste svih vozila
     * @return vraca tu listu
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * Metoda za dobijanje instance objekta ResultsController-a
     * @return vraca taj objekat
     */
    public ResultsController getResultsController() {
        return resultsController;
    }

    /**
     * Metoda za dobijanje liste sumarnih izvjestaja poslovanja
     * @return vraca tu listu
     */
    public List<Results> getSummaryResults() {
        return summaryResults;
    }

    /**
     * Metoda za postavljanje liste sumarnih izvjestaja poslovanja
     * @param summaryResults - lista sumarnih izvjestaja
     */
    public void setSummaryResults(List<Results> summaryResults) {
        this.summaryResults = summaryResults;
    }

    /**
     * Metoda za postavljanje liste dnevnih izvjestaja poslovanje
     * @param dailyResults - lista dnevnih izvjestaja
     */
    public void setDailyResults(List<Results> dailyResults) {
        this.dailyResults = dailyResults;
    }

    /**
     * Metoda koja vraca tip vozila
     * @return vraca tip vozila
     */
    public static Map<String, String> getVehicleTypes() {
        Map<String, String> vehicleTypes = new HashMap<>();
        for (Vehicle v : vehicles) {
            vehicleTypes.put(v.getId(), v.getClass().getSimpleName());
        }
        return vehicleTypes;
    }

}




