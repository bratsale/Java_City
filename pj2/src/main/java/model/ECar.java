package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Klasa ECar nasljedjuje klasu Vehicle, predstavlja jedan od tri tipa vozila
 */
public class ECar extends Vehicle {
    private String car_description;
    private LocalDate car_date;

    /**
     * Konstruktor klase ECar
     * @param id - identifikator automobila
     * @param manufacturer - proizvodjac
     * @param model - model automobila
     * @param price - cijena automobila
     * @param car_description - opis automobila
     * @param carDate - datum proizvodnje automobila
     */
    public ECar(String id, String manufacturer, String model, double price,
               String car_description, String carDate) {
        super(id, manufacturer, model, price,  "automobil");
        this.car_description = car_description;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy.");
        this.car_date = LocalDate.parse(carDate, formatter);
    }

    /**
     * Metoda za dobijanje opisa automobila
     * @return vraca opis
     */
    public String getCarDescription() {
        return car_description;
    }

    /**
     * Metoda za dobijanje datuma proizvodnje automobila
     * @return datum proizvodnje
     */
    public LocalDate getCarDdate() {
        return car_date;
    }

    /**
     * Override toString metoda koja formatira podake o automobilu u String format
     * @return vraca konstruktor iz roditeljske klase Vehicle zajedno sa dva nova atributa koja smo dodijelili klasi ECar
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy.");
        return super.toString() + String.format(
                ", Description: %s, Purchase Date: %s",
                car_description,
                car_date.format(formatter)
        );
    }
}
