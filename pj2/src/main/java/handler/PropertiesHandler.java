package handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Klasa koja se bavi ucitavanjem properties
 */
public class PropertiesHandler {

    final private Properties properties;

    /**
     * Kontsruktor za objekat koji ce cuvati podatke iz fajla properties
     * @param filePath - relativna putanja do properties fajla
     * @throws IOException ako se fajl neuspjesno ucita
     */
    public PropertiesHandler(String filePath) throws IOException {
        properties = new Properties();
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        }
    }

    /**
     * Metoda koja dobija osnovnu cijenu automobila
     * @return vraca osnovnu cijenu
     */
    public double getCarUnitPrice() {
        return Double.parseDouble(properties.getProperty("CAR_UNIT_PRICE"));
    }

    /**
     * Metoda koja dobija osnovnu cijenu bicikla
     * @return vraca osnovnu cijenu
     */
    public double getBikeUnitPrice() {
        return Double.parseDouble(properties.getProperty("BIKE_UNIT_PRICE"));
    }

    /**
     * Metoda koja dobija osnovnu cijenu trotineta
     * @return vraca osnovnu cijenu
     */
    public double getScooterUnitPrice() {
        return Double.parseDouble(properties.getProperty("SCOOTER_UNIT_PRICE"));
    }

    /**
     * Metoda koja dobija koeficijent cijene ako se vozilo kretalo u uzem dijelu grada
     * @return vraca taj koeficijent
     */
    public double getDistanceNarrow() {
        return Double.parseDouble(properties.getProperty("DISTANCE_NARROW"));
    }

    /**
     * Metoda koja dobija koeficijent cijene ako se vozilo kretalo u sirem dijelu grada
     * @return vraca taj koeficijent
     */
    public double getDistanceWide() {
        return Double.parseDouble(properties.getProperty("DISTANCE_WIDE"));
    }

    /**
     * Metoda koja vraca koeficijent popusta
     * @return vraca taj koeficijent
     */
    public double getDiscount() {
        return Double.parseDouble(properties.getProperty("DISCOUNT"));
    }

    /**
     * Metoda koja vraca koeficijent popusta promocije
     * @return vraca taj koeficijent
     */
    public double getDiscountProm() {
        String value = properties.getProperty("DISCOUNT_PROM").split("#")[0].trim(); // Uklanjanje komentara
        return Double.parseDouble(value);
    }

}
