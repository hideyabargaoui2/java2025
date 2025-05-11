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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.hotel;
import services.HotelService;
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
    @FXML private Button btnNavReservation; // Nouveau bouton de navigation
    @FXML private TextField searchField;
    private ObservableList<hotel> hotelsData = FXCollections.observableArrayList();
    private ObservableList<hotel> filteredList = FXCollections.observableArrayList();
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
        setupDoubleClickHandler();
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

        // Configuration du bouton de navigation vers les réservations
        if (btnNavReservation != null) {
            btnNavReservation.setOnAction(event -> {
                System.out.println("Navigation vers la gestion des réservations");
                navigateToReservations();
            });
        } else {
            System.err.println("Erreur: btnNavReservation est null - Vérifiez l'ID dans le fichier FXML");
        }


        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                rechercherHotel();
            });
        } else {
            System.err.println("Erreur: searchField est null - Vérifiez l'ID dans le fichier FXML");
        }
    }

    /**
     * Navigue vers l'écran de gestion des réservations
     */
    @FXML
    public void navigateToReservations() {
        try {
            // Charger l'écran des réservations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherResHotel.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Scene currentScene = tableView.getScene();
            Stage stage = (Stage) currentScene.getWindow();

            // Remplacer la scène actuelle par celle des réservations
            Scene scene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
            stage.setTitle("Gestion des Réservations - TRAVELPRO");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de Navigation");
            alert.setContentText("Impossible d'ouvrir l'écran des réservations : " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Configure le gestionnaire d'événements de double-clic sur une ligne du tableau
     */
    private void setupDoubleClickHandler() {
        tableView.setRowFactory(tv -> {
            TableRow<hotel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {
                    hotel selectedHotel = row.getItem();
                    System.out.println("Double-clic sur l'hôtel: " + selectedHotel.getNom());
                    ouvrirDetailsHotel(selectedHotel);
                }
            });
            return row;
        });
    }

    /**
     * Ouvre la fenêtre de détails d'un hôtel
     * @param h L'hôtel à afficher en détail
     */
    private void ouvrirDetailsHotel(hotel h) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hotelDetails.fxml"));
            Parent root = loader.load();

            HotelDetailsController controller = loader.getController();
            controller.setHotel(h);

            Stage stage = new Stage();
            stage.setTitle("Détails de l'hôtel: " + h.getNom());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible d'ouvrir la fenêtre de détails : " + e.getMessage());
            alert.showAndWait();
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
            List<hotel> hotels = hotelService.getA();
            hotelsData = FXCollections.observableArrayList(hotels);
            tableView.setItems(hotelsData);
            tableView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setContentText("Impossible de charger les données : " + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    public void rechercherHotel() {
        String searchTerm = searchField.getText().toLowerCase().trim();

        if (searchTerm.isEmpty()) {
            // Si le champ de recherche est vide, affichez toutes les données
            tableView.setItems(hotelsData);
        } else {
            // Filtrer les données en fonction du terme de recherche
            filteredList.clear();

            for (hotel h : hotelsData) {
                if (h.getNom().toLowerCase().contains(searchTerm)) {
                    filteredList.add(h);
                }
            }

            tableView.setItems(filteredList);
        }

        tableView.refresh();
    }

    /**
     * Navigue vers l'écran de gestion des transports
     */
    @FXML
    public void naviguerVersTransport() {
        try {
            // Charger l'écran des transports
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherTransport.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Scene currentScene = tableView.getScene();
            Stage stage = (Stage) currentScene.getWindow();

            // Remplacer la scène actuelle par celle des transports
            Scene scene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
            stage.setTitle("Gestion des Transports - TRAVELPRO");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de Navigation");
            alert.setContentText("Impossible d'ouvrir l'écran des transports : " + e.getMessage());
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