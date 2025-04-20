package model;

import javafx.beans.property.SimpleDoubleProperty;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * klasa Vehicle je osnovna apstrakna klasa sa atributima,
 * koju ce kasnije naslijediti klase ECar, EBike i EScooter
 */
public abstract class Vehicle {
    private String id;
    private String manufacturer;
    private String model;
    private double price;          // Cena nabavke
    private double batteryLevel;
    private VehicleStatus status;  // Status vozila (dostupno, u održavanju)
    private String type;           // Tip vozila (npr. Car, EBike, EScooter)
    private String failReason;
    private LocalDateTime failTime;

    /**
     * Konstruktor klase Vehicle (atributi koji su zajednicki za sve tipove vozila)
     * @param id - identifikator vozila
     * @param manufacturer - proizvodjaca
     * @param model - model vozila
     * @param price - cijena vozila
     * @param type - tip vozila
     */
    protected Vehicle(String id, String manufacturer, String model, double price, String type) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.price = price;
        batteryLevel = 100.0;
        this.status = VehicleStatus.AVAILABLE;  // Podrazumevani status
        this.type = type;// Dodajemo tip vozila
        failReason = null;
        failTime = null;
    }

    /**
     * Metoda za simulaciju potrosnje baterije (svakim pomjeranjem vozila smanjivace se za 1%)
     */
    public void dischargeBattery(){
        batteryLevel--;
    }

    /**
     * Metoda za dobijanje identifikatora vozila
     * @return vraca identifikator
     */
    public String getId() {
        return id;
    }

    /**
     * Metoda za dobijanje nivoa baterije
     * @return vraca nivo baterije
     */
    public double getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * metoda za postavljanje novog nivoa baterije
     * @param newBatteryLevel - novi nivo baterije
     */
    public void setBatteryLevel(double newBatteryLevel) {
        this.batteryLevel = newBatteryLevel;
    }

    /**
     * Metoda za dobjanje statusa vozila
     * @return vraca status
     */
    public VehicleStatus getStatus() {
        return status;
    }

    /**
     * Metoda za postavljanje statusa vozila
     * @param status - status vozila
     */
    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    /**
     * Metoda koja vraca tip vozila
     * @return vraca tip
     */
    public String getType() {
        return type;
    }

    /**
     * Metoda koja postavlja tip vozila
     * @param type - tip vozila
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Metoda koja vraca proizvodjaca vozila
     * @return vraca proizvodjaca
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Metoda koja vraca model vozila
     * @return vraca model
     */
    public String getModel() {
        return model;
    }

    /**
     * Metoda koja vraca cijenu vozila
     * @return vraca cijenu
     */
    public double getPrice() {
        return price;
    }

    /**
     * Metoda koja simulira punjenje baterije vozila
     */
    public void chargeBattery() {
            System.out.println("Amount cannot be negative.");

        batteryLevel = Constants.MAX_BATTERY_LEVEL;
        System.out.println("Battery charged. Current battery level: " + batteryLevel + "%");
    }

    /**
     * Metoda koja dobija razlog/opis kvara vozila
     * @return vraca opis
     */
    public String getFailReason(){
        return failReason;
    }

    /**
     * Metoda koja dobija vrijeme kvara vozila
     * @return vraca vrijeme kvara
     */
    public LocalDateTime getFailTime(){
        return failTime;
    }

    /**
     * Metoda koja postavlja razlog kvara vozila,
     * implementirana tako da nasumicno bira izmedju dve vrste kvara
     */
    public void setFailReason() {
        // Kreiramo instancu Random klase
        Random random = new Random();
        // Nasumično biramo između dva razloga
        if (random.nextBoolean()) {
            this.failReason = "tire failure";
        } else {
            this.failReason = "engine failure";
        }
    }

    /**
     * Metoda koja postavlja vrijeme kvara
     * @param failTime - vrijeme kvara
     */
    public void setFailTime(LocalDateTime failTime) {
        this.failTime = failTime;
    }

    /**
     * Override toString metoda (ako je potrebno formatirati podatke o vozilu u String)
     * @return
     */
    @Override
    public String toString() {
        return String.format(
                "ID: %s, Type: %s, Manufacturer: %s, Model: %s, Price: %.2f, Battery Level: %.2f%%, Status: %s",
                id, type, manufacturer, model, price, batteryLevel, status
        );
    }

}
