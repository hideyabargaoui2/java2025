package controllers;

import Modules.Revenue;
import Service.Revenueservice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Affichierrevenue {

    @FXML
    private TableView<Revenue> tableRevenues;

    @FXML
    private TableColumn<Revenue, String> colDateRevenue;

    @FXML
    private TableColumn<Revenue, String> colModeReception;

    @FXML
    private TableColumn<Revenue, Integer> colMontant;

    @FXML
    private TableColumn<Revenue, Integer> colDevise;

    @FXML
    private ComboBox<Revenue> comboRevenue;

    @FXML
    private void ouvrirModification() {
        Revenue selection = tableRevenues.getSelectionModel().getSelectedItem();

        if (selection != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateRevenue.fxml"));
                Parent root = loader.load();

                UpdateRevenue controller = loader.getController();
                controller.setRevenueData(selection); // تمرير البيانات

                Stage stage = new Stage();
                stage.setTitle("Modifier Revenu");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("تنبيه");
            alert.setContentText("يرجى اختيار إيراد من الجدول أولاً.");
            alert.showAndWait();
        }
    }




    @FXML
    private void afficherRevenues() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Affichierrevenue.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Revenus");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture d'Affichierrevenue : " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void initialize() {
        chargerRevenues();
        comboRevenue.setItems(tableRevenues.getItems());
    }

    private void chargerRevenues() {
        try {
            Revenueservice service = new Revenueservice();
            List<Revenue> liste = service.getAll();

            ObservableList<Revenue> observableList = FXCollections.observableArrayList(liste);
            tableRevenues.setItems(observableList);

            colDateRevenue.setCellValueFactory(new PropertyValueFactory<>("daterevenue"));
            colModeReception.setCellValueFactory(new PropertyValueFactory<>("modereception"));
            colMontant.setCellValueFactory(new PropertyValueFactory<>("rmontant"));
            colDevise.setCellValueFactory(new PropertyValueFactory<>("devise"));

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setContentText("Impossible de charger les revenus : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void retourAjout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRevenue.fxml")); // À adapter si le FXML s'appelle autrement
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Revenu");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) tableRevenues.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void supprimerRevenue(ActionEvent actionEvent) {
        Revenue selected = comboRevenue.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setContentText("Veuillez sélectionner un revenu à supprimer.");
            alert.showAndWait();
            return;
        }

        try {
            Revenueservice service = new Revenueservice();
            service.delete(selected);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Revenu supprimé avec succès !");
            alert.showAndWait();

            chargerRevenues();
            comboRevenue.getSelectionModel().clearSelection();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de la suppression : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
