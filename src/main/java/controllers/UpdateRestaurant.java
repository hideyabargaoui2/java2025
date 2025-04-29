package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import models.Restaurant;
import services.RestaurantServices;

public class UpdateRestaurant {

    @FXML
    private TextField idField, nomField, adresseField, typeField, heureOuvField, heureFermField, classementField;

    @FXML
    void updateRestaurant(ActionEvent event) {
        try {
            int id = Integer.parseInt(idField.getText());
            String nom = nomField.getText();
            String adresse = adresseField.getText();
            String type = typeField.getText();
            String heureOuv = heureOuvField.getText();
            String heureFerm = heureFermField.getText();
            int classement = Integer.parseInt(classementField.getText());

            if (nom.isEmpty() || adresse.isEmpty() || type.isEmpty() || heureOuv.isEmpty() || heureFerm.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs vides");
                alert.setContentText("Veuillez remplir tous les champs !");
                alert.showAndWait();
                return;
            }

            Restaurant r = new Restaurant();
            r.setId(id);
            r.setNom(nom);
            r.setAdresse(adresse);
            r.setType(type);
            r.setHeure_ouv(java.sql.Time.valueOf(heureOuv + ":00"));
            r.setHeure_ferm(java.sql.Time.valueOf(heureFerm + ":00"));
            r.setClassement(classement);

            RestaurantServices service = new RestaurantServices();
            service.update(r);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Restaurant mis à jour avec succès !");
            alert.showAndWait();

            idField.clear();
            nomField.clear();
            adresseField.clear();
            typeField.clear();
            heureOuvField.clear();
            heureFermField.clear();
            classementField.clear();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Veuillez entrer des nombres valides pour l'ID et le classement.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de la mise à jour : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
