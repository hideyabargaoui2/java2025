package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import models.Menu;
import models.Restaurant;
import services.MenuService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherMenu {

    @FXML private TableView<Menu> tableMenus;
    @FXML private TableColumn<Menu, String> colRestaurant;
    @FXML private TableColumn<Menu, String> colPlat;
    @FXML private TableColumn<Menu, String> colPrix;
    @FXML private TableColumn<Menu, String> colDescription;
    @FXML private TableColumn<Menu, Void> colActions;
    @FXML private ImageView logoImage;
    @FXML private TextField searchField;

    private final MenuService menuService = new MenuService();
    private ObservableList<Menu> allMenus = FXCollections.observableArrayList();

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void ajouterMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterMenu.fxml"));
            Parent root = loader.load();
            Scene scene = colPlat.getTableView().getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Impossible d'ouvrir la fenêtre d'ajout.");
        }
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadMenus();
        addActionButtonsToTable();
        setupSearch();

        Circle circle = new Circle(40, 40, 40);
        logoImage.setClip(circle);
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
            allMenus.setAll(menus);
            tableMenus.setItems(allMenus);
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
                    openUpdateWindow(selectedMenu);
                });

                btnSupprimer.setOnAction(event -> {
                    Menu selectedMenu = getTableView().getItems().get(getIndex());
                    menuService.delete(selectedMenu);
                    loadMenus();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10, btnModifier, btnSupprimer);
                    setGraphic(container);
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
            stage.setOnHidden(e -> loadMenus());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Impossible d'ouvrir la fenêtre de modification.");
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                tableMenus.setItems(allMenus);
            } else {
                String lower = newValue.toLowerCase();
                ObservableList<Menu> filtered = FXCollections.observableArrayList(
                        allMenus.stream()
                                .filter(menu -> menu.getName().toLowerCase().contains(lower)
                                        || menu.getDescription().toLowerCase().contains(lower)
                                        || (menu.getRestaurant() != null &&
                                        menu.getRestaurant().getNom().toLowerCase().contains(lower)))
                                .collect(Collectors.toList()));
                tableMenus.setItems(filtered);
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
