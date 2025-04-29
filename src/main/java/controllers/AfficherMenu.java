package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Menu;
import services.MenuService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherMenu {
    @FXML
    private Button btnUpdateMenu;
    @FXML
    private TableView<Menu> tableMenus;

    @FXML
    private TableColumn<Menu, Integer> colRestaurant;

    @FXML
    private TableColumn<Menu, String> colPlat;

    @FXML
    private TableColumn<Menu, Double> colPrix;

    @FXML
    private TableColumn<Menu, String> colDescription;

    private final MenuService menuService = new MenuService();

    @FXML
    public void initialize() {
        colRestaurant.setCellValueFactory(new PropertyValueFactory<>("restaurant"));
        colPlat.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadMenus();
    }

    private void loadMenus() {
        try {
            List<Menu> menuList = menuService.getAll();
            ObservableList<Menu> menus = FXCollections.observableArrayList(menuList);
            tableMenus.setItems(menus);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void versAfficherMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Afficher Menu");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur ouverture AfficherMenu : " + e.getMessage());
        }
    }

    @FXML
    void goToUpdateMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mise à jour du Menu");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
