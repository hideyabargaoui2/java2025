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
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Trajet;
import services.Trajetservice;

import java.io.IOException;
import java.util.List;

public class AfficherTrajetController {

    private final Trajetservice trajetService = new Trajetservice();

    @FXML private TableView<Trajet> tableView;
    @FXML private TableColumn<Trajet, String> dateCol;
    @FXML private TableColumn<Trajet, Integer> heureCol;
    @FXML private TableColumn<Trajet, String> destinationCol;
    @FXML private TableColumn<Trajet, String> transportCol;
    @FXML private TableColumn<Trajet, Integer> dureeCol;
    @FXML private TableColumn<Trajet, Void> actionCol;
    @FXML private Button btnAjouter;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur AfficherTrajetController");

        // Configurer les colonnes
        dateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate().toLocalDate().toString()));

        heureCol.setCellValueFactory(new PropertyValueFactory<>("heure"));
        destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
        transportCol.setCellValueFactory(new PropertyValueFactory<>("transport"));
        dureeCol.setCellValueFactory(new PropertyValueFactory<>("duree"));

        // Configurer la colonne d'action
        setupActionColumn();

        // Charger les données
        loadData();
    }

    private void setupActionColumn() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            final Button btnEdit = new Button("Modifier");
            final Button btnDelete = new Button("Supprimer");
            final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                // Essayer de charger les icônes
                try {
                    String editPath = "/icons/edit.jpg";
                    String deletePath = "/icons/delete.jpg";

                    Image editImage = new Image(getClass().getResourceAsStream(editPath), 20, 20, true, true);
                    Image deleteImage = new Image(getClass().getResourceAsStream(deletePath), 20, 20, true, true);

                    btnEdit.setGraphic(new ImageView(editImage));
                    btnDelete.setGraphic(new ImageView(deleteImage));

                    System.out.println("Icônes chargées avec succès");
                } catch (Exception e) {
                    System.out.println("Impossible de charger les icônes, utilisation du texte à la place: " + e);
                    // Laisser les boutons avec texte
                }

                // Définir les actions des boutons
                btnEdit.setOnAction(e -> {
                    if (getTableRow() != null) {
                        Trajet trajet = getTableView().getItems().get(getIndex());
                        System.out.println("Modifier : " + trajet);
                        ouvrirModifierTrajet(trajet);
                    }
                });

                btnDelete.setOnAction(e -> {
                    if (getTableRow() != null) {
                        Trajet trajet = getTableView().getItems().get(getIndex());
                        System.out.println("Supprimer : " + trajet);

                        // Demander confirmation avant de supprimer
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation de suppression");
                        alert.setHeaderText("Suppression d'un trajet");
                        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce trajet ?");

                        alert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                trajetService.supprimer(trajet);
                                loadData(); // Rafraîchir la table
                            }
                        });
                    }
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
     * Méthode pour ouvrir la fenêtre de modification d'un trajet
     * @param trajet Le trajet à modifier
     */
    private void ouvrirModifierTrajet(Trajet trajet) {
        try {
            // Journalisation pour le débogage
            System.out.println("Tentative d'ouverture de la fenêtre de modification pour le trajet: " + trajet.getId());

            // Plusieurs façons de charger le fichier FXML, essayons-les successivement
            FXMLLoader loader = null;

            // Option 1: Essayer de charger depuis le même répertoire que cette classe
            try {
                loader = new FXMLLoader(getClass().getResource("../modifierTrajet.fxml"));
                System.out.println("Tentative de chargement avec chemin relatif: ../modifierTrajet.fxml");
            } catch (Exception e) {
                System.out.println("Erreur avec le premier chemin: " + e.getMessage());
            }

            // Option 2: Essayer de charger depuis la racine des ressources
            if (loader == null || loader.getLocation() == null) {
                try {
                    loader = new FXMLLoader(getClass().getResource("/modifierTrajet.fxml"));
                    System.out.println("Tentative de chargement avec chemin absolu: /modifierTrajet.fxml");
                } catch (Exception e) {
                    System.out.println("Erreur avec le deuxième chemin: " + e.getMessage());
                }
            }

            // Option 3: Essayer avec un chemin spécifique aux vues
            if (loader == null || loader.getLocation() == null) {
                try {
                    loader = new FXMLLoader(getClass().getResource("/views/modifierTrajet.fxml"));
                    System.out.println("Tentative de chargement depuis /views/: /views/modifierTrajet.fxml");
                } catch (Exception e) {
                    System.out.println("Erreur avec le troisième chemin: " + e.getMessage());
                }
            }

            // Vérifier si on a bien pu charger le fichier
            if (loader == null || loader.getLocation() == null) {
                throw new IOException("Impossible de localiser le fichier FXML pour la modification");
            }

            // Charger le contenu du fichier FXML
            Parent root = loader.load();
            System.out.println("FXML chargé avec succès!");

            // Récupérer le contrôleur et lui passer les données nécessaires
            Modifiertrajetcontroller controller = loader.getController();
            controller.setTrajet(trajet);
            controller.setParentController(this);

            // Créer et configurer la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier un trajet");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Pour bloquer l'interaction avec la fenêtre principale

            // Afficher la fenêtre
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
            e.printStackTrace();

            // Afficher une alerte en cas d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Méthode publique pour charger/recharger les données
     * Cette méthode est appelée depuis ModifierTrajetController après modification
     */
    public void loadData() {
        try {
            System.out.println("Chargement des trajets...");
            List<Trajet> trajets = trajetService.getA();
            System.out.println("Nombre de trajets récupérés : " + trajets.size());

            ObservableList<Trajet> observableList = FXCollections.observableArrayList(trajets);
            tableView.setItems(observableList);

            if (observableList.isEmpty()) {
                System.out.println("Aucun trajet à afficher");
            } else {
                System.out.println("Trajets chargés dans la table");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterTrajet() {
        System.out.println("Ajouter un trajet cliqué !");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutertrajet.fxml"));
            Parent root = loader.load();

            // Si tu veux passer des infos au contrôleur, tu peux les obtenir ici :
            // AjouterTrajetController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un nouveau trajet");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Pour bloquer la fenêtre principale
            stage.showAndWait(); // Attend la fermeture de la fenêtre

            // Recharger la table après l'ajout éventuel
            loadData();

        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de la fenêtre d'ajout : " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir le formulaire");
            alert.setContentText("Erreur : " + e.getMessage());
            alert.showAndWait();
        }
    }

}