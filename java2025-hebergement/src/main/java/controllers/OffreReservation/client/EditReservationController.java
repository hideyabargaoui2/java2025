package controllers.OffreReservation.client;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Offre;
import models.Reservation;
import service.OffreService;
import service.ReservationService;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class EditReservationController implements Initializable {

    @FXML private ComboBox<Offre> offreCombo;
    @FXML private TextField clientNomField;
    @FXML private TextField clientEmailField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private Spinner<Integer> personnesSpinner;

    private ReservationService reservationService;
    private OffreService offreService;
    private Reservation currentReservation;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reservationService = new ReservationService();
        offreService = new OffreService();



        // Charger les offres disponibles
        try {
            List<Offre> offres = offreService.getAllOffres();
            offreCombo.setItems(FXCollections.observableArrayList(offres));
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les offres", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void setReservation(Reservation reservation) {
        this.currentReservation = reservation;
        this.isEditMode = true;
        populateFields();
    }

    private void populateFields() {
        offreCombo.getSelectionModel().select(currentReservation.getOffre());
        clientNomField.setText(currentReservation.getClientNom());
        clientEmailField.setText(currentReservation.getClientEmail());
        dateReservationPicker.setValue(currentReservation.getDateReservation());
        personnesSpinner.getValueFactory().setValue(currentReservation.getNombrePersonnes());
     }

    @FXML
    private void enregistrer() {
        try {
            Offre offre = offreCombo.getSelectionModel().getSelectedItem();
            String clientNom = clientNomField.getText();
            String clientEmail = clientEmailField.getText();
            LocalDate dateReservation = dateReservationPicker.getValue();
            int nombrePersonnes = personnesSpinner.getValue();

            // Validation des champs
            if (offre == null || clientNom.isEmpty() || clientEmail.isEmpty() || dateReservation == null) {
                showAlert("Erreur", "Champs manquants", "Veuillez remplir tous les champs obligatoires", Alert.AlertType.ERROR);
                return;
            }

            if (!clientEmail.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                showAlert("Erreur", "Email invalide", "Veuillez entrer une adresse email valide", Alert.AlertType.ERROR);
                return;
            }

            if (isEditMode) {
                // Mise à jour de la réservation existante
                currentReservation.setOffre(offre);
                currentReservation.setClientNom(clientNom);
                currentReservation.setClientEmail(clientEmail);
                currentReservation.setDateReservation(dateReservation);
                currentReservation.setNombrePersonnes(nombrePersonnes);
                currentReservation.setStatut("En attente");

                reservationService.updateReservation(currentReservation);
            } else {
                // Création d'une nouvelle réservation
                Reservation newReservation = new Reservation();
                newReservation.setOffre(offre);
                newReservation.setClientNom(clientNom);
                newReservation.setClientEmail(clientEmail);
                newReservation.setDateReservation(dateReservation);
                newReservation.setNombrePersonnes(nombrePersonnes);
                newReservation.setStatut("En attente");

                reservationService.addReservation(newReservation);
            }

            // Fermer la fenêtre
            clientNomField.getScene().getWindow().hide();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'enregistrement", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void annuler() {
        clientNomField.getScene().getWindow().hide();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}