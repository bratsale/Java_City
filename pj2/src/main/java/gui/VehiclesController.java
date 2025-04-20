package gui;

import handler.CSVHandler;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import main.SimulationManager;
import model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Klasa koja se bavi prozorom Vehicles i sluzi kao njegov kontroler
 * Obradjuje spisak vozila za iznajmljivanje, kao i postotke baterije i atribute tokom simulacije
 */
public class VehiclesController {

    @FXML
    private TableView<ECar> carTable;
    @FXML
    private TableColumn<ECar, String> carIdColumn;
    @FXML
    private TableColumn<ECar, String> carManufacturerColumn;
    @FXML
    private TableColumn<ECar, String> carModelColumn;
    @FXML
    private TableColumn<ECar, Double> carPriceColumn;
    @FXML
    private TableColumn<ECar, String> carDescriptionColumn;
    @FXML
    private TableColumn<ECar, String> carPurchaseDateColumn;
    @FXML
    private TableColumn<ECar, Double> carBatteryColumn;

    @FXML
    private TableView<EBike> bikeTable;
    @FXML
    private TableColumn<EBike, String> bikeIdColumn;
    @FXML
    private TableColumn<EBike, String> bikeManufacturerColumn;
    @FXML
    private TableColumn<EBike, String> bikeModelColumn;
    @FXML
    private TableColumn<EBike, Double> bikePriceColumn;
    @FXML
    private TableColumn<EBike, Double> bikeRangeColumn;
    @FXML
    private TableColumn<EBike, Double> bikeBatteryColumn;

    @FXML
    private TableView<EScooter> scooterTable;
    @FXML
    private TableColumn<EScooter, String> scooterIdColumn;
    @FXML
    private TableColumn<EScooter, String> scooterManufacturerColumn;
    @FXML
    private TableColumn<EScooter, String> scooterModelColumn;
    @FXML
    private TableColumn<EScooter, Double> scooterPriceColumn;
    @FXML
    private TableColumn<EScooter, Double> scooterSpeedColumn;
    @FXML
    private TableColumn<EScooter, Double> scooterBatteryColumn;

    /**
     * Metoda za inicijalizaciju prozora, poziva se kada se klikne na dugme Vehicles
     * Takodje, kreira tabelu i postavlja vrijednosti atributa vozila u kolone tabele prema svakom vozilu
     */
    @FXML
    public void initialize() {
        List<Vehicle> vehicles = CSVHandler.loadVehicles("src/main/java/resources/vozila.csv");

        // Filtriranje vozila
        for (Vehicle vehicle : vehicles) {
            if (vehicle instanceof ECar) {
                ECar car = (ECar) vehicle;
                carTable.getItems().add(car);
            } else if (vehicle instanceof EBike) {
                EBike bike = (EBike) vehicle;
                bikeTable.getItems().add(bike);
            } else if (vehicle instanceof EScooter) {
                EScooter scooter = (EScooter) vehicle;
                scooterTable.getItems().add(scooter);
            }
        }

        carIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        carManufacturerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getManufacturer()));
        carModelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        carPriceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));
        carDescriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCarDescription()));
        carPurchaseDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCarDdate().toString()));
        carBatteryColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBatteryLevel()));

        bikeIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        bikeManufacturerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getManufacturer()));
        bikeModelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        bikePriceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));
        bikeRangeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRangePerCharge()));
        bikeBatteryColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBatteryLevel()));

        scooterIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        scooterManufacturerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getManufacturer()));
        scooterModelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        scooterPriceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));
        scooterSpeedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMaxSpeed()));
        scooterBatteryColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBatteryLevel()));

    }

    /**
     * Metoda za azuriranje atributa vozila prilikom klika na dugme Refresh
     * (sluzi za azuriranje nivoa baterije tokom simulacije)
     */
    @FXML
    public void onVehiclesButtonClick() {
        List<Vehicle> updatedVehicles = SimulationManager.getVehiclesAfterSimulation();

        carTable.getItems().clear();
        bikeTable.getItems().clear();
        scooterTable.getItems().clear();

        for (Vehicle vehicle : updatedVehicles) {
            if (vehicle instanceof ECar) {
                ECar car = (ECar) vehicle;
                carTable.getItems().add(car);
            } else if (vehicle instanceof EBike) {
                EBike bike = (EBike) vehicle;
                bikeTable.getItems().add(bike);
            } else if (vehicle instanceof EScooter) {
                EScooter scooter = (EScooter) vehicle;
                scooterTable.getItems().add(scooter);
            }
        }
    }

}