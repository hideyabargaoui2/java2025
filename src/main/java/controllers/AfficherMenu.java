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
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Pagination;

public class AfficherMenu {

    @FXML private TableView<Menu> tableMenus;
    @FXML private TableColumn<Menu, String> colRestaurant;
    @FXML private TableColumn<Menu, String> colPlat;
    @FXML private TableColumn<Menu, String> colPrix;
    @FXML private TableColumn<Menu, String> colDescription;
    @FXML private TableColumn<Menu, Void> colActions;
    @FXML private ImageView logoImage;
    @FXML private TextField searchField;
    @FXML private TextField prixmax;
    @FXML private TextField prixmin;
    @FXML private Pagination pagination;

    private final MenuService menuService = new MenuService();
    private ObservableList<Menu> allMenus = FXCollections.observableArrayList();

    private FilteredList<Menu> filteredMenus;
    private static final int ROWS_PER_PAGE = 10;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadMenus();
        addActionButtonsToTable();
        setupFilters();
        setupPagination();

        Circle circle = new Circle(40, 40, 40);
        logoImage.setClip(circle);
    }

    private void setupTableColumns() {
        colRestaurant.setCellValueFactory(cellData -> {
            Restaurant r = cellData.getValue().getRestaurant();
            return new SimpleStringProperty(r != null ? r.getNom() : "Inconnu");
        });
        colPlat.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colPrix.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrix())));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        colPrix.setSortType(TableColumn.SortType.ASCENDING);
        tableMenus.getSortOrder().add(colPrix);

    }

    private void loadMenus() {
        try {
            List<Menu> menus = menuService.getAll();
            menus.sort(Comparator.comparingDouble(Menu::getPrix));
            allMenus.setAll(menus);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les menus.", e.getMessage());
        }
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnModifier.setStyle("-fx-background-color: #3E8E41; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #3E8E41; -fx-text-fill: white;");

                btnModifier.setOnAction(event -> {
                    Menu selectedMenu = getTableView().getItems().get(getIndex());
                    openUpdateWindow(selectedMenu);
                });

                btnSupprimer.setOnAction(event -> {
                    Menu selectedMenu = getTableView().getItems().get(getIndex());
                    menuService.delete(selectedMenu);
                    refreshData();
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
            stage.setOnHidden(e -> refreshData());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Impossible d'ouvrir la fenêtre de modification.");
        }
    }

    private void setupFilters() {
        filteredMenus = new FilteredList<>(allMenus, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        prixmin.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        prixmax.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        filteredMenus.setPredicate(menu -> {
            String searchText = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
            boolean matchesSearch = searchText.isEmpty()
                    || menu.getName().toLowerCase().contains(searchText)
                    || menu.getDescription().toLowerCase().contains(searchText)
                    || (menu.getRestaurant() != null && menu.getRestaurant().getNom().toLowerCase().contains(searchText));

            if (!matchesSearch) return false;

            double prix = menu.getPrix();

            double minVal = 0;
            double maxVal = Double.MAX_VALUE;

            try {
                if (!prixmin.getText().trim().isEmpty()) {
                    minVal = Double.parseDouble(prixmin.getText().trim());
                }
                if (!prixmax.getText().trim().isEmpty()) {
                    maxVal = Double.parseDouble(prixmax.getText().trim());
                }
            } catch (NumberFormatException e) {
                return false;
            }

            return prix >= minVal && prix <= maxVal;
        });

        updatePagination();
    }

    private void setupPagination() {
        pagination.setPageFactory(this::createPage);
        updatePagination();
    }

    private Node createPage(int pageIndex) {
        SortedList<Menu> sortedMenus = new SortedList<>(filteredMenus);
        sortedMenus.comparatorProperty().bind(tableMenus.comparatorProperty());

        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, sortedMenus.size());

        tableMenus.setItems(FXCollections.observableArrayList(sortedMenus.subList(fromIndex, toIndex)));
        return tableMenus;
    }


    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredMenus.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);

        int currentPage = pagination.getCurrentPageIndex();
        if (currentPage >= pageCount) {
            currentPage = pageCount - 1;
        }
        if (currentPage < 0) {
            currentPage = 0;
        }
        pagination.setCurrentPageIndex(currentPage);
        createPage(currentPage);
    }

    private void refreshData() {
        loadMenus();
        applyFilters();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
    private void closeWindow(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
