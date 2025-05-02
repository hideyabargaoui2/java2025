package controllers;

import javafx.beans.property.SimpleObjectProperty;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AfficherResHotelController {

    private final ResHotelService resHotelService = new ResHotelService();

    @FXML private TableView<ResHotel> tableView;
    @FXML private TableColumn<ResHotel, String> hotelCol;
    @FXML private TableColumn<ResHotel, String> startresCol;
    @FXML private TableColumn<ResHotel, String> dateresCol;
    @FXML private TableColumn<ResHotel, Void> actionCol;
    @FXML private Button btnAjouter;

    @FXML
    public void initialize() {
        System.out.println("Initialisation de AfficherResHotelController");

        // Configuration des colonnes
        hotelCol.setCellValueFactory(new PropertyValueFactory<>("hotel"));
        startresCol.setCellValueFactory(new PropertyValueFactory<>("startres"));

        // Pour formater la date de façon lisible
        dateresCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDateres().toLocalDate().toString()));

        ;

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
    }

    private void setupActionColumn() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            final Button btnEdit = new Button("Modifier");
            final Button btnDelete = new Button("Supprimer");
            final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                try {
                    Image editImage = new Image(getClass().getResourceAsStream("/icons/edit.jpg"), 20, 20, true, true);
                    Image deleteImage = new Image(getClass().getResourceAsStream("/icons/delete.jpg"), 20, 20, true, true);

                    btnEdit.setGraphic(new ImageView(editImage));
                    btnDelete.setGraphic(new ImageView(deleteImage));
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
                            boolean success = resHotelService.supprimer(res);
                            if (success) {
                                // Retirer directement de la liste observable
                                getTableView().getItems().remove(res);
                                // Mettre à jour aussi le tableau complet
                                loadData();
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
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
