package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Restaurant;
import services.RestaurantServices;

import java.sql.SQLException;
import java.util.List;

public class AfficherRestaurant {
    private final RestaurantServices r = new RestaurantServices();

    @FXML
    private TableColumn<Restaurant, String> colAdresseRest;
    @FXML
    private TableColumn<Restaurant, Integer> colClassRest;
    @FXML
    private TableColumn<Restaurant, String> colFermRest;
    @FXML
    private TableColumn<Restaurant, Integer> colIdRest;
    @FXML
    private TableColumn<Restaurant, String> colNomRest;
    @FXML
    private TableColumn<Restaurant, String> colOuvRest;
    @FXML
    private TableColumn<Restaurant, String> colTypeRest;

    @FXML
    private TableView<Restaurant> tableRestaurants;

    @FXML
    private ComboBox<Restaurant> comboRestaurants;

    @FXML
    void initialize() {
        chargerRestaurants();
    }

    private void chargerRestaurants() {
        try {
            RestaurantServices service = new RestaurantServices();
            List<Restaurant> restaurants = service.getAll();

            // Mettre à jour la TableView avec les restaurants
            ObservableList<Restaurant> observableList = FXCollections.observableArrayList(restaurants);
            tableRestaurants.setItems(observableList);

            // Mettre à jour la ComboBox avec les restaurants
            ObservableList<Restaurant> options = FXCollections.observableArrayList(restaurants);
            comboRestaurants.setItems(options);

            // Configurer les colonnes de la TableView
            colIdRest.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNomRest.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colAdresseRest.setCellValueFactory(new PropertyValueFactory<>("adresse"));
            colTypeRest.setCellValueFactory(new PropertyValueFactory<>("type"));
            colOuvRest.setCellValueFactory(new PropertyValueFactory<>("heure_ouv"));
            colFermRest.setCellValueFactory(new PropertyValueFactory<>("heure_ferm"));
            colClassRest.setCellValueFactory(new PropertyValueFactory<>("classement"));

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void supprimerRestaurant(ActionEvent event) {
        Restaurant selected = comboRestaurants.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setContentText("Veuillez sélectionner un restaurant à supprimer.");
            alert.showAndWait();
            return;
        }
        try {
            RestaurantServices service = new RestaurantServices();
            service.delete(selected);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Restaurant supprimé avec succès !");
            alert.showAndWait();
            chargerRestaurants(); // Actualise la liste après suppression
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de la suppression : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
