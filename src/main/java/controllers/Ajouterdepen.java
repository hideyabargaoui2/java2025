package controllers;

import Modules.Depensse;
import Service.Depensseservice;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class Ajouterdepen {

    @FXML
    private TextField categories, datepay, desc, modepay, monta;


    @FXML
    public void initialize() {
        // فقط حروف (مع مسافات) لـ categories
        categories.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                categories.setText(oldValue);
            }
        });

        // فقط حروف لـ desc
        desc.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                desc.setText(oldValue);
            }
        });

        // فقط حروف لـ modepay
        modepay.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                modepay.setText(oldValue);
            }
        });

        // فقط أرقام موجبة لـ monta
        monta.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                monta.setText(oldValue);
            }
        });
    }


    @FXML
    private Button submit,monafficher,update;

    @FXML
    void adduser(ActionEvent event) {
        try {
            // Récupération des valeurs depuis les champs
            String categorie = categories.getText().trim();
            String datePaiement = datepay.getText().trim();
            String modePaiement = modepay.getText().trim();
            String description = desc.getText().trim();
            String montantText = monta.getText().trim();

            // Vérification des champs vides
            if (categorie.isEmpty() || datePaiement.isEmpty() || modePaiement.isEmpty() || description.isEmpty() || montantText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champ manquant");
                alert.setContentText("Tous les champs doivent être remplis !");
                alert.showAndWait();
                return;
            }

            // Vérification du format du montant
            int montant = Integer.parseInt(montantText);

            // Création de l'objet Depensse
            Depensse depense = new Depensse(modePaiement, datePaiement, categorie, montant, description);

            // Appel au service pour ajouter la dépense
            Depensseservice service = new Depensseservice();
            service.add(depense);

            // Affichage d'une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Dépense ajoutée avec succès !");
            alert.showAndWait();

            // Réinitialisation des champs
            clearFields();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Veuillez entrer un montant valide (chiffre uniquement).");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur est survenue : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    void ondisplay(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Affichierdepensses.fxml"));
            categories.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    void ouvrirUpdate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateDepensse.fxml"));
            Parent root = loader.load();
            // ouvrir une nouvelle fenêtre (Stage)
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Modifier Dépense");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible d'ouvrir la fenêtre de modification.");
            alert.showAndWait();
        }
    }






    @FXML

    void ouvrirepageupdate(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateDepensse.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mettre à jour une dépense");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible d'ouvrir la page de mise à jour !");
            alert.showAndWait();
        }
    }

















    // Réinitialisation des champs
    private void clearFields() {
        categories.clear();
        datepay.clear();
        desc.clear();
        modepay.clear();
        monta.clear();
    }

    @FXML
    private void ouvrirerevenue(ActionEvent event) {
        // مثال: فتح نافذة جديدة أو تحميل FXML آخر
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouterrevenue.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Revenue");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ouvrirepageupdate(javafx.scene.input.MouseEvent mouseEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateDepensse.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mettre à jour une dépense");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible d'ouvrir la page de mise à jour !");
            alert.showAndWait();
        }
    }
}
