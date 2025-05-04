package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Restaurant;
import services.RestaurantServices;

import java.io.IOException;
import java.sql.Time;

public class AjouterRestaurant {

    @FXML private TextField TFadresse, TFclass, TFferme, TFnom, TFouvert, TFtype;
    @FXML private Button addresto, displayresto, ajoutermenu, modifierresto;

    private final RestaurantServices service = new RestaurantServices();

    @FXML
    void ajouter(ActionEvent event) {
        try {
            int classement = Integer.parseInt(TFclass.getText().trim());
            String nom = TFnom.getText().trim();
            String adresse = TFadresse.getText().trim();
            String type = TFtype.getText().trim();
            String horaireOuvert = TFouvert.getText().trim();
            String horaireFerme = TFferme.getText().trim();

            if (nom.isEmpty() || adresse.isEmpty() || type.isEmpty() || horaireOuvert.isEmpty() || horaireFerme.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            if (!horaireOuvert.matches("\\d{2}:\\d{2}") || !horaireFerme.matches("\\d{2}:\\d{2}")) {
                showAlert(Alert.AlertType.ERROR, "Format horaire invalide", "Utilisez le format hh:mm pour les horaires.");
                return;
            }

            Restaurant r = new Restaurant();
            r.setNom(nom);
            r.setAdresse(adresse);
            r.setType(type);
            r.setHeure_ouv(Time.valueOf(horaireOuvert + ":00"));
            r.setHeure_ferm(Time.valueOf(horaireFerme + ":00"));
            r.setClassement(classement);

            service.add(r);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Restaurant ajouté avec succès.");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le classement doit être un entier.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    @FXML
    void afficherresto(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherRestaurant.fxml"));
            TFnom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Menu");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirUpdate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateRestaurant.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Modifier Restaurant");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        TFnom.clear();
        TFadresse.clear();
        TFtype.clear();
        TFouvert.clear();
        TFferme.clear();
        TFclass.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
