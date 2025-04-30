package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Transport;
import services.TransportService;

public class AjouterTransportController {

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField compagnieField;

    @FXML
    private TextField prixField;

    @FXML
    private Button ajouterButton;

    @FXML
    private void initialize() {
        // Initialisation si nécessaire
    }

    @FXML
    private void ajouterTransport() {
        if (!validateFields()) return;  // Validation des champs

        try {
            // Récupération des valeurs
            String type = typeComboBox.getValue();
            String compagnie = compagnieField.getText().trim();
            double prix = Double.parseDouble(prixField.getText().trim());

            // Création du transport
            Transport transport = new Transport(type, compagnie, prix);

            // Ajout du transport à la base de données
            TransportService ts = new TransportService();
            boolean success = ts.ajouter(transport);

            if (success) {
                showSuccessAlert("Transport ajouté avec succès !");
                // Fermeture de la fenêtre
                ((Stage) ajouterButton.getScene().getWindow()).close();
            } else {
                showAlert("Erreur", "Impossible d'ajouter le transport.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "Le prix doit être un nombre valide.");
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty()) {
            errors.append("- Le type de transport est requis.\n");
        }

        if (compagnieField.getText().trim().isEmpty()) {
            errors.append("- Le nom de la compagnie est requis.\n");
        }

        if (prixField.getText().trim().isEmpty()) {
            errors.append("- Le prix est requis.\n");
        } else {
            try {
                double prix = Double.parseDouble(prixField.getText().trim());
                if (prix <= 0) {
                    errors.append("- Le prix doit être un nombre positif.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Prix invalide (doit être un nombre).\n");
            }
        }

        if (errors.length() > 0) {
            showAlert("Erreur de validation", errors.toString());
            return false;
        }

        return true;
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
}