package controllers.OffreReservation.client;

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

    private Offre offre;
    private ShowOffreController showOffreController;
    private ReservationService reservationService = new ReservationService();

    @FXML
    public void initialize() {
        // Initialiser le spinner pour le nombre de personnes
        personnesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, offre != null ? offre.getCapacite() : 20, 1));

        // Initialiser la combo box pour le statut

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

    public void setShowOffreController(controllers.OffreReservation.client.ShowOffreController controller) {
        this.showOffreController = controller;
    }

    @FXML
    private void handleSave() {
        try {
            String nom = clientNomField.getText().trim();
            String email = clientEmailField.getText().trim();
            LocalDate dateReservation = dateReservationPicker.getValue();
            int nbPersonnes = personnesSpinner.getValue();

            // 1. Validation du nom
            if (nom.isEmpty()) {
                showAlert("Validation", "Nom manquant", "Veuillez entrer votre nom.", Alert.AlertType.WARNING);
                return;
            }

            // 2. Validation de l'email
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                showAlert("Validation", "Email invalide", "Veuillez entrer un email valide.", Alert.AlertType.WARNING);
                return;
            }

            // 3. Validation de la date
            if (dateReservation == null) {
                showAlert("Validation", "Date manquante", "Veuillez choisir une date de réservation.", Alert.AlertType.WARNING);
                return;
            }

            if (dateReservation.isBefore(offre.getDateDepart()) || dateReservation.isAfter(offre.getDateRetour())) {
                showAlert("Validation", "Date invalide",
                        "La date de réservation doit être entre le " +
                                offre.getDateDepart() + " et le " + offre.getDateRetour() + ".",
                        Alert.AlertType.WARNING);
                return;
            }

            // 4. Validation du nombre de personnes
            if (nbPersonnes < 1 || nbPersonnes > offre.getCapacite()) {
                showAlert("Validation", "Nombre de personnes invalide",
                        "Le nombre de personnes doit être entre 1 et " + offre.getCapacite() + ".",
                        Alert.AlertType.WARNING);
                return;
            }

            // Création et sauvegarde
            Reservation reservation = new Reservation();
            reservation.setOffre(offre);
            reservation.setClientNom(nom);
            reservation.setClientEmail(email);
            reservation.setDateReservation(dateReservation);
            reservation.setNombrePersonnes(nbPersonnes);

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