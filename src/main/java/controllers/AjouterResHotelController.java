package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.ResHotel;
import models.hotel;
import services.HotelService;
import services.ResHotelService;
import services.WhatsAppService;
import services.SMSService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjouterResHotelController {

    @FXML private ComboBox<String> hotelCombo;
    @FXML private TextField startresField;
    @FXML private DatePicker dateResPicker;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;
    @FXML private ToggleGroup notificationGroup;
    @FXML private RadioButton rbWhatsApp;
    @FXML private RadioButton rbSMS;
    @FXML private Spinner<Integer> spinnerNombreChambres; // Nouveau champ pour le nombre de chambres

    private final ResHotelService resHotelService = new ResHotelService();
    private final HotelService hotelService = new HotelService();
    private AfficherResHotelController parentController;
    private Map<String, hotel> hotelMap = new HashMap<>(); // Pour associer le nom d'hôtel à l'objet hôtel

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

        // Configurer le spinner pour le nombre de chambres (entre 1 et 10 par défaut)
        if (spinnerNombreChambres != null) {
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
            spinnerNombreChambres.setValueFactory(valueFactory);
        } else {
            System.err.println("Attention: le spinner pour le nombre de chambres n'est pas injecté");
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

        // Initialiser WhatsApp comme option par défaut si les RadioButtons existent
        if (rbWhatsApp != null) {
            rbWhatsApp.setSelected(true);
        }

        // Ajouter un écouteur pour le changement d'hôtel
        hotelCombo.setOnAction(event -> updateChambresDisponibles());
    }

    private void chargerHotels() {
        try {
            List<hotel> hotels = hotelService.getA();
            ObservableList<String> hotelNames = FXCollections.observableArrayList();
            hotelMap.clear();

            for (hotel h : hotels) {
                hotelNames.add(h.getNom());
                hotelMap.put(h.getNom(), h); // Stocker l'association nom-objet
            }

            hotelCombo.setItems(hotelNames);

            // Sélectionner le premier hôtel s'il y en a
            if (!hotelNames.isEmpty()) {
                hotelCombo.setValue(hotelNames.get(0));
                updateChambresDisponibles(); // Mettre à jour le nombre de chambres disponibles
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des hôtels: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Mise à jour des chambres disponibles lorsqu'un hôtel est sélectionné
    private void updateChambresDisponibles() {
        String selectedHotelName = hotelCombo.getValue();
        if (selectedHotelName != null && hotelMap.containsKey(selectedHotelName)) {
            hotel selectedHotel = hotelMap.get(selectedHotelName);
            int chambresDisponibles = selectedHotel.getNombreChambresDisponibles();

            // Limiter le spinner au nombre de chambres disponibles
            if (spinnerNombreChambres != null) {
                int maxValue = Math.max(1, chambresDisponibles); // Au moins 1 pour ne pas avoir d'erreur
                SpinnerValueFactory<Integer> valueFactory =
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxValue, 1);
                spinnerNombreChambres.setValueFactory(valueFactory);
            }

            // Optionnel: afficher un message sur le nombre de chambres disponibles
            System.out.println("Chambres disponibles pour " + selectedHotelName + ": " + chambresDisponibles);
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
            String hotelName = hotelCombo.getValue();
            String startres = startresField.getText();
            LocalDateTime dateres = dateResPicker.getValue().atStartOfDay();

            // Récupérer le nombre de chambres à réserver
            int nombreChambres = spinnerNombreChambres != null ? spinnerNombreChambres.getValue() : 1;

            // Validation des champs
            if (hotelName == null || hotelName.isEmpty() || startres.isEmpty() || dateres == null) {
                afficherAlerte("Veuillez remplir tous les champs.");
                return;
            }

            // Vérifier que l'hôtel existe dans la map
            if (!hotelMap.containsKey(hotelName)) {
                afficherAlerte("L'hôtel sélectionné n'est pas valide.");
                return;
            }

            // Récupérer l'objet hôtel
            hotel selectedHotel = hotelMap.get(hotelName);

            // Vérifier la disponibilité des chambres
            if (!selectedHotel.isDisponible(nombreChambres)) {
                afficherAlerte("Il n'y a pas assez de chambres disponibles dans cet hôtel.\n" +
                        "Chambres disponibles: " + selectedHotel.getNombreChambresDisponibles() +
                        "\nChambres demandées: " + nombreChambres);
                return;
            }

            // Créer un nouvel objet réservation avec le nombre de chambres
            ResHotel nouvelleReservation = new ResHotel(hotelName, startres, dateres, nombreChambres);
            System.out.println("Nouvelle réservation à ajouter: " + nouvelleReservation);

            // Réserver les chambres dans l'hôtel
            boolean chambresReservees = hotelService.reserverChambres(selectedHotel.getId(), nombreChambres);

            if (!chambresReservees) {
                afficherAlerte("Erreur lors de la réservation des chambres.");
                return;
            }

            // Ajouter la réservation à la base de données
            boolean success = resHotelService.ajouter(nouvelleReservation);
            System.out.println("Résultat de l'ajout: " + success);

            if (success) {
                // Demander à l'utilisateur s'il souhaite envoyer une confirmation
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Souhaitez-vous envoyer une confirmation au client?");

                ButtonType buttonOui = new ButtonType("Oui");
                ButtonType buttonNon = new ButtonType("Non");

                confirmAlert.getButtonTypes().setAll(buttonOui, buttonNon);

                if (confirmAlert.showAndWait().orElse(buttonNon) == buttonOui) {
                    // Vérifier quel type de notification est sélectionné
                    boolean useWhatsApp = true; // Par défaut WhatsApp

                    if (notificationGroup != null && rbSMS != null && rbSMS.isSelected()) {
                        useWhatsApp = false;
                    }

                    if (useWhatsApp) {
                        envoyerWhatsAppConfirmation(nouvelleReservation);
                    } else {
                        envoyerSMSConfirmation(nouvelleReservation);
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Réservation ajoutée avec succès pour " + nombreChambres + " chambre(s).");
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
                // En cas d'échec de l'ajout de la réservation, annuler la réservation des chambres
                selectedHotel.setNombreChambresReservees(selectedHotel.getNombreChambresReservees() - nombreChambres);
                hotelService.modifier(selectedHotel);

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
        String phoneNumber = DEFAULT_PHONE_NUMBER;

        if (phoneNumber.isEmpty()) {
            return; // L'utilisateur a annulé
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = reservation.getDateres().toLocalDate().format(formatter);

        String message = "Votre réservation à l'hôtel " + reservation.getHotel() +
                " pour le " + dateFormatted + " a été enregistrée avec succès. " +
                "Nombre de chambres: " + reservation.getNombreChambres() + ". " +
                "Statut: " + reservation.getStartres() + ". " +
                "Merci pour votre confiance. - TRAVELPRO";

        boolean messageEnvoye = WhatsAppService.sendWhatsApp(phoneNumber, message);

        if (messageEnvoye) {
            System.out.println("Message WhatsApp de confirmation envoyé avec succès au " + phoneNumber);
        } else {
            System.err.println("Échec de l'envoi du message WhatsApp de confirmation");
        }
    }

    /**
     * Envoie un SMS de confirmation pour une nouvelle réservation
     * @param reservation La réservation pour laquelle envoyer la confirmation
     */
    private void envoyerSMSConfirmation(ResHotel reservation) {
        // Demander le numéro de téléphone pour SMS
        String phoneNumber = DEFAULT_PHONE_NUMBER;

        if (phoneNumber.isEmpty()) {
            return; // L'utilisateur a annulé
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = reservation.getDateres().toLocalDate().format(formatter);

        String message = "Votre réservation à l'hôtel " + reservation.getHotel() +
                " pour le " + dateFormatted + " a été enregistrée avec succès. " +
                "Nombre de chambres: " + reservation.getNombreChambres() + ". " +
                "Statut: " + reservation.getStartres() + ". " +
                "Merci pour votre confiance. - TRAVELPRO";

        boolean messageEnvoye = SMSService.sendSMS(phoneNumber, message);

        if (messageEnvoye) {
            System.out.println("SMS de confirmation envoyé avec succès au " + phoneNumber);
        } else {
            System.err.println("Échec de l'envoi du SMS de confirmation");
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