package controllers;

import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import models.Restaurant;
import services.RestaurantServices;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AfficherRestaurant implements Initializable {

    @FXML
    private TableView<Restaurant> table;
    @FXML
    private TableColumn<Restaurant, String> colNom;
    @FXML
    private TableColumn<Restaurant, String> colAdresse;
    @FXML
    private TableColumn<Restaurant, String> colType;
    @FXML
    private TableColumn<Restaurant, String> colClassement;
    @FXML
    private TableColumn<Restaurant, String> colHoraireOuvert;
    @FXML
    private TableColumn<Restaurant, String> colHoraireFerme;
    @FXML
    private TableColumn<Restaurant, String> colModifier;
    @FXML
    private TableColumn<Restaurant, String> colSupprimer;
    @FXML
    private TableColumn<Restaurant, String> colMap;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;

    private final RestaurantServices service = new RestaurantServices();
    private final ObservableList<Restaurant> masterData = FXCollections.observableArrayList();
    private final static int ROWS_PER_PAGE = 10;
    private FilteredList<Restaurant> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colClassement.setCellValueFactory(new PropertyValueFactory<>("classement"));
        colHoraireOuvert.setCellValueFactory(new PropertyValueFactory<>("horaireOuvert"));
        colHoraireFerme.setCellValueFactory(new PropertyValueFactory<>("horaireFerme"));

        initActions();
        afficherRestaurants();
    }

    private void initActions() {
        colModifier.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    Restaurant r = getTableView().getItems().get(getIndex());
                    modifierRestaurant(r);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        colSupprimer.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    Restaurant r = getTableView().getItems().get(getIndex());
                    supprimerRestaurant(r);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        colMap.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("📍");

            {
                btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    Restaurant r = getTableView().getItems().get(getIndex());
                    String adresse = r.getAdresse();
                    RestaurantMapService mapService = new RestaurantMapService();
                    mapService.showMap(adresse);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(restaurant -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lower = newValue.toLowerCase();
                return restaurant.getNom().toLowerCase().contains(lower)
                        || restaurant.getAdresse().toLowerCase().contains(lower)
                        || restaurant.getType().toLowerCase().contains(lower);
            });
            updatePagination();
        });
    }

    private void afficherRestaurants() {
        masterData.setAll(service.afficher());
        filteredData = new FilteredList<>(masterData, p -> true);
        updatePagination();
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredData.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());

        SortedList<Restaurant> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex))
        );
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        // Forcer la mise à jour des colonnes personnalisées
        colModifier.setVisible(false);
        colModifier.setVisible(true);
        colSupprimer.setVisible(false);
        colSupprimer.setVisible(true);
        colMap.setVisible(false);
        colMap.setVisible(true);

        return new Region();
    }

    @FXML
    void ajouterRestaurant(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();
            Scene scene = colNom.getTableView().getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
