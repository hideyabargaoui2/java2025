package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.hotel;
import service.HotelService;

public class AjouterHotelController {

    @FXML private TextField nomField;
    @FXML private TextField prixNuitField;
    @FXML private TextField nombreNuitField;
    @FXML private TextField standingField;
    @FXML private TextField adresseField;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;

    private final HotelService hotelService = new HotelService();
    private AfficherHotelController parentController;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du AjouterHotelController");
        // Vérification que les champs et boutons sont correctement injectés
        if (nomField == null || prixNuitField == null || nombreNuitField == null ||
                standingField == null || adresseField == null || btnConfirmer == null || btnAnnuler == null) {
            System.err.println("Erreur: un ou plusieurs éléments FXML n'ont pas été injectés correctement");
        }

        // Configuration explicite des événements boutons
        if (btnConfirmer != null) {
            btnConfirmer.setOnAction(event -> confirmerAjout());
        }

        if (btnAnnuler != null) {
            btnAnnuler.setOnAction(event -> annulerAjout());
        }
    }

    public void setParentController(AfficherHotelController controller) {
        this.parentController = controller;
    }

    @FXML
    public void confirmerAjout() {
        try {
            System.out.println("Confirmation de l'ajout d'un hôtel");
            // Récupérer les valeurs des champs
            String nom = nomField.getText();

            // Validation des valeurs numériques avec messages d'erreur spécifiques
            double prixNuit;
            try {
                prixNuit = Double.parseDouble(prixNuitField.getText());
                if (prixNuit <= 0) {
                    afficherAlerte("Le prix par nuit doit être supérieur à zéro.");
                    return;
                }
            } catch (NumberFormatException e) {
                afficherAlerte("Veuillez entrer un nombre valide pour le prix par nuit.");
                return;
            }

            int nombreNuit;
            try {
                nombreNuit = Integer.parseInt(nombreNuitField.getText());
                if (nombreNuit <= 0) {
                    afficherAlerte("Le nombre de nuits doit être supérieur à zéro.");
                    return;
                }
            } catch (NumberFormatException e) {
                afficherAlerte("Veuillez entrer un nombre entier valide pour le nombre de nuits.");
                return;
            }

            String standing = standingField.getText();
            String adresse = adresseField.getText();

            // Validation des champs
            if (nom.isEmpty() || standing.isEmpty() || adresse.isEmpty()) {
                afficherAlerte("Veuillez remplir tous les champs.");
                return;
            }

            // Créer un nouvel objet hôtel
            hotel nouveauHotel = new hotel(0, nom, prixNuit, nombreNuit, standing, adresse);
            System.out.println("Nouvel hôtel à ajouter: " + nouveauHotel);

            // Ajouter l'hôtel à la base de données
            boolean success = hotelService.ajouter(nouveauHotel);
            System.out.println("Résultat de l'ajout: " + success);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Hôtel ajouté avec succès.");
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
                afficherAlerte("Erreur lors de l'ajout de l'hôtel.");
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