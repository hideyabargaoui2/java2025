package controllers.OffreReservation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Reservation;

public class ReservationDetailsController {

    @FXML private Label idLabel;
    @FXML private Label offreLabel;
    @FXML private Label clientLabel;
    @FXML private Label emailLabel;
    @FXML private Label dateLabel;
    @FXML private Label personnesLabel;
    @FXML private Label statutLabel;

    public void setReservation(Reservation reservation) {
        idLabel.setText(String.valueOf(reservation.getId()));
        offreLabel.setText(reservation.getOffre().getLieu());
        clientLabel.setText(reservation.getClientNom());
        emailLabel.setText(reservation.getClientEmail());
        dateLabel.setText(reservation.getDateReservation().toString());
        personnesLabel.setText(String.valueOf(reservation.getNombrePersonnes()));
        statutLabel.setText(reservation.getStatut());
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) idLabel.getScene().getWindow();
        stage.close();
    }
}