package controllers;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Restaurant;
import services.RestaurantServices;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AfficherRestaurant implements Initializable {

    @FXML
    private TableView<Restaurant> table;  // Assurez-vous que la TableView existe dans le FXML
    @FXML
    private TableColumn<Restaurant, String> colNom;
    @FXML
    private TableColumn<Restaurant, String> colAdresse;
    @FXML
    private TableColumn<Restaurant, String> colType;
    @FXML
    private TableColumn<Restaurant, String> colClassement;
    @FXML
    private TableColumn<Restaurant, String> colHoraireOuvert;
    @FXML
    private TableColumn<Restaurant, String> colHoraireFerme;
    @FXML
    private TableColumn<Restaurant, String> colModifier;
    @FXML
    private TableColumn<Restaurant, String> colSupprimer;
    @FXML
    private TableColumn<Restaurant, String> colMap; // Colonne pour la carte

    private final RestaurantServices service = new RestaurantServices();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colClassement.setCellValueFactory(new PropertyValueFactory<>("classement"));
        colHoraireOuvert.setCellValueFactory(new PropertyValueFactory<>("horaireOuvert"));
        colHoraireFerme.setCellValueFactory(new PropertyValueFactory<>("horaireFerme"));

        // Modifier Column
        colModifier.setCellFactory(col -> new TableCell<Restaurant, String>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    Restaurant r = getTableView().getItems().get(getIndex());
                    modifierRestaurant(r);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Supprimer Column
        colSupprimer.setCellFactory(col -> new TableCell<Restaurant, String>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setOnAction(event -> {
                    Restaurant r = getTableView().getItems().get(getIndex());
                    supprimerRestaurant(r);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Carte Column
        colMap.setCellFactory(col -> new TableCell<Restaurant, String>() {
            private final Button btn = new Button("Carte");

            {
                btn.setOnAction(event -> {
                    Restaurant r = getTableView().getItems().get(getIndex());
                    String adresse = r.getAdresse().replace(" ", "+");
                    String nom = r.getNom().replace(" ", "+");
                    String url = "https://www.google.com/maps/search/?api=1&query=" + nom + "+" + adresse;

                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        afficherRestaurants();
    }

    @FXML
    void ajouterRestaurant(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();

            // Obtenir la scène à partir d'un composant quelconque (ici colNom)
            Scene scene = colNom.getTableView().getScene();
            scene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void afficherRestaurants() {
        ObservableList<Restaurant> listRestaurants = FXCollections.observableArrayList(service.afficher());
        System.out.println("Restaurants chargés : " + listRestaurants.size());  // Vérification de la taille de la liste

        listRestaurants.sort((r1, r2) -> r1.getHoraireOuvert().compareTo(r2.getHoraireOuvert()));
        table.setItems(listRestaurants);
    }

    private void modifierRestaurant(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateRestaurant.fxml"));
            Parent root = loader.load();
            UpdateRestaurant controller = loader.getController();
            controller.setRestaurant(restaurant);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            afficherRestaurants();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerRestaurant(Restaurant restaurant) {
        service.supprimer(restaurant.getId());
        afficherRestaurants();
    }
}
