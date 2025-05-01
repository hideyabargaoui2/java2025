package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Trajet;
import services.Trajetservice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Modifiertrajetcontroller {

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField heureField;
    @FXML
    private TextField destinationField;
    @FXML
    private ComboBox<String> transportComboBox;
    @FXML
    private TextField dureeField;
    @FXML
    private Button btnConfirmer;
    @FXML
    private Button btnAnnuler;

    private Trajet trajet;
    private final Trajetservice trajetService = new Trajetservice();
    private AfficherTrajetController parentController;

    /**
     * Initialise le contrôleur de modification
     */
    @FXML
    public void initialize() {
        System.out.println("Initialisation du ModifierTrajetController");

        // Empêcher les dates passées
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

        // Initialiser les options de transport (exemple)
        transportComboBox.getItems().addAll("Voiture", "Train", "Avion", "Bus", "Métro", "Vélo", "Marche");
    }

    /**
     * Définit le trajet à modifier et pré-remplit les champs
     *
     * @param trajet Le trajet à modifier
     */
    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
        remplirChamps();
    }

    /**
     * Pré-remplit les champs avec les valeurs du trajet
     */
    private void remplirChamps() {
        if (trajet != null) {
            // Remplir les champs avec les données du trajet
            datePicker.setValue(trajet.getDate().toLocalDate());
            heureField.setText(String.valueOf(trajet.getHeure()));
            destinationField.setText(trajet.getDestination());
            transportComboBox.setValue(trajet.getTransport());
            dureeField.setText(String.valueOf(trajet.getDuree()));
        }
    }

    /**
     * Définit le contrôleur parent pour rafraîchir la liste après modification
     *
     * @param controller Le contrôleur parent
     */
    public void setParentController(AfficherTrajetController controller) {
        this.parentController = controller;
    }

    /**
     * Gère la confirmation de la modification
     */
    @FXML
    private void confirmerModification() {
        try {
            // Récupérer les valeurs des champs
            LocalDate date = datePicker.getValue();

            // Vérifier que la date n'est pas dans le passé
            if (date.isBefore(LocalDate.now())) {
                afficherAlerte("La date ne peut pas être dans le passé");
                return;
            }

            int heure = Integer.parseInt(heureField.getText());
            String destination = destinationField.getText();
            String transport = transportComboBox.getValue();
            int duree = Integer.parseInt(dureeField.getText());

            // Vérifier que tous les champs sont remplis
            if (date == null || destination.isEmpty() || transport == null) {
                afficherAlerte("Veuillez remplir tous les champs");
                return;
            }

            // Mettre à jour l'objet trajet
            trajet.setDate(LocalDateTime.of(date, LocalTime.of(0, 0))); // On garde juste la date, l'heure est gérée séparément
            trajet.setHeure(heure);
            trajet.setDestination(destination);
            trajet.setTransport(transport);
            trajet.setDuree(duree);

            // Enregistrer les modifications
            trajetService.modifier(trajet);

            // Rafraîchir la liste des trajets
            if (parentController != null) {
                parentController.loadData();
            }

            // Fermer la fenêtre
            fermerFenetre();

        } catch (NumberFormatException e) {
            afficherAlerte("Veuillez entrer des nombres valides pour l'heure et la durée");
        } catch (Exception e) {
            afficherAlerte("Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gère l'annulation de la modification
     */
    @FXML
    private void annulerModification() {
        fermerFenetre();
    }

    /**
     * Ferme la fenêtre courante
     */
    private void fermerFenetre() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    /**
     * Affiche une alerte avec le message spécifié
     *
     * @param message Le message à afficher
     */
    private void afficherAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}