package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Restaurant;
import services.RestaurantServices;

public class UpdateRestaurant {

    @FXML private TextField nomField;
    @FXML private TextField adresseField;
    @FXML private TextField typeField;
    @FXML private TextField TFclass;

    private Restaurant restaurant;
    private final RestaurantServices service = new RestaurantServices();

    public void setRestaurant(Restaurant r) {
        this.restaurant = r;
        nomField.setText(r.getNom());
        adresseField.setText(r.getAdresse());
        typeField.setText(r.getType());
        TFclass.setText(String.valueOf(r.getClassement()));
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        restaurant.setNom(nomField.getText());
        restaurant.setAdresse(adresseField.getText());
        restaurant.setType(typeField.getText());
        try {
            restaurant.setClassement(Integer.parseInt(TFclass.getText()));
        } catch (NumberFormatException e) {
            restaurant.setClassement(0);
        }

        service.modifier(restaurant);
        ((Stage) nomField.getScene().getWindow()).close();
    }
}
