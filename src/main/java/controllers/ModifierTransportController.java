package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Transport;
import services.TransportService;

public class ModifierTransportController {

    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField compagnieField;
    @FXML private TextField prixField;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;

    private Transport transport;
    private final TransportService transportService = new TransportService();
    private AfficherTransportController parentController;

    /**
     * Initialise le contrôleur de modification
     */
    @FXML
    public void initialize() {
        System.out.println("Initialisation du ModifierTransportController");
    }

    /**
     * Définit le transport à modifier et pré-remplit les champs
     * @param transport Le transport à modifier
     */
    public void setTransport(Transport transport) {
        this.transport = transport;
        remplirChamps();
    }

    /**
     * Pré-remplit les champs avec les valeurs du transport
     */
    private void remplirChamps() {
        if (transport != null) {
            // Remplir les champs avec les données du transport
            typeComboBox.setValue(transport.getType());
            compagnieField.setText(transport.getCompagnie());
            prixField.setText(String.valueOf(transport.getPrix()));
        }
    }

    /**
     * Définit le contrôleur parent pour rafraîchir la liste après modification
     * @param controller Le contrôleur parent
     */
    public void setParentController(AfficherTransportController controller) {
        this.parentController = controller;
    }

    /**
     * Gère la confirmation de la modification
     */
    @FXML
    private void confirmerModification() {
        try {
            // Récupérer les valeurs des champs
            String type = typeComboBox.getValue();
            String compagnie = compagnieField.getText().trim();
            double prix = Double.parseDouble(prixField.getText().trim());

            // Vérifier que tous les champs sont remplis
            if (type == null || type.isEmpty() || compagnie.isEmpty()) {
                afficherAlerte("Veuillez remplir tous les champs");
                return;
            }

            // Vérifier que le prix est positif
            if (prix <= 0) {
                afficherAlerte("Le prix doit être positif");
                return;
            }

            // Mettre à jour l'objet transport
            transport.setType(type);
            transport.setCompagnie(compagnie);
            transport.setPrix(prix);

            // Enregistrer les modifications
            transportService.modifier(transport);

            // Rafraîchir la liste des transports
            if (parentController != null) {
                parentController.loadData();
            }

            // Fermer la fenêtre
            fermerFenetre();

        } catch (NumberFormatException e) {
            afficherAlerte("Veuillez entrer un nombre valide pour le prix");
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