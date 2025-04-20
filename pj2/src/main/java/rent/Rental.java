package rent;

import model.*;
import handler.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

/**
 * Klasa Rental nam daje sve podatke o jednom iznajmljivanju, kao i metode koje su se koristile pri racunanju
 */
public class Rental {
    private Vehicle vehicle;
    private String userId;
    private String vehicleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double simulationDuration; // Trajanje u sekundama za simulaciju
    private String startLocation;
    private String endLocation;
    private double totalPrice;
    private double basePrice;
    private boolean hasFault;
    public boolean hasPromotion;
    private double narrowFactor;
    private double wideFactor;
    private int rentCount;
    private static int counter = 0;
    private String driversLicense;

    /**
     * Konstruktor klase Rental
     * @param date - datum iznajmljivanje
     * @param userId - identifikator korisnika
     * @param vehicle - vozilo koje je iznajmljeno
     * @param startLocation - pocetna lokacija
     * @param endLocation - krajnja lokacija
     * @param simulationDuration - trajanje simulacije
     * @param fault - kvar
     * @param promotion - promocija
     */
    public Rental(String date, String userId, Vehicle vehicle, String startLocation,
                  String endLocation, double simulationDuration, String fault, String promotion) {
        // Parsiranje datuma iz stringa u LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");
        this.startTime = LocalDateTime.parse(date, formatter);

        this.userId = userId;
        this.vehicle = vehicle;
        this.vehicleId = vehicle.getId();
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.simulationDuration = simulationDuration;

        this.hasFault = fault.equalsIgnoreCase("da");
        this.hasPromotion = promotion.equalsIgnoreCase("da");
        UserDocumentHandler userDocumentHandler = new UserDocumentHandler();
        this.driversLicense = userDocumentHandler.getDocument(userId);
        PropertiesHandler propertiesHandler = null;
        try {
            propertiesHandler = new PropertiesHandler("src/main/java/resources/config.properties");
        } catch (IOException e) {
            System.out.println("Greška prilikom učitavanja konfiguracije: " + e.getMessage());
        }

        if (propertiesHandler != null) {
            this.narrowFactor = propertiesHandler.getDistanceNarrow();
            this.wideFactor = propertiesHandler.getDistanceWide();
        }

        // Izračunavanje završnog vremena na osnovu simulacije
        calculateEndTime();
        try {
            calculateTotalPrice();
        } catch (IOException e) {
            System.out.println("Došlo je do greške prilikom izračunavanja cene: " + e.getMessage());
        }

        this.rentCount = ++counter;
    }

    /**
     * Metoda koja racuna da li se vozilo na odredjenoj lokaciji nalazi u uzem dijelu grada
     * @param location - lokacija vozila
     * @return vraca boolean potvrdu da li se vozilo nalazi u uzem dijelu ili ne
     */
    public boolean isNarrowArea(String location){
        String[] coordinates = location.split(",");
        int x = Integer.parseInt(coordinates[0]);
        int y = Integer.parseInt(coordinates[1]);

        return x >= 5 && x<15 && y >= 5 && y<15;
    }

    /**
     * Metoda koja koristi boolean isNarrowArea
     * Provjerava da li ce se vozilo tokom simulacije naci u uzem dijelu grada ili ne
     * @return vraca informaciju o tome da li ce se vozilo tokom simulacije naci u uzem dijelu grada ili ne
     */
    public boolean isNarrow() {
        // Provjera za startLocation i endLocation
        return isNarrowArea(startLocation) && isNarrowArea(endLocation);
    }

    /**
     * Metoda koja racuna u koje vrijeme ce se tacno zavrsiti iznajmljivanje
     */
    private void calculateEndTime() {
        // 1 sekunda u simulaciji = 30 minuta realnog vremena
        double realDurationInMinutes = simulationDuration * 30;
        this.endTime = startTime.plusMinutes((long) realDurationInMinutes);
    }

    /**
     * Metoda koja racuna ukupnu cijenu iznajmljivanja
     * Metoda je prilagodjena da racuna "realnu cijenu", obzirom da je trajanje simulacije u sekundama
     * @throws IOException ako dodje do greske prilikom racunanje cijene (u konstruktoru)
     */
    public void calculateTotalPrice() throws IOException {
        // Ako postoji kvar, cijena je 0
        if (hasFault) {
            totalPrice = 0;
            basePrice = 0;
            return; // Izaći iz metode jer nema potrebe za daljnjim izračunom
        }

        double unitPrice = 0;
        PropertiesHandler propertiesHandler = new PropertiesHandler("src/main/java/resources/config.properties");
        if (vehicle instanceof ECar) {
            unitPrice = propertiesHandler.getCarUnitPrice();
        } else if (vehicle instanceof EBike) {
            unitPrice = propertiesHandler.getBikeUnitPrice();
        } else if (vehicle instanceof EScooter) {
            unitPrice = propertiesHandler.getScooterUnitPrice();
        }

        // Dodavanje promocije
        if (hasPromotion) {
            unitPrice *= propertiesHandler.getDiscountProm(); //
        }

        // Provjeriti da li je vozilo u širem dijelu grada
        boolean wideArea = !isNarrowArea(startLocation) || !isNarrowArea(endLocation);
        double distanceFactor = wideArea ? wideFactor : narrowFactor;

        // Cena na osnovu simulacije (realDurationInMinutes se konvertuje u sate)
        double realDurationInHours = (simulationDuration * 30 * 60);
        totalPrice = unitPrice * realDurationInHours * distanceFactor;
        basePrice = unitPrice * realDurationInHours;
    }

    /**
     * Metoda koja dobija ukupnu cijenu iznajmljivanja
     * @return vraca ukupnu cijenu
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Metoda koja dobija identifikator korisnika
     * @return vraca identifikator
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Metoda koja vraca identifikator vozila
     * @return vraca identifikator
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Metoda koja dobija vrijeme pocetka simulacije
     * @return vraca vrijeme pocetka
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Metoda koja postavlja vrijeme trajanja simulacije
     * @param rentTime - trajanje simulacije
     */
    public void setRentTime(LocalDateTime rentTime) {
        this.startTime = rentTime;
    }

    /**
     * Metoda koja dobija vrijeme zavrsetka simulacije
     * @return vraca vrijeme zavrsetka
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Metoda koja dobija povetnu lokaciju simulacije
     * @return vraca pocetnu lokaciju
     */
    public String getStartLocation() {
        return startLocation;
    }

    /**
     * Metoda koja dobija krajnju lokaciju simulacije
     * @return vraca krajnju lokaciju
     */
    public String getEndLocation() {
        return endLocation;
    }

    /**
     * Metoda koja dobija vrijeme trajanja simulacije
     * @return vraca vrijeme trajanja
     */
    public double getSimulationDuration() {
        return simulationDuration;
    }

    /**
     * Metoda koja dobija broj iznajmljivanja (zbog promocije)
     * @return vraca broj iznajmljivanja
     */
    public int getRentCount() {
        return rentCount;
    }

    /**
     * Metoda koja dobija osnovnu cijenu iznajmljivanja (bez promocija i popusta)
     * @return vraca osnovnu cijenu
     */
    public double getBasePrice() {
        return basePrice;
    }

    /**
     * Metoda koja provjerava da li je vozilo pokvareno ili ne
     * @return vraca boolean o pokvarenom vozilu
     */
    public boolean isFaulty(){
        if(hasFault)
            return true;
        else
            return false;
    }

    /**
     * Metoda koja dobija informaciju o pokvarenom vozilu
     * @return vraca "yes" ili "no" (na racunu) u odnosu na to da li je vozilo pokvareno ili ne
     */
    public String getFault(){
        if(hasFault){
            return "yes";
        }else return "no";
    }

    /**
     * Metoda koja provjerava da li iznajmljivanje ima primjenjenu promociju ili ne
     * @return vraca "yes" ili "no" u zavisnosti da li ima pravo na promociju
     */
    public String getPromotion(){
        if(hasPromotion){
            return "yes";
        }else return "no";
    }

    /**
     * Metoda koja računa Manhattan distance (najkraći put) između start i end lokacije.
     * Lokacije su u formatu "x,y" (npr. "5,10").
     * @return broj polja koje vozilo treba da pređe
     */
    public int calculateShortestPath() {
        // Parsiranje startLocation i endLocation u koordinate (x, y)
        String[] startCoords = startLocation.split(",");
        int startX = Integer.parseInt(startCoords[0]);
        int startY = Integer.parseInt(startCoords[1]);

        String[] endCoords = endLocation.split(",");
        int endX = Integer.parseInt(endCoords[0]);
        int endY = Integer.parseInt(endCoords[1]);

        // Računanje Manhattan distance
        return Math.abs(startX - endX) + Math.abs(startY - endY);
    }

    /**
     * Metoda koja računa vreme zadržavanja na svakom polju vozila.
     * Vreme zadržavanja se računa na osnovu ukupne simulacije i broja polja.
     * @return vreme zadržavanja na svakom polju u sekundama
     */
    public double calculateTimePerField() {
        int totalFields = calculateShortestPath();  // Broj polja koja vozilo mora da pređe
        return (totalFields > 0) ? simulationDuration / totalFields : 0;
    }

    /**
     * Metoda koja dobija podatke o iznajmljivanju
     * @return vraca te podatke
     */
    public Rental getRental() {
        return this;
    }

    /**
     * Metoda koja dobija podatke o racunu i podacima sa njega
     * @return vraca novi objekat klase Receipt, da bi se preko njega mogli ucitati podaci s racuna
     */
    public Receipt getReceipt() {
        String userId = this.userId;
        String idNumber = vehicle.getId(); // Ako vozilo ima svoj ID, koristimo ga
        String driverLicense = UserDocumentHandler.getDocument(userId);
        String vehicleId = this.vehicleId;
        String startLocation = this.startLocation;
        String endLocation = this.endLocation;
        LocalDateTime startTime = this.startTime;
        LocalDateTime endTime = this.endTime;
        double totalPrice = this.totalPrice;
        String promotion = this.getPromotion(); // Da li je promocija primenjena
        String fault = this.getFault(); // Da li je došlo do kvara

        // Kreiranje i vraćanje objekta Receipt
        return new Receipt(userId, idNumber, driverLicense, vehicleId, startLocation, endLocation, startTime, endTime, totalPrice, promotion, fault);
    }

}
