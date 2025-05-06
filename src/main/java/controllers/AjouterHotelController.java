package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.hotel;
import service.HotelService;
import service.HotelMapService;
import javafx.scene.input.KeyEvent;
import java.util.function.UnaryOperator;
import javafx.util.converter.DoubleStringConverter;

public class AjouterHotelController {

    @FXML private TextField nomField;
    @FXML private TextField prixNuitField;
    @FXML private TextField nombreNuitField;
    @FXML private TextField adresseField;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;
    @FXML private ComboBox<String> standingComboBox;

    private final HotelService hotelService = new HotelService();
    private final HotelMapService mapService = new HotelMapService();
    private AfficherHotelController parentController;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du AjouterHotelController");
        // Vérification que les champs et boutons sont correctement injectés
        if (nomField == null || prixNuitField == null || nombreNuitField == null ||
                adresseField == null || btnConfirmer == null || btnAnnuler == null) {
            System.err.println("Erreur: un ou plusieurs éléments FXML n'ont pas été injectés correctement");
        }

        // Configuration explicite des événements boutons
        if (btnConfirmer != null) {
            btnConfirmer.setOnAction(event -> confirmerAjout());
        }

        if (btnAnnuler != null) {
            btnAnnuler.setOnAction(event -> annulerAjout());
        }

        // Ajouter le contrôle de saisie pour le champ de prix nuit (accepte uniquement les chiffres)
        configurerChampNumerique(prixNuitField);

        // Également configurer le champ de nombre de nuits pour coherence
        configurerChampNumerique(nombreNuitField);

        // Initialiser la ComboBox avec les options d'étoiles sous forme de symboles
        standingComboBox.getItems().addAll(
                "⭐",
                "⭐⭐",
                "⭐⭐⭐",
                "⭐⭐⭐⭐",
                "⭐⭐⭐⭐⭐"
        );

        // Configurer la recherche automatique d'adresse lors de la saisie du nom d'hôtel
        configureHotelSearch();
    }

    /**
     * Configure la recherche automatique d'adresse d'hôtel basée sur le nom
     */
    private void configureHotelSearch() {
        // Utiliser le service de carte pour configurer la recherche automatique
        mapService.setupAutoAddressLookup(nomField, adresseField);

        // Ajouter un button d'action pour rechercher explicitement
        nomField.setOnAction(event -> {
            String hotelName = nomField.getText();
            if (hotelName != null && !hotelName.isEmpty()) {
                mapService.openInOpenStreetMap(hotelName);
                mapService.searchHotelAddress(hotelName)
                        .thenAccept(address -> {
                            if (!address.isEmpty()) {
                                adresseField.setText(address);
                            }
                        });
            }
        });
    }

    /**
     * Configure un champ de texte pour n'accepter que des valeurs numériques
     * @param textField Le champ de texte à configurer
     */
    private void configurerChampNumerique(TextField textField) {
        // Définir un filtre qui n'accepte que les chiffres et un point décimal
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Permet les chiffres, un point décimal et champ vide
            if (newText.matches("^\\d*\\.?\\d*$")) {
                return change;
            }
            return null; // Rejette tous les autres caractères
        };

        // Appliquer le TextFormatter avec notre filtre au champ
        textField.setTextFormatter(new TextFormatter<>(filter));
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

            String standing = standingComboBox.getValue();
            String adresse = adresseField.getText();

            // Validation des champs
            if (nom.isEmpty() || standing == null || adresse.isEmpty()) {
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