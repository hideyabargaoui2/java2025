package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.hotel;
import service.HotelService;

public class ModifierHotelController {

    @FXML private TextField nomField;
    @FXML private TextField prixNuitField;
    @FXML private TextField nombreNuitField;
    @FXML private TextField standingField;
    @FXML private TextField adresseField;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;

    private hotel hotel;
    private final HotelService hotelService = new HotelService();
    private AfficherHotelController parentController;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du ModifierHotelController");
    }

    public void setHotel(hotel hotel) {
        this.hotel = hotel;
        remplirChamps();
    }

    private void remplirChamps() {
        if (hotel != null) {
            nomField.setText(hotel.getNom());
            prixNuitField.setText(String.valueOf(hotel.getPrixnuit()));
            nombreNuitField.setText(String.valueOf(hotel.getNombrenuit()));
            standingField.setText(hotel.getStanding());
            adresseField.setText(hotel.getAdresse());
        }
    }

    public void setParentController(AfficherHotelController controller) {
        this.parentController = controller;
    }

    @FXML
    private void confirmerModification() {
        try {
            String nom = nomField.getText();
            double prixNuit = Double.parseDouble(prixNuitField.getText());
            int nombreNuit = Integer.parseInt(nombreNuitField.getText());
            String standing = standingField.getText();
            String adresse = adresseField.getText();

            if (nom.isEmpty() || standing.isEmpty() || adresse.isEmpty()) {
                afficherAlerte("Veuillez remplir tous les champs.");
                return;
            }

            hotel.setNom(nom);
            hotel.setPrixnuit(prixNuit);
            hotel.setNombrenuit(nombreNuit);
            hotel.setStanding(standing);
            hotel.setAdresse(adresse);

            boolean success = hotelService.modifier(hotel);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Hôtel modifié avec succès.");
                alert.showAndWait();
            }

            if (parentController != null) {
                parentController.loadData();
            }

            fermerFenetre();

        } catch (NumberFormatException e) {
            afficherAlerte("Veuillez entrer des nombres valides pour le prix et le nombre de nuits.");
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