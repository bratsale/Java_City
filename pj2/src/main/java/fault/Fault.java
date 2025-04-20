package fault;

import java.time.LocalDate;
import model.VehicleStatus;

/**
 * Klasa koja sadrzi opis kvarova i informacije vezane za kvarove automobila
 */
public abstract class Fault {
    private String description;   // Opis kvara
    private LocalDate faultDate;  // Datum kada se kvar desio
    private VehicleStatus status; // Status vozila nakon kvara (može biti u obliku enum klase)

    /**
     * Konstruktor klase Fault
     * @param description - opis kvara
     * @param faultDate - datum kvara
     * @param status - status vozila
     */
    public Fault(String description, LocalDate faultDate, VehicleStatus status) {
        this.description = description;
        this.faultDate = faultDate;
        this.status = status;
    }

    /**
     * Metoda za dobijanje opisa kvara
     * @return vraca opis kvara
     */
    public String getDescription() {
    return description;
    }

    /**
     * Metoda za dobijanje datuma kvara
     * @return vraca datum kvara
     */
    public LocalDate getFaultDate() {
    return faultDate;
    }

    /**
     * Metoda za dobijanje statusa vozila
     * @return vraca status vozila
     */
    public VehicleStatus getStatus() {
        return status;
    }


}

