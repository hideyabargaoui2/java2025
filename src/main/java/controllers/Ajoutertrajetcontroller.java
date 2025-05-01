package controllers;

import javafx.animation.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Trajet;
import services.Trajetservice;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Contrôleur pour l'ajout et la modification de trajets
 */
public class Ajoutertrajetcontroller {

    @FXML private BorderPane mainContainer;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private TextField destinationField;
    @FXML private TextField transportField;
    @FXML private Spinner<Integer> heureSpinner;
    @FXML private Spinner<Integer> dureeSpinner;
    @FXML private DatePicker datePicker;
    @FXML private Label titleLabel;
    @FXML private VBox formContainer;
    @FXML private HBox buttonBar;
    @FXML private ImageView headerIcon;

    private final Trajetservice trajetService = new Trajetservice();
    private Trajet trajet;
    private Runnable onCloseCallback;
    private final SimpleBooleanProperty editMode = new SimpleBooleanProperty(false);

    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur Ajoutertrajetcontroller");

        // Initialiser les spinners
        SpinnerValueFactory<Integer> heureValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9);
        heureSpinner.setValueFactory(heureValueFactory);

        SpinnerValueFactory<Integer> dureeValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 24, 2);
        dureeSpinner.setValueFactory(dureeValueFactory);

        // Initialiser le DatePicker
        datePicker.setValue(LocalDate.now());

        // Configurer les boutons
        setupButtons();

        // Configurer les validations
        setupValidations();

        // Animation d'entrée
        playEntranceAnimation();


        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
                if (date.isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #ffc0cb;"); // Couleur rouge pâle pour les dates interdites
                }
            }
        });

        // Mise à jour du titre selon le mode (ajout ou modification)
        editMode.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                titleLabel.setText("Modifier un trajet");
                try {
                    headerIcon.setImage(new Image(getClass().getResourceAsStream("/icons/edit_form.png")));
                } catch (Exception e) {
                    System.err.println("Icône non trouvée: " + e.getMessage());
                }
            } else {
                titleLabel.setText("Ajouter un nouveau trajet");
                try {
                    headerIcon.setImage(new Image(getClass().getResourceAsStream("/icons/add_form.png")));
                } catch (Exception e) {
                    System.err.println("Icône non trouvée: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Configure les boutons et leurs actions
     */
    private void setupButtons() {
        // Style des boutons
        btnSave.getStyleClass().add("btn-primary");
        btnCancel.getStyleClass().add("btn-secondary");

        // Ajouter des icônes aux boutons
        try {
            ImageView saveIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/save.png")));
            saveIcon.setFitHeight(16);
            saveIcon.setFitWidth(16);
            btnSave.setGraphic(saveIcon);

            ImageView cancelIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/cancel.png")));
            cancelIcon.setFitHeight(16);
            cancelIcon.setFitWidth(16);
            btnCancel.setGraphic(cancelIcon);
        } catch (Exception e) {
            System.err.println("Icônes non trouvées: " + e.getMessage());
        }

        // Action du bouton Enregistrer
        btnSave.setOnAction(event -> {
            if (validateForm()) {
                saveTrajet();
            }
        });

        // Action du bouton Annuler
        btnCancel.setOnAction(event -> close());

        // Animations des boutons
        setupButtonAnimation(btnSave);
        setupButtonAnimation(btnCancel);
    }
    @FXML
    public void ajouterTrajet(ActionEvent event) {
        if (validateForm()) {
            saveTrajet();
        }
    }
    /**
     * Configure les validations des champs du formulaire
     */
    private void setupValidations() {
        // Validation pour le champ destination
        destinationField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && destinationField.getText().trim().isEmpty()) {
                destinationField.setStyle("-fx-border-color: red;");
                showFieldError(destinationField, "La destination est requise");
            } else {
                destinationField.setStyle("");
            }
        });

        // Validation pour le champ transport
        transportField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && transportField.getText().trim().isEmpty()) {
                transportField.setStyle("-fx-border-color: red;");
                showFieldError(transportField, "Le mode de transport est requis");
            } else {
                transportField.setStyle("");
            }
        });
    }

    /**
     * Affiche une erreur de validation pour un champ
     */
    private void showFieldError(TextField field, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24;");

        field.setTooltip(tooltip);

        // Animation de secousse
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), field);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    /**
     * Ajoute une animation au survol et au clic d'un bouton
     */
    private void setupButtonAnimation(Button button) {
        // Animation au survol
        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.DODGERBLUE);
            glow.setRadius(10);
            glow.setSpread(0.2);
            button.setEffect(glow);
        });

        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
            button.setEffect(null);
        });

        // Animation au clic
        button.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(0.95);
            st.setToY(0.95);
            st.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    /**
     * Animation d'entrée pour le formulaire
     */
    private void playEntranceAnimation() {
        // Animation du conteneur principal
        mainContainer.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), mainContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Animation des champs du formulaire
        for (int i = 0; i < formContainer.getChildren().size(); i++) {
            formContainer.getChildren().get(i).setOpacity(0);

            FadeTransition fieldFade = new FadeTransition(Duration.millis(200), formContainer.getChildren().get(i));
            fieldFade.setFromValue(0);
            fieldFade.setToValue(1);
            fieldFade.setDelay(Duration.millis(100 * i));
            fieldFade.play();
        }

        // Animation des boutons
        buttonBar.setOpacity(0);

        FadeTransition buttonsFade = new FadeTransition(Duration.millis(300), buttonBar);
        buttonsFade.setFromValue(0);
        buttonsFade.setToValue(1);
        buttonsFade.setDelay(Duration.millis(500));

        // Jouer les animations
        fadeIn.play();
        buttonsFade.play();
    }

    /**
     * Valide les champs du formulaire
     */
    private boolean validateForm() {
        boolean isValid = true;

        // Validation de la destination
        if (destinationField.getText().trim().isEmpty()) {
            destinationField.setStyle("-fx-border-color: red;");
            showFieldError(destinationField, "La destination est requise");
            isValid = false;
        } else {
            destinationField.setStyle("");
        }

        // Validation du transport
        if (transportField.getText().trim().isEmpty()) {
            transportField.setStyle("-fx-border-color: red;");
            showFieldError(transportField, "Le mode de transport est requis");
            isValid = false;
        } else {
            transportField.setStyle("");
        }

        // Validation de la date (empêcher les dates passées)
        if (datePicker.getValue() == null) {
            datePicker.setStyle("-fx-border-color: red;");
            showFieldError(datePicker.getEditor(), "La date est requise");
            isValid = false;
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            datePicker.setStyle("-fx-border-color: red;");
            showFieldError(datePicker.getEditor(), "La date ne peut pas être dans le passé");
            isValid = false;
        } else {
            datePicker.setStyle("");
        }

        return isValid;
    }
    private void showFieldError(DatePicker field, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24;");

        field.setTooltip(tooltip);

        // Animation de secousse
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), field);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
    /**
     * Enregistre le trajet (ajout ou modification)
     */
    private void saveTrajet() {
        try {
            // Créer ou mettre à jour le trajet
            if (trajet == null) {
                trajet = new Trajet();
            }

            // Remplir les données du trajet
            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.of(0, 0));
            trajet.setDate(dateTime);
            trajet.setHeure(heureSpinner.getValue());
            trajet.setDestination(destinationField.getText());
            trajet.setTransport(transportField.getText());
            trajet.setDuree(dureeSpinner.getValue());

            // Enregistrer le trajet
            if (editMode.get()) {
                trajetService.modifier(trajet);
                playSuccessAnimation("Trajet modifié avec succès");
            } else {
                trajetService.ajouter(trajet);
                playSuccessAnimation("Trajet ajouté avec succès");
            }

            // Fermer après un délai
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(e -> close());
            delay.play();

        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement du trajet: " + e.getMessage());
            e.printStackTrace();

            // Afficher un message d'erreur
            showErrorMessage("Erreur lors de l'enregistrement", e.getMessage());
        }
    }

    /**
     * Affiche un message d'erreur
     */
    private void showErrorMessage(String title, String message) {
        // Créer un label d'erreur
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-padding: 10px; -fx-background-radius: 5px;");

        // Ajouter au conteneur principal
        StackPane errorPane = new StackPane(errorLabel);
        errorPane.setStyle("-fx-padding: 10px;");
        errorPane.setTranslateY(-buttonBar.getHeight() - 20);

        mainContainer.getChildren().add(errorPane);

        // Animation d'apparition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), errorPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Animation de disparition après 3 secondes
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), errorPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(3));
        fadeOut.setOnFinished(e -> mainContainer.getChildren().remove(errorPane));

        // Jouer les animations
        fadeIn.play();
        fadeOut.play();
    }

    /**
     * Joue une animation de succès
     */
    private void playSuccessAnimation(String message) {
        // Créer un label de succès
        Label successLabel = new Label(message);
        successLabel.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-padding: 10px; -fx-background-radius: 5px;");

        // Ajouter une icône de validation
        try {
            ImageView checkIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/check.png")));
            checkIcon.setFitHeight(16);
            checkIcon.setFitWidth(16);

            HBox content = new HBox(10, checkIcon, successLabel);
            content.setAlignment(javafx.geometry.Pos.CENTER);

            // Ajouter au conteneur principal
            StackPane successPane = new StackPane(content);
            successPane.setStyle("-fx-padding: 10px;");
            successPane.setTranslateY(-buttonBar.getHeight() - 20);

            mainContainer.getChildren().add(successPane);

            // Animation d'apparition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), successPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            // Animation de disparition après 1.5 secondes
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), successPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setDelay(Duration.seconds(1.5));
            fadeOut.setOnFinished(e -> mainContainer.getChildren().remove(successPane));

            // Appliquer un effet de flou sur le formulaire
            GaussianBlur blur = new GaussianBlur(0);
            formContainer.setEffect(blur);

            Timeline blurTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(blur.radiusProperty(), 0)),
                    new KeyFrame(Duration.millis(300), new KeyValue(blur.radiusProperty(), 5)),
                    new KeyFrame(Duration.millis(1500), new KeyValue(blur.radiusProperty(), 5)),
                    new KeyFrame(Duration.millis(1800), new KeyValue(blur.radiusProperty(), 0))
            );

            // Jouer les animations
            fadeIn.play();
            fadeOut.play();
            blurTimeline.play();

        } catch (Exception e) {
            System.err.println("Icône non trouvée: " + e.getMessage());
        }
    }

    /**
     * Ferme la fenêtre
     */
    private void close() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        } else {
            // Animation de fermeture
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), mainContainer);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                Stage stage = (Stage) mainContainer.getScene().getWindow();
                stage.close();
            });
            fadeOut.play();
        }
    }

    /**
     * Définit le trajet à modifier
     */
    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;

        if (trajet != null) {
            // Remplir les champs du formulaire
            datePicker.setValue(trajet.getDate().toLocalDate());
            heureSpinner.getValueFactory().setValue(trajet.getHeure());
            destinationField.setText(trajet.getDestination());
            transportField.setText(trajet.getTransport());
            dureeSpinner.getValueFactory().setValue(trajet.getDuree());

            // Mettre à jour le titre
            setEditMode(true);
        }
    }

    /**
     * Définit le mode d'édition (ajout ou modification)
     */
    public void setEditMode(boolean isEditMode) {
        this.editMode.set(isEditMode);
    }

    /**
     * Définit la callback à exécuter lors de la fermeture
     */
    public void setOnClose(Runnable callback) {
        this.onCloseCallback = callback;
    }
}