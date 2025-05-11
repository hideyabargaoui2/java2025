package controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Transport;
import services.TransportService;
import utils.Maconnexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AfficherTransportController {
    private final TransportService transportService = new TransportService();

    @FXML private TableView<Transport> tableView;
    @FXML private TableColumn<Transport, String> typeCol;
    @FXML private TableColumn<Transport, String> compagnieCol;
    @FXML private TableColumn<Transport, Double> prixCol;
    @FXML private TableColumn<Transport, Void> actionCol;
    @FXML private Button btnAjouter;
    @FXML private VBox contentArea;
    @FXML private BorderPane mainContainer;
    @FXML private StackPane logoContainer;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private static final int ROWS_PER_PAGE = 5;
    private ObservableList<Transport> allTransports;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur AfficherTransportController");

        // Vérifier la connexion à la base de données avant d'initialiser
        verifyDatabaseConnection();

        // Appliquer les styles CSS spécifiques des composants
        applyCustomStyles();

        // Configure columns
        try {
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            compagnieCol.setCellValueFactory(new PropertyValueFactory<>("compagnie"));
            prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));

            // Formatter les prix pour afficher le symbole €
            prixCol.setCellFactory(column -> new TableCell<Transport, Double>() {
                @Override
                protected void updateItem(Double prix, boolean empty) {
                    super.updateItem(prix, empty);
                    if (empty || prix == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f €", prix));
                    }
                }
            });

            // Configure action column
            setupActionColumn();

            // Configure pagination
            setupPagination();

            // Configure search
            setupSearch();

            // Set up logo animation if logoContainer is not null
            if (logoContainer != null) {
                setupLogoAnimation();
            }

            // Play entrance animation for better UX
            playEntranceAnimation();

            // Try to load data safely
            try {
                // Load data with animation for a more dynamic feel
                loadDataWithAnimation();
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des données: " + e.getMessage());
                e.printStackTrace();

                // Afficher une alerte en cas d'erreur
                Platform.runLater(() -> {
                    Alert alert = createStyledAlert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de base de données");
                    alert.setHeaderText("Impossible de charger les transports");
                    alert.setContentText("Erreur: " + e.getMessage() + "\n\nVérifiez votre connexion à la base de données.");
                    alert.showAndWait();
                });

                // Essayer de charger une liste vide pour éviter les erreurs d'interface
                tableView.setItems(FXCollections.observableArrayList());
            }

        } catch (Exception e) {
            System.err.println("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Applique des styles spécifiques pour une meilleure intégration avec le CSS
     */
    private void applyCustomStyles() {
        // Assurer que le tableView a toutes les classes CSS nécessaires
        if (tableView != null) {
            tableView.getStyleClass().add("transparent-table");
        }

        // Ajouter les classes de style au conteneur principal
        if (mainContainer != null) {
            mainContainer.getStyleClass().add("transparent-container");
        }

        // Ajouter des styles aux boutons
        if (btnAjouter != null) {
            btnAjouter.getStyleClass().add("action-button");
        }

        // Conteneur du logo
        if (logoContainer != null) {
            logoContainer.getStyleClass().add("logo-container");
        }

        // Zone de contenu
        if (contentArea != null) {
            contentArea.getStyleClass().add("content-area-transparent");
        }

        // Si on a un pagination, ajouter la classe appropriée
        if (pagination != null) {
            pagination.getStyleClass().add("transparent-pagination");
        }
    }

    /**
     * Configure la recherche
     */
    private void setupSearch() {
        // Trouver le champ de recherche s'il existe dans le FXML
        searchField = (TextField) lookup(".search-field-transparent");

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (allTransports != null) {
                    if (newValue == null || newValue.isEmpty()) {
                        updatePaginationData(allTransports);
                    } else {
                        String searchText = newValue.toLowerCase();
                        ObservableList<Transport> filteredList = allTransports.filtered(transport ->
                                transport.getType().toLowerCase().contains(searchText) ||
                                        transport.getCompagnie().toLowerCase().contains(searchText) ||
                                        String.valueOf(transport.getPrix()).contains(searchText)
                        );
                        updatePaginationData(filteredList);
                    }
                }
            });
        }
    }

    /**
     * Trouve un nœud par classe CSS
     */
    private Node lookup(String cssClass) {
        if (mainContainer != null) {
            return mainContainer.lookup(cssClass);
        }
        return null;
    }

    /**
     * Configure la pagination
     */
    private void setupPagination() {
        // Rechercher la pagination dans le FXML
        pagination = (Pagination) lookup(".transparent-pagination");
        if (pagination != null) {
            pagination.setPageFactory(this::createPage);
            pagination.setPageCount(1); // Par défaut
        }
    }

    /**
     * Crée une page pour la pagination
     */
    private Node createPage(int pageIndex) {
        if (allTransports == null || allTransports.isEmpty()) {
            return new VBox(); // Page vide
        }

        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, allTransports.size());

        ObservableList<Transport> pageData = FXCollections.observableArrayList();
        for (int i = fromIndex; i < toIndex; i++) {
            pageData.add(allTransports.get(i));
        }

        tableView.setItems(pageData);
        return tableView;
    }

    /**
     * Met à jour les données de pagination
     */
    private void updatePaginationData(ObservableList<Transport> data) {
        if (pagination != null) {
            int pageCount = (data.size() / ROWS_PER_PAGE) + ((data.size() % ROWS_PER_PAGE > 0) ? 1 : 0);
            pageCount = Math.max(1, pageCount);
            pagination.setPageCount(pageCount);
            pagination.setCurrentPageIndex(0);

            // Stocker les données et créer la première page
            allTransports = data;
            createPage(0);
        } else {
            // Si pas de pagination, juste mettre à jour les données du tableau
            tableView.setItems(data);
        }
    }

    /**
     * Vérifie la connexion à la base de données
     */
    private void verifyDatabaseConnection() {
        try {
            Connection connection = Maconnexion.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                System.err.println("La connexion à la base de données est nulle ou fermée");

                // Try to reconnect
                Maconnexion.getInstance().getConnection();
            } else {
                System.out.println("Connexion à la base de données vérifiée avec succès");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Animation d'entrée pour toute l'interface
     */
    private void playEntranceAnimation() {
        // Animation du conteneur principal
        if (mainContainer == null) {
            System.out.println("Le conteneur principal est null, animation ignorée");
            return;
        }

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(800), mainContainer);
        scaleIn.setFromX(0.95);
        scaleIn.setFromY(0.95);
        scaleIn.setToX(1);
        scaleIn.setToY(1);

        TranslateTransition translateIn = new TranslateTransition(Duration.millis(800), mainContainer);
        translateIn.setFromY(10);
        translateIn.setToY(0);

        // Animation du contenu
        if (contentArea != null) {
            FadeTransition contentFade = new FadeTransition(Duration.millis(800), contentArea);
            contentFade.setFromValue(0);
            contentFade.setToValue(1);
            contentFade.setDelay(Duration.millis(300));

            // Jouer les animations ensemble
            ParallelTransition parallelTransition = new ParallelTransition(
                    fadeIn, scaleIn, translateIn, contentFade
            );

            parallelTransition.play();
        } else {
            // Jouer les animations sans le contentArea
            ParallelTransition parallelTransition = new ParallelTransition(
                    fadeIn, scaleIn, translateIn
            );

            parallelTransition.play();
        }
    }

    /**
     * Configure l'animation du logo
     */
    private void setupLogoAnimation() {
        // Animation du logo au survol
        logoContainer.setOnMouseEntered(e -> {
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(300), logoContainer);
            rotateTransition.setToAngle(10);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), logoContainer);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), logoContainer);
            fadeTransition.setToValue(0.9);

            ParallelTransition parallelTransition = new ParallelTransition(
                    rotateTransition, scaleTransition, fadeTransition
            );

            parallelTransition.play();
        });

        logoContainer.setOnMouseExited(e -> {
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(300), logoContainer);
            rotateTransition.setToAngle(0);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), logoContainer);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), logoContainer);
            fadeTransition.setToValue(1.0);

            ParallelTransition parallelTransition = new ParallelTransition(
                    rotateTransition, scaleTransition, fadeTransition
            );

            parallelTransition.play();
        });
    }

    /**
     * Helper method to load resources properly
     * @param resourcePath The path to the resource (starting with /)
     * @return InputStream for the resource or null if not found
     */
    private InputStream getResourceAsStream(String resourcePath) {
        // Try different approaches to load the resource

        // 1. Try with class.getResourceAsStream (absolute path)
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream != null) {
            System.out.println("Resource found with absolute path: " + resourcePath);
            return stream;
        }

        // 2. Try with class.getResourceAsStream (relative path)
        if (!resourcePath.startsWith("/")) {
            stream = getClass().getResourceAsStream("/" + resourcePath);
            if (stream != null) {
                System.out.println("Resource found with prepended slash: /" + resourcePath);
                return stream;
            }
        }

        // 3. Try with ClassLoader.getResourceAsStream
        stream = getClass().getClassLoader().getResourceAsStream(resourcePath.startsWith("/") ?
                resourcePath.substring(1) : resourcePath);
        if (stream != null) {
            System.out.println("Resource found with ClassLoader: " +
                    (resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath));
            return stream;
        }

        // 4. Try with Thread's ContextClassLoader
        stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
        if (stream != null) {
            System.out.println("Resource found with Thread ContextClassLoader");
            return stream;
        }

        // Resource not found with any method
        System.err.println("Resource not found: " + resourcePath);
        return null;
    }

    /**
     * Configure action column with clean buttons that match the CSS style
     */
    private void setupActionColumn() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            final Button btnEdit = new Button("Modifier");
            final Button btnDelete = new Button("Supprimer");
            final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                // Appliquer les styles CSS du fichier de style
                btnEdit.getStyleClass().add("action-button");
                btnDelete.getStyleClass().add("action-button");

                // Style spécifique pour le bouton delete
                btnDelete.setStyle("-fx-background-color: #f44336;");

                pane.setAlignment(javafx.geometry.Pos.CENTER);

                // Essayer de charger les icônes de plusieurs façons possibles
                try {
                    // Liste des chemins possibles pour les icônes
                    String[] editPaths = {
                            "/icons/edit.png",
                            "icons/edit.png",
                            "../icons/edit.png",
                            "/resources/icons/edit.png"
                    };

                    String[] deletePaths = {
                            "/icons/delete.png",
                            "icons/delete.png",
                            "../icons/delete.png",
                            "/resources/icons/delete.png"
                    };

                    // Essayer chaque chemin pour l'icône edit
                    Image editImage = null;
                    for (String path : editPaths) {
                        InputStream stream = getResourceAsStream(path);
                        if (stream != null) {
                            try {
                                editImage = new Image(stream, 20, 20, true, true);
                                System.out.println("Icône edit chargée depuis: " + path);
                                break;
                            } catch (Exception e) {
                                System.err.println("Erreur chargement de l'image " + path + ": " + e.getMessage());
                            }
                        }
                    }

                    // Essayer chaque chemin pour l'icône delete
                    Image deleteImage = null;
                    for (String path : deletePaths) {
                        InputStream stream = getResourceAsStream(path);
                        if (stream != null) {
                            try {
                                deleteImage = new Image(stream, 20, 20, true, true);
                                System.out.println("Icône delete chargée depuis: " + path);
                                break;
                            } catch (Exception e) {
                                System.err.println("Erreur chargement de l'image " + path + ": " + e.getMessage());
                            }
                        }
                    }

                    // Appliquer les icônes si elles ont été chargées
                    if (editImage != null) {
                        btnEdit.setGraphic(new ImageView(editImage));
                    }

                    if (deleteImage != null) {
                        btnDelete.setGraphic(new ImageView(deleteImage));
                    }
                } catch (Exception e) {
                    System.err.println("Impossible de charger les icônes, utilisation du texte à la place: " + e);
                    e.printStackTrace();
                    // Laisser les boutons avec texte
                }

                // Définir les actions des boutons
                btnEdit.setOnAction(e -> {
                    if (getTableRow() != null) {
                        Transport transport = getTableView().getItems().get(getIndex());
                        System.out.println("Modifier : " + transport);
                        ouvrirModifierTransport(transport);
                    }
                });

                btnDelete.setOnAction(e -> {
                    if (getTableRow() != null) {
                        Transport transport = getTableView().getItems().get(getIndex());
                        System.out.println("Supprimer : " + transport);

                        // Demander confirmation avant de supprimer
                        Alert alert = createStyledAlert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation de suppression");
                        alert.setHeaderText("Suppression d'un transport");
                        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce transport ?");

                        alert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                transportService.supprimer(transport);
                                loadData(); // Rafraîchir la table
                                afficherToast("Transport supprimé avec succès", "success");
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    /**
     * Crée une alerte stylisée pour correspondre au thème
     */
    private Alert createStyledAlert(Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);

        // Obtenir la scène de l'alerte
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        // Ajouter le CSS à l'alerte
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        // Classes CSS spécifiques pour l'alerte
        alert.getDialogPane().getStyleClass().add("transparent-container");

        return alert;
    }

    /**
     * Méthode pour ouvrir la fenêtre de modification d'un transport
     * @param transport Le transport à modifier
     */
    private void ouvrirModifierTransport(Transport transport) {
        try {
            // Journalisation pour le débogage
            System.out.println("Tentative d'ouverture de la fenêtre de modification pour le transport: " + transport.getId());

            // Liste des chemins possibles pour le fichier FXML
            String[] possiblePaths = {
                    "modifierTransport.fxml",
                    "/modifierTransport.fxml",
                    "../modifierTransport.fxml",
                    "/views/modifierTransport.fxml",
                    "/fxml/modifierTransport.fxml"
            };

            FXMLLoader loader = null;

            // Essayer chaque chemin jusqu'à ce qu'un fonctionne
            for (String path : possiblePaths) {
                try {
                    loader = new FXMLLoader(getClass().getResource(path));
                    System.out.println("Tentative de chargement avec: " + path);
                    if (loader.getLocation() != null) {
                        System.out.println("Fichier FXML trouvé à: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Erreur avec le chemin " + path + ": " + e.getMessage());
                }
            }

            // Vérifier si on a bien pu charger le fichier
            if (loader == null || loader.getLocation() == null) {
                throw new IOException("Impossible de localiser le fichier FXML pour la modification");
            }

            // Charger le contenu du fichier FXML
            Parent root = loader.load();
            System.out.println("FXML chargé avec succès!");

            // Récupérer le contrôleur et lui passer les données nécessaires
            ModifierTransportController controller = loader.getController();
            controller.setTransport(transport);
            controller.setParentController(this);

            // Appliquer le même CSS
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            // Créer et configurer la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier un transport");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); // Pour bloquer l'interaction avec la fenêtre principale

            // Afficher la fenêtre
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
            e.printStackTrace();

            // Afficher une alerte en cas d'erreur
            Alert alert = createStyledAlert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Méthode pour naviguer vers l'interface des trajets avec une transition animée
     */
    @FXML
    public void naviguerVersTrajet() {
        try {
            System.out.println("Navigation vers l'interface de gestion des trajets");

            // Liste des chemins possibles pour le fichier FXML
            String[] possiblePaths = {
                    "afficherTrajet.fxml",
                    "/afficherTrajet.fxml",
                    "../afficherTrajet.fxml",
                    "/views/afficherTrajet.fxml",
                    "/fxml/afficherTrajet.fxml"
            };

            FXMLLoader loader = null;

            // Essayer chaque chemin jusqu'à ce qu'un fonctionne
            for (String path : possiblePaths) {
                try {
                    loader = new FXMLLoader(getClass().getResource(path));
                    System.out.println("Tentative de chargement avec: " + path);
                    if (loader.getLocation() != null) {
                        System.out.println("Fichier FXML trouvé à: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Erreur avec le chemin " + path + ": " + e.getMessage());
                }
            }

            // Vérifier si on a bien pu charger le fichier
            if (loader == null || loader.getLocation() == null) {
                throw new IOException("Impossible de localiser le fichier FXML pour la gestion des trajets");
            }

            // Récupérer le conteneur parent actuel pour l'animation
            StackPane rootContainer = (StackPane) mainContainer.getScene().getRoot();

            // Charger la nouvelle vue
            Parent nouveauContenu = loader.load();

            // S'assurer que le nouveau contenu utilise le même CSS
            nouveauContenu.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            // Créer une transition pour déplacer le contenu actuel hors de l'écran
            TranslateTransition sortieActuelle = new TranslateTransition(Duration.millis(300), mainContainer);
            sortieActuelle.setFromX(0);
            sortieActuelle.setToX(-rootContainer.getWidth());

            // Préparation de la nouvelle vue
            nouveauContenu.setTranslateX(rootContainer.getWidth());

            // Ajouter le nouveau contenu à côté de l'actuel
            rootContainer.getChildren().add(nouveauContenu);

            // Créer une transition pour faire entrer le nouveau contenu
            TranslateTransition entreeNouvelle = new TranslateTransition(Duration.millis(300), nouveauContenu);
            entreeNouvelle.setFromX(rootContainer.getWidth());
            entreeNouvelle.setToX(0);

            // Jouer les deux transitions en parallèle
            ParallelTransition transition = new ParallelTransition(sortieActuelle, entreeNouvelle);

            // Nettoyer après la transition
            transition.setOnFinished(event -> {
                // Remplacer la scène complètement
                Scene scene = mainContainer.getScene();
                Stage stage = (Stage) scene.getWindow();

                Scene nouvelleScene = new Scene(nouveauContenu, scene.getWidth(), scene.getHeight());
                nouvelleScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

                stage.setScene(nouvelleScene);

                // Nettoyer l'ancienne vue
                rootContainer.getChildren().remove(mainContainer);
            });

            // Lancer la transition
            transition.play();

        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation vers la gestion des trajets: " + e.getMessage());
            e.printStackTrace();

            // Afficher une alerte en cas d'erreur
            Alert alert = createStyledAlert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setHeaderText("Impossible d'afficher la gestion des trajets");
            alert.setContentText("Erreur: " + e.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Charge les données avec animation
     */
    private void loadDataWithAnimation() {
        try {
            // Récupérer les transports depuis le service
            List<Transport> transports = transportService.getA();
            System.out.println("Nombre de transports récupérés: " + transports.size());

            if (transports.isEmpty()) {
                System.out.println("Aucun transport trouvé dans la base de données");
                tableView.setItems(FXCollections.observableArrayList());
                return;
            }

            // Convertir en ObservableList pour la pagination
            allTransports = FXCollections.observableArrayList(transports);

            // Setup pagination with the data
            if (pagination != null) {
                updatePaginationData(allTransports);
            } else {
                // Animation pour charger les lignes
                tableView.setItems(FXCollections.observableArrayList());

                Timeline timeline = new Timeline();
                int delay = 100;

                for (int i = 0; i < transports.size(); i++) {
                    final int index = i;
                    KeyFrame keyFrame = new KeyFrame(Duration.millis(delay * i), event -> {
                        try {
                            ObservableList<Transport> currentItems = FXCollections.observableArrayList(tableView.getItems());
                            currentItems.add(transports.get(index));
                            tableView.setItems(currentItems);
                        } catch (Exception e) {
                            System.err.println("Erreur lors de l'ajout de la ligne " + index + ": " + e.getMessage());
                        }
                    });

                    timeline.getKeyFrames().add(keyFrame);
                }

                // Ajouter un écouteur pour animer les lignes après qu'elles sont rendues
                timeline.setOnFinished(event -> {
                    Platform.runLater(() -> {
                        for (int i = 0; i < tableView.getItems().size(); i++) {
                            try {
                                TableRow<Transport> row = findTableRow(i);
                                if (row != null) {
                                    FadeTransition ft = new FadeTransition(Duration.millis(300), row);
                                    ft.setFromValue(0.3);
                                    ft.setToValue(1);
                                    ft.play();
                                }
                            } catch (Exception e) {
                                System.err.println("Erreur d'animation pour la ligne " + i + ": " + e.getMessage());
                            }
                        }
                    });
                });

                timeline.play();
            }
        } catch (Exception e) {
            System.err.println("Erreur dans loadDataWithAnimation: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rethrow to be handled by the calling method
        }
    }

    /**
     * Helper method to find a table row by index
     */
    private TableRow<Transport> findTableRow(int index) {
        for (TableRow<Transport> row : tableView.lookupAll(".table-row-cell").stream()
                .filter(node -> node instanceof TableRow)
                .map(node -> (TableRow<Transport>) node)
                .toList()) {
            if (row.getIndex() == index) {
                return row;
            }
        }
        return null;
    }

    @FXML
    public void ajouterTransport() {
        System.out.println("Ajouter un transport cliqué !");
        try {
            // Liste des chemins possibles pour le fichier FXML
            String[] possiblePaths = {
                    "ajouterTransport.fxml",
                    "/ajouterTransport.fxml",
                    "../ajouterTransport.fxml",
                    "/views/ajouterTransport.fxml",
                    "/fxml/ajouterTransport.fxml"
            };

            FXMLLoader loader = null;

            // Essayer chaque chemin jusqu'à ce qu'un fonctionne
            for (String path : possiblePaths) {
                try {
                    loader = new FXMLLoader(getClass().getResource(path));
                    System.out.println("Tentative de chargement avec: " + path);
                    if (loader.getLocation() != null) {
                        System.out.println("Fichier FXML trouvé à: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Erreur avec le chemin " + path + ": " + e.getMessage());
                }
            }

            // Vérifier si on a bien pu charger le fichier
            if (loader == null || loader.getLocation() == null) {
                throw new IOException("Impossible de localiser le fichier FXML pour l'ajout");
            }

            Parent root = loader.load();

            // Appliquer le même CSS
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            // Si tu veux passer des infos au contrôleur, tu peux les obtenir ici :
            AjouterTransportController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un nouveau transport");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); // Pour bloquer la fenêtre principale
            stage.showAndWait(); // Attend la fermeture de la fenêtre

            // Recharger la table après l'ajout éventuel
            loadData();
            afficherToast("Transport ajouté avec succès", "success");

        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de la fenêtre d'ajout : " + e.getMessage());
            e.printStackTrace();

            Alert alert = createStyledAlert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir le formulaire");
            alert.setContentText("Erreur : " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Charge les données depuis la base de données et met à jour la TableView
     */
    public void loadData() {
        try {
            // Vérifier la connexion à la base de données
            verifyDatabaseConnection();

            // Créer une animation de fadout pour la table actuelle
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), tableView);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.5);

            fadeOut.setOnFinished(e -> {
                try {
                    // Récupérer les transports depuis le service
                    List<Transport> transports = transportService.getA();
                    System.out.println("Nombre de transports récupérés: " + transports.size());

                    // Mettre à jour la liste des transports
                    allTransports = FXCollections.observableArrayList(transports);

                    // Mettre à jour la pagination ou directement la table
                    if (pagination != null) {
                        updatePaginationData(allTransports);
                    } else {
                        tableView.setItems(allTransports);
                    }

                    // Animation de fade in de la nouvelle donnée
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), tableView);
                    fadeIn.setFromValue(0.5);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                } catch (Exception ex) {
                    System.err.println("Erreur lors du chargement des données: " + ex.getMessage());
                    ex.printStackTrace();

                    // Afficher une alerte en cas d'erreur
                    Platform.runLater(() -> {
                        Alert alert = createStyledAlert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur de données");
                        alert.setHeaderText("Impossible de charger les transports");
                        alert.setContentText("Erreur: " + ex.getMessage());
                        alert.showAndWait();
                    });
                }
            });

            // Exécuter l'animation de fadeout
            fadeOut.play();

        } catch (Exception e) {
            System.err.println("Erreur dans loadData: " + e.getMessage());
            e.printStackTrace();

            // Afficher une alerte en cas d'erreur
            Platform.runLater(() -> {
                Alert alert = createStyledAlert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors du chargement des données");
                alert.setContentText("Erreur: " + e.getMessage());
                alert.showAndWait();
            });
        }
    }
    /**
     * Méthode pour naviguer vers l'interface des réservations d'hôtels avec une transition animée
     */
    @FXML
    public void naviguerVersHotel() {
        try {
            System.out.println("Navigation vers l'interface de gestion des réservations d'hôtels");

            // Liste des chemins possibles pour le fichier FXML
            String[] possiblePaths = {
                    "afficherResHotel.fxml",
                    "/afficherResHotel.fxml",
                    "../afficherResHotel.fxml",
                    "/views/afficherResHotel.fxml",
                    "/fxml/afficherResHotel.fxml"
            };

            FXMLLoader loader = null;

            // Essayer chaque chemin jusqu'à ce qu'un fonctionne
            for (String path : possiblePaths) {
                try {
                    loader = new FXMLLoader(getClass().getResource(path));
                    System.out.println("Tentative de chargement avec: " + path);
                    if (loader.getLocation() != null) {
                        System.out.println("Fichier FXML trouvé à: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Erreur avec le chemin " + path + ": " + e.getMessage());
                }
            }

            // Vérifier si on a bien pu charger le fichier
            if (loader == null || loader.getLocation() == null) {
                throw new IOException("Impossible de localiser le fichier FXML pour la gestion des réservations d'hôtels");
            }

            // Récupérer le conteneur parent actuel pour l'animation
            StackPane rootContainer = (StackPane) mainContainer.getScene().getRoot();

            // Charger la nouvelle vue
            Parent nouveauContenu = loader.load();

            // S'assurer que le nouveau contenu utilise le même CSS
            nouveauContenu.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            // Créer une transition pour déplacer le contenu actuel hors de l'écran
            TranslateTransition sortieActuelle = new TranslateTransition(Duration.millis(300), mainContainer);
            sortieActuelle.setFromX(0);
            sortieActuelle.setToX(-rootContainer.getWidth());

            // Préparation de la nouvelle vue
            nouveauContenu.setTranslateX(rootContainer.getWidth());

            // Ajouter le nouveau contenu à côté de l'actuel
            rootContainer.getChildren().add(nouveauContenu);

            // Créer une transition pour faire entrer le nouveau contenu
            TranslateTransition entreeNouvelle = new TranslateTransition(Duration.millis(300), nouveauContenu);
            entreeNouvelle.setFromX(rootContainer.getWidth());
            entreeNouvelle.setToX(0);

            // Jouer les deux transitions en parallèle
            ParallelTransition transition = new ParallelTransition(sortieActuelle, entreeNouvelle);

            // Nettoyer après la transition
            transition.setOnFinished(event -> {
                // Remplacer la scène complètement
                Scene scene = mainContainer.getScene();
                Stage stage = (Stage) scene.getWindow();

                Scene nouvelleScene = new Scene(nouveauContenu, scene.getWidth(), scene.getHeight());
                nouvelleScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

                stage.setScene(nouvelleScene);

                // Nettoyer l'ancienne vue
                rootContainer.getChildren().remove(mainContainer);
            });

            // Lancer la transition
            transition.play();

        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation vers la gestion des réservations d'hôtels: " + e.getMessage());
            e.printStackTrace();

            // Afficher une alerte en cas d'erreur
            Alert alert = createStyledAlert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setHeaderText("Impossible d'afficher la gestion des réservations d'hôtels");
            alert.setContentText("Erreur: " + e.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Charge les données avec animation
    /**
     * Affiche un message toast animé
     * @param message Le message à afficher
     * @param type Le type de message (success, error, warning, info)
     */
    private void afficherToast(String message, String type) {
        try {
            // Créer le conteneur du toast
            StackPane toastContainer = new StackPane();
            toastContainer.setMaxWidth(300);
            toastContainer.setMinHeight(50);
            toastContainer.getStyleClass().add("toast");

            // Ajouter une classe selon le type
            if ("success".equals(type)) {
                toastContainer.getStyleClass().add("toast-success");
            } else if ("error".equals(type)) {
                toastContainer.getStyleClass().add("toast-error");
            } else if ("warning".equals(type)) {
                toastContainer.getStyleClass().add("toast-warning");
            } else {
                toastContainer.getStyleClass().add("toast-info");
            }

            // Appliquer un effet d'ombre
            toastContainer.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));

            // Créer un HBox pour contenir l'icône et le texte
            HBox toastContent = new HBox(10);
            toastContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Ajouter une icône selon le type
            ImageView icon = new ImageView();
            icon.setFitHeight(24);
            icon.setFitWidth(24);
            icon.setPreserveRatio(true);

            try {
                String iconPath = "/icons/info.png"; // Default icon

                if ("success".equals(type)) {
                    iconPath = "/icons/success.png";
                } else if ("error".equals(type)) {
                    iconPath = "/icons/error.png";
                } else if ("warning".equals(type)) {
                    iconPath = "/icons/warning.png";
                }

                // Essayer de charger l'icône
                InputStream iconStream = getResourceAsStream(iconPath);
                if (iconStream != null) {
                    icon.setImage(new Image(iconStream));
                    toastContent.getChildren().add(icon);
                }
            } catch (Exception e) {
                System.err.println("Impossible de charger l'icône pour le toast: " + e.getMessage());
                // Continuer sans icône
            }

            // Créer le texte du toast
            Label toastText = new Label(message);
            toastText.getStyleClass().add("toast-text");
            toastText.setWrapText(true);

            // Ajouter le texte au contenu
            toastContent.getChildren().add(toastText);

            // Ajouter le contenu au conteneur
            toastContainer.getChildren().add(toastContent);

            // Positionner le toast en bas de la fenêtre
            toastContainer.setTranslateY(50); // Caché initialement (sous l'écran)
            toastContainer.setOpacity(0);

            // Ajouter le toast à la scène
            if (mainContainer != null && mainContainer.getScene() != null) {
                StackPane root = new StackPane();
                root.setMouseTransparent(true);
                root.setPickOnBounds(false);
                root.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);
                root.setPadding(new javafx.geometry.Insets(0, 0, 20, 0));
                root.getChildren().add(toastContainer);

                // Ajouter le root à la scène comme overlay
                StackPane sceneRoot = (StackPane) mainContainer.getScene().getRoot();
                sceneRoot.getChildren().add(root);

                // Animation d'entrée
                TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), toastContainer);
                slideIn.setFromY(50);
                slideIn.setToY(0);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toastContainer);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);

                ParallelTransition animIn = new ParallelTransition(slideIn, fadeIn);

                // Animation de sortie
                TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), toastContainer);
                slideOut.setFromY(0);
                slideOut.setToY(50);

                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toastContainer);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);

                ParallelTransition animOut = new ParallelTransition(slideOut, fadeOut);

                // Séquence complète: entrée, pause, sortie
                animIn.setOnFinished(e -> {
                    PauseTransition pause = new PauseTransition(Duration.seconds(3));
                    pause.setOnFinished(event -> animOut.play());
                    pause.play();
                });

                animOut.setOnFinished(e -> {
                    // Retirer le toast de la scène
                    sceneRoot.getChildren().remove(root);
                });

                // Démarrer l'animation
                animIn.play();
            } else {
                System.err.println("Impossible d'afficher le toast: mainContainer ou scene est null");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du toast: " + e.getMessage());
            e.printStackTrace();
        }



    }}