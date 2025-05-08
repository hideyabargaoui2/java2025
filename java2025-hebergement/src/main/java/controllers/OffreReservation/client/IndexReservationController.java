package controllers.OffreReservation.client;

import controllers.OffreReservation.ReservationFormController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Reservation;
import service.ReservationService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IndexReservationController implements Initializable {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> clientColumn;
    @FXML private TableColumn<Reservation, String> emailColumn;
    @FXML private TableColumn<Reservation, String> offreColumn;
    @FXML private TableColumn<Reservation, String> dateColumn;
    @FXML private TableColumn<Reservation, Integer> personnesColumn;
    @FXML private TableColumn<Reservation, String> statutColumn;

    private ReservationService reservationService = new ReservationService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadReservations();
    }

    private void setupTableColumns() {
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientNom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("clientEmail"));
        offreColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getOffre().getLieu()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        personnesColumn.setCellValueFactory(new PropertyValueFactory<>("nombrePersonnes"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    private void loadReservations() {
        reservationTable.getItems().setAll(reservationService.getAllReservations());
    }




    @FXML
    private void gotores() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/offreReservation/client/indexOffre.fxml"));
                Parent root = loader.load();


                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Détails de la Réservation");
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir les détails",
                        e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une réservation",
                    Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void handleDeleteReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer la réservation");
            confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (reservationService.deleteReservation(selected.getId())) {
                    loadReservations();
                } else {
                    showAlert("Erreur", "Échec de la suppression",
                            "La réservation n'a pas pu être supprimée", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une réservation à supprimer",
                    Alert.AlertType.WARNING);
        }
    }



    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}