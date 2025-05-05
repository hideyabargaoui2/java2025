package controllers;

import Modules.Depensse;
import Service.Depensseservice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;



import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherDepensse {

    @FXML
    private TableView<Depensse> tableDepenses;

    @FXML
    private TableView<Depensse> depenseTable;

    @FXML
    private void modifierDepense() {
        Depensse depenseSelectionnee = depenseTable.getSelectionModel().getSelectedItem();
        if (depenseSelectionnee != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateDepensse.fxml"));
                Parent root = loader.load();

                UpdateDepensse controller = loader.getController();
                controller.setDepensseData(depenseSelectionnee); // تمرير الكائن المحدد

                Stage stage = new Stage();
                stage.setTitle("Modifier une dépense");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Veuillez sélectionner une dépense à modifier.");
        }
    }


    @FXML
    private TableColumn<Depensse, String> colModePay;

    @FXML
    private TableColumn<Depensse, String> colDatePay;

    @FXML
    private TableColumn<Depensse, String> colCategorie;

    @FXML
    private TableColumn<Depensse, Integer> colMontant;

    @FXML
    private TableColumn<Depensse, String> colDescription;

    @FXML
    private TableView<Depensse> tableRestaurants;

    @FXML
    private ComboBox<Depensse> comboaffiche;


    @FXML
    void initialize() {
        chargerDepenses();
        comboaffiche.setItems(tableDepenses.getItems());

    }

    @FXML
    void ouvrirModifier(ActionEvent event) {
        Depensse selected = tableDepenses.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setContentText("Veuillez sélectionner une dépense à modifier.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateDepensse.fxml"));
            Parent root = loader.load();

            // تمرير بيانات الديبونس إلى الكنترولر
            controllers.UpdateDepensse controller = loader.getController();
            controller.setDepensseData(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier Dépense");
            stage.setScene(new Scene(root));
            stage.show();

            // (اختياري) إغلاق الصفحة الحالية
            // ((Stage) tableDepenses.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void chargerDepenses() {
        try {
            Depensseservice service = new Depensseservice();
            List<Depensse> liste = service.getAll();

            ObservableList<Depensse> observableList = FXCollections.observableArrayList(liste);
            tableDepenses.setItems(observableList);

            colModePay.setCellValueFactory(new PropertyValueFactory<>("moddepay"));
            colDatePay.setCellValueFactory(new PropertyValueFactory<>("datepay"));
            colCategorie.setCellValueFactory(new PropertyValueFactory<>("categories"));
            colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
            colDescription.setCellValueFactory(new PropertyValueFactory<>("descripiton"));

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setContentText("Impossible de charger les dépenses : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void retourAjout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouterdepensses.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Dépense");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer l'ancienne fenêtre
            Stage currentStage = (Stage) tableDepenses.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    @FXML
    public void supprimeraffichage(ActionEvent actionEvent) {
        Depensse selected = comboaffiche.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setContentText("Veuillez sélectionner une dépense à supprimer.");
            alert.showAndWait();
            return;
        }

        try {
            Depensseservice service = new Depensseservice();
            service.delete(selected); // فرضًا أن لديك méthode delete(Depensse d)

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Dépense supprimée avec succès !");
            alert.showAndWait();

            // Rafraîchir l'affichage
            chargerDepenses();

            // Vider la sélection du combo
            comboaffiche.getSelectionModel().clearSelection();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de la suppression : " + e.getMessage());
            alert.showAndWait();
        }
    }

}
