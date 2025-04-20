package rent;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.*;
import gui.*;
import handler.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Results klasa sadrzi rezultate poslovanja kompanije koja vrsi iznajmljivanje vozila.
 * Sluzi za mogucnost generisanja sumarnog i dnevnog izvjestaja poslovanja date kompanije.
 */
public class Results {

    private double totalRevenue = 0;
    private double totalDiscount = 0;
    private double totalPromo = 0;
    private double totalNarrowIncome = 0;
    private double totalWideIncome = 0;
    private double maintenanceCost = 0;
    private double repairCost = 0;
    private double companyCosts = 0;
    private double totalTax = 0;
    private LocalDate resultDate = null;
    private ResultsController controller;

    /**
     * Konstruktor za klasu Results kojem prosljedjujemo ResultsController instancu
     * (koristicemo je pri racunanju rezultata poslovanja kompanije po zavrsetku simulacije)
     * @param controller - ta instanca kontrolera
     */
    public Results(ResultsController controller) {
        this.controller = controller;
    }

    /**
     * Konstruktor koji generise prazan rezultat.
     * Ovo je dobra pocetna tacka za dobijanje rezultata koji se koriste za generisanje izvjestaja, jer nema prethodnih informacija.
     */
    public Results() {

    }

    /**
     * Konstruktor koji generise rezultat sa datim vrijednostima.
     * Predstavlja nacin generisanja izvjestaja ukoliko vec unaprijed imamo date informacije o rezultatima.
     *
     */
    public Results(LocalDate date, double totalFinalIncome, double totalDiscount, double totalPromo, double totalNarrowIncome, double totalWideIncome, double totalUpkeep, double totalRepair) throws IOException {
        this.totalRevenue = totalFinalIncome;
        this.totalDiscount = totalDiscount;
        this.totalPromo = totalPromo;
        this.totalNarrowIncome = totalNarrowIncome;
        this.totalWideIncome = totalWideIncome;
        this.maintenanceCost = totalUpkeep;
        this.repairCost = totalRepair;
        resultDate = date;
    }

    /**
     * Metoda koja vraca datum na koji se rezultat poslovanja odnosi.
     *
     * @return Vraca datum na koji se odnosi rezultat poslovanja.
     */
    public LocalDate getResultDate() {
        return resultDate;
    }

    /**
     * Metoda koja vraca ukupni prihod kompanije od vozila koja se krecu samo u uzem krugu grada.
     *
     * @return Vraca prihod od vozila koja se krecu u uzem krugu grada.
     */
    public double getTotalNarrowIncome() {
        return totalNarrowIncome;
    }

    /**
     * Metoda koja vraca ukupni prihod kompanije od vozila koja se krecu i u sirem krugu grada.
     *
     * @return Vraca prihod od vozila koja se krecu i u sirem krugu grada.
     */
    public double getTotalWideIncome() {
        return totalWideIncome;
    }

    /**
     * Metoda koja vraca ukupni prihod kompanije nezavisno od toga gdje su se vozila kretala.
     *
     * @return Vraca ukupni prihod.
     */
    public double getTotalRevenue() {
        return totalRevenue;
    }

    /**
     * Metoda koja vraca kolicinu novca koju su korisnici ustedili na svojim racunima zbog popusta.
     *
     * @return Vraca kolicinu novca koja je ustedjena zbog popusta.
     */
    public double getTotalDiscount() {
        return totalDiscount;
    }

    /**
     * Metoda koja vraca kolicinu nmovca koju su korisnici ustedili na svojim racunima zbog promocija.
     *
     * @return Vraca kolicinu novca koja je ustedjena zbog promocija.
     */
    public double getTotalPromo() {
        return totalPromo;
    }

    /**
     * Metoda koja vraca kolicinu novca koja je potrebna za odrzavanje kompanije.
     *
     * @return Vraca kolicinu novca potrebnu za odrzavanje.
     */
    public double getMaintenanceCost() {
        return maintenanceCost;
    }

    /**
     * Metoda koja vraca ukupnu kolicinu novca koja je potrosena na troskove popravke vozila.
     *
     * @return Vraca kolicinu novca potrebnu za popravke.
     */
    public double getRepairCost() {
        return repairCost;
    }

    /**
     * Metoda koja vraca kolicinu novca koju kompanija treba potrositi na troskove funkcionisanja kompanije.
     *
     * @return Vraca iznos troskova kompanije.
     */
    public double getCompanyCosts() {
        return companyCosts;
    }

    /**
     * Metoda koja vraca iznos poreza koji kompanija mora platiti.
     *
     * @return Vraca iznos poreza kompanije.
     */
    public double getTotalTax() {
        return totalTax;
    }

    /**
     * Metoda koja resetuje vrijednosti rezultata, postavlja ih na nulu
     */
    private void resetValues() {
        totalRevenue = 0;
        totalDiscount = 0;
        totalPromo = 0;
        totalNarrowIncome = 0;
        totalWideIncome = 0;
        maintenanceCost = 0;
        repairCost = 0;
        companyCosts = 0;
        totalTax = 0;
    }

    /**
     * Metoda koja nam racuna vrijednosti jednog iznajmljivanja
     * U principu, ona nam sluzi umjesto ucitavanja podataka direktno s racuna,
     * posto se ne nalaze svi potrebni podaci za rezultate poslovanja na racunu
     * @param rentals - lista iznajmljivanja
     * @throws IOException ako dodje do greske prilikom racunanja ili prosljedjivanja podataka
     */
    public void calculateRentalValues(List<Rental> rentals) throws IOException {
        for(Rental rental : rentals){
            rental.calculateTotalPrice();
            rental.isNarrow();
            rental.isFaulty();
        }
    }

    /**
     * Metoda za racunanje dnevnih izvjestaja rezultata poslovanja
     * Grupisemo iznajmljivanja prema vremenu iznajmljivanja, zatim pozivamo metodu calculateSummaryValues
     * i onda dodajemo samo one vrijednosti koje su nam potrebne za dnevni izvjestaj
     * @param rentals - lista iznajmljivanja
     * @throws IOException - ako dodje do greske prilikom racunanja, dodavanja u tabelu...
     */
    public void calculateDailyValues(List<Rental> rentals) throws IOException {
        for (Rental r : rentals)
            r.getRental().setRentTime(r.getRental().getStartTime().toLocalDate().atStartOfDay());

        Map<LocalDateTime, List<Rental>> RentalsByDate = rentals.stream().collect(Collectors.groupingBy(r -> r.getRental().getStartTime().toLocalDate().atStartOfDay())); //Lista samo onih iznajmljivanja koja su se desila tog dana

        for (Map.Entry<LocalDateTime, List<Rental>> entry : RentalsByDate.entrySet()) {
            LocalDate date = LocalDate.from(entry.getKey());
            List<Rental> dailyList = entry.getValue();

            resetValues();
            calculateSummaryValues(dailyList);

            controller.addToTableDaily(date, totalRevenue, totalDiscount, totalPromo, totalNarrowIncome, totalWideIncome, maintenanceCost, repairCost);
        }
    }

    /**
     * Metoda koja racuna sumarni izvjestaj rezultata poslovanja
     * bavi se samo racunanjem i postavljanjem vrijednosti, one se dodaju u tabelu kasnije
     * @param allRentals - lista svih iznajmljivanja
     */
    public void calculateSummaryValues(List<Rental> allRentals) {
        totalRevenue = allRentals.stream().mapToDouble(Rental::getTotalPrice).sum();
        totalWideIncome = allRentals.stream().filter(r -> r.isNarrow()).mapToDouble(Rental::getTotalPrice).sum();
        totalNarrowIncome = allRentals.stream().filter(r -> !r.isNarrow()).mapToDouble(Rental::getTotalPrice).sum();
        totalDiscount = allRentals.stream().filter(r -> r.getRentCount() % 10 == 0).mapToDouble(r -> r.getBasePrice() - r.getBasePrice() * 0.1).sum();
        totalPromo = allRentals.stream().filter(r -> r.getRental().getPromotion().equalsIgnoreCase("yes")).mapToDouble(r -> {
            if (r.getRentCount() % 10 == 0)
                return r.getBasePrice() * 0.1 - r.getBasePrice() * 0.1 * 0.15;
            else return r.getBasePrice() - r.getBasePrice() * 0.15;
        }).sum();
        repairCost = allRentals.stream().filter(r -> r.getRental().isFaulty()).mapToDouble(r -> {
            if (r.getVehicle() instanceof ECar)
                return 0.07 * r.getRental().getVehicle().getPrice();
            else if (r.getVehicle() instanceof EBike)
                return 0.04 * r.getRental().getVehicle().getPrice();
            else if (r.getVehicle() instanceof EScooter)
                return 0.02 * r.getRental().getVehicle().getPrice();
            return 0;
        }).sum();

        maintenanceCost = totalRevenue * 0.2; // 5
        companyCosts = maintenanceCost; // 7
        totalTax = (totalRevenue - maintenanceCost - repairCost - companyCosts) * 0.1; // 8
    }

    /**
     * Metoda koja koristi calculateSummaryValues da izracuna sumarni izvjestaj
     * i doda rezultate u tabelu. Za nju je napravljen konstruktor kome se prosljedjuje ResultsController instanca
     * da bismo mogli da generisemo rezultat i dodamo ga u tabelu
     * @param rentals - sva iznajmljivanja
     * @throws IOException ako dodje do greske prilikom racunanja, inicijalizacije ili dodavanja u tabelu
     */
    public void calculateSummaryValuesAndAddToTable(List<Rental> rentals) throws IOException {
        // Resetuj postojeće vrijednosti
        resetValues();

        // Izračunaj sumarne vrijednosti
        calculateSummaryValues(rentals);

        // Kreiraj objekt rezultata na temelju izračunatih vrijednosti
        Results results = new Results(resultDate, totalRevenue, totalDiscount, totalPromo, totalNarrowIncome, totalWideIncome, maintenanceCost, repairCost);
        controller.addToTableSummary(results);  // Koristi predefinisani ResultsController za dodavanje u tabelu
    }
}