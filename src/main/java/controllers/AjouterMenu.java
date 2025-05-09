package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Menu;
import models.Restaurant;
import services.MenuService;
import services.RestaurantServices;

import java.io.IOException;
import java.util.List;

public class AjouterMenu {
    @FXML
    private TextField TFname;
    @FXML
    private TextField TFprix;
    @FXML
    private TextArea TFdesc;
    @FXML
    private ComboBox<Restaurant> TFnomresto;
    @FXML
    private Button addrmenu;
    @FXML
    private Button afficherMenus;
    @FXML
    private Button addresto;

    private final MenuService menuService = new MenuService();
    private final RestaurantServices restaurantServices = new RestaurantServices();

    @FXML
    void addresto(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterRestaurant.fxml"));
            TFname.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private AnchorPane rootPane;

    @FXML
    public void initialize() {
        try {
            List<Restaurant> restaurants = restaurantServices.getAll();
            TFnomresto.getItems().addAll(restaurants);

            TFnomresto.setCellFactory(param -> new ListCell<Restaurant>() {
                @Override
                protected void updateItem(Restaurant item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((item == null || empty) ? null : item.getNom());
                }
            });

            TFnomresto.setButtonCell(new ListCell<Restaurant>() {
                @Override
                protected void updateItem(Restaurant item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((item == null || empty) ? null : item.getNom());
                }
            });
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des restaurants : " + e.getMessage());
        }

        // Animation fade + scale
        FadeTransition fade = new FadeTransition(Duration.seconds(1), rootPane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.8), rootPane);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }

    @FXML
    private void ajouter() {
        if (TFname.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Nom du plat est obligatoire !");
            return;
        }
        if (TFprix.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Prix du plat est obligatoire !");
            return;
        }
        if (TFdesc.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Description du plat est obligatoire !");
            return;
        }


        Restaurant selectedRestaurant = TFnomresto.getSelectionModel().getSelectedItem();
        if (selectedRestaurant == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un restaurant !");
            return;
        }


        Menu menu = new Menu();
        menu.setName(TFname.getText());
        try {
            int prix = Integer.parseInt(TFprix.getText());
            menu.setPrix(prix);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Prix doit être un nombre valide !");
            return;
        }
        menu.setDescription(TFdesc.getText());
        menu.setRestaurant(selectedRestaurant);

        try {
            menuService.add(menu);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Menu ajouté avec succès !");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du menu : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void afficherMenus() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherMenu.fxml"));
            TFname.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
