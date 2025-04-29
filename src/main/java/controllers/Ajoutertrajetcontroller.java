package controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.Trajet;
import services.Trajetservice;

import java.util.ArrayList;
import java.util.List;

public class Ajoutertrajetcontroller {

    @FXML
    private DatePicker dateDatePicker;

    @FXML
    private TextField HEUREField;

    @FXML
    private TextField destinationField;
    @FXML
    private TextField  dureeField;
    @FXML
    private TextField transportField;

    @FXML
    private Button ajouterButton;

    @FXML
    private void initialize() {
        // Initialisation si nécessaire
    }

    @FXML
    private void ajouterTrajet() {
        if (!validateFields()) return;  // Validation des champs

        // Création du produit
        Trajet ta = new Trajet(
                dateDatePicker.getValue().atStartOfDay(),
                Integer.parseInt(HEUREField.getText().trim()),
                destinationField.getText().trim(),
                transportField.getText().trim(),
                Integer.parseInt(dureeField.getText().trim() )
        );

        try {
            // Ajout du produit
            Trajetservice ts = new Trajetservice();
            ts.ajouter(ta);
            showSuccessAlert("Trajet ajouté avec succès avec ");

            // ✅ Fermer la fenêtre
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

        if (dateDatePicker.getValue() == null) {
            errors.append("- La date est requise.\n");
        }

        if (HEUREField.getText().trim().isEmpty()) {
            errors.append("- L'heure est requise.\n");
        } else {
            try {
                int heure = Integer.parseInt(HEUREField.getText().trim());
                if (heure < 0 || heure > 23) {
                    errors.append("- L'heure doit être comprise entre 0 et 23.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Heure invalide (doit être un nombre).\n");
            }
        }

        if (destinationField.getText().trim().isEmpty()) {
            errors.append("- La destination est requise.\n");
        }

        if (transportField.getText().trim().isEmpty()) {
            errors.append("- Le moyen de transport est requis.\n");
        }

        if (dureeField.getText().trim().isEmpty()) {
            errors.append("- La durée est requise.\n");
        } else {
            try {
                int duree = Integer.parseInt(dureeField.getText().trim());
                if (duree <= 0) {
                    errors.append("- La durée doit être un nombre positif.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Durée invalide (doit être un nombre).\n");
            }
        }

        if (errors.length() > 0) {
            showAlert("Erreur de validation", errors.toString());
            return false;
        }

        return true;
    }

}







