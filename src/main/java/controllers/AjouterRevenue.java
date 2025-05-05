package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Modules.Revenue;
import Service.Revenueservice;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AjouterRevenue {

    @FXML
    private TextField TFdate;
    @FXML
    private TextField TFmode;
    @FXML
    private TextField TFmontant;
    @FXML
    private TextField TFdevise;
    @FXML
    private ComboBox<Revenue> comboRevenues;

    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnAfficher;

    private final Revenueservice revenueService = new Revenueservice();

    @FXML
    public void initialize() {
        try {
            List<Revenue> revenues = revenueService.getAll();
            comboRevenues.getItems().addAll(revenues);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des revenus : " + e.getMessage());
        }

        // ✅ Contrôle de saisie
        allowOnlyLetters(TFmode);
        allowOnlyLetters(TFdevise);
        allowOnlyPositiveNumbers(TFmontant);
    }

    @FXML
    private void ajouter() {
        try {
            Revenue revenue = new Revenue();
            revenue.setDaterevenue(TFdate.getText());
            revenue.setModereception(TFmode.getText());
            revenue.setRmontant(Integer.parseInt(TFmontant.getText()));
            revenue.setDevise(TFdevise.getText());

            revenueService.add(revenue);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Revenue ajouté avec succès !");

            comboRevenues.getItems().clear();
            comboRevenues.getItems().addAll(revenueService.getAll());

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Montant ou devise invalide !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void supprimer(ActionEvent event) {
        Revenue selectedRevenue = comboRevenues.getSelectionModel().getSelectedItem();
        if (selectedRevenue == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un revenu à supprimer !");
            return;
        }
        try {
            revenueService.delete(selectedRevenue);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Revenue supprimé avec succès !");

            comboRevenues.getItems().clear();
            comboRevenues.getItems().addAll(revenueService.getAll());

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @FXML
    private void afficherRevenues() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Affichierrevenue.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Revenues");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture d'AfficherRevenue : " + e.getMessage());
        }
    }

    public void ouvrirUpdate(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Updaterevenue.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Modifier Revenu");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification.");
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ✅ Autoriser uniquement des lettres
    private void allowOnlyLetters(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                textField.setText(oldValue);
            }
        });
    }

    // ✅ Autoriser uniquement des chiffres positifs
    private void allowOnlyPositiveNumbers(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
            }
        });
    }
}
