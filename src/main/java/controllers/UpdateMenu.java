package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Menu;
import models.Restaurant;
import services.MenuService;
import services.RestaurantServices;

import java.sql.SQLException;
import java.util.List;

public class UpdateMenu {

    @FXML
    private TextArea TFupdesc;

    @FXML
    private TextField TFupname;

    @FXML
    private ComboBox<Restaurant> TFupnomresto;

    @FXML
    private TextField TFupprix;

    @FXML
    private ComboBox<Menu> comboupMenus;

    @FXML
    private Button updateMenu;

    private final MenuService menuService = new MenuService();
    private final RestaurantServices restaurantServices = new RestaurantServices();

    @FXML
    void initialize() {
        chargerMenus();
        chargerRestaurants();

        comboupMenus.setOnAction(event -> remplirChampsDepuisSelection());
    }

    private void chargerMenus() {
        try {
            List<Menu> menus = menuService.getAll();
            comboupMenus.getItems().setAll(menus);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Impossible de charger les menus : " + e.getMessage());
        }
    }

    private void chargerRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantServices.getAll();
            TFupnomresto.getItems().setAll(restaurants);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Impossible de charger les restaurants : " + e.getMessage());
        }
    }

    private void remplirChampsDepuisSelection() {
        Menu selected = comboupMenus.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TFupname.setText(selected.getName());
            TFupdesc.setText(selected.getDescription());
            TFupprix.setText(String.valueOf(selected.getPrix()));
            TFupnomresto.getSelectionModel().select(selected.getRestaurant());
        }
    }

    @FXML
    void UpdateMenu(ActionEvent event) {
        Menu selectedMenu = comboupMenus.getSelectionModel().getSelectedItem();
        Restaurant selectedResto = TFupnomresto.getSelectionModel().getSelectedItem();

        if (selectedMenu == null || selectedResto == null) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", null, "Veuillez sélectionner un menu et un restaurant.");
            return;
        }

        try {
            String nom = TFupname.getText().trim();
            String desc = TFupdesc.getText().trim();
            String prixText = TFupprix.getText().replace(',', '.').trim();
            double prix = Double.parseDouble(prixText);

            if (nom.isEmpty() || desc.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs requis", null, "Tous les champs doivent être remplis.");
                return;
            }

            selectedMenu.setName(nom);
            selectedMenu.setDescription(desc);
            selectedMenu.setPrix(String.valueOf(prix));
            selectedMenu.setRestaurant(selectedResto);

            menuService.update(selectedMenu);

            showAlert(Alert.AlertType.INFORMATION, "Succès", null, "Menu mis à jour avec succès !");
            chargerMenus(); // Recharge pour refléter les modifications

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", null, "Le prix doit être un nombre valide (ex: 12.5)");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", null, "Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
