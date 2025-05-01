package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Transport;
import services.TransportService;

import java.util.Arrays;
import java.util.List;

public class AjouterTransportController {

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField compagnieField;

    @FXML
    private TextField prixField;

    @FXML
    private Button ajouterButton;

    // Liste des types de transport autorisés
    private final List<String> typesDeTransportValides = Arrays.asList(
            "Voiture", "Train", "Bus", "Avion", "Bateau", "Autre", "Métro", "Vélo", "Marche"
    );

    @FXML
    private void initialize() {
        // Initialisation du ComboBox avec les valeurs autorisées
        ObservableList<String> typesList = FXCollections.observableArrayList(typesDeTransportValides);
        typeComboBox.setItems(typesList);

        // Optionnel: Empêcher la saisie libre dans le ComboBox
        typeComboBox.setEditable(false);
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

        // Validation du type de transport
        String typeSelectionne = typeComboBox.getValue();
        if (typeSelectionne == null || typeSelectionne.isEmpty()) {
            errors.append("- Le type de transport est requis.\n");
        } else if (!typesDeTransportValides.contains(typeSelectionne)) {
            errors.append("- Type de transport non valide. Veuillez sélectionner un type dans la liste.\n");
        }

        // Validation de la compagnie
        if (compagnieField.getText().trim().isEmpty()) {
            errors.append("- Le nom de la compagnie est requis.\n");
        }

        // Validation du prix
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

        // Affichage des erreurs s'il y en a
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