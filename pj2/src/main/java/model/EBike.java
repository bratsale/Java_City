package model;

/**
 * Klasa EBike nasljedjuje klasu Vehicle, predstavlja jedan od tri tipa vozila
 */
public class EBike extends Vehicle {
    private double rangePerCharge;

    /**
     * Kontstruktor klase EBike
     * @param id - identifikator bicikla
     * @param manufacturer - proizvodjac bicikla
     * @param model - model bicikla
     * @param price - cijena bicikla
     * @param rangePerCharge - domet bicikla po punjenju
     */
    public EBike(String id, String manufacturer, String model, double price, double rangePerCharge) {
        super(id, manufacturer, model, price, "bicikl");
        this.rangePerCharge = rangePerCharge;
    }

    /**
     * Metoda koja vraca domet bicikla
     * @return vraca domet po punjenju bicikla
     */
    public double getRangePerCharge() {
        return rangePerCharge;
    }

    /**
     * Override toString metoda koja formatira podatke o biciklu u String format
     * @return vraca konstruktor roditeljske klase zajedno za jednim dodatnim atributom iz klase EBike
     */
    @Override
    public String toString() {
        return super.toString() + String.format(", Range per charge: %.2f km", rangePerCharge);
    }
}
