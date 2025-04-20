package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;
import rent.RentalSimulation;

import java.util.Map;
import java.time.format.DateTimeFormatter;

/**
 * Klasa koja sluzi kao kontroler za prozor Faults
 * Prikazuje kvarove vozila kao i njihove opise, vrijeme kvara itd
 * Prikaz je tabelaran
 */
public class FaultsController {

    @FXML
    private TableView<FaultInfo> faultsTable;

    @FXML
    private TableColumn<FaultInfo, String> typeColumn;

    @FXML
    private TableColumn<FaultInfo, String> idColumn;

    @FXML
    private TableColumn<FaultInfo, String> timeColumn;

    @FXML
    private TableColumn<FaultInfo, String> descriptionColumn;

    /**
     * Metoda za inicijalizaciju koja se poziva kada se klikne na dugme Faults
     * Kreira i postavlja vrijednosti u kolone
     */
    @FXML
    public void initialize() {
        System.out.println("Broken vehicles view loaded!");

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Ucitavanje podataka o kvarovima
        loadFaultyVehicles();
    }

    /**
     * Metoda koja ucitava podatke o kvarovima i pokvarenim vozilima
     */
    private void loadFaultyVehicles() {
        Map<String, Vehicle> faultyVehicles = RentalSimulation.getFaultyVehicles();
        ObservableList<FaultInfo> data = FXCollections.observableArrayList();

        // Formatiramo LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Vehicle vehicle : faultyVehicles.values()) {
            data.add(new FaultInfo(
                    vehicle.getClass().getSimpleName(),
                    vehicle.getId(),
                    vehicle.getFailTime().format(formatter), // Konverzija LocalDateTime u String
                    vehicle.getFailReason()
            ));
        }

        // Postavljanje podataka u tabelu
        faultsTable.setItems(data);
    }

    /**
     * Pomocna klasa za kvarove u tabeli
     */
    public static class FaultInfo {
        private final String type;
        private final String id;
        private final String time;
        private final String description;

        /**
         * Konstruktor pomocne klase
         * @param type - tip vozila
         * @param id - identifikator vozila
         * @param time - vrijeme kvara
         * @param description - opis kvara
         */
        public FaultInfo(String type, String id, String time, String description) {
            this.type = type;
            this.id = id;
            this.time = time;
            this.description = description;
        }

        // Getter metode za svako polje
        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public String getTime() {
            return time;
        }

        public String getDescription() {
            return description;
        }
    }
}
