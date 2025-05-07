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

public class ModifierResHotelController {

    @FXML private ComboBox<String> hotelCombo;
    @FXML private TextField startresField;
    @FXML private DatePicker dateResPicker;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmationSMS; // Renommé en interne mais peut rester le même dans le FXML

    private final ResHotelService resHotelService = new ResHotelService();
    private final HotelService hotelService = new HotelService();
    private AfficherResHotelController parentController;
    private ResHotel resHotel;

    // Numéro de téléphone par défaut - sera demandé à l'utilisateur au moment de l'envoi
    private static final String DEFAULT_PHONE_NUMBER = "+21652348380";

    @FXML
    public void initialize() {
        System.out.println("Initialisation du ModifierResHotelController");

        // Vérification que les champs et boutons sont correctement injectés
        if (hotelCombo == null || startresField == null || dateResPicker == null ||
                btnConfirmer == null || btnAnnuler == null) {
            System.err.println("Erreur: un ou plusieurs éléments FXML n'ont pas été injectés correctement");
        }

        // Charger la liste des hôtels dans le ComboBox
        chargerHotels();

        // Configuration explicite des événements boutons
        if (btnConfirmer != null) {
            btnConfirmer.setOnAction(event -> confirmerModification());
        }

        if (btnAnnuler != null) {
            btnAnnuler.setOnAction(event -> annulerModification());
        }

        if (btnConfirmationSMS != null) {
            btnConfirmationSMS.setText("Confirmer avec WhatsApp"); // Mise à jour du texte du bouton
            btnConfirmationSMS.setOnAction(event -> confirmerAvecWhatsApp());
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
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des hôtels: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setResHotel(ResHotel resHotel) {
        this.resHotel = resHotel;

        // Remplir les champs avec les valeurs de la réservation
        if (resHotel != null) {
            hotelCombo.setValue(resHotel.getHotel());
            startresField.setText(resHotel.getStartres());
            dateResPicker.setValue(resHotel.getDateres().toLocalDate());
        }
    }

    public void setParentController(AfficherResHotelController controller) {
        this.parentController = controller;
    }

    /**
     * Méthode pour confirmer directement avec envoi de WhatsApp
     */
    @FXML
    private void confirmerAvecWhatsApp() {
        // Mettre à jour le statut en "Confirmé"
        startresField.setText("Confirmé");

        // Sauvegarder et envoyer un message WhatsApp
        if (confirmerModification()) {
            envoyerWhatsAppConfirmation();
        }
    }

    @FXML
    public boolean confirmerModification() {
        try {
            System.out.println("Confirmation de la modification d'une réservation");

            // Récupérer les valeurs des champs
            String hotel = hotelCombo.getValue();
            String startres = startresField.getText();
            LocalDateTime dateres = dateResPicker.getValue().atStartOfDay();

            // Validation des champs
            if (hotel == null || hotel.isEmpty() || startres.isEmpty() || dateres == null) {
                afficherAlerte("Veuillez remplir tous les champs.");
                return false;
            }

            // Mettre à jour l'objet réservation
            resHotel.setHotel(hotel);
            resHotel.setStartres(startres);
            resHotel.setDateres(dateres);
            System.out.println("Réservation à modifier: " + resHotel);

            // Modifier la réservation dans la base de données
            boolean success = resHotelService.modifier(resHotel);
            System.out.println("Résultat de la modification: " + success);

            if (success) {
                // Envoyer un WhatsApp uniquement si le statut est "Confirmé" et
                // si le bouton standard est cliqué (pour éviter double envoi)
                if ("Confirmé".equals(startres) &&
                        !btnConfirmationSMS.isFocused()) { // Ne pas envoyer si c'est le bouton WhatsApp qui a été cliqué
                    envoyerWhatsAppConfirmation();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Réservation modifiée avec succès.");
                alert.showAndWait();

                // Mettre à jour l'affichage dans le contrôleur parent
                if (parentController != null) {
                    parentController.loadData();
                } else {
                    System.err.println("Erreur: parentController est null");
                }

                // Fermer la fenêtre
                fermerFenetre();
                return true;
            } else {
                afficherAlerte("Erreur lors de la modification de la réservation.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur inattendue lors de la modification : " + e.getMessage());
            return false;
        }
    }

    /**
     * Envoie un WhatsApp de confirmation
     */
    private void envoyerWhatsAppConfirmation() {
        // Demander le numéro de téléphone WhatsApp
        String phoneNumber = WhatsAppService.demanderNumeroTelephone(DEFAULT_PHONE_NUMBER);

        if (phoneNumber.isEmpty()) {
            return; // L'utilisateur a annulé
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = resHotel.getDateres().toLocalDate().format(formatter);

        String message = "Votre réservation à l'hôtel " + resHotel.getHotel() +
                " pour le " + dateFormatted + " a été CONFIRMÉE. " +
                "Merci pour votre confiance! - TRAVELPRO";

        WhatsAppService.sendWhatsApp(phoneNumber, message);
    }

    @FXML
    public void annulerModification() {
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