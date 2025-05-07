package controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Trajet;

import java.time.format.DateTimeFormatter;

public class QRCodeViewController {

    /**
     * Affiche une fenêtre avec le QR code d'un trajet
     * @param trajet Le trajet dont le QR code doit être affiché
     */
    public static void showQRCode(Trajet trajet) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Code QR pour le trajet vers " + trajet.getDestination());

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new javafx.geometry.Insets(20));
        vbox.setStyle("-fx-background-color: white;");

        // Titre
        Label title = new Label("Code QR pour le trajet");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Détails du trajet
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Label details = new Label(
                "Date: " + trajet.getDate().format(dateFormatter) + "\n" +
                        "Heure: " + trajet.getHeure() + "h\n" +
                        "Destination: " + trajet.getDestination() + "\n" +
                        "Transport: " + trajet.getTransport() + "\n" +
                        "Durée: " + trajet.getDuree() + " heure(s)"
        );
        details.setStyle("-fx-font-size: 14px;");

        // QR Code
        ImageView qrCodeView = new ImageView(trajet.getQRCodeImage());
        qrCodeView.setFitWidth(300);
        qrCodeView.setFitHeight(300);
        qrCodeView.setPreserveRatio(true);

        // Ajouter un effet d'ombre
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        qrCodeView.setEffect(dropShadow);

        // Bouton pour fermer
        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> stage.close());
        closeButton.setStyle(
                "-fx-background-color: #0066a4; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 5;"
        );

        vbox.getChildren().addAll(title, qrCodeView, details, closeButton);

        Scene scene = new Scene(vbox, 400, 550);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
