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
import java.time.LocalTime;
import java.util.List;
public class ModifierResHotelController {
    @FXML private ComboBox<String> hotelCombo;
    @FXML private TextField startresField;
    @FXML private DatePicker dateResPicker;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;
    private ResHotel resHotel;
    private final ResHotelService resHotelService = new ResHotelService();
    private final HotelService hotelService = new HotelService();
    private AfficherResHotelController parentController;
    @FXML
    public void initialize() {
        System.out.println("Initialisation du ModifierResHotelController");

        // Charger la liste des hôtels dans le ComboBox
        chargerHotels();

        // Configuration explicite des événements boutons
        if (btnConfirmer != null) {
            btnConfirmer.setOnAction(event -> confirmerModification());
        }

        if (btnAnnuler != null) {
            btnAnnuler.setOnAction(event -> annulerModification());
        }
    }
    private void chargerHotels() {
        try {
            List<hotel> hotels = HotelService.getAllHotels(); // Utilisation de la méthode statique
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
        remplirChamps();
    }
    private void remplirChamps() {
        if (resHotel != null) {
            hotelCombo.setValue(resHotel.getHotel());
            startresField.setText(resHotel.getStartres());
            LocalDateTime dateTime = resHotel.getDateres();
            if (dateTime != null) {
                dateResPicker.setValue(dateTime.toLocalDate());
            }
        }
    }
    public void setParentController(AfficherResHotelController controller) {
        this.parentController = controller;
    }
    @FXML
    private void confirmerModification() {
        try {
            String hotel = hotelCombo.getValue();
            String startres = startresField.getText();  // Corrigé: startres au lieu de statres
            LocalDate dateResDate = dateResPicker.getValue();

            // Conversion de LocalDate à LocalDateTime pour correspondre au type datetime dans la BD
            LocalDateTime dateres = null;
            if (dateResDate != null) {
                // Si la réservation avait déjà une heure, la conserver, sinon mettre 00:00
                if (resHotel.getDateres() != null) {
                    LocalTime time = resHotel.getDateres().toLocalTime();
                    dateres = LocalDateTime.of(dateResDate, time);
                } else {
                    dateres = LocalDateTime.of(dateResDate, LocalTime.of(0, 0));
                }
            }

            if (hotel == null || hotel.isEmpty() || startres.isEmpty() || dateres == null) {
                afficherAlerte("Veuillez remplir tous les champs.");
                return;
            }

            resHotel.setHotel(hotel);
            resHotel.setStartres(startres);  // Corrigé: setStartres au lieu de setStatres
            resHotel.setDateres(dateres);

            boolean success = resHotelService.modifier(resHotel);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Réservation modifiée avec succès.");
                alert.showAndWait();
            } else {
                afficherAlerte("Erreur lors de la modification de la réservation.");
            }

            if (parentController != null) {
                parentController.loadData();
            }

            fermerFenetre();
        } catch (Exception e) {
            afficherAlerte("Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerModification() {
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