package handler;

import  java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Klasa koja se bavi ucitavanjem i obradom korisnickih podataka
 */
public class UserDocumentHandler {
    private static final String FILE_PATH = "src/main/java/resources/user_document.properties";

    /**
     * Metoda za generisanje nasumicnih korisnickih dokumenata, koji se kasnije ispisuju u namijenjen fajl
     * @throws IOException u slucaju neuspijevanja da se dokumenti generisu i sacuvaju u fajl
     */
    public static void generateUserDocuments() throws IOException {
        Properties properties = new Properties();

        // Generišemo nasumične brojeve dokumenata za korisnike K1-K5
        for (int i = 1; i <= 5; i++) {
            String userId = "K" + i;
            String idNumber = "ID-" + (int) (Math.random() * 1_000_000);
            String driverLicense = "DL-" + (int) (Math.random() * 1_000_000);
            properties.setProperty(userId + ".id", idNumber);
            properties.setProperty(userId + ".license", driverLicense);
        }

        try (FileOutputStream out = new FileOutputStream(FILE_PATH)) {
            properties.store(out, "User Documents");
            System.out.println("User documents generated and saved to " + FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda za ucitavanje korisnickih dokumenata iz fajla
     * @return vraca instancu klase Properties (java.util) sa ucitanim dokumentima
     */
    public static Properties loadUserDocuments() {
        Properties properties = new Properties();
        try (var in = new FileInputStream(FILE_PATH)) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Metoda koja dobija podatke o dokumentima
     * @param userId - identifikator korisnika
     * @return u zavisnosti od if-else petlje, vraca ili dokument, ili "N/A"
     */
    public static String getDocument(String userId) {
        Properties properties = loadUserDocuments();

        // Prvo tražimo vozačku dozvolu, ako postoji
        String document = properties.getProperty(userId + ".license");
        if (document != null) {
            return document; // Ako je vozačka dozvola prisutna, vraćamo je
        }

        // Ako vozačka dozvola nije pronađena, tražimo pasoš
        document = properties.getProperty(userId + ".passport");
        if (document != null) {
            return document; // Ako je pasoš prisutan, vraćamo ga
        }

        // Ako nijedan dokument nije pronađen, vraćamo "N/A"
        return "N/A";
    }
}
