package controllers;




import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.hotel;
import service.hotelservice;

import java.sql.SQLException;

public class ajouterhotelcontroller {

    @FXML
    private TextField adressehotel;

    @FXML
    private Button ajouter;

    @FXML
    private TextField nmbrsnuit;

    @FXML
    private Label nom;

    @FXML
    private TextField nomhotel;

    @FXML
    private TextField prixnuit;

    @FXML
    private ComboBox<String> stand;
    @FXML
    public void initialize() {
        stand.getItems().addAll("1 étoile", "2 étoiles", "3 étoiles", "4 étoiles", "5 étoiles");
    }
    @FXML
    void ajouterhotel(ActionEvent event) {
        String nom = nomhotel.getText();
        double prixParNuit = Double.parseDouble(prixnuit.getText());
        int nombreNuits = Integer.parseInt(nmbrsnuit.getText());
        String standing = stand.getSelectionModel().getSelectedItem().toString();
        String adresse = adressehotel.getText();
        hotel h = new hotel(nom, prixParNuit, nombreNuits, adresse, standing);
        hotelservice hs = new hotelservice();
        try {
            hs.ajouter(h);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();

        }


    }

}
