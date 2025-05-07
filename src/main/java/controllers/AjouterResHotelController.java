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
import service.WhatsAppService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    // Numéro de téléphone par défaut - sera demandé à l'utilisateur au moment de l'envoi
    private static final String DEFAULT_PHONE_NUMBER = "+21652348380";

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

        // Initialiser la valeur par défaut du statut de réservation
        startresField.setText("En attente de confirmation");

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
                // Demander à l'utilisateur s'il souhaite envoyer un WhatsApp de confirmation
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation WhatsApp");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Souhaitez-vous envoyer une confirmation par WhatsApp?");

                ButtonType buttonOui = new ButtonType("Oui");
                ButtonType buttonNon = new ButtonType("Non");

                confirmAlert.getButtonTypes().setAll(buttonOui, buttonNon);

                if (confirmAlert.showAndWait().orElse(buttonNon) == buttonOui) {
                    envoyerWhatsAppConfirmation(nouvelleReservation);
                }

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

    /**
     * Envoie un WhatsApp de confirmation pour une nouvelle réservation
     * @param reservation La réservation pour laquelle envoyer la confirmation
     */
    private void envoyerWhatsAppConfirmation(ResHotel reservation) {
        // Demander le numéro de téléphone WhatsApp
        String phoneNumber = WhatsAppService.demanderNumeroTelephone(DEFAULT_PHONE_NUMBER);

        if (phoneNumber.isEmpty()) {
            return; // L'utilisateur a annulé
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = reservation.getDateres().toLocalDate().format(formatter);

        String message = "Votre réservation à l'hôtel " + reservation.getHotel() +
                " pour le " + dateFormatted + " a été enregistrée avec succès. " +
                "Statut: " + reservation.getStartres() + ". " +
                "Merci pour votre confiance. - TRAVELPRO";

        boolean messageEnvoye = WhatsAppService.sendWhatsApp(phoneNumber, message);

        if (messageEnvoye) {
            System.out.println("Message WhatsApp de confirmation envoyé avec succès au " + phoneNumber);
        } else {
            System.err.println("Échec de l'envoi du message WhatsApp de confirmation");
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