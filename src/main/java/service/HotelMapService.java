package service;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service pour interagir avec l'API OpenStreetMap et obtenir des images d'hôtels
 */
public class HotelMapService {

    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search";
    private static final String OSM_URL = "https://www.openstreetmap.org/search?query=";
    private static final String USER_AGENT = "TravelPro/1.0"; // Identifiant pour l'API
    private static final int REQUEST_TIMEOUT = 30000; // 30 secondes pour les requêtes

    /**
     * Récupère les coordonnées géographiques d'une adresse
     * @param address L'adresse à rechercher
     * @return CompletableFuture contenant une Pair de coordonnées (latitude, longitude)
     */
    public CompletableFuture<Pair<Double, Double>> getCoordinatesFromAddress(String address) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Encoder l'adresse pour l'URL
                String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
                URL url = new URL(NOMINATIM_API + "?q=" + encodedAddress + "&format=json&limit=1");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setConnectTimeout(REQUEST_TIMEOUT);
                connection.setReadTimeout(REQUEST_TIMEOUT);

                // Respecter la politique d'utilisation de Nominatim (max 1 requête par seconde)
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Analyser la réponse JSON
                    JSONArray results = new JSONArray(response.toString());
                    if (results.length() > 0) {
                        JSONObject result = results.getJSONObject(0);
                        double lat = result.getDouble("lat");
                        double lon = result.getDouble("lon");

                        // Respecter les limites de taux de Nominatim (1 requête par seconde)
                        TimeUnit.MILLISECONDS.sleep(1100);

                        return new Pair<>(lat, lon);
                    }
                } else {
                    System.err.println("Erreur de l'API Nominatim: " + responseCode);
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la récupération des coordonnées: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("Interruption lors de l'attente entre les requêtes: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
            return null;
        });
    }

    /**
     * Recherche une image pour un hôtel donné
     * @param hotelName Le nom de l'hôtel
     * @return CompletableFuture contenant l'URL de l'image ou null
     */
    public CompletableFuture<String> searchHotelImage(String hotelName) {
        return CompletableFuture.supplyAsync(() -> {
            if (hotelName == null || hotelName.trim().isEmpty()) {
                return null;
            }

            // Plusieurs sources d'images à essayer pour une meilleure fiabilité
            try {
                // Encoder le nom de l'hôtel pour l'URL
                String encodedHotelName = URLEncoder.encode(hotelName, StandardCharsets.UTF_8.toString());

                // Tableau de services d'images à essayer dans l'ordre
                String[] imageServices = {
                        "https://source.unsplash.com/featured/?" + encodedHotelName + ",hotel",
                        "https://loremflickr.com/600/400/" + encodedHotelName + ",hotel",
                        "https://picsum.photos/seed/" + encodedHotelName + "/600/400"
                };

                // Essayer chaque service d'images jusqu'à trouver une image fonctionnelle
                for (String serviceUrl : imageServices) {
                    try {
                        URL url = new URL(serviceUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("HEAD");
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);

                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            // Vérifier le type de contenu
                            String contentType = connection.getContentType();
                            if (contentType != null && contentType.startsWith("image/")) {
                                return serviceUrl;
                            }
                        }
                    } catch (Exception e) {
                        // Ignorer et essayer le service suivant
                        System.err.println("Erreur avec le service d'images " + serviceUrl + ": " + e.getMessage());
                    }
                }

                // Essayer de générer une image de couleur aléatoire avec le nom de l'hôtel
                // Cela fonctionne comme une solution de secours
                String fallbackImageUrl = "https://dummyimage.com/600x400/" + getRandomColor() +
                        "/ffffff&text=" + encodedHotelName;

                return fallbackImageUrl;

            } catch (IOException e) {
                System.err.println("Erreur lors de la recherche d'image: " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Génère une couleur hexadécimale aléatoire pour les images de secours
     * @return Code de couleur hexadécimal sans #
     */
    private String getRandomColor() {
        // Générer une couleur pastel pour un meilleur contraste avec le texte blanc
        int r = 100 + (int)(Math.random() * 155);
        int g = 100 + (int)(Math.random() * 155);
        int b = 100 + (int)(Math.random() * 155);

        return String.format("%02x%02x%02x", r, g, b);
    }

    /**
     * Configure la recherche automatique d'adresses basée sur le nom d'hôtel
     * @param nomField Champ de saisie du nom de l'hôtel
     * @param adresseField Champ de saisie de l'adresse qui sera automatiquement rempli
     */
    public static void setupAutoAddressLookup(TextField nomField, TextField adresseField) {
        // Ajouter un listener pour détecter quand l'utilisateur a fini de saisir le nom
        nomField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            // Si le focus est perdu et qu'il y a un texte dans le champ
            if (!newVal && !nomField.getText().isEmpty() && (adresseField.getText() == null || adresseField.getText().isEmpty())) {
                // Créer une instance du service et rechercher l'adresse
                HotelMapService service = new HotelMapService();
                // Ajouter un indicateur de chargement
                adresseField.setPromptText("Recherche en cours...");
                service.searchHotelAddress(nomField.getText())
                        .thenAccept(address -> {
                            if (address != null && !address.isEmpty()) {
                                // Mettre à jour le champ d'adresse sur le thread JavaFX
                                Platform.runLater(() -> {
                                    adresseField.setText(address);
                                    adresseField.setPromptText("");
                                });
                            } else {
                                Platform.runLater(() -> {
                                    adresseField.setPromptText("Adresse non trouvée");
                                });
                            }
                        })
                        .exceptionally(e -> {
                            Platform.runLater(() -> {
                                adresseField.setPromptText("Erreur lors de la recherche");
                            });
                            return null;
                        });
            }
        });
    }

    /**
     * Recherche l'adresse d'un hôtel basée sur son nom
     * @param hotelName Le nom de l'hôtel à rechercher
     * @return CompletableFuture contenant l'adresse trouvée ou null
     */
    public CompletableFuture<String> searchHotelAddress(String hotelName) {
        return CompletableFuture.supplyAsync(() -> {
            if (hotelName == null || hotelName.trim().isEmpty()) {
                return null;
            }

            try {
                // Encoder le nom de l'hôtel pour l'URL
                String encodedHotelName = URLEncoder.encode(hotelName + " hotel", StandardCharsets.UTF_8.toString());
                URL url = new URL(NOMINATIM_API + "?q=" + encodedHotelName + "&format=json&limit=1");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setConnectTimeout(REQUEST_TIMEOUT);
                connection.setReadTimeout(REQUEST_TIMEOUT);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Analyser la réponse JSON
                    JSONArray results = new JSONArray(response.toString());
                    if (results.length() > 0) {
                        JSONObject result = results.getJSONObject(0);

                        // Construire une adresse complète à partir des différents champs disponibles
                        StringBuilder addressBuilder = new StringBuilder();

                        // Ajouter le nom du lieu si disponible
                        if (result.has("display_name")) {
                            String displayName = result.getString("display_name");

                            // Filtrer le nom d'hôtel de l'adresse pour éviter la redondance
                            String lowerDisplayName = displayName.toLowerCase();
                            String lowerHotelName = hotelName.toLowerCase();

                            if (lowerDisplayName.contains(lowerHotelName)) {
                                // Si le nom d'hôtel est dans l'adresse, prendre l'adresse complète
                                addressBuilder.append(displayName);
                            } else {
                                // Sinon ajouter le nom de l'hôtel devant l'adresse
                                addressBuilder.append(hotelName).append(", ").append(displayName);
                            }
                        } else {
                            // Construction d'adresse alternative si display_name n'est pas disponible
                            if (result.has("name")) {
                                addressBuilder.append(result.getString("name")).append(", ");
                            } else {
                                addressBuilder.append(hotelName).append(", ");
                            }

                            // Ajouter les éléments d'adresse disponibles
                            if (result.has("address")) {
                                JSONObject address = result.getJSONObject("address");

                                // Ordre de priorité pour les éléments d'adresse
                                String[] addressElements = {
                                        "road", "house_number", "suburb", "city", "town",
                                        "village", "municipality", "district", "county",
                                        "state", "postcode", "country"
                                };

                                for (String element : addressElements) {
                                    if (address.has(element)) {
                                        addressBuilder.append(address.getString(element)).append(", ");
                                    }
                                }

                                // Supprimer la dernière virgule et espace
                                if (addressBuilder.length() > 2) {
                                    addressBuilder.setLength(addressBuilder.length() - 2);
                                }
                            }
                        }

                        // Respecter la politique de Nominatim (1 requête par seconde)
                        TimeUnit.MILLISECONDS.sleep(1100);

                        return addressBuilder.toString();
                    }
                } else {
                    System.err.println("Erreur de l'API Nominatim lors de la recherche d'adresse: " + responseCode);
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la recherche d'adresse: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("Interruption lors de l'attente entre les requêtes: " + e.getMessage());
                Thread.currentThread().interrupt();
            }

            return null;
        });
    }

    /**
     * Ouvre une carte OpenStreetMap dans le navigateur par défaut
     * @param address L'adresse à montrer sur la carte
     */
    public void openMapInBrowser(String address) {
        if (address != null && !address.isEmpty()) {
            try {
                String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
                URI uri = new URI(OSM_URL + encodedAddress);

                // Vérifier si Desktop est supporté
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(uri);
                } else {
                    System.err.println("L'ouverture du navigateur n'est pas supportée sur cette plateforme");
                }
            } catch (IOException | URISyntaxException e) {
                System.err.println("Erreur lors de l'ouverture de la carte dans le navigateur: " + e.getMessage());
            }
        }
    }

    /**
     * Récupère les détails d'un lieu à partir de ses coordonnées
     * @param lat Latitude
     * @param lon Longitude
     * @return CompletableFuture contenant les détails du lieu (nom, adresse)
     */
    public CompletableFuture<JSONObject> getLocationDetails(double lat, double lon) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(NOMINATIM_API + "?format=json&lat=" + lat + "&lon=" + lon + "&addressdetails=1");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setConnectTimeout(REQUEST_TIMEOUT);
                connection.setReadTimeout(REQUEST_TIMEOUT);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray results = new JSONArray(response.toString());
                    if (results.length() > 0) {
                        // Respecter la politique de Nominatim (1 requête par seconde)
                        TimeUnit.MILLISECONDS.sleep(1100);
                        return results.getJSONObject(0);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération des détails de l'emplacement: " + e.getMessage());
            }
            return null;
        });
    }
}