package controllers.OffreReservation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Reservation;
import models.Offre;
import service.ReservationService;
import service.OffreService;

import java.sql.SQLException;
import java.time.LocalDate;

public class ReservationFormController {

    @FXML private ComboBox<Offre> offreComboBox;
    @FXML private TextField clientNomField;
    @FXML private TextField clientEmailField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private Spinner<Integer> personnesSpinner;
    @FXML private ComboBox<String> statutComboBox;

    private Reservation reservation;
    private IndexReservationController indexController;
    private ReservationService reservationService = new ReservationService();
    private OffreService offreService = new OffreService();

    @FXML
    public void initialize() throws SQLException {
        // Initialize spinner for number of people
        personnesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));

        // Initialize status combo box
        statutComboBox.getItems().addAll("Confirmée", "En attente", "Annulée");

        // Load offers
        offreComboBox.getItems().setAll(offreService.getAllOffres());
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        if (reservation != null) {
            // Populate form with existing reservation data
            offreComboBox.getSelectionModel().select(reservation.getOffre());
            clientNomField.setText(reservation.getClientNom());
            clientEmailField.setText(reservation.getClientEmail());
            dateReservationPicker.setValue(reservation.getDateReservation());
            personnesSpinner.getValueFactory().setValue(reservation.getNombrePersonnes());
            statutComboBox.getSelectionModel().select(reservation.getStatut());
        } else {
            // Set default values for new reservation
            dateReservationPicker.setValue(LocalDate.now());
            statutComboBox.getSelectionModel().selectFirst();
        }
    }

    public void setIndexController(IndexReservationController indexController) {
        this.indexController = indexController;
    }

    @FXML
    private void handleSave() {
        try {
            if (reservation == null) {
                reservation = new Reservation();
            }

            reservation.setOffre(offreComboBox.getValue());
            reservation.setClientNom(clientNomField.getText());
            reservation.setClientEmail(clientEmailField.getText());
            reservation.setDateReservation(dateReservationPicker.getValue());
            reservation.setNombrePersonnes(personnesSpinner.getValue());
            reservation.setStatut(statutComboBox.getValue());

            boolean success;
            if (reservation.getId() == 0) {
                success = reservationService.addReservation(reservation);
            } else {
                success = reservationService.updateReservation(reservation);
            }

            if (success) {
                if (indexController != null) {
                    indexController.refreshReservations();
                }
                closeWindow();
            } else {
                showAlert("Erreur", "Échec de l'opération",
                        "La réservation n'a pas pu être enregistrée", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Données invalides",
                    "Veuillez vérifier les informations saisies", Alert.AlertType.ERROR);
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