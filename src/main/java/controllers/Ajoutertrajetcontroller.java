package controllers;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
public class Ajoutertrajetcontroller {


    @FXML
    private TextField departField;

    @FXML
    private TextField destinationField;

    @FXML
    private DatePicker dateField;

    @FXML
    private Button ajouterButton;

    @FXML
    private void initialize() {
        // Initialisation si nécessaire
    }

    @FXML
    private void ajouterTrajet() {
        String depart = departField.getText();
        String destination = destinationField.getText();
        String date = (dateField.getValue() != null) ? dateField.getValue().toString() : "";

        // Logique d'ajout de trajet (à adapter selon ton modèle ou ta base de données)
        System.out.println("Trajet ajouté : " + depart + " -> " + destination + " le " + date);

        // Optionnel : réinitialiser les champs
        departField.clear();
        destinationField.clear();
        dateField.setValue(null);
    }
}





