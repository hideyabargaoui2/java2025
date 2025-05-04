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
import java.sql.SQLException;
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
    private ComboBox<Menu> comboMenus;
    @FXML
    private Button addrmenu;
    @FXML
    private Button deletemenu;
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

            // Rafraîchir la liste des menus affichés
            List<Menu> updatedMenus = menuService.getAll();  // Récupérer tous les menus
            comboMenus.getItems().clear();  // Vider la liste actuelle
            comboMenus.getItems().addAll(updatedMenus);  // Ajouter la liste mise à jour des menus

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
    private void supprimer(ActionEvent event) {
        Menu selectedMenu = comboMenus.getSelectionModel().getSelectedItem();
        if (selectedMenu == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un menu à supprimer !");
            return;
        }
        try {
            menuService.delete(selectedMenu);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Menu supprimé avec succès !");

            // Rafraîchir la liste
            comboMenus.getItems().clear();
            comboMenus.getItems().addAll(menuService.getAll());

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }
    @FXML
    public void initialize2() {
        List<String> menus = Arrays.asList("Pizza", "Burger", "Salade", "Pâtes", "Sushi", "Tacos");

        int columns = 3; // Nombre de colonnes
        int row = 0;
        int col = 0;

        for (String menu : menus) {
            Button btn = new Button(menu);
            btn.setPrefSize(150, 100);

            gridMenu.add(btn, col, row);

            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }
    }
    @FXML
    private void afficherMenus() {
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