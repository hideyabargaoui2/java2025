package controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Restaurant;
import services.RestaurantServices;
import java.io.IOException;

public class AjouterRestaurant {

    @FXML
    private TextField TFid, TFadresse, TFclass, TFferme, TFnom, TFouvert, TFtype;

    @FXML
    private Button addresto, displayresto, ajoutermenu, modifierresto;

    @FXML
    void ajouter(ActionEvent event) {
        try {
            int id = Integer.parseInt(TFid.getText());
            int classement = Integer.parseInt(TFclass.getText());
            String nom = TFnom.getText();
            String adresse = TFadresse.getText();
            String type = TFtype.getText();
            String horaireOuvert = TFouvert.getText();
            String horaireFerme = TFferme.getText();

            if (nom.isEmpty() || adresse.isEmpty() || type.isEmpty() || horaireOuvert.isEmpty() || horaireFerme.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champ manquant");
                alert.setContentText("Tous les champs doivent être remplis !");
                alert.showAndWait();
                return;
            }

            Restaurant r = new Restaurant();
            r.setId(id);
            r.setNom(nom);
            r.setAdresse(adresse);
            r.setType(type);
            r.setHeure_ouv(java.sql.Time.valueOf(horaireOuvert + ":00"));
            r.setHeure_ferm(java.sql.Time.valueOf(horaireFerme + ":00"));
            r.setClassement(classement);

            RestaurantServices service = new RestaurantServices();
            service.add(r);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Restaurant ajouté avec succès !");
            alert.showAndWait();

            TFid.clear();
            TFadresse.clear();
            TFclass.clear();
            TFnom.clear();
            TFtype.clear();
            TFouvert.clear();
            TFferme.clear();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Erreur de saisie ! Vérifiez les champs numériques.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur est survenue : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void afficherresto(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherRestaurant.fxml"));
            TFid.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Menu");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirUpdate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateRestaurant.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Modifier Restaurant");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
