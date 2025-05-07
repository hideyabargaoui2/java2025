package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.ResHotel;
import service.ResHotelService;
import service.SMSService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherResHotelController {

    private final ResHotelService resHotelService = new ResHotelService();
    private static final String PHONE_NUMBER = "+21652348380"; // Numéro de téléphone à utiliser

    @FXML private TableView<ResHotel> tableView;
    @FXML private TableColumn<ResHotel, String> hotelCol;
    @FXML private TableColumn<ResHotel, String> startresCol;
    @FXML private TableColumn<ResHotel, String> dateresCol;
    @FXML private TableColumn<ResHotel, Void> actionCol;
    @FXML private Button btnAjouter;
    @FXML private Button btnConfirmSMS;
    @FXML private Button btnNavHotels; // Bouton de navigation vers les hôtels

    @FXML
    public void initialize() {
        System.out.println("Initialisation de AfficherResHotelController");

        // Configuration des colonnes
        hotelCol.setCellValueFactory(new PropertyValueFactory<>("hotel"));
        startresCol.setCellValueFactory(new PropertyValueFactory<>("startres"));

        // Pour formater la date de façon lisible
        dateresCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateres().toLocalDate().toString()));

        setupActionColumn();
        loadData();

        // Vérifier que le bouton est correctement configuré
        if (btnAjouter != null) {
            // Définir explicitement le gestionnaire d'événements
            btnAjouter.setOnAction(event -> {
                System.out.println("Bouton Ajouter cliqué");
                ajouterResHotel();
            });
        } else {
            System.err.println("Erreur: btnAjouter est null - Vérifiez l'ID dans le fichier FXML");
        }

        // Configuration du bouton de confirmation SMS
        if (btnConfirmSMS != null) {
            btnConfirmSMS.setOnAction(event -> {
                System.out.println("Bouton Confirmation SMS cliqué");
                envoyerSMSConfirmation();
            });
        } else {
            System.err.println("Erreur: btnConfirmSMS est null - Vérifiez l'ID dans le fichier FXML");
        }

        // Configuration du bouton de navigation vers les hôtels
        if (btnNavHotels != null) {
            btnNavHotels.setOnAction(event -> {
                System.out.println("Navigation vers la gestion des hôtels");
                navigateToHotels();
            });
        } else {
            System.err.println("Erreur: btnNavHotels est null - Vérifiez l'ID dans le fichier FXML");
        }
    }

    /**
     * Navigue vers l'écran de gestion des hôtels
     */
    @FXML
    public void navigateToHotels() {
        try {
            // Charger l'écran des hôtels
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherHotel.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Scene currentScene = tableView.getScene();
            Stage stage = (Stage) currentScene.getWindow();

            // Remplacer la scène actuelle par celle des hôtels
            Scene scene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
            stage.setTitle("Gestion des Hôtels - TRAVELPRO");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de Navigation");
            alert.setContentText("Impossible d'ouvrir l'écran des hôtels : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void setupActionColumn() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            final Button btnEdit = new Button("Modifier");
            final Button btnDelete = new Button("Supprimer");
            final Button btnConfirm = new Button("Confirmer"); // Bouton de confirmation par ligne
            final HBox pane = new HBox(5, btnEdit, btnDelete, btnConfirm);

            {
                try {
                    Image editImage = new Image(getClass().getResourceAsStream("/icons/edit.jpg"), 20, 20, true, true);
                    Image deleteImage = new Image(getClass().getResourceAsStream("/icons/delete.jpg"), 20, 20, true, true);
                    // On peut ajouter une icône pour le bouton de confirmation également
                    Image confirmImage = new Image(getClass().getResourceAsStream("/icons/confirm.png"), 20, 20, true, true);

                    btnEdit.setGraphic(new ImageView(editImage));
                    btnDelete.setGraphic(new ImageView(deleteImage));
                    btnConfirm.setGraphic(new ImageView(confirmImage));
                } catch (Exception e) {
                    System.out.println("Erreur chargement icônes : " + e.getMessage());
                    // Continuer sans icônes plutôt que d'échouer
                }

                btnEdit.setOnAction(e -> {
                    ResHotel res = getTableView().getItems().get(getIndex());
                    ouvrirModifierResHotel(res);
                });

                btnDelete.setOnAction(e -> {
                    ResHotel res = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Supprimer la réservation");
                    alert.setContentText("Voulez-vous vraiment supprimer cette réservation ?");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            boolean success = false;
                            success = resHotelService.supprimer(res);
                            if (success) {
                                // Retirer directement de la liste observable
                                getTableView().getItems().remove(res);
                                // Mettre à jour aussi le tableau complet
                                loadData();
                            }
                        }
                    });
                });

                // Action pour le bouton de confirmation individuel
                btnConfirm.setOnAction(e -> {
                    ResHotel res = getTableView().getItems().get(getIndex());
                    confirmerReservation(res);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    /**
     * Confirme une réservation spécifique et envoie un SMS
     * @param res La réservation à confirmer
     */
    private void confirmerReservation(ResHotel res) {
        // Mise à jour du statut de la réservation en "Confirmé" si ce n'est pas déjà le cas
        if (!res.getStartres().equals("Confirmé")) {
            res.setStartres("Confirmé");
            boolean success = resHotelService.modifier(res);

            if (success) {
                // Actualiser la vue
                loadData();

                // Envoyer un SMS pour cette réservation spécifique
                String message = "Merci pour votre confirmation de réservation à l'hôtel " + res.getHotel()
                        + " pour le " + res.getDateres().toLocalDate().toString();
                SMSService.sendSMS(PHONE_NUMBER, message);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Impossible de confirmer la réservation");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Cette réservation est déjà confirmée");
            alert.showAndWait();
        }
    }

    /**
     * Envoie un SMS de confirmation global
     */
    @FXML
    private void envoyerSMSConfirmation() {
        boolean smsEnvoye = SMSService.sendSMS(PHONE_NUMBER, "Merci pour votre confirmation");

        if (smsEnvoye) {
            // L'alerte est déjà gérée dans le SMSService
            System.out.println("SMS de confirmation envoyé avec succès");
        } else {
            System.err.println("Échec de l'envoi du SMS de confirmation");
        }
    }

    private void ouvrirModifierResHotel(ResHotel res) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierResHotel.fxml"));
            Parent root = loader.load();

            ModifierResHotelController controller = loader.getController();
            controller.setResHotel(res);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Modifier une réservation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible d'ouvrir la fenêtre de modification : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void loadData() {
        try {
            List<ResHotel> reservations = resHotelService.getA();
            ObservableList<ResHotel> observableList = FXCollections.observableArrayList(reservations);
            tableView.setItems(observableList);
            tableView.refresh(); // Forcer le rafraîchissement de l'affichage
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setContentText("Impossible de charger les données : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void ajouterResHotel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterResHotel.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur
            AjouterResHotelController controller = loader.getController();
            // Lui passer une référence au contrôleur parent
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter une réservation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Le chargement des données est maintenant fait dans le contrôleur enfant après l'ajout

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible d'ouvrir le formulaire d'ajout : " + e.getMessage());
            alert.showAndWait();
        }
    }
}