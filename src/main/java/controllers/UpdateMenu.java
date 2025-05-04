package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Menu;
import models.Restaurant;
import services.MenuService;
import services.RestaurantServices;

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
        // Initialisez ici le ComboBox avec les restaurants
        TFupnomresto.setItems(FXCollections.observableArrayList(restaurantService.afficher()));
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        if (menu != null) {
            TFupname.setText(menu.getName());
            TFupdesc.setText(menu.getDescription());
            TFupprix.setText(String.valueOf(menu.getPrix()));
            TFupnomresto.setValue(menu.getRestaurant());
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun menu sélectionné pour la modification.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void modifierMenu(ActionEvent event) {
        String newName = TFupname.getText();
        String newDescription = TFupdesc.getText();
        String newPrix = TFupprix.getText();
        Restaurant newRestaurant = TFupnomresto.getValue();

        // Mettre à jour le menu avec les nouvelles valeurs
        menu.setName(newName);
        menu.setDescription(newDescription);
        menu.setPrix((int) Double.parseDouble(newPrix));
        menu.setRestaurant(newRestaurant);

        // Sauvegarder la mise à jour dans la base de données
        menuService.update(menu);

        // Fermer la fenêtre de mise à jour
        Stage stage = (Stage) TFupname.getScene().getWindow();
        stage.close();
    }

}
