package controllers.OffreReservation.client;

import controllers.OffreReservation.client.EditReservationController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Offre;
import models.Reservation;
import service.OffreService;
import service.ReservationService;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ShowOffreController implements Initializable {

    @FXML private Label offreTitle;
    @FXML private Label datesLabel;
    @FXML private Label capaciteLabel;
    @FXML private Label prixLabel;
    @FXML private TextArea descriptionArea;
    @FXML private FlowPane reservationsContainer;

    private OffreService offreService;
    private ReservationService reservationService;
    private Offre currentOffre;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        offreService = new OffreService();
        reservationService = new ReservationService();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
    }

    public void setOffre(Offre offre) {
        this.currentOffre = offre;
        loadOffreDetails();
        loadReservationsAsCards();
    }

    private void loadOffreDetails() {
        if (currentOffre != null) {
            offreTitle.setText(currentOffre.getLieu());
            datesLabel.setText("Du " + currentOffre.getDateDepart().format(dateFormatter) +
                    " au " + currentOffre.getDateRetour().format(dateFormatter));
            capaciteLabel.setText("Capacité: " + currentOffre.getCapacite());
            prixLabel.setText("Prix: " + String.format("%.2f TND", currentOffre.getPrixTotal()));
            descriptionArea.setText(currentOffre.getDescription());
        }
    }

    private void loadReservationsAsCards() {
        reservationsContainer.getChildren().clear();

        if (currentOffre != null) {
            try {
                for (Reservation reservation : reservationService.getReservationsByOffre(currentOffre.getId())) {
                    VBox card = createReservationCard(reservation);
                    reservationsContainer.getChildren().add(card);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de charger les réservations", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private VBox createReservationCard(Reservation reservation) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPadding(new Insets(15));
        card.setSpacing(10);
        card.setPrefWidth(300);

        // Titre
        Label clientLabel = new Label(reservation.getClientNom());
        clientLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Email
        Label emailLabel = new Label(reservation.getClientEmail());
        emailLabel.setStyle("-fx-text-fill: #555;");

        // Date réservation
        Label dateLabel = new Label("Réservé le: " + reservation.getDateReservation().format(dateFormatter));

        // Nombre de personnes
        Label personnesLabel = new Label("Personnes: " + reservation.getNombrePersonnes());

        // Statut
        Label statutLabel = new Label("Statut: " + reservation.getStatut());
        statutLabel.setStyle(reservation.getStatut().equals("Confirmée") ?
                "-fx-font-weight: bold; -fx-text-fill: green;" :
                reservation.getStatut().equals("Annulée") ?
                        "-fx-font-weight: bold; -fx-text-fill: red;" :
                        "-fx-font-weight: bold; -fx-text-fill: orange;");

        // Boutons d'action
        HBox buttonsBox = new HBox(10);
        Button editBtn = new Button("Modifier");
        editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        buttonsBox.getChildren().addAll(editBtn, deleteBtn);

        // Ajout des éléments à la carte
        card.getChildren().addAll(clientLabel, emailLabel, dateLabel, personnesLabel, statutLabel, buttonsBox);

        // Gestion des événements
        editBtn.setOnAction(e -> editReservation(reservation));
        deleteBtn.setOnAction(e -> confirmDeleteReservation(reservation));

        return card;
    }

    @FXML
    private void ajouterReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/OffreReservation/client/AddReservation.fxml"));
            Parent root = loader.load();

            AddReservationController controller = loader.getController();
            controller.setOffre(currentOffre);
            controller.setShowOffreController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Nouvelle Réservation pour " + currentOffre.getLieu());
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void editReservation(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/OffreReservation/client/EditReservation.fxml"));
            Parent root = loader.load();

            EditReservationController controller = loader.getController();
            controller.setReservation(reservation);
            controller.setReservation(reservation);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Réservation");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void confirmDeleteReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la réservation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la réservation de " + reservation.getClientNom() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteReservation(reservation);
            }
        });
    }

    private void deleteReservation(Reservation reservation) {
        try {
            if (reservationService.deleteReservation(reservation.getId())) {
                showAlert("Succès", "Réservation supprimée", "La réservation a été supprimée avec succès", Alert.AlertType.INFORMATION);
                loadReservationsAsCards();
            } else {
                showAlert("Erreur", "Échec de la suppression", "La réservation n'a pas pu être supprimée", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de supprimer la réservation", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refreshReservations() {
        loadReservationsAsCards();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}