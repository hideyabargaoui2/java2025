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
import models.hotel;
import service.HotelService;
import java.sql.SQLException;

import java.io.IOException;
import java.util.List;

public class AfficherHotelController {

    private final HotelService hotelService = new HotelService();

    @FXML private TableView<hotel> tableView;
    @FXML private TableColumn<hotel, String> nomCol;
    @FXML private TableColumn<hotel, Double> prixNuitCol;
    @FXML private TableColumn<hotel, Integer> nombreNuitCol;
    @FXML private TableColumn<hotel, String> standingCol;
    @FXML private TableColumn<hotel, String> adresseCol;
    @FXML private TableColumn<hotel, Void> actionCol;
    @FXML private Button btnAjouter;

    @FXML
    public void initialize() {
        System.out.println("Initialisation de AfficherHotelController");

        // Configuration des colonnes
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prixNuitCol.setCellValueFactory(new PropertyValueFactory<>("prixnuit"));
        nombreNuitCol.setCellValueFactory(new PropertyValueFactory<>("nombrenuit"));
        standingCol.setCellValueFactory(new PropertyValueFactory<>("standing"));
        adresseCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        setupActionColumn();
        loadData();

        // Vérifier que le bouton est correctement configuré
        if (btnAjouter != null) {
            // Définir explicitement le gestionnaire d'événements
            btnAjouter.setOnAction(event -> {
                System.out.println("Bouton Ajouter cliqué");
                ajouterHotel();
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
                    hotel h = getTableView().getItems().get(getIndex());
                    ouvrirModifierHotel(h);
                });

                btnDelete.setOnAction(e -> {
                    hotel h = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Supprimer l'hôtel");
                    alert.setContentText("Voulez-vous vraiment supprimer cet hôtel ?");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            boolean success = false; // Utilisation de la méthode d'instance
                            try {
                                success = hotelService.supprimer(h);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                            if (success) {
                                // Retirer directement de la liste observable
                                getTableView().getItems().remove(h);
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

    private void ouvrirModifierHotel(hotel h) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierHotel.fxml"));
            Parent root = loader.load();

            ModifierHotelController controller = loader.getController();
            controller.setHotel(h);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Modifier un hôtel");
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
            List<hotel> hotels = hotelService.getA(); // Utilisation de la méthode d'instance
            ObservableList<hotel> observableList = FXCollections.observableArrayList(hotels);
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
    private void ajouterHotel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterHotel.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur
            AjouterHotelController controller = loader.getController();
            // Lui passer une référence au contrôleur parent
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un hôtel");
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