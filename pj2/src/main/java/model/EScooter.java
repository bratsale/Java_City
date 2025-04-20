package model;

/**
 * Klasa EScooter nasljedjuje klasu Vehicle, predstavlja jedan od tri tipa vozila
 */
public class EScooter extends Vehicle {
    private double maxSpeed;

    /**
     * Kontsruktor klase EScooter
     * @param id - identifikator trotineta
     * @param manufacturer - proizvodjac trotineta
     * @param model - model trotineta
     * @param price - cijena trotineta
     * @param maxSpeed - maksimalna brzina trotineta
     */
    public EScooter(String id, String manufacturer, String model, double price, double maxSpeed) {
        super(id, manufacturer, model, price, "trotinet");
        this.maxSpeed = maxSpeed;
    }

    /**
     * Metoda za dobijanje maksimalne brzine trotineta
     * @return vraca maksimalnu brzinu
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Override toString metoda koja formatira atribute u String format
     * @return vraca konstruktor roditeljske klase i jos jedan dodatni EScooter atribut
     */
    @Override
    public String toString() {
        return super.toString() + String.format(", Max speed: %.2f km/h", maxSpeed);
    }
}
