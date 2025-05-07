package controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Trajet;
import services.Trajetservice;
import utils.Maconnexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatistiquesController {

    private final Trajetservice trajetService = new Trajetservice();

    @FXML private BorderPane mainContainer;
    @FXML private VBox chartsContainer;
    @FXML private HBox filtersContainer;
    @FXML private ComboBox<String> periodeComboBox;
    @FXML private ComboBox<String> critereComboBox;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private Button applyFilterBtn;
    @FXML private StackPane logoContainer;
    @FXML private TabPane tabPane;
    @FXML private Label totalTrajetsLabel;
    @FXML private Label avgDurationLabel;
    @FXML private Label mostPopularDestLabel;
    @FXML private Label mostUsedTransportLabel;
    @FXML private GridPane kpiGrid;

    private ObservableList<Trajet> allTrajets;
    private Map<String, Integer> transportStats;
    private Map<String, Integer> destinationStats;
    private Map<String, Double> monthlyStats;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur StatistiquesController");

        // Vérifier la connexion à la base de données
        verifyDatabaseConnection();

        // Initialiser les composants UI
        setupUIComponents();

        // Appliquer les styles et animations
        applyCustomStyles();
        playEntranceAnimation();

        // Charger les données et générer les statistiques
        loadData();
    }

    private void setupUIComponents() {
        // Configurer les filtres
        periodeComboBox.setItems(FXCollections.observableArrayList(
                "Tous", "Aujourd'hui", "Cette semaine", "Ce mois", "Cette année", "Personnalisé"
        ));
        periodeComboBox.setValue("Tous");

        critereComboBox.setItems(FXCollections.observableArrayList(
                "Transport", "Destination", "Durée", "Multi-critères"
        ));
        critereComboBox.setValue("Multi-critères");

        // Configurer les date pickers (initialement cachés)
        dateDebut.setValue(LocalDate.now().minusMonths(1));
        dateFin.setValue(LocalDate.now());
        dateDebut.setVisible(false);
        dateFin.setVisible(false);

        // Listeners pour les filtres
        periodeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCustom = "Personnalisé".equals(newVal);
            dateDebut.setVisible(isCustom);
            dateFin.setVisible(isCustom);
        });

        // Bouton d'application des filtres
        applyFilterBtn.setOnAction(e -> applyFilters());
    }

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

    private void applyCustomStyles() {
        // Appliquer des styles aux composants
        if (mainContainer != null) {
            mainContainer.getStyleClass().add("transparent-container");
        }

        if (chartsContainer != null) {
            chartsContainer.getStyleClass().add("charts-container");
            chartsContainer.setSpacing(20);
            chartsContainer.setPadding(new Insets(15));
        }

        if (filtersContainer != null) {
            filtersContainer.getStyleClass().add("filters-container");
            filtersContainer.setSpacing(10);
            filtersContainer.setPadding(new Insets(10));
            filtersContainer.setAlignment(Pos.CENTER_LEFT);
        }

        if (applyFilterBtn != null) {
            applyFilterBtn.getStyleClass().add("action-button");
        }

        if (tabPane != null) {
            tabPane.getStyleClass().add("transparent-tab-pane");
        }

        if (kpiGrid != null) {
            kpiGrid.getStyleClass().add("kpi-grid");
            kpiGrid.setHgap(20);
            kpiGrid.setVgap(15);
            kpiGrid.setPadding(new Insets(15));
        }

        // Configure animation du logo si présent
        if (logoContainer != null) {
            setupLogoAnimation();
        }
    }

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

    private void playEntranceAnimation() {
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

        // Jouer les animations ensemble
        ParallelTransition parallelTransition = new ParallelTransition(
                fadeIn, scaleIn, translateIn
        );

        parallelTransition.play();
    }

    private void loadData() {
        try {
            System.out.println("Chargement des données pour les statistiques...");

            // Récupérer tous les trajets
            allTrajets = FXCollections.observableArrayList(trajetService.getA());
            System.out.println("Nombre de trajets récupérés: " + allTrajets.size());

            if (allTrajets.isEmpty()) {
                showNoDataMessage();
                return;
            }

            // Calculer les statistiques
            calculateStatistics(allTrajets);

            // Générer les graphiques
            generateCharts(allTrajets);

            // Mettre à jour les KPIs
            updateKPIs(allTrajets);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("Erreur de chargement", "Impossible de charger les données: " + e.getMessage());
        }
    }

    private void showNoDataMessage() {
        if (chartsContainer != null) {
            chartsContainer.getChildren().clear();

            Label noDataLabel = new Label("Aucune donnée disponible");
            noDataLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            noDataLabel.getStyleClass().add("no-data-label");

            chartsContainer.getChildren().add(noDataLabel);
            chartsContainer.setAlignment(Pos.CENTER);
        }
    }

    private void calculateStatistics(List<Trajet> trajets) {
        // Stats par type de transport
        transportStats = trajets.stream()
                .collect(Collectors.groupingBy(Trajet::getTransport, Collectors.summingInt(t -> 1)));

        // Stats par destination
        destinationStats = trajets.stream()
                .collect(Collectors.groupingBy(Trajet::getDestination, Collectors.summingInt(t -> 1)));

        // Stats mensuelles (exemple: moyenne de durée par mois)
        monthlyStats = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        trajets.forEach(trajet -> {
            String monthKey = trajet.getDate().toLocalDate().format(formatter);
            monthlyStats.merge(monthKey, (double) trajet.getDuree(), Double::sum);
        });

        // Calculer les moyennes pour chaque mois
        Map<String, Long> countPerMonth = trajets.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDate().toLocalDate().format(formatter),
                        Collectors.counting()
                ));

        countPerMonth.forEach((month, count) -> {
            if (monthlyStats.containsKey(month)) {
                monthlyStats.put(month, monthlyStats.get(month) / count);
            }
        });

        System.out.println("Statistiques calculées avec succès");
    }

    private void generateCharts(List<Trajet> trajets) {
        if (chartsContainer == null) return;

        chartsContainer.getChildren().clear();

        // 1. Graphique par type de transport (Camembert)
        PieChart transportChart = createTransportPieChart(transportStats);
        VBox transportChartBox = createChartBox("Répartition par type de transport", transportChart);

        // 2. Graphique par destination (Barres)
        BarChart<String, Number> destinationChart = createDestinationBarChart(destinationStats);
        VBox destinationChartBox = createChartBox("Destinations les plus fréquentes", destinationChart);

        // 3. Graphique d'évolution dans le temps (Lignes)
        LineChart<String, Number> timelineChart = createTimelineChart(trajets);
        VBox timelineChartBox = createChartBox("Évolution du nombre de trajets", timelineChart);

        // 4. Graphique de durée moyenne par type de transport (Barres)
        BarChart<String, Number> durationChart = createDurationBarChart(trajets);
        VBox durationChartBox = createChartBox("Durée moyenne par type de transport", durationChart);

        // Organiser les graphiques en grille
        GridPane chartsGrid = new GridPane();
        chartsGrid.setHgap(20);
        chartsGrid.setVgap(20);
        chartsGrid.add(transportChartBox, 0, 0);
        chartsGrid.add(destinationChartBox, 1, 0);
        chartsGrid.add(timelineChartBox, 0, 1);
        chartsGrid.add(durationChartBox, 1, 1);

        // Configurer la grille
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col2.setPercentWidth(50);
        chartsGrid.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row1.setPercentHeight(50);
        row2.setPercentHeight(50);
        chartsGrid.getRowConstraints().addAll(row1, row2);

        chartsContainer.getChildren().add(chartsGrid);

        // Animer l'apparition des graphiques
        animateCharts(chartsGrid);
    }

    private VBox createChartBox(String title, Node chart) {
        VBox chartBox = new VBox(10);
        chartBox.getStyleClass().add("chart-box");
        chartBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("chart-title");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.4, 0.4, 0.5));
        chartBox.setEffect(dropShadow);

        chartBox.getChildren().addAll(titleLabel, chart);
        chartBox.setPadding(new Insets(15));
        chartBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 8;");

        return chartBox;
    }

    private PieChart createTransportPieChart(Map<String, Integer> stats) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        stats.forEach((transport, count) -> {
            pieChartData.add(new PieChart.Data(transport + " (" + count + ")", count));
        });

        PieChart chart = new PieChart(pieChartData);
        chart.setLabelsVisible(true);
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setPrefSize(400, 300);

        // Ajouter des effets au survol
        for (PieChart.Data data : chart.getData()) {
            Node node = data.getNode();

            Tooltip tooltip = new Tooltip(data.getName());
            Tooltip.install(node, tooltip);

            node.setOnMouseEntered(e -> {
                node.setScaleX(1.1);
                node.setScaleY(1.1);
            });

            node.setOnMouseExited(e -> {
                node.setScaleX(1);
                node.setScaleY(1);
            });
        }

        return chart;
    }

    private BarChart<String, Number> createDestinationBarChart(Map<String, Integer> stats) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Destination");
        yAxis.setLabel("Nombre de trajets");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setAnimated(true);
        barChart.setPrefSize(400, 300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de trajets");

        // Limiter à top 5 pour la lisibilité
        stats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> series.getData().add(
                        new XYChart.Data<>(entry.getKey(), entry.getValue())
                ));

        barChart.getData().add(series);

        // Ajouter des labels sur les barres
        for (XYChart.Data<String, Number> data : series.getData()) {
            StackPane bar = (StackPane) data.getNode();

            Label label = new Label(data.getYValue().toString());
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("System", 10));

            bar.getChildren().add(label);
            StackPane.setAlignment(label, Pos.TOP_CENTER);

            Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue());
            Tooltip.install(bar, tooltip);
        }

        return barChart;
    }

    private LineChart<String, Number> createTimelineChart(List<Trajet> trajets) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Mois");
        yAxis.setLabel("Nombre de trajets");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setAnimated(true);
        lineChart.setPrefSize(400, 300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Évolution mensuelle");

        // Agréger par mois
        Map<String, Long> countsByMonth = trajets.stream()
                .collect(Collectors.groupingBy(
                        t -> {
                            LocalDate date = t.getDate().toLocalDate();
                            return date.getMonth().toString() + " " + date.getYear();
                        },
                        Collectors.counting()
                ));

        // Trier par ordre chronologique
        TreeMap<String, Long> sortedCounts = new TreeMap<>(countsByMonth);

        sortedCounts.forEach((month, count) -> {
            series.getData().add(new XYChart.Data<>(month, count));
        });

        lineChart.getData().add(series);

        // Ajouter des points plus visibles
        for (XYChart.Data<String, Number> data : series.getData()) {
            StackPane stackPane = new StackPane();
            stackPane.setShape(new javafx.scene.shape.Circle(5));
            stackPane.setStyle("-fx-background-color: #4285f4;");
            data.setNode(stackPane);

            Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue());
            Tooltip.install(stackPane, tooltip);

            // Animation au survol
            stackPane.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), stackPane);
                st.setToX(1.5);
                st.setToY(1.5);
                st.play();
            });

            stackPane.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), stackPane);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });
        }

        return lineChart;
    }

    private BarChart<String, Number> createDurationBarChart(List<Trajet> trajets) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Type de transport");
        yAxis.setLabel("Durée moyenne (min)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setAnimated(true);
        barChart.setPrefSize(400, 300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Durée moyenne");

        // Calculer la durée moyenne par type de transport
        Map<String, Double> avgDurationByTransport = trajets.stream()
                .collect(Collectors.groupingBy(
                        Trajet::getTransport,
                        Collectors.averagingDouble(Trajet::getDuree)
                ));

        avgDurationByTransport.forEach((transport, avgDuration) -> {
            series.getData().add(new XYChart.Data<>(transport, avgDuration));
        });

        barChart.getData().add(series);

        // Ajouter des labels sur les barres
        for (XYChart.Data<String, Number> data : series.getData()) {
            StackPane bar = (StackPane) data.getNode();

            Label label = new Label(String.format("%.1f", data.getYValue().doubleValue()));
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("System", 10));

            bar.getChildren().add(label);
            StackPane.setAlignment(label, Pos.TOP_CENTER);

            Tooltip tooltip = new Tooltip(data.getXValue() + ": " +
                    String.format("%.1f min", data.getYValue().doubleValue()));
            Tooltip.install(bar, tooltip);
        }

        return barChart;
    }

    private void animateCharts(GridPane chartsGrid) {
        for (Node node : chartsGrid.getChildren()) {
            if (node instanceof VBox) {
                node.setOpacity(0);

                FadeTransition ft = new FadeTransition(Duration.millis(800), node);
                ft.setFromValue(0);
                ft.setToValue(1);

                ScaleTransition st = new ScaleTransition(Duration.millis(800), node);
                st.setFromX(0.9);
                st.setFromY(0.9);
                st.setToX(1);
                st.setToY(1);

                int index = chartsGrid.getChildren().indexOf(node);
                int delay = index * 200;

                ParallelTransition pt = new ParallelTransition(ft, st);
                pt.setDelay(Duration.millis(delay));
                pt.play();
            }
        }
    }

    private void updateKPIs(List<Trajet> trajets) {
        if (totalTrajetsLabel != null) {
            totalTrajetsLabel.setText(String.valueOf(trajets.size()));
        }

        if (avgDurationLabel != null) {
            double avgDuration = trajets.stream()
                    .mapToInt(Trajet::getDuree)
                    .average()
                    .orElse(0);
            avgDurationLabel.setText(String.format("%.1f min", avgDuration));
        }

        if (mostPopularDestLabel != null) {
            String mostPopularDest = destinationStats.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            mostPopularDestLabel.setText(mostPopularDest);
        }

        if (mostUsedTransportLabel != null) {
            String mostUsedTransport = transportStats.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            mostUsedTransportLabel.setText(mostUsedTransport);
        }
    }

    private void applyFilters() {
        try {
            System.out.println("Application des filtres...");

            // Récupérer tous les trajets
            List<Trajet> filteredTrajets = trajetService.getA();

            // Filtrer par période
            String periode = periodeComboBox.getValue();
            LocalDate today = LocalDate.now();

            switch (periode) {
                case "Aujourd'hui":
                    filteredTrajets = filteredTrajets.stream()
                            .filter(t -> t.getDate().toLocalDate().equals(today))
                            .collect(Collectors.toList());
                    break;

                case "Cette semaine":
                    LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
                    LocalDate endOfWeek = startOfWeek.plusDays(6);
                    filteredTrajets = filteredTrajets.stream()
                            .filter(t -> {
                                LocalDate date = t.getDate().toLocalDate();
                                return !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek);
                            })
                            .collect(Collectors.toList());
                    break;

                case "Ce mois":
                    filteredTrajets = filteredTrajets.stream()
                            .filter(t -> {
                                LocalDate date = t.getDate().toLocalDate();
                                return date.getMonth() == today.getMonth() &&
                                        date.getYear() == today.getYear();
                            })
                            .collect(Collectors.toList());
                    break;

                case "Cette année":
                    filteredTrajets = filteredTrajets.stream()
                            .filter(t -> t.getDate().toLocalDate().getYear() == today.getYear())
                            .collect(Collectors.toList());
                    break;

                case "Personnalisé":
                    LocalDate debut = dateDebut.getValue();
                    LocalDate fin = dateFin.getValue();

                    if (debut != null && fin != null) {
                        filteredTrajets = filteredTrajets.stream()
                                .filter(t -> {
                                    LocalDate date = t.getDate().toLocalDate();
                                    return !date.isBefore(debut) && !date.isAfter(fin);
                                })
                                .collect(Collectors.toList());
                    }
                    break;
            }

            // Mettre à jour avec les données filtrées
            System.out.println("Nombre de trajets après filtrage: " + filteredTrajets.size());

            if (filteredTrajets.isEmpty()) {
                showNoDataMessage();
                return;
            }

            // Recalculer les statistiques et mettre à jour les graphiques
            calculateStatistics(filteredTrajets);
            generateCharts(filteredTrajets);
            updateKPIs(filteredTrajets);

            // Afficher un message de confirmation
            showInfoToast("Filtres appliqués avec succès");

        } catch (Exception e) {
            System.err.println("Erreur lors de l'application des filtres: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("Erreur de filtrage", "Impossible d'appliquer les filtres: " + e.getMessage());
        }
    }

    private void showErrorMessage(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Appliquer le style de l'application
            if (mainContainer != null && mainContainer.getScene() != null) {
                alert.getDialogPane().getStylesheets().add(
                        mainContainer.getScene().getStylesheets().get(0)
                );
            }

            alert.showAndWait();
        });
    }

    private void showInfoToast(String message) {
        if (mainContainer == null) return;

        // Créer le conteneur du toast
        StackPane toastContainer = new StackPane();
        toastContainer.setMaxWidth(300);
        toastContainer.setMinHeight(50);
        toastContainer.getStyleClass().add("toast");
        toastContainer.getStyleClass().add("toast-info");

        // Appliquer un effet d'ombre
        toastContainer.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));

        // Texte du toast
        Label toastText = new Label(message);
        toastText.getStyleClass().add("toast-text");
        toastContainer.getChildren().add(toastText);

        // Ajouter le toast à la scène principale
        StackPane root = new StackPane();
        root.setPrefSize(mainContainer.getWidth(), mainContainer.getHeight());
        root.setMouseTransparent(true);

        // Positionner le toast en bas
        StackPane.setAlignment(toastContainer, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toastContainer, new Insets(0, 0, 50, 0));

        root.getChildren().add(toastContainer);

        // Ajouter le root temporairement à la hiérarchie de scène
        mainContainer.getChildren().add(root);

        // Animations
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toastContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), toastContainer);
        slideIn.setFromY(50);
        slideIn.setToY(0);

        ParallelTransition ptIn = new ParallelTransition(fadeIn, slideIn);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toastContainer);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), toastContainer);
        slideOut.setFromY(0);
        slideOut.setToY(50);

        ParallelTransition ptOut = new ParallelTransition(fadeOut, slideOut);

        // Séquencer les animations
        SequentialTransition sequentialTransition = new SequentialTransition(
                ptIn,
                new PauseTransition(Duration.seconds(3)),
                ptOut
        );

        sequentialTransition.setOnFinished(e -> {
            mainContainer.getChildren().remove(root);
        });

        sequentialTransition.play();
    }

    // Méthode pour exporter les données au format CSV

    @FXML
    private void exportData() {
        try {
            String periode = periodeComboBox.getValue();
            String critere = critereComboBox.getValue();

            // Créer le chemin du fichier d'export
            String fileName = "statistiques_trajets_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";

            // Préparer les données à exporter
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("Date,Transport,Destination,Durée\n");

            for (Trajet trajet : allTrajets) {
                csvContent.append(trajet.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(",");
                csvContent.append(trajet.getTransport()).append(",");
                csvContent.append(trajet.getDestination()).append(",");
                csvContent.append(trajet.getDuree()).append("\n");
            }

            // Écrire dans un fichier
            // Dans une implémentation complète, vous utiliseriez FileChooser
            // Pour simplifier, nous allons directement montrer un message de confirmation

            showInfoToast("Données exportées avec succès: " + fileName);

            // Navigation vers afficherTrajet.fxml après un court délai
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherTrajet.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) mainContainer.getScene().getWindow();

                    // Animation de transition
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), mainContainer);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);

                    fadeOut.setOnFinished(e -> {
                        stage.setScene(scene);

                        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                        fadeIn.setFromValue(0);
                        fadeIn.setToValue(1);
                        fadeIn.play();
                    });

                    fadeOut.play();
                } catch (IOException e) {
                    showErrorMessage("Erreur de navigation", "Impossible de charger la vue des trajets: " + e.getMessage());
                }
            });
            delay.play();

        } catch (Exception e) {
            showErrorMessage("Erreur d'exportation", "Impossible d'exporter les données: " + e.getMessage());
        }
    }

    // Méthode pour rafraîchir les données
    @FXML
    private void refreshData() {
        loadData();
        showInfoToast("Données actualisées");
    }

    // Méthode pour basculer vers un autre écran
    @FXML
    private void navigateToScreen(javafx.event.ActionEvent event) {
        try {
            Button clickedButton = (Button) event.getSource();
            String screenName = clickedButton.getText().toLowerCase();

            // Map button text to screen name
            String fxmlName;
            switch (screenName) {
                case "home":
                    fxmlName = "dashboard";
                    break;
                case "trajets":
                    fxmlName = "afficherTrajet";
                    break;
                default:
                    fxmlName = screenName;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource( fxmlName + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) mainContainer.getScene().getWindow();

            // Animation de transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), mainContainer);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            fadeOut.setOnFinished(e -> {
                stage.setScene(scene);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (IOException e) {
            showErrorMessage("Erreur de navigation", "Impossible de charger l'écran demandé: " + e.getMessage());
        }
    }
    @FXML
    private void showDetailedDataTable() {
        if (chartsContainer == null) return;

        // Check if we already created a table
        Tab detailsTab = null;
        for (Tab tab : tabPane.getTabs()) {
            if ("Voir détails".equals(tab.getText())) {
                detailsTab = tab;
                break;
            }
        }

        // Only create table if tab exists and doesn't already have content
        if (detailsTab != null && detailsTab.getContent() == null) {
            // Créer un tableau pour afficher les données
            TableView<Trajet> tableView = new TableView<>();
            tableView.setEditable(false);

            // Définir les colonnes
            TableColumn<Trajet, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

            TableColumn<Trajet, String> transportCol = new TableColumn<>("Transport");
            transportCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTransport()));

            TableColumn<Trajet, String> destinationCol = new TableColumn<>("Destination");
            destinationCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDestination()));

            TableColumn<Trajet, String> dureeCol = new TableColumn<>("Durée (min)");
            dureeCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDuree())));

            tableView.getColumns().addAll(dateCol, transportCol, destinationCol, dureeCol);
            tableView.setItems(FXCollections.observableArrayList(allTrajets));

            // Apply styling to the table
            tableView.getStyleClass().add("transparent-table");

            // Set the table as the tab content
            detailsTab.setContent(tableView);
        }
    }}