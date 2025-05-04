package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Menu;
import models.Restaurant;
import services.MenuService;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AfficherMenu {

    @FXML
    private TableView<Menu> tableMenus;
    @FXML
    private TableColumn<Menu, String> colRestaurant;
    @FXML
    private TableColumn<Menu, String> colPlat;
    @FXML
    private TableColumn<Menu, String> colPrix;
    @FXML
    private TableColumn<Menu, String> colDescription;

    private final MenuService menuService = new MenuService();

    @FXML
    public void initialize() {
        try {
            // Récupérer tous les menus à partir du service
            List<Menu> menus = menuService.getAll();

            // Lier les colonnes à leurs propriétés respectives
            colRestaurant.setCellValueFactory(cellData -> {
                Menu menu = cellData.getValue();
                Restaurant restaurant = menu.getRestaurant(); // Obtenez le restaurant
                if (restaurant != null) {
                    return new SimpleStringProperty(restaurant.getNom()); // Si le restaurant est non nul, renvoyer son nom
                } else {
                    return new SimpleStringProperty("Inconnu"); // Sinon, afficher "Inconnu" ou une autre valeur par défaut
                }
            });
            colPlat.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getName()));
            colPrix.setCellValueFactory(cellData ->
                    new SimpleStringProperty(String.valueOf(cellData.getValue().getPrix())));
            colDescription.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDescription()));

            // Ajouter les menus à la TableView
            ObservableList<Menu> observableMenus = FXCollections.observableArrayList(menus);
            tableMenus.setItems(observableMenus);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement des menus : " + e.getMessage(), null);
        }
    }

    // Méthode pour ouvrir la fenêtre de mise à jour du menu
    @FXML
    private void goToUpdateMenu(ActionEvent event) {
        // Code pour naviguer vers la page de mise à jour du menu
        System.out.println("Naviguer vers la page de mise à jour du menu.");
    }

    // Méthode pour supprimer un menu sélectionné
    @FXML
    private void deleteMenu(ActionEvent event) {
        Menu selectedMenu = tableMenus.getSelectionModel().getSelectedItem();
        if (selectedMenu != null) {
            // Supprimer le menu de la base de données
            menuService.delete(selectedMenu);
            System.out.println("Menu supprimé !");

            // Supprimer le menu de la TableView
            tableMenus.getItems().remove(selectedMenu);
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun menu sélectionné", null, "Veuillez sélectionner un menu à supprimer.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
      // Votre TableView qui affiche les menus
    @FXML private Button modifierMenuButton;  // Bouton Modifier

    @FXML
    private void modifierMenu() {
        Menu selectedMenu = tableMenus.getSelectionModel().getSelectedItem();

        if (selectedMenu != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateMenu.fxml")); // vérifie bien le chemin ici
                Parent root = loader.load();

                UpdateMenu updateMenuController = loader.getController();
                updateMenuController.setMenu(selectedMenu);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Alerte", null, "Veuillez sélectionner un menu à modifier.");
        }
    }



}
