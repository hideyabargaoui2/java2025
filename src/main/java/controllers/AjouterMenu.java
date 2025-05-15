package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Menu;
import models.Restaurant;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import services.MenuService;
import services.RestaurantServices;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AjouterMenu implements Initializable {

    @FXML
    private TextField TFname;
    @FXML
    private TextField TFprix;
    @FXML
    private TextArea TFdesc;
    @FXML
    private ComboBox<Restaurant> TFnomresto;
    @FXML
    private Button addrmenu;
    @FXML
    private Button addresto;
    @FXML
    private Button afficherMenus;

    @FXML
    private Button genererIA;

    private final MenuService menuService = new MenuService();
    private final RestaurantServices restaurantServices = new RestaurantServices();

    // Clé API (remplacer par votre propre clé API après l'avoir sécurisée)
    private static final String GEMINI_API_KEY = "AIzaSyCn36S-Jz699VBEGxKGitXS9r53Qy2OGxI";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Restaurant> restaurants = restaurantServices.recuperer();
        TFnomresto.getItems().addAll(restaurants);

        TFnomresto.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        TFnomresto.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        TFnomresto.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau != null) {
                String suggestion = suggérerPlat(nouveau.getAdresse());
                TFname.setText(suggestion);
            }
        });
    }

    private String suggérerPlat(String adresse) {
        adresse = adresse.toLowerCase();

        Map<String, String> platsParAdresse = Map.ofEntries(
                Map.entry("italie", "Pizza Margherita"),
                Map.entry("rome", "Pizza Margherita"),
                Map.entry("milano", "Pizza Margherita"),
                Map.entry("japon", "Sushi"),
                Map.entry("tokyo", "Sushi"),
                Map.entry("france", "Boeuf Bourguignon"),
                Map.entry("paris", "Boeuf Bourguignon"),
                Map.entry("ariena", "Couscous"),
                Map.entry("casablanca", "Couscous")
        );

        for (Map.Entry<String, String> entry : platsParAdresse.entrySet()) {
            if (adresse.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "Plat du jour";
    }



    @FXML
    private void genererDescriptionIA(ActionEvent event) {
        String nomPlat = TFname.getText().trim();
        if (nomPlat.isEmpty()) {
            showAlert("Veuillez d'abord saisir un nom de plat !");
            return;
        }

        String description = genererDescriptionAvecGemini(nomPlat);
        TFdesc.setText(description);
    }

    private String genererDescriptionAvecGemini(String nomPlat) {
        OkHttpClient client = new OkHttpClient();

        String json = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        { \"text\": \"Rédige une courte description appétissante pour le plat suivant : " + nomPlat + "\" }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(GEMINI_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray candidates = jsonResponse.getJSONArray("candidates");
                JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                return parts.getJSONObject(0).getString("text");
            } else {
                System.err.println("Erreur Gemini : " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Description non disponible pour le moment.";
    }
    private void clearFields() {
        TFname.clear();
        TFdesc.clear();
        TFprix.clear();

    }
    @FXML
    private void ajouter(ActionEvent event) {
        String nomPlat = TFname.getText();
        String prixText = TFprix.getText();
        String description = TFdesc.getText();
        Restaurant restaurant = TFnomresto.getValue();

        if (nomPlat.isEmpty() || prixText.isEmpty() || description.isEmpty() || restaurant == null) {
            showAlert("Veuillez remplir tous les champs !");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(prixText);
        } catch (NumberFormatException e) {
            showAlert("Le prix doit être un nombre valide !");
            return;
        }

        Menu menu = new Menu(restaurant, nomPlat, (int) prix, description);
        menuService.add(menu);
        showAlert("Menu ajouté avec succès !");
        //fermerFenetre();
        clearFields();
    }

    @FXML
    public void afficherMenus() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherMenu.fxml"));
            TFname.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addresto(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterRestaurant.fxml"));
            TFname.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

   /* private void fermerFenetre() {
        Stage stage = (Stage) TFprix.getScene().getWindow();
        stage.close();
    }*/
}
