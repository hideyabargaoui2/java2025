package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.hotel;

public class AjouterHotelController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prixParNuitField;

    @FXML
    private TextField nombreNuitsField;

    @FXML
    private TextField standingField;

    @FXML
    private TextField adresseField;

    @FXML
    private Button ajouterButton;

    @FXML
    private void initialize() {
        // Initialisation si nécessaire
    }

    @FXML
    private void ajouterHotel() {
        if (!validateFields()) return;  // Validation des champs

        try {
            // Création de l'hôtel
            hotel h = new hotel(
                    nomField.getText().trim(),
                    Double.parseDouble(prixParNuitField.getText().trim()),
                    Integer.parseInt(nombreNuitsField.getText().trim()),
                    standingField.getText().trim(),
                    adresseField.getText().trim()
            );

            // Ajout de l'hôtel
            service.hotelservice hs = new service.hotelservice();
            hs.ajouter(h);
            showSuccessAlert("Hôtel ajouté avec succès!");

            // Fermer la fenêtre
            ((Stage) ajouterButton.getScene().getWindow()).close();

        } catch (Exception e) {
            showAlert("Erreur SQL", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().trim().isEmpty()) {
            errors.append("- Le nom de l'hôtel est requis.\n");
        }

        if (prixParNuitField.getText().trim().isEmpty()) {
            errors.append("- Le prix par nuit est requis.\n");
        } else {
            try {
                double prix = Double.parseDouble(prixParNuitField.getText().trim());
                if (prix <= 0) {
                    errors.append("- Le prix par nuit doit être un nombre positif.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Prix par nuit invalide (doit être un nombre).\n");
            }
        }

        if (nombreNuitsField.getText().trim().isEmpty()) {
            errors.append("- Le nombre de nuits est requis.\n");
        } else {
            try {
                int nuits = Integer.parseInt(nombreNuitsField.getText().trim());
                if (nuits <= 0) {
                    errors.append("- Le nombre de nuits doit être un nombre positif.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Nombre de nuits invalide (doit être un nombre entier).\n");
            }
        }

        if (standingField.getText().trim().isEmpty()) {
            errors.append("- Le standing est requis.\n");
        }

        if (adresseField.getText().trim().isEmpty()) {
            errors.append("- L'adresse est requise.\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreur de validation", errors.toString());
            return false;
        }

        return true;
    }
}