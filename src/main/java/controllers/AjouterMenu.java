package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.Menu;
import models.Restaurant;
import services.MenuService;
import services.RestaurantServices;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AjouterMenu {
    @FXML
    private GridPane gridMenu;
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
    private TextField TFadresse;

    private final MenuService menuService = new MenuService();
    private final RestaurantServices restaurantServices = new RestaurantServices();

    @FXML
    public void initialize() {
        try {
            List<Restaurant> restaurants = restaurantServices.getAll();
            System.out.println("Restaurants loaded: " + restaurants.size());  // Vérification du nombre de restaurants
            TFnomresto.getItems().addAll(restaurants);

            // Définir un StringConverter pour afficher le nom du restaurant dans le ComboBox
            TFnomresto.setCellFactory(param -> new ListCell<Restaurant>() {
                @Override
                protected void updateItem(Restaurant item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getNom());  // Afficher seulement le nom du restaurant
                    }
                }
            });

            // Assurez-vous que le ComboBox affiche correctement le nom du restaurant dans la sélection
            TFnomresto.setButtonCell(new ListCell<Restaurant>() {
                @Override
                protected void updateItem(Restaurant item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getNom());  // Afficher le nom sélectionné
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des restaurants : " + e.getMessage());
        }
    }


    @FXML
    private void ajouter() {
        // Validation des champs de texte
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

        // Vérifier si un restaurant a été sélectionné dans le ComboBox
        Restaurant selectedRestaurant = TFnomresto.getSelectionModel().getSelectedItem();
        if (selectedRestaurant == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un restaurant !");
            return;
        }

        // Créer un objet Menu à partir des informations fournies
        Menu menu = new Menu();
        menu.setName(TFname.getText());
        try {
            int prix = Integer.parseInt(TFprix.getText()); // Convertir le texte en entier
            menu.setPrix(prix);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Prix doit être un nombre valide !");
            return;
        }
        menu.setDescription(TFdesc.getText());
        menu.setRestaurant(selectedRestaurant);  // Assigner le restaurant sélectionné

        try {
            menuService.add(menu);  // Ajouter le menu dans la base de données
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Menus");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture d'AfficherMenu : " + e.getMessage());
        }
    }
}
