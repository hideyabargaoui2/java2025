package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file from the resources folder
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherhotel.fxml"));
            Parent root = loader.load();

            // Set up the scene
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gestion des Hôtels");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);


    }


    /*@Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherreshotel.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Afficher Une Reservation Hotel");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }*/


}
