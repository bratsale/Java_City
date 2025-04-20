package rent;

import java.util.*;

import handler.*;
import gui.*;
import model.*;
import java.awt.Point;

/**
 * Klasa RentalSimulation se bavi jednim singularnim iznajmljivanjem, i svime sto ide uz njega
 * Ona implementira interfejs Runnable jer cemo koristiti konkurentno programiranje odnosno niti
 */
public class RentalSimulation implements Runnable {
    private Rental rental;
    private MainController mainController; // Referenca na MainController

    private static Map<String, Vehicle> faultyVehicles = new HashMap<>();

    /**
     * Ovo je konstruktor klase
     * @param rental predstavlja jedno iznajmljivanje
     * @param mainController predstavlja instancu glavnog kontrolera za graficki interfejs
     *                       jer cemo ga koristiti pri azuriranju pozicije vozila na mapi
     */
    public RentalSimulation(Rental rental, MainController mainController) {
        this.rental = rental;
        this.mainController = mainController;
    }

    /**
     * Ova override run metoda simulira kretanje vozila od pocetne do krajnje lokacije,
     * azurira GUI, simulira trosenje baterije i generise racun. Koristimo je za pokretanje
     * u posebnoj niti. Obuhvata parsiranje tacaka lokacije, racunanje najkraceg puta,
     * vremena po pomjeranju vozila, provjera kvarova itd.
     */
    @Override
    public void run() {
        Vehicle vehicle = rental.getVehicle();

        // Parsiranje početne i krajnje lokacije
        String[] startCoords = rental.getStartLocation().split(",");
        int startX = Integer.parseInt(startCoords[0]);
        int startY = Integer.parseInt(startCoords[1]);

        String[] endCoords = rental.getEndLocation().split(",");
        int endX = Integer.parseInt(endCoords[0]);
        int endY = Integer.parseInt(endCoords[1]);

        // Generisanje najkraće putanje koristeći calculateShortestPath iz klase Rental
        int shortestPath = rental.calculateShortestPath();
        List<Point> path = calculatePathSteps(new Point(startX, startY), new Point(endX, endY), shortestPath);

        // Vreme po koraku
        double timePerField = rental.calculateTimePerField();

        // Iteracija kroz korake
        for (Point step : path) {
            // Ažuriranje pozicije vozila na GUI-ju
            mainController.updateVehiclePosition(vehicle.getId(), step.x, step.y, endX, endY, false); // false označava da simulacija nije završena
            vehicle.dischargeBattery();

            try {
                // Pauza između koraka
                Thread.sleep((long) (timePerField * 1000));
                if(vehicle.getBatteryLevel() == 0){
                    System.out.println("Battery is empty, waiting for vehicle to recharge...");
                    Thread.sleep(1000);
                    vehicle.chargeBattery();
                    System.out.println("Battery is fully charged.");
                }// Konvertovanje sekundi u milisekunde
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Na kraju, kad vozilo stigne na krajnju lokaciju, uklanjamo marker sa mape
        mainController.updateVehiclePosition(vehicle.getId(), endX, endY, endX, endY, true); // true oznacava da je simulacija zavrsena
        if(rental.isFaulty()){
            vehicle.setFailReason();
            vehicle.setFailTime(rental.getEndTime());
            System.out.println("Fault occured for vehicle " + vehicle.getId() + ". Fault description: " + vehicle.getFailReason());
            // Dodavanje vozila u mapu s kvarovima
            faultyVehicles.put(vehicle.getId(), vehicle);
        }
        // Generisanje racuna na kraju
        generateReceiptAndPrint();
        System.out.println("****************************************");
    }

    /**
     * Metoda calculatePathSteps nam racuna i daje putanju kojom ce se vozilo kretati tokom simulacije
     * @param start - pocetna tacka
     * @param end - krajnja tacka
     * @param steps - broj koraka odnosno polja koje ce vozilo preci tokom simulacije
     * @return
     */
    private List<Point> calculatePathSteps(Point start, Point end, int steps) {
        List<Point> path = new ArrayList<>();

        // Razlika u koordinatama
        int dx = Math.abs(end.x - start.x);
        int dy = Math.abs(end.y - start.y);

        // Računanje pravca kretanja
        int stepX = (end.x > start.x) ? 1 : (end.x < start.x) ? -1 : 0;
        int stepY = (end.y > start.y) ? 1 : (end.y < start.y) ? -1 : 0;

        int x = start.x;
        int y = start.y;

        // Uzimamo manji broj izmedjuu dx i dy da bi putanja bila u pravilnom pravcu
        int maxSteps = Math.max(dx, dy); // Maksimalan broj koraka je najveca razlika u koordinatama

        // Iteracija kroz korake puta
        for (int i = 0; i < steps; i++) {
            if (dx > 0) {
                x += stepX; // Pomeri X
                dx--;
            } else if (dy > 0) {
                y += stepY; // Pomeri Y
                dy--;
            }

            path.add(new Point(x, y));
        }

        return path;
    }


    /**
     * Jednostavna metoda za generisanje i ispisivanje racuna
     * Koristimo ReceiptHandler za metodu generateReceipt()
     */
    private void generateReceiptAndPrint() {
        // Pretpostavljamo da se korisnički dokumenti učitavaju i račun generiše ovde
        Properties userDocuments = UserDocumentHandler.loadUserDocuments();
        String idNumber = userDocuments.getProperty(rental.getUserId() + ".id");
        String driverLicense = userDocuments.getProperty(rental.getUserId() + ".license");

        // Generiši račun
        ReceiptHandler.generateReceipt(rental, idNumber, driverLicense);

        // Ispisivanje računa ili dodatna logika
        System.out.println("Rental finished for vehicle " + rental.getVehicle().getId() + "\n");
    }

    /**
     * Metoda koja nam vraca listu odnosno mapu pokvarenih vozila
     * @return vraca tu mapu
     */
    public static Map<String, Vehicle> getFaultyVehicles() {
        return faultyVehicles;
    }

}
