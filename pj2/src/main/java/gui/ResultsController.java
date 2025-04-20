package gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rent.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Ova klasa predstavlja kontroler za prozor o rezultatima poslovanja
 * Prikazuje dnevne i sumarne izvjestaje poslovanja kompanije
 */
public class ResultsController {

    @FXML
    private TableView<Results> dailyReportTable;

    @FXML
    private TableColumn<Results, LocalDate> dateColumn = new TableColumn<>("Date");
    @FXML
    private TableColumn<Results, Double> totalRevenueColumn = new TableColumn<>("Revenue");
    @FXML
    private TableColumn<Results, Double> totalDiscountColumn = new TableColumn<>("Discount");
    @FXML
    private TableColumn<Results, Double> totalPromotionsColumn = new TableColumn<>("Promotions");
    @FXML
    private TableColumn<Results, Double> narrowCityRidesColumn = new TableColumn<>("Narrow City Rides");
    @FXML
    private TableColumn<Results, Double> wideCityRidesColumn = new TableColumn<>("Wide City Rides");
    @FXML
    private TableColumn<Results, Double> maintenanceCostColumn = new TableColumn<>("Maintenance Cost");
    @FXML
    private TableColumn<Results, Double> repairCostColumn = new TableColumn<>("Repair Cost");

    @FXML
    private TableView<Results> summaryReportTable;

    @FXML
    private TableColumn<Results, Double> summaryTotalRevenueColumn = new TableColumn<>("Total Revenue");
    @FXML
    private TableColumn<Results, Double> summaryTotalDiscountColumn = new TableColumn<>("Total Discount");
    @FXML
    private TableColumn<Results, Double> summaryTotalPromotionsColumn = new TableColumn<>("Total Promotions");
    @FXML
    private TableColumn<Results, Double> summaryNarrowCityRidesColumn = new TableColumn<>("Narrow City Rides");
    @FXML
    private TableColumn<Results, Double> summaryWideCityRidesColumn = new TableColumn<>("Wide City Rides");
    @FXML
    private TableColumn<Results, Double> summaryMaintenanceCostColumn = new TableColumn<>("Maintenance Cost");
    @FXML
    private TableColumn<Results, Double> summaryRepairCostColumn = new TableColumn<>("Repair Cost");
    @FXML
    private TableColumn<Results, Double> companyCostsColumn = new TableColumn<>("Company Costs");
    @FXML
    private TableColumn<Results, Double> totalTaxColumn = new TableColumn<>("Total Tax");

    private  ObservableList<Results> dailyResults = FXCollections.observableArrayList();
    private  ObservableList<Results> summaryResults = FXCollections.observableArrayList();

    /**
     * Prazan konstruktor
     * log je dodat zbog provjere
     */
    public ResultsController() {
        System.out.println("ResultsController konstruktor pozvan!");
    }

    /**
     * Metoda koja obavlja inicijalizaciju prozora i kreiranje tabele
     * Dodaje podatke u tabelu i ispisuje ih
     * Ostalo je puno logova zbog debuggovanja
     */
    @FXML
    public void initialize() {
        System.out.println("ResultsController initialize pozvan!");

        if (summaryReportTable == null) {
            System.err.println("summaryReportTable nije inicijalizovana!");
            return;
        }

        // Podesavanje kolona i sirina samo jednom
        if (summaryResults == null || summaryResults.isEmpty() || dailyResults == null || dailyResults.isEmpty()){
            setupColumns();
            summaryResults = FXCollections.observableArrayList();
            dailyResults = FXCollections.observableArrayList();
            summaryReportTable.getColumns().addAll(summaryTotalRevenueColumn, summaryTotalDiscountColumn, summaryTotalPromotionsColumn, summaryNarrowCityRidesColumn, summaryWideCityRidesColumn, summaryMaintenanceCostColumn, summaryRepairCostColumn, companyCostsColumn, totalTaxColumn);
            summaryReportTable.setItems(summaryResults);
            dailyReportTable.getColumns().addAll(dateColumn, totalRevenueColumn, totalDiscountColumn, totalPromotionsColumn, narrowCityRidesColumn, wideCityRidesColumn, maintenanceCostColumn, repairCostColumn);
            dailyReportTable.setItems(dailyResults);

        } else {
            // Osvjezavanje tabele ako vec postoje podaci
            summaryReportTable.getColumns().addAll(summaryTotalRevenueColumn, summaryTotalDiscountColumn, summaryTotalPromotionsColumn, summaryNarrowCityRidesColumn, summaryWideCityRidesColumn, summaryMaintenanceCostColumn, summaryRepairCostColumn, companyCostsColumn, totalTaxColumn);
            summaryReportTable.setItems(summaryResults);
            dailyReportTable.getColumns().addAll(dateColumn, totalRevenueColumn, totalDiscountColumn, totalPromotionsColumn, narrowCityRidesColumn, wideCityRidesColumn, maintenanceCostColumn, repairCostColumn);
            dailyReportTable.setItems(dailyResults);
        }

        summaryReportTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        setColumnWidths(summaryReportTable);
        dailyReportTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        setColumnWidths(dailyReportTable);

        System.out.println("ResultsController initialize završen.");
    }


    /**
     * Metoda koja postavlja sirinu kolona
     * @param table - tabela prikazana u prozoru
     */
    private void setColumnWidths(TableView<Results> table) {
        table.getColumns().forEach(column -> column.setPrefWidth(150));
    }

    /**
     * Metoda koja postavlja vrijednosti rezultata poslovanja u kolone
     * Koristili smo ? operater da vrijednosti na pocetku ne bi bile null
     */
    private void setupColumns() {
        // Bindovanje kolona za summary tabelu
        summaryTotalRevenueColumn.setCellValueFactory(data -> {
            Double totalRevenue = data.getValue().getTotalRevenue();
            return new SimpleObjectProperty<>(totalRevenue != null ? roundToTwoDecimals(totalRevenue) : 0.0);
        });
        summaryTotalDiscountColumn.setCellValueFactory(data -> {
            Double totalDiscount = data.getValue().getTotalDiscount();
            return new SimpleObjectProperty<>(totalDiscount != null ? roundToTwoDecimals(totalDiscount) : 0.0);
        });
        summaryTotalPromotionsColumn.setCellValueFactory(data -> {
            Double totalPromotions = data.getValue().getTotalPromo();
            return new SimpleObjectProperty<>(totalPromotions != null ? roundToTwoDecimals(totalPromotions) : 0.0);
        });
        summaryNarrowCityRidesColumn.setCellValueFactory(data -> {
            Double narrowCityRides = data.getValue().getTotalNarrowIncome();
            return new SimpleObjectProperty<>(narrowCityRides != null ? roundToTwoDecimals(narrowCityRides) : 0);
        });
        summaryWideCityRidesColumn.setCellValueFactory(data -> {
            Double wideCityRides = data.getValue().getTotalWideIncome();
            return new SimpleObjectProperty<>(wideCityRides != null ? roundToTwoDecimals(wideCityRides) : 0);
        });
        summaryMaintenanceCostColumn.setCellValueFactory(data -> {
            Double maintenanceCost = data.getValue().getMaintenanceCost();
            return new SimpleObjectProperty<>(maintenanceCost != null ? roundToTwoDecimals(maintenanceCost) : 0.0);
        });
        summaryRepairCostColumn.setCellValueFactory(data -> {
            Double repairCost = data.getValue().getRepairCost();
            return new SimpleObjectProperty<>(repairCost != null ? roundToTwoDecimals(repairCost) : 0.0);
        });
        companyCostsColumn.setCellValueFactory(data -> {
            Double companyCosts = data.getValue().getCompanyCosts();
            return new SimpleObjectProperty<>(companyCosts != null ? roundToTwoDecimals(companyCosts) : 0.0);
        });
        totalTaxColumn.setCellValueFactory(data -> {
            Double totalTax = data.getValue().getTotalTax();
            return new SimpleObjectProperty<>(totalTax != null ? roundToTwoDecimals(totalTax) : 0.0);
        });

        // Bindovanje kolona za daily tabelu
        dateColumn.setCellValueFactory(data -> {
            LocalDate resultDate = data.getValue().getResultDate();
            return new SimpleObjectProperty<>(resultDate);
        });
        totalRevenueColumn.setCellValueFactory(data -> {
            Double totalRevenue = data.getValue().getTotalRevenue();
            return new SimpleObjectProperty<>(totalRevenue != null ? roundToTwoDecimals(totalRevenue) : 0.0);
        });
        totalDiscountColumn.setCellValueFactory(data -> {
            Double totalDiscount = data.getValue().getTotalDiscount();
            return new SimpleObjectProperty<>(totalDiscount != null ? roundToTwoDecimals(totalDiscount) : 0.0);
        });
        totalPromotionsColumn.setCellValueFactory(data -> {
            Double totalPromotions = data.getValue().getTotalPromo();
            return new SimpleObjectProperty<>(totalPromotions != null ? roundToTwoDecimals(totalPromotions) : 0.0);
        });
        narrowCityRidesColumn.setCellValueFactory(data -> {
            Double narrowCityRides = data.getValue().getTotalNarrowIncome();
            return new SimpleObjectProperty<>(narrowCityRides != null ? roundToTwoDecimals(narrowCityRides) : 0);
        });
        wideCityRidesColumn.setCellValueFactory(data -> {
            Double wideCityRides = data.getValue().getTotalWideIncome();
            return new SimpleObjectProperty<>(wideCityRides != null ? roundToTwoDecimals(wideCityRides) : 0);
        });
        maintenanceCostColumn.setCellValueFactory(data -> {
            Double maintenanceCost = data.getValue().getMaintenanceCost();
            return new SimpleObjectProperty<>(maintenanceCost != null ? roundToTwoDecimals(maintenanceCost) : 0.0);
        });
        repairCostColumn.setCellValueFactory(data -> {
            Double repairCost = data.getValue().getRepairCost();
            return new SimpleObjectProperty<>(repairCost != null ? roundToTwoDecimals(repairCost) : 0.0);
        });
    }

    /**
     * Pomocna metoda za zaokruzivanje na dve decimale
     * @param value - vrijednost koju prosljedjujemo
     * @return vraca vrijednost zaokruzenu na dve decimale
     */
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Metoda koja dodaje dnevne izvjestaje u svoju odgovarajucu dabelu
     * @param date - datum za koji je vezan izvjestaj
     * @param income - ukupan prihod
     * @param discount - ukupan popust
     * @param promotion - ukupno oduzeto od cijene zbog promocije
     * @param narrow - ukupan iznos za uzi dio grada
     * @param wide - ukupan iznos za siri dio grada
     * @param maintanance - ukupan iznos za odrzavanje
     * @param repair - ukupan iznos za popravku vozila
     * @throws IOException
     */
    @FXML
    public void addToTableDaily(LocalDate date, double income, double discount, double promotion, double narrow, double wide, double maintanance, double repair) throws IOException {
        dailyResults.add(new Results(date, income, discount, promotion, narrow, wide, maintanance, repair));
    }

    /**
     * Metoda koja dodaje podatke u tabelu za sumarni izvjestaj
     * pazi da ne dodaje dublikate
     * @param result - rezultat koji prosljedjujemo i koji se dodaje u tabelu
     */
    public void addToTableSummary(Results result) {
        System.out.println("addToTableSummary pozvano sa rezultatom: " + result);

        // dodavanje rezultata u listu ako vec nije prisutan
        if (!summaryResults.contains(result)) {
            summaryResults.add(result);
            System.out.println("Rezultat dodat u summaryResults: " + result);
        } else {
            System.out.println("Rezultat već postoji u summaryResults: " + result);
        }

        // Azuriranje
        summaryReportTable.setItems(summaryResults);
        System.out.println("summaryReportTable ažuriran sa summaryResults. Trenutna veličina: " + summaryResults.size());
    }

    /**
     * Metoda koja postavlja rezultate u tabelu i azurira je
     * @param resultsList - lista rezultata koja ce biti postavljena u tabelu
     */
    public void setResults(List<Results> resultsList) {
        if (resultsList != null && !resultsList.isEmpty()) {
            // Pretvaranje oicne liste u observable (koristi se kod poziva u runSimulations metodi)
            ObservableList<Results> observableResults = FXCollections.observableArrayList(resultsList);

            summaryResults.clear();
            summaryResults.addAll(observableResults);

            summaryReportTable.setItems(observableResults);
            System.out.println("Rezultati postavljeni u summaryResults.");
        } else {
            System.err.println("Nema rezultata za postavljanje.");
        }
    }
}
