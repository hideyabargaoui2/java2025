package service;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service pour intégrer OpenStreetMap et rechercher des emplacements d'hôtels
 */
public class HotelMapService {

    /**
     * Recherche un hôtel par son nom et retourne son adresse en utilisant Nominatim (API OpenStreetMap)
     * @param hotelName Le nom de l'hôtel à rechercher
     * @return CompletableFuture avec l'adresse de l'hôtel
     */
    public CompletableFuture<String> searchHotelAddress(String hotelName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Construire l'URL pour la recherche Nominatim (OpenStreetMap)
                String encodedName = URLEncoder.encode(hotelName + " hotel", StandardCharsets.UTF_8);
                String urlString = "https://nominatim.openstreetmap.org/search"
                        + "?q=" + encodedName
                        + "&format=json"
                        + "&addressdetails=1"
                        + "&limit=1";

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Important: ajouter un User-Agent pour respecter les conditions d'utilisation de Nominatim
                connection.setRequestProperty("User-Agent", "HotelApplication/1.0");
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Analyser la réponse JSON
                    JSONArray results = new JSONArray(response.toString());

                    if (results.length() > 0) {
                        JSONObject place = results.getJSONObject(0);
                        // Extraire l'adresse complète du résultat
                        String address = place.getString("display_name");
                        return address;
                    }
                }

                return "";
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        });
    }

    /**
     * Recherche une image pour l'hôtel spécifié
     * @param hotelName Le nom de l'hôtel pour lequel chercher une image
     * @return CompletableFuture avec l'URL de l'image
     */
    public CompletableFuture<String> searchHotelImage(String hotelName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Note: Dans une application réelle, vous utiliseriez une API d'images comme Unsplash, Pixabay
                // Pour simplifier cet exemple, nous retournons un placeholder d'image
                return "https://via.placeholder.com/400x300?text=" + URLEncoder.encode(hotelName, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        });
    }

    /**
     * Recherche les coordonnées d'un lieu par son nom
     * @param placeName Le nom du lieu à rechercher
     * @return CompletableFuture avec un tableau contenant latitude et longitude
     */
    public CompletableFuture<double[]> searchCoordinates(String placeName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String encodedName = URLEncoder.encode(placeName, StandardCharsets.UTF_8);
                String urlString = "https://nominatim.openstreetmap.org/search"
                        + "?q=" + encodedName
                        + "&format=json"
                        + "&limit=1";

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "HotelApplication/1.0");
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONArray results = new JSONArray(response.toString());

                    if (results.length() > 0) {
                        JSONObject place = results.getJSONObject(0);
                        double lat = Double.parseDouble(place.getString("lat"));
                        double lon = Double.parseDouble(place.getString("lon"));
                        return new double[]{lat, lon};
                    }
                }

                return new double[]{0, 0};
            } catch (Exception e) {
                e.printStackTrace();
                return new double[]{0, 0};
            }
        });
    }

    /**
     * Ouvre OpenStreetMap avec l'emplacement de l'hôtel spécifié dans le navigateur
     * @param hotelName Le nom de l'hôtel à rechercher sur la carte
     */
    public void openInOpenStreetMap(String hotelName) {
        try {
            // D'abord rechercher les coordonnées
            searchCoordinates(hotelName + " hotel").thenAccept(coords -> {
                if (coords[0] != 0 && coords[1] != 0) {
                    try {
                        // Format d'URL pour OpenStreetMap
                        String osmUrl = "https://www.openstreetmap.org/?mlat=" + coords[0] + "&mlon=" + coords[1] + "&zoom=16";
                        Desktop.getDesktop().browse(new URI(osmUrl));
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Erreur lors de l'ouverture de OpenStreetMap: " + e.getMessage());
                    }
                } else {
                    showAlert("Impossible de trouver les coordonnées pour cet hôtel");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la recherche: " + e.getMessage());
        }
    }

    /**
     * Génère une URL pour ouvrir OpenStreetMap dans un navigateur externe
     * @param query La requête de recherche (nom + adresse de l'hôtel)
     * @return URL de OpenStreetMap pour la recherche
     */
    public String getOpenStreetMapUrl(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            return "https://www.openstreetmap.org/search?query=" + encodedQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return "https://www.openstreetmap.org";
        }
    }

    /**
     * Configure un champ texte pour rechercher automatiquement l'adresse lorsque son contenu change
     * @param nomField Champ de texte contenant le nom de l'hôtel
     * @param adresseField Champ de texte pour stocker l'adresse
     */
    public void setupAutoAddressLookup(TextField nomField, TextField adresseField) {
        // Ajouter un écouteur de changement de texte
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Ne déclencher la recherche que si le nouveau texte est suffisamment long
            if (newValue != null && newValue.length() > 3) {
                // Rechercher l'adresse de l'hôtel
                searchHotelAddress(newValue)
                        .thenAccept(address -> {
                            if (!address.isEmpty()) {
                                // Mettre à jour le champ d'adresse dans le thread JavaFX
                                Platform.runLater(() -> {
                                    adresseField.setText(address);
                                });
                            }
                        });
            }
        });
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}