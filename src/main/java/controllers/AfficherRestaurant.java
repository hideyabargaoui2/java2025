package controllers;

import javafx.collections.*;
import javafx.collections.transformation.*;
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
    private TableView<Restaurant> table;
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
    private TableColumn<Restaurant, String> colMap;
    @FXML
    private TextField searchField;

    private final RestaurantServices service = new RestaurantServices();
    private final ObservableList<Restaurant> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colClassement.setCellValueFactory(new PropertyValueFactory<>("classement"));
        colHoraireOuvert.setCellValueFactory(new PropertyValueFactory<>("horaireOuvert"));
        colHoraireFerme.setCellValueFactory(new PropertyValueFactory<>("horaireFerme"));

        initActions();
        afficherRestaurants();
    }

    private void initActions() {
        // Modifier
        colModifier.setCellFactory(col -> new TableCell<Restaurant, String>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
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

        // Supprimer
        colSupprimer.setCellFactory(col -> new TableCell<Restaurant, String>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
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

        // Carte (Google Maps)
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

        // Recherche dynamique
        FilteredList<Restaurant> filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(restaurant -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return restaurant.getNom().toLowerCase().contains(lowerCaseFilter)
                        || restaurant.getAdresse().toLowerCase().contains(lowerCaseFilter)
                        || restaurant.getType().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Restaurant> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }

    private void afficherRestaurants() {
        masterData.setAll(service.afficher());
    }

    @FXML
    void ajouterRestaurant(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();
            Scene scene = colNom.getTableView().getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
