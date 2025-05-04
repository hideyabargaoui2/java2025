package controllers;

import models.Menu;
import models.Restaurant;
import services.MenuService;
import services.RestaurantServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UpdateMenu {

    @FXML private TextField TFupname;
    @FXML private TextField TFupdesc;
    @FXML private TextField TFupprix;
    @FXML private ComboBox<Restaurant> TFupnomresto;

    private Menu menu;
    private final MenuService menuService = new MenuService();
    private final RestaurantServices restaurantService = new RestaurantServices();

    @FXML
    public void initialize() {
        ObservableList<Restaurant> restaurants = FXCollections.observableArrayList(restaurantService.afficher());
        TFupnomresto.setItems(restaurants);
    }

    public void setMenu(Menu menu) {
        this.menu = menu;

        if (menu != null) {
            TFupname.setText(menu.getName());
            TFupdesc.setText(menu.getDescription());
            TFupprix.setText(String.valueOf(menu.getPrix()));
            TFupnomresto.setValue(menu.getRestaurant());
        } else {
            showAlert(AlertType.ERROR, "Erreur", "Aucun menu sélectionné pour la modification.");
        }
    }

    @FXML
    private void modifierMenu() {
        if (menu == null) {
            showAlert(AlertType.ERROR, "Erreur", "Aucun menu à modifier.");
            return;
        }

        try {
            String name = TFupname.getText();
            String desc = TFupdesc.getText();
            double prix = Double.parseDouble(TFupprix.getText());
            Restaurant selectedRestaurant = TFupnomresto.getValue();

            menu.setName(name);
            menu.setDescription(desc);
            menu.setPrix((int) prix);
            menu.setRestaurant(selectedRestaurant);

            menuService.update(menu);
            showAlert(AlertType.INFORMATION, "Succès", "Menu modifié avec succès !");
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur de format", "Le prix doit être un nombre valide.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Échec de la mise à jour du menu : " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
