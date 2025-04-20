package rent;

import java.time.LocalDateTime;

/**
 * Klasa Receipt predstavlja racun koji se kreira za svako iznajmljivanje
 */
public class Receipt {
    private String userId;
    private String idNumber;
    private String driverLicense;
    private String vehicleId;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalPrice;
    private String promotion;
    private String fault;

    /**
     * Konstruktor klase Receipt
     * @param userId - identifikator korisnika
     * @param idNumber - broj licne karte
     * @param driverLicense - broj vozacke dozvole
     * @param vehicleId - identifikator vozila
     * @param startLocation - pocetna lokacija
     * @param endLocation - krajnja lokacija
     * @param startTime - vrijeme pocetka simulacije
     * @param endTime - vrijeme zavrsetka simulacije
     * @param totalPrice - ukupna cijena iznajmljivanja
     * @param promotion - parametar koji nam govori da li je iznajmljivanje sadrzalo promociju
     * @param fault - parametar koji nam govori da li se vozilo pokvarilo tokom iznajmljivanja
     */
    public Receipt(String userId, String idNumber, String driverLicense, String vehicleId,
                   String startLocation, String endLocation, LocalDateTime startTime,
                   LocalDateTime endTime, double totalPrice, String promotion, String fault) {
        this.userId = userId;
        this.idNumber = idNumber;
        this.driverLicense = driverLicense;
        this.vehicleId = vehicleId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.promotion = promotion;
        this.fault = fault;
    }

    /**
     * Metoda za dobijanje identifikatora korisnika
     * @return vraca identifikator
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Metoda koja dobija broj licne karte
     * @return vraca broj licne karte
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * Metoda koja dobija broj vozacke dozvole
     * @return vraca broj vozacke dozvole
     */
    public String getDriverLicense() {
        return driverLicense;
    }

    /**
     * Metoda koja dobija identifikator vozila
     * @return vraca identifikator
     */
    public String getVehicleId() {
        return vehicleId;
    }

    /**
     * Metoda koja dobija pocetnu lokaciju
     * @return vraca pocetnu lokaciju
     */
    public String getStartLocation() {
        return startLocation;
    }

    /**
     * Metoda koja dobija krajnju lokaciju
     * @return vraca krajnju lokaciju
     */
    public String getEndLocation() {
        return endLocation;
    }

    /**
     * Metoda koja vraca vrijeme pocetka iznajmljivanja
     * @return vraca vrijeme pocetka
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Metoda koja dobija vrijeme kraja iznajmljivanje
     * @return vraca krajnje vrijeme
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Metoda koja dobija ukupnu cijenu iznajmljivanja
     * @return vraca ukupnu cijanu
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Metoda koja dobija informaciju o promociji
     * @return vraca promociju
     */
    public String getPromotion() {
        return promotion;
    }

    /**
     * Metoda koja vraca informaciju o kvaru vozila
     * @return vraca kvar
     */
    public String getFault() {
        return fault;
    }
}
