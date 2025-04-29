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

    private final MenuService menuService = new MenuService();
    private final RestaurantServices restaurantServices = new RestaurantServices();

    @FXML
    public void initialize() {
        try {
            List<Restaurant> restaurants = restaurantServices.getAll();
            TFnomresto.getItems().addAll(restaurants);

            List<Menu> menus = menuService.getAll();
            comboMenus.getItems().addAll(menus);

        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    @FXML
    private void ajouter() {
        try {
            Restaurant selectedRestaurant = TFnomresto.getSelectionModel().getSelectedItem();
            if (selectedRestaurant == null) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un restaurant !");
                return;
            }

            Menu menu = new Menu();
            menu.setName(TFname.getText());
            menu.setPrix(TFprix.getText());
            menu.setDescription(TFdesc.getText());
            menu.setRestaurant(selectedRestaurant);

            menuService.add(menu);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Menu ajouté avec succès !");

            // Rafraîchir la liste des menus
            comboMenus.getItems().clear();
            comboMenus.getItems().addAll(menuService.getAll());

        } catch (SQLException e) {
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

        } catch (SQLException e) {
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