package controllers;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestaurantMapService {

    public void showMap(String address) {
        if (address == null || address.trim().isEmpty()) {
            System.err.println("Adresse invalide");
            return;
        }

        new Thread(() -> {
            try {
                String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
                String apiUrl = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress;

                HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (JavaFX RestaurantApp)");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());

                if (jsonArray.length() > 0) {
                    JSONObject location = jsonArray.getJSONObject(0);
                    double lat = Double.parseDouble(location.getString("lat"));
                    double lon = Double.parseDouble(location.getString("lon"));

                    Platform.runLater(() -> displayMap(lat, lon));
                } else {
                    System.err.println("Aucune localisation trouvée pour l'adresse : " + address);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displayMap(double lat, double lon) {
        WebView webView = new WebView();
        String mapUrl = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=16/" + lat + "/" + lon;
        webView.getEngine().load(mapUrl);

        Stage mapStage = new Stage();
        mapStage.setTitle("Carte du restaurant");
        mapStage.setScene(new Scene(webView, 800, 600));
        mapStage.show();
    }
}
