package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
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
import services.ResHotelService;
import services.SMSService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

public class AfficherResHotelController {

    private final ResHotelService resHotelService = new ResHotelService();
    private static final String PHONE_NUMBER = "+21652348380"; // Numéro de téléphone à utiliser
    private ObservableList<ResHotel> masterData = FXCollections.observableArrayList();
    private FilteredList<ResHotel> filteredData;

    @FXML private TableView<ResHotel> tableView;
    @FXML private TableColumn<ResHotel, String> hotelCol;
    @FXML private TableColumn<ResHotel, String> startresCol;
    @FXML private TableColumn<ResHotel, String> dateresCol;
    @FXML private TableColumn<ResHotel, Integer> nombreChambresCol; // Nouvelle colonne pour le nombre de chambres
    @FXML private TableColumn<ResHotel, Void> actionCol;
    @FXML private Button btnAjouter;
    @FXML private Button btnConfirmSMS;
    @FXML private Button btnNavHotels; // Bouton de navigation vers les hôtels
    @FXML private TextField searchField; // Champ de recherche pour le nom d'hôtel
    @FXML private DatePicker searchDatePicker; // Date picker pour la recherche par date
    @FXML private Button searchButton; // Bouton pour déclencher la recherche
    @FXML private Button resetSearchButton; // Bouton pour réinitialiser la recherche

    @FXML
    public void initialize() {
        System.out.println("Initialisation de AfficherResHotelController");

        // Configuration des colonnes
        hotelCol.setCellValueFactory(new PropertyValueFactory<>("hotel"));
        startresCol.setCellValueFactory(new PropertyValueFactory<>("startres"));

        // Pour formater la date de façon lisible
        dateresCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateres().toLocalDate().toString()));

        // Configuration de la colonne du nombre de chambres
        if (nombreChambresCol != null) {
            nombreChambresCol.setCellValueFactory(new PropertyValueFactory<>("nombreChambres"));
        } else {
            System.err.println("Erreur: nombreChambresCol est null - Vérifiez l'ID dans le fichier FXML");
        }

        setupActionColumn();
        loadData();
        setupSearch();

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
     * Configure la fonctionnalité de recherche
     */
    private void setupSearch() {
        // Initialiser la liste filtrée avec toutes les données
        filteredData = new FilteredList<>(masterData, p -> true);
        tableView.setItems(filteredData);

        // Vérifier que les composants de recherche sont correctement chargés
        if (searchField == null) {
            System.err.println("Erreur: searchField est null - Vérifiez l'ID dans le fichier FXML");
        } else {
            // Permettre d'effectuer une recherche en appuyant sur Entrée dans le champ de recherche
            searchField.setOnAction(this::handleSearch);
        }

        if (searchButton == null) {
            System.err.println("Erreur: searchButton est null - Vérifiez l'ID dans le fichier FXML");
        } else {
            // Configurer le bouton de recherche
            searchButton.setOnAction(this::handleSearch);
        }

        if (resetSearchButton == null) {
            System.err.println("Erreur: resetSearchButton est null - Vérifiez l'ID dans le fichier FXML");
        } else {
            // Configurer le bouton de réinitialisation
            resetSearchButton.setOnAction(event -> {
                if (searchField != null) {
                    searchField.clear();
                }
                if (searchDatePicker != null) {
                    searchDatePicker.setValue(null);
                }
                filteredData.setPredicate(p -> true); // Afficher toutes les données
            });
        }
    }

    /**
     * Gère l'événement de recherche
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        LocalDate searchDate = searchDatePicker != null ? searchDatePicker.getValue() : null;

        // Définir le prédicat de filtrage
        if (filteredData != null) {
            filteredData.setPredicate(createSearchPredicate(searchText, searchDate));
        }
    }

    /**
     * Crée un prédicat pour filtrer les données selon les critères de recherche
     */
    private Predicate<ResHotel> createSearchPredicate(String searchText, LocalDate searchDate) {
        return reservation -> {
            boolean matchesText = true;
            boolean matchesDate = true;

            // Vérifier si le texte de recherche correspond au nom de l'hôtel
            if (searchText != null && !searchText.isEmpty()) {
                matchesText = reservation.getHotel().toLowerCase().contains(searchText);
            }

            // Vérifier si la date de recherche correspond à la date de réservation
            if (searchDate != null) {
                LocalDate reservationDate = reservation.getDateres().toLocalDate();
                matchesDate = reservationDate.equals(searchDate);
            }

            // La réservation doit correspondre à tous les critères spécifiés
            return matchesText && matchesDate;
        };
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
                            try {
                                success = resHotelService.supprimer(res);
                                if (success) {
                                    // Retirer directement de la liste observable
                                    getTableView().getItems().remove(res);
                                    // Mettre à jour aussi le tableau complet
                                    loadData();
                                }
                            } catch (Exception ex) {
                                System.err.println("Erreur lors de la suppression : " + ex.getMessage());
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Erreur");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("Erreur lors de la suppression : " + ex.getMessage());
                                errorAlert.showAndWait();
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
            boolean success = false;
            try {
                success = resHotelService.modifier(res);

                if (success) {
                    // Actualiser la vue
                    loadData();

                    // Envoyer un SMS pour cette réservation spécifique
                    String message = "Merci pour votre confirmation de réservation à l'hôtel " + res.getHotel()
                            + " pour le " + res.getDateres().toLocalDate().toString()
                            + " avec " + res.getNombreChambres() + " chambre(s)";
                    SMSService.sendSMS(PHONE_NUMBER, message);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Impossible de confirmer la réservation");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la confirmation : " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la confirmation : " + e.getMessage());
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
            masterData.setAll(reservations);

            // Si la liste filtrée n'est pas encore initialisée
            if (filteredData == null) {
                filteredData = new FilteredList<>(masterData, p -> true);
                tableView.setItems(filteredData);
            } else {
                // Réappliquer le dernier filtre utilisé
                // Utilisation d'une solution qui évite l'erreur de compatibilité des types
                String searchText = searchField != null ? searchField.getText().toLowerCase().trim() : "";
                LocalDate searchDate = searchDatePicker != null ? searchDatePicker.getValue() : null;

                // Réinitialiser puis appliquer un nouveau prédicat pour éviter l'erreur de type
                filteredData.setPredicate(p -> true); // Réinitialiser d'abord
                filteredData.setPredicate(createSearchPredicate(searchText, searchDate));
            }

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