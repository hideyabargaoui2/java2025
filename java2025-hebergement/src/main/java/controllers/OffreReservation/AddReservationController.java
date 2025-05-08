package controllers.OffreReservation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Offre;
import models.Reservation;
import service.ReservationService;

import java.time.LocalDate;

public class AddReservationController {

    @FXML private Label offreLabel;
    @FXML private TextField clientNomField;
    @FXML private TextField clientEmailField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private Spinner<Integer> personnesSpinner;
    @FXML private ComboBox<String> statutComboBox;

    private Offre offre;
    private ShowOffreController showOffreController;
    private ReservationService reservationService = new ReservationService();

    @FXML
    public void initialize() {
        // Initialiser le spinner pour le nombre de personnes
        personnesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, offre != null ? offre.getCapacite() : 20, 1));

        // Initialiser la combo box pour le statut
        statutComboBox.getItems().addAll("Confirmée", "En attente", "Annulée");
        statutComboBox.getSelectionModel().selectFirst();

        // Définir la date par défaut à aujourd'hui
        dateReservationPicker.setValue(LocalDate.now());
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
        if (offre != null) {
            offreLabel.setText(offre.getLieu());
            // Mettre à jour la valeur maximale du spinner
            personnesSpinner.getValueFactory().setValue(offre.getCapacite());
        }
    }

    public void setShowOffreController(ShowOffreController controller) {
        this.showOffreController = controller;
    }

    @FXML
    private void handleSave() {
        try {
            Reservation reservation = new Reservation();
            reservation.setOffre(offre);
            reservation.setClientNom(clientNomField.getText());
            reservation.setClientEmail(clientEmailField.getText());
            reservation.setDateReservation(dateReservationPicker.getValue());
            reservation.setNombrePersonnes(personnesSpinner.getValue());
            reservation.setStatut(statutComboBox.getValue());

            if (reservationService.addReservation(reservation)) {
                if (showOffreController != null) {
                    showOffreController.refreshReservations();
                }
                closeWindow();
            } else {
                showAlert("Erreur", "Échec de l'ajout", "La réservation n'a pas pu être ajoutée", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Données invalides", "Veuillez vérifier les informations saisies", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) clientNomField.getScene().getWindow();
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