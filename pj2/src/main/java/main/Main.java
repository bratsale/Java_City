package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main klasa je pocetak programa
 * Ovdje se poziva osnovni GUI iz kojeg dalje prolazimo kroz program i simulaciju
 */
public class Main extends Application {

    /**
     * Metoda start nam pokrece osnovni prozor GUI-ja
     * @param stage predstavlja primarni "prozor" ove aplikacije, koji moze po potrebi da kreira druge
     *              prozore ako je neophodno, ali oni nece biti primarni
     * @throws IOException ako ne otvori "Simulation.fxml"
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Pokretanje GUI-ja
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Simulation.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 850);
        stage.setTitle("ePJ2");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * main metoda koja pokrece samu aplikaciju
     * @param args su dodatni argumenti koji se mogu proslijediti metodi
     */
    public static void main(String[] args) {
        launch();
    }
}