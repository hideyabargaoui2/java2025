package controllers;

import javafx.beans.property.SimpleDoubleProperty;
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
import models.Transport;
import services.TransportService;

import java.io.IOException;
import java.util.List;

public class AfficherTransportController {

    private final TransportService transportService = new TransportService();

    @FXML private TableView<Transport> tableView;
    @FXML private TableColumn<Transport, Integer> idCol;
    @FXML private TableColumn<Transport, String> typeCol;
    @FXML private TableColumn<Transport, String> compagnieCol;
    @FXML private TableColumn<Transport, Double> prixCol;
    @FXML private TableColumn<Transport, Void> actionCol;
    @FXML private Button btnAjouter;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur AfficherTransportController");

        // Configurer les colonnes
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        compagnieCol.setCellValueFactory(new PropertyValueFactory<>("compagnie"));
        prixCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrix()).asObject());

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
                        Transport transport = getTableView().getItems().get(getIndex());
                        System.out.println("Modifier : " + transport);
                        ouvrirModifierTransport(transport);
                    }
                });

                btnDelete.setOnAction(e -> {
                    if (getTableRow() != null) {
                        Transport transport = getTableView().getItems().get(getIndex());
                        System.out.println("Supprimer : " + transport);

                        // Demander confirmation avant de supprimer
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation de suppression");
                        alert.setHeaderText("Suppression d'un transport");
                        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce transport ?");

                        alert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                transportService.supprimer(transport);
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
     * Méthode pour ouvrir la fenêtre de modification d'un transport
     * @param transport Le transport à modifier
     */
    private void ouvrirModifierTransport(Transport transport) {
        try {
            // Journalisation pour le débogage
            System.out.println("Tentative d'ouverture de la fenêtre de modification pour le transport: " + transport.getId());

            // Plusieurs façons de charger le fichier FXML, essayons-les successivement
            FXMLLoader loader = null;

            // Option 1: Essayer de charger depuis le même répertoire que cette classe
            try {
                loader = new FXMLLoader(getClass().getResource("../modifierTransport.fxml"));
                System.out.println("Tentative de chargement avec chemin relatif: ../modifierTransport.fxml");
            } catch (Exception e) {
                System.out.println("Erreur avec le premier chemin: " + e.getMessage());
            }

            // Option 2: Essayer de charger depuis la racine des ressources
            if (loader == null || loader.getLocation() == null) {
                try {
                    loader = new FXMLLoader(getClass().getResource("/modifierTransport.fxml"));
                    System.out.println("Tentative de chargement avec chemin absolu: /modifierTransport.fxml");
                } catch (Exception e) {
                    System.out.println("Erreur avec le deuxième chemin: " + e.getMessage());
                }
            }

            // Option 3: Essayer avec un chemin spécifique aux vues
            if (loader == null || loader.getLocation() == null) {
                try {
                    loader = new FXMLLoader(getClass().getResource("/views/modifierTransport.fxml"));
                    System.out.println("Tentative de chargement depuis /views/: /views/modifierTransport.fxml");
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
            ModifierTransportController controller = loader.getController();
            controller.setTransport(transport);
            controller.setParentController(this);

            // Créer et configurer la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier un transport");
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
     * Cette méthode est appelée depuis ModifierTransportController après modification
     */
    public void loadData() {
        try {
            System.out.println("Chargement des transports...");
            List<Transport> transports = transportService.getA();
            System.out.println("Nombre de transports récupérés : " + transports.size());

            ObservableList<Transport> observableList = FXCollections.observableArrayList(transports);
            tableView.setItems(observableList);

            if (observableList.isEmpty()) {
                System.out.println("Aucun transport à afficher");
            } else {
                System.out.println("Transports chargés dans la table");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterTransport() {
        System.out.println("Ajouter un transport cliqué !");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterTransport.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un nouveau transport");
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