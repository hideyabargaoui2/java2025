package controllers.OffreReservation.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Offre;
import models.Reservation;
import service.OffreService;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class IndexOffreController implements Initializable {

    @FXML
    private FlowPane cardsContainer;

    private OffreService offreService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        offreService = new OffreService();
        loadOffresAsCards();
    }

    private void loadOffresAsCards() {
        try {
            cardsContainer.getChildren().clear();
            List<Offre> offres = offreService.getAllOffres();

            for (Offre offre : offres) {
                VBox card = createOffreCard(offre);
                cardsContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les offres", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createOffreCard(Offre offre) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPadding(new Insets(15));
        card.setSpacing(10);
        card.setPrefWidth(300);

        // Titre
        Label title = new Label(offre.getLieu());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Dates
        Label datesLabel = new Label("Du " + offre.getDateDepart().format(dateFormatter) +
                " au " + offre.getDateRetour().format(dateFormatter));
        datesLabel.setStyle("-fx-text-fill: #555;");

        // Capacité et Prix
        Label capaciteLabel = new Label("Capacité: " + offre.getCapacite());
        Label prixLabel = new Label("Prix: " + String.format("%.2f TND", offre.getPrixTotal()));
        HBox infoBox = new HBox(20, capaciteLabel, prixLabel);

        // Description
        TextArea description = new TextArea(offre.getDescription());
        description.setEditable(false);
        description.setWrapText(true);
        description.setPrefHeight(80);
        description.setStyle("-fx-control-inner-background: white; -fx-border-color: transparent;");
        description.setMouseTransparent(true);
        description.setFocusTraversable(false);

        // Boutons d'action
        Button detailsBtn = new Button("Consulter");

        detailsBtn.getStyleClass().add("button");

        HBox buttonsBox = new HBox(10, detailsBtn);

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(title, datesLabel, infoBox, description, buttonsBox);

        // Gestion des événements
        detailsBtn.setOnAction(e -> showOffreDetails(offre));

        return card;
    }

    private void showOffreDetails(Offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/OffreReservation/client/showOffre.fxml"));
            Parent root = loader.load();

            // Si vous avez un contrôleur pour showOffre.fxml, vous pouvez passer les données ici
            ShowOffreController controller = loader.getController();
            controller.setOffre(offre);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'offre");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la vue des détails", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void deleteOffre(Offre offre) {
        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer l'offre");
            confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette offre ?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                offreService.deleteOffre(offre.getId());
                loadOffresAsCards(); // Rafraîchir l'affichage
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de supprimer l'offre", e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    // Méthode pour rafraîchir la liste depuis d'autres contrôleurs
    public void refreshOffres() {
        loadOffresAsCards();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void gotores() {
             try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/offreReservation/client/indexReservation.fxml"));
                Parent root = loader.load();


                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Détails de la Réservation");
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir les détails",
                        e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
