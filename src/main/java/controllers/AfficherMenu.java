package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Menu;
import models.Restaurant;
import services.MenuService;

import java.io.IOException;
import java.util.List;

public class AfficherMenu {

    @FXML private TableView<Menu> tableMenus;
    @FXML private TableColumn<Menu, String> colRestaurant;
    @FXML private TableColumn<Menu, String> colPlat;
    @FXML private TableColumn<Menu, String> colPrix;
    @FXML private TableColumn<Menu, String> colDescription;
    @FXML private TableColumn<Menu, Void> colActions;

    private final MenuService menuService = new MenuService();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadMenus();
        addActionButtonsToTable();
    }

    private void setupTableColumns() {
        colRestaurant.setCellValueFactory(cellData -> {
            Restaurant restaurant = cellData.getValue().getRestaurant();
            return new SimpleStringProperty(restaurant != null ? restaurant.getNom() : "Inconnu");
        });
        colPlat.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colPrix.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrix())));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    }

    private void loadMenus() {
        try {
            List<Menu> menus = menuService.getAll();
            ObservableList<Menu> observableMenus = FXCollections.observableArrayList(menus);
            tableMenus.setItems(observableMenus);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les menus.", e.getMessage());
        }
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                btnModifier.setOnAction(event -> {
                    Menu selectedMenu = getTableView().getItems().get(getIndex());
                    openUpdateWindow(selectedMenu);  // Méthode pour ouvrir la fenêtre de modification
                });

                btnSupprimer.setOnAction(event -> {
                    Menu selectedMenu = getTableView().getItems().get(getIndex());
                    menuService.delete(selectedMenu);  // Méthode pour supprimer le menu
                    loadMenus();  // Recharger la table
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10, btnModifier, btnSupprimer);
                    setGraphic(container);  // Afficher les boutons Modifier et Supprimer dans chaque ligne
                }
            }
        });
    }

    private void openUpdateWindow(Menu selectedMenu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateMenu.fxml"));
            Parent root = loader.load();

            UpdateMenu updateMenuController = loader.getController();
            updateMenuController.setMenu(selectedMenu);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Recharger les menus après la fermeture de la fenêtre de modification
            stage.setOnHidden(e -> loadMenus());  // Recharge les menus après la mise à jour
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Impossible d'ouvrir la fenêtre de modification.");
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
