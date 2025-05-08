package controllers.OffreReservation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Offre;
import service.OffreService;

public class EditOffreController {
    @FXML private TextField lieuField;
    @FXML private DatePicker dateDepartPicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private TextField capaciteField;
    @FXML private TextField prixField;
    @FXML private TextArea descriptionArea;

    private OffreService offreService = new OffreService();
    private Offre offre;
    private IndexOffreController indexController;

    public void setOffre(Offre offre) {
        this.offre = offre;
        populateFields();
    }

    public void setIndexController(IndexOffreController indexController) {
        this.indexController = indexController;
    }

    private void populateFields() {
        lieuField.setText(offre.getLieu());
        dateDepartPicker.setValue(offre.getDateDepart());
        dateRetourPicker.setValue(offre.getDateRetour());
        capaciteField.setText(String.valueOf(offre.getCapacite()));
        prixField.setText(String.valueOf(offre.getPrixTotal()));
        descriptionArea.setText(offre.getDescription());
    }

    @FXML
    private void updateOffre() {
        try {
            offre.setLieu(lieuField.getText());
            offre.setDateDepart(dateDepartPicker.getValue());
            offre.setDateRetour(dateRetourPicker.getValue());
            offre.setCapacite(Integer.parseInt(capaciteField.getText()));
            offre.setPrixTotal(Double.parseDouble(prixField.getText()));
            offre.setDescription(descriptionArea.getText());

            offreService.updateOffre(offre);

            if (indexController != null) {
                indexController.refreshOffres();
            }

            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", "Données invalides", "Veuillez vérifier les informations saisies", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) lieuField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}