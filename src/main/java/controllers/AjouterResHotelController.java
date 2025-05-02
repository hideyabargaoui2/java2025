package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.ResHotel;
import models.hotel;
import service.HotelService;
import service.ResHotelService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AjouterResHotelController {

    @FXML private ComboBox<String> hotelCombo;
    @FXML private TextField startresField;
    @FXML private DatePicker dateResPicker;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;

    private final ResHotelService resHotelService = new ResHotelService();
    private final HotelService hotelService = new HotelService();
    private AfficherResHotelController parentController;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du AjouterResHotelController");

        // Vérification que les champs et boutons sont correctement injectés
        if (hotelCombo == null || startresField == null || dateResPicker == null ||
                btnConfirmer == null || btnAnnuler == null) {
            System.err.println("Erreur: un ou plusieurs éléments FXML n'ont pas été injectés correctement");
        }

        // Charger la liste des hôtels dans le ComboBox
        chargerHotels();

        // Initialiser la date à aujourd'hui
        dateResPicker.setValue(LocalDate.now());

        // Configuration explicite des événements boutons
        if (btnConfirmer != null) {
            btnConfirmer.setOnAction(event -> confirmerAjout());
        }

        if (btnAnnuler != null) {
            btnAnnuler.setOnAction(event -> annulerAjout());
        }
    }

    private void chargerHotels() {
        try {
            List<hotel> hotels = hotelService.getA();
            ObservableList<String> hotelNames = FXCollections.observableArrayList();

            for (hotel h : hotels) {
                hotelNames.add(h.getNom());
            }

            hotelCombo.setItems(hotelNames);

            // Sélectionner le premier hôtel s'il y en a
            if (!hotelNames.isEmpty()) {
                hotelCombo.setValue(hotelNames.get(0));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des hôtels: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setParentController(AfficherResHotelController controller) {
        this.parentController = controller;
    }

    @FXML
    public void confirmerAjout() {
        try {
            System.out.println("Confirmation de l'ajout d'une réservation");

            // Récupérer les valeurs des champs
            String hotel = hotelCombo.getValue();
            String startres = startresField.getText();
            LocalDateTime dateres = dateResPicker.getValue().atStartOfDay();

            // Validation des champs
            if (hotel == null || hotel.isEmpty() || startres.isEmpty() || dateres == null) {
                afficherAlerte("Veuillez remplir tous les champs.");
                return;
            }

            // Créer un nouvel objet réservation
            ResHotel nouvelleReservation = new ResHotel(hotel, startres, dateres);
            System.out.println("Nouvelle réservation à ajouter: " + nouvelleReservation);

            // Ajouter la réservation à la base de données
            boolean success = resHotelService.ajouter(nouvelleReservation);
            System.out.println("Résultat de l'ajout: " + success);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Réservation ajoutée avec succès.");
                alert.showAndWait();

                // Mettre à jour l'affichage dans le contrôleur parent
                if (parentController != null) {
                    parentController.loadData();
                } else {
                    System.err.println("Erreur: parentController est null");
                }

                // Fermer la fenêtre
                fermerFenetre();
            } else {
                afficherAlerte("Erreur lors de l'ajout de la réservation.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur inattendue lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    public void annulerAjout() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    private void afficherAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}