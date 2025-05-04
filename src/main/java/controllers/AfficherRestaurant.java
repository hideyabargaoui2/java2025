package controllers;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Restaurant;
import services.RestaurantServices;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AfficherRestaurant implements Initializable {

    @FXML private TableView<Restaurant> table;
    @FXML private TableColumn<Restaurant, String> colNom;
    @FXML private TableColumn<Restaurant, String> colAdresse;
    @FXML private TableColumn<Restaurant, String> colType;
    @FXML private TableColumn<Restaurant, Integer> colClassement;
    @FXML private TableColumn<Restaurant, Void> colModifier;
    @FXML private TableColumn<Restaurant, Void> colSupprimer;
    @FXML private TableColumn<Restaurant, String> colHoraireOuvert;
    @FXML private TableColumn<Restaurant, String> colHoraireFerme;

    private final RestaurantServices service = new RestaurantServices();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colClassement.setCellValueFactory(new PropertyValueFactory<>("classement"));
        colHoraireOuvert.setCellValueFactory(new PropertyValueFactory<>("horaireOuvert"));

        colHoraireFerme.setCellValueFactory(new PropertyValueFactory<>("horaireFerme"));

        colModifier.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Modifier");
            {
                btn.setOnAction(event -> {
                    Restaurant r = getTableView().getItems().get(getIndex());
                    modifierRestaurant(r);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        colSupprimer.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Supprimer");
            {
                btn.setOnAction(event -> {
                    Restaurant r = getTableView().getItems().get(getIndex());
                    supprimerRestaurant(r);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        afficherRestaurants();
    }

    @FXML
    void ajouterRestaurant(ActionEvent event) {
        System.out.println("Bouton Ajouter Restaurant cliqué !");
        // Code pour ouvrir le formulaire d’ajout ici
    }

    private void afficherRestaurants() {
        ObservableList<Restaurant> listRestaurants = FXCollections.observableArrayList(service.afficher());
        table.setItems(listRestaurants);
    }

    private void modifierRestaurant(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateRestaurant.fxml"));

            Parent root = loader.load();
            UpdateRestaurant controller = loader.getController();
            controller.setRestaurant(restaurant);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            afficherRestaurants();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerRestaurant(Restaurant restaurant) {
        service.supprimer(restaurant.getId());
        afficherRestaurants();
    }
}
