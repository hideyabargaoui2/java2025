package controllers.OffreReservation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Offre;
import service.OffreService;

import java.time.LocalDate;

public class NewOffreController {
    @FXML private TextField lieuField;
    @FXML private DatePicker dateDepartPicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private TextField capaciteField;
    @FXML private TextField prixField;
    @FXML private TextArea descriptionArea;

    private OffreService offreService = new OffreService();
    private IndexOffreController indexController;

    public void setIndexController(IndexOffreController indexController) {
        this.indexController = indexController;
    }

    @FXML
    private void saveOffre() {
        try {
            String lieu = lieuField.getText().trim();
            LocalDate dateDepart = dateDepartPicker.getValue();
            LocalDate dateRetour = dateRetourPicker.getValue();
            String capaciteText = capaciteField.getText().trim();
            String prixText = prixField.getText().trim();
            String description = descriptionArea.getText().trim();

            // Validation lieu
            if (lieu.isEmpty()) {
                showAlert("Validation", "Champ manquant", "Le lieu est requis.", Alert.AlertType.WARNING);
                return;
            }

            // Validation des dates
            if (dateDepart == null || dateRetour == null) {
                showAlert("Validation", "Dates manquantes", "Veuillez sélectionner les dates de départ et de retour.", Alert.AlertType.WARNING);
                return;
            }

            if (dateRetour.isBefore(dateDepart)) {
                showAlert("Validation", "Dates invalides", "La date de retour doit être après la date de départ.", Alert.AlertType.WARNING);
                return;
            }

            // Validation capacité
            int capacite;
            try {
                capacite = Integer.parseInt(capaciteText);
                if (capacite <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                showAlert("Validation", "Capacité invalide", "Veuillez entrer une capacité valide (> 0).", Alert.AlertType.WARNING);
                return;
            }

            // Validation prix
            double prix;
            try {
                prix = Double.parseDouble(prixText);
                if (prix < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                showAlert("Validation", "Prix invalide", "Veuillez entrer un prix valide (≥ 0).", Alert.AlertType.WARNING);
                return;
            }

            // Validation description
            if (description.isEmpty()) {
                showAlert("Validation", "Champ manquant", "La description est requise.", Alert.AlertType.WARNING);
                return;
            }

            // Création et enregistrement de l'offre
            Offre offre = new Offre();
            offre.setLieu(lieu);
            offre.setDateDepart(dateDepart);
            offre.setDateRetour(dateRetour);
            offre.setCapacite(capacite);
            offre.setPrixTotal(prix);
            offre.setDescription(description);

            offreService.addOffre(offre);

            if (indexController != null) {
                indexController.refreshOffres();
            }

            closeWindow();

        } catch (Exception e) {
            showAlert("Erreur", "Données invalides", "Veuillez vérifier les informations saisies", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) lieuField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}