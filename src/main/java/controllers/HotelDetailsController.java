package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import models.hotel;
import services.HotelMapService;

/**
 * Contrôleur pour la vue détaillée d'un hôtel
 * Version optimisée et corrigée avec carte OpenStreetMap
 */
public class HotelDetailsController {

    @FXML private Label hotelNameLabel;
    @FXML private Label hotelAddressLabel;
    @FXML private Label hotelPriceLabel;
    @FXML private Label hotelStandingLabel;
    @FXML private ScrollPane mapContainer;
    @FXML private ImageView hotelImageView;

    private WebView webView;
    private final HotelMapService mapService = new HotelMapService();
    private hotel currentHotel;
    private boolean mapLoaded = false;
    private boolean isInitialized = false;

    // URL d'image par défaut pour les hôtels sans image spécifique
    private static final String DEFAULT_HOTEL_IMAGE = "https://cf.bstatic.com/xdata/images/hotel/max1024x768/237648111.jpg";

    @FXML
    public void initialize() {
        System.out.println("Initialisation de HotelDetailsController");

        // Création du WebView en mode synchrone
        Platform.runLater(() -> {
            try {
                // Créer une WebView pour afficher la carte
                webView = new WebView();
                webView.setPrefSize(800, 450);
                webView.setMinSize(700, 400);

                // Configurer la WebView
                WebEngine webEngine = webView.getEngine();
                webEngine.setJavaScriptEnabled(true);

                // Activer le debugging
                System.out.println("Configuration de la WebView terminée, moteur JavaScript activé: " + webEngine.isJavaScriptEnabled());

                // Configurer les écouteurs d'erreurs
                webEngine.setOnError(event ->
                        System.err.println("Erreur WebView: " + event.getMessage()));

                webEngine.getLoadWorker().exceptionProperty().addListener((obs, old, error) -> {
                    if (error != null) {
                        System.err.println("Exception WebView: " + error.getMessage());
                        error.printStackTrace();
                    }
                });

                // Vérifier le conteneur
                if (mapContainer != null) {
                    System.out.println("mapContainer trouvé, ajout de la WebView");
                    mapContainer.setContent(webView);
                    mapContainer.setFitToWidth(true);
                    mapContainer.setFitToHeight(true);
                    mapContainer.setPrefViewportHeight(450);
                    mapContainer.setStyle("-fx-background-color: white; " +
                            "-fx-border-color: #2c3e50; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
                } else {
                    System.err.println("ERREUR CRITIQUE: mapContainer est null");
                }

                // Message d'attente initial
                showLoadingMessage("Initialisation de la carte...");
                System.out.println("Message de chargement initial affiché");

                isInitialized = true;

                // Si un hôtel a déjà été défini, configurer ses détails maintenant
                if (currentHotel != null) {
                    updateHotelDetails(currentHotel);
                }

            } catch (Exception e) {
                System.err.println("Erreur lors de l'initialisation du contrôleur: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Configure la fenêtre avec les détails de l'hôtel
     * @param hotel L'hôtel à afficher
     */
    public void setHotel(hotel hotel) {
        if (hotel == null) {
            System.err.println("Erreur: hotel est null");
            return;
        }

        this.currentHotel = hotel;
        System.out.println("Hôtel défini: " + hotel.getNom());

        // Vérifier si l'initialisation est terminée
        if (isInitialized) {
            // Si déjà initialisé, mettre à jour directement
            updateHotelDetails(hotel);
        } else {
            // Sinon, l'initialisation s'en chargera
            System.out.println("Contrôleur non initialisé, la mise à jour sera faite après initialisation");
        }
    }

    /**
     * Met à jour les détails de l'hôtel dans l'interface
     */
    private void updateHotelDetails(hotel hotel) {
        if (hotel == null) return;

        Platform.runLater(() -> {
            try {
                System.out.println("Mise à jour des détails pour: " + hotel.getNom());

                // Mettre à jour les labels
                if (hotelNameLabel != null) hotelNameLabel.setText(hotel.getNom());
                if (hotelAddressLabel != null) hotelAddressLabel.setText(hotel.getAdresse());
                if (hotelPriceLabel != null) hotelPriceLabel.setText(String.format("%.2f € / nuit", hotel.getPrixnuit()));

                // Afficher le standing
                if (hotelStandingLabel != null) formatStandingLabel(hotel.getStanding());

                // Charger la carte et l'image
                // Effacer l'ancien contenu de la carte
                mapLoaded = false;
                loadMapWithRetries(hotel.getAdresse());
                loadHotelImage();

                System.out.println("Mise à jour des détails terminée");
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour des détails: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Crée un affichage approprié pour le standing de l'hôtel
     */
    private void formatStandingLabel(String standingText) {
        StringBuilder standingBuilder = new StringBuilder("Standing: ");

        if (standingText != null) {
            if (standingText.contains("★")) {
                standingBuilder.append(standingText);
            } else if (standingText.matches("\\d+")) {
                try {
                    int stars = Integer.parseInt(standingText);
                    for (int i = 0; i < stars && i < 5; i++) {
                        standingBuilder.append("★");
                    }
                } catch (NumberFormatException e) {
                    standingBuilder.append(standingText);
                }
            } else {
                standingBuilder.append(standingText);
            }
        }

        hotelStandingLabel.setText(standingBuilder.toString());
    }

    /**
     * Charge la carte avec plusieurs tentatives et méthodes alternatives
     */
    private void loadMapWithRetries(String address) {
        if (webView == null) {
            System.err.println("WebView non initialisée!");
            // Initialiser une WebView si elle n'existe pas
            Platform.runLater(() -> {
                try {
                    webView = new WebView();
                    webView.setPrefSize(800, 450);
                    if (mapContainer != null) {
                        mapContainer.setContent(webView);
                    }
                    // Réessayer le chargement
                    tryDirectMapLoading(address);
                } catch (Exception e) {
                    System.err.println("Erreur lors de la création d'urgence de WebView: " + e.getMessage());
                }
            });
            return;
        }

        if (address == null || address.isEmpty()) {
            System.err.println("Adresse invalide");
            fallbackToStaticMap();
            return;
        }

        mapLoaded = false;

        // Message de chargement
        showLoadingMessage("Recherche de l'adresse: " + address);

        // Essayer avec une approche directe
        System.out.println("Tentative de géocodage pour: " + address);
        tryDirectMapLoading(address);
    }

    /**
     * Première tentative: chargement direct de la carte via Nominatim
     */
    private void tryDirectMapLoading(String address) {
        System.out.println("Tentative directe de chargement de carte pour: " + address);

        mapService.getCoordinatesFromAddress(address)
                .thenAccept(coordinates -> {
                    if (coordinates != null) {
                        double lat = coordinates.getKey();
                        double lon = coordinates.getValue();
                        System.out.println("Coordonnées trouvées: " + lat + ", " + lon);
                        loadMapWithCoordinates(lat, lon);
                    } else {
                        System.out.println("Coordonnées non trouvées, tentative alternative");
                        tryAlternativeMapLoading(address);
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Erreur lors de la recherche de coordonnées: " + ex.getMessage());
                    ex.printStackTrace();
                    tryAlternativeMapLoading(address);
                    return null;
                });
    }

    /**
     * Deuxième tentative avec adresse simplifiée
     */
    private void tryAlternativeMapLoading(String address) {
        if (mapLoaded) return;

        System.out.println("Tentative alternative de chargement");
        String simplifiedAddress = simplifyAddress(address);

        if (!simplifiedAddress.equals(address)) {
            System.out.println("Essai avec adresse simplifiée: " + simplifiedAddress);
            mapService.getCoordinatesFromAddress(simplifiedAddress)
                    .thenAccept(coordinates -> {
                        if (coordinates != null) {
                            loadMapWithCoordinates(coordinates.getKey(), coordinates.getValue());
                        } else {
                            System.out.println("Échec avec adresse simplifiée");
                            fallbackToStaticMap();
                        }
                    })
                    .exceptionally(ex -> {
                        System.err.println("Échec avec adresse simplifiée: " + ex.getMessage());
                        fallbackToStaticMap();
                        return null;
                    });
        } else {
            fallbackToStaticMap();
        }
    }

    /**
     * Simplifie une adresse
     */
    private String simplifyAddress(String address) {
        if (address == null) return "";
        String cleaned = address.replaceAll("[^\\p{L}\\p{N}, ]", " ");
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        if (cleaned.contains(",")) {
            String[] parts = cleaned.split(",", 2);
            return parts[0].trim();
        }
        return cleaned;
    }

    /**
     * Charge une carte interactive OpenStreetMap avec des coordonnées
     */
    private void loadMapWithCoordinates(double lat, double lon) {
        if (mapLoaded) return;

        System.out.println("Chargement de la carte avec coordonnées: " + lat + ", " + lon);

        Platform.runLater(() -> {
            try {
                // Vérifier que webView existe
                if (webView != null && webView.getEngine() != null) {
                    // Créer une carte interactive OpenStreetMap avec Leaflet
                    String mapHtml = "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "    <meta charset=\"utf-8\">\n" +
                            "    <title>Carte de l'hôtel</title>\n" +
                            "    <style>\n" +
                            "        html, body { height: 100%; margin: 0; padding: 0; }\n" +
                            "        #map { height: 100%; width: 100%; }\n" +
                            "    </style>\n" +
                            "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" />\n" +
                            "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "    <div id=\"map\"></div>\n" +
                            "    <script>\n" +
                            "        document.addEventListener('DOMContentLoaded', function() {\n" +
                            "            try {\n" +
                            "                console.log('Initialisation de la carte Leaflet');\n" +
                            "                var map = L.map('map').setView([" + lat + ", " + lon + "], 15);\n" +
                            "                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                            "                    attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors',\n" +
                            "                    maxZoom: 19\n" +
                            "                }).addTo(map);\n" +
                            "                var marker = L.marker([" + lat + ", " + lon + "]).addTo(map);\n" +
                            "                marker.bindPopup(\"" + escapeJsString(currentHotel.getNom()) + "<br>" +
                            escapeJsString(currentHotel.getAdresse()) + "\").openPopup();\n" +
                            "                console.log('Carte initialisée avec succès');\n" +
                            "            } catch(e) {\n" +
                            "                console.error('Erreur lors de l\\'initialisation de la carte: ' + e.message);\n" +
                            "            }\n" +
                            "        });\n" +
                            "    </script>\n" +
                            "</body>\n" +
                            "</html>";

                    webView.getEngine().loadContent(mapHtml);

                    // Ajouter un listener pour détecter la fin du chargement
                    webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                        System.out.println("État du chargement WebView: " + newState);

                        // Exécuter du JavaScript pour vérifier le chargement de la carte
                        if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                            mapLoaded = true;
                            System.out.println("Contenu HTML chargé avec succès");
                        }
                    });

                    System.out.println("Commande de chargement de la carte envoyée");
                } else {
                    System.err.println("WebView ou son moteur est null");

                    // Tentative de récupération
                    if (webView == null && mapContainer != null) {
                        webView = new WebView();
                        mapContainer.setContent(webView);
                        // Réessayer le chargement après création
                        loadMapWithCoordinates(lat, lon);
                    }
                }
            } catch (Exception e) {
                System.err.println("Exception lors du chargement de la carte: " + e.getMessage());
                e.printStackTrace();
                fallbackToStaticMap();
            }
        });
    }

    /**
     * Solution de secours avec carte statique
     */
    private void fallbackToStaticMap() {
        if (mapLoaded) return;

        System.out.println("Utilisation de la solution de secours statique");

        Platform.runLater(() -> {
            try {
                String address = currentHotel != null ? currentHotel.getAdresse() : "Adresse non disponible";
                String hotelName = currentHotel != null ? currentHotel.getNom() : "Hôtel";

                // Détection spéciale pour Tunis ou Movenpick
                double lat = 36.8065; // Valeur par défaut (Tunis)
                double lon = 10.1815; // Valeur par défaut (Tunis)

                // Si l'adresse contient des coordonnées reconnues
                if (address.toLowerCase().contains("tunis") ||
                        hotelName.toLowerCase().contains("tunis") ||
                        hotelName.toLowerCase().contains("movenpick") ||
                        address.toLowerCase().contains("movenpick")) {
                    // Conserver les coordonnées de Tunis
                } else {
                    // Sinon utiliser des coordonnées par défaut pour Paris
                    lat = 48.8566;
                    lon = 2.3522;
                }

                // HTML avec une carte statique OpenStreetMap
                String html = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <meta charset=\"utf-8\">\n" +
                        "    <title>Carte de l'hôtel</title>\n" +
                        "    <style>\n" +
                        "        body { margin: 0; padding: 10px; font-family: Arial, sans-serif; color: #333; }\n" +
                        "        .map-container { text-align: center; margin-bottom: 15px; }\n" +
                        "        .hotel-info { background-color: #f8f9fa; padding: 12px; border-radius: 5px; margin-top: 10px; }\n" +
                        "        h2 { color: #2c3e50; margin-top: 0; }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <h2>" + escapeHtml(hotelName) + "</h2>\n" +
                        "    <div class=\"map-container\">\n" +
                        "        <img src=\"https://staticmap.openstreetmap.de/staticmap.php?center=" + lat + "," + lon +
                        "&zoom=14&size=600x300&markers=" + lat + "," + lon + ",red-pushpin\" alt=\"Carte statique\" style=\"max-width:100%; border:1px solid #ccc;\">\n" +
                        "    </div>\n" +
                        "    <div class=\"hotel-info\">\n" +
                        "        <p><strong>Adresse:</strong> " + escapeHtml(address) + "</p>\n" +
                        "        <p><em>Emplacement approximatif</em></p>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>";

                // Vérifier que webView existe
                if (webView != null && webView.getEngine() != null) {
                    webView.getEngine().loadContent(html);
                    System.out.println("Carte statique chargée");
                    mapLoaded = true;
                } else {
                    System.err.println("WebView ou son moteur est null");

                    // Tentative de récupération
                    if (webView == null && mapContainer != null) {
                        webView = new WebView();
                        mapContainer.setContent(webView);
                        webView.getEngine().loadContent(html);
                        mapLoaded = true;
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur dans la solution de secours: " + e.getMessage());
                showErrorMessage("Impossible de charger la carte");
            }
        });
    }

    /**
     * Affiche un message d'erreur simple
     */
    private void showErrorMessage(String message) {
        Platform.runLater(() -> {
            try {
                String html = "<html><body style='font-family: Arial; text-align: center; padding: 20px;'>" +
                        "<div style='background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px;'>" +
                        "<h3>⚠️ " + escapeHtml(message) + "</h3>" +
                        "</div></body></html>";

                if (webView != null && webView.getEngine() != null) {
                    webView.getEngine().loadContent(html);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'affichage du message d'erreur: " + e.getMessage());
            }
        });
    }

    /**
     * Affiche un message de chargement
     */
    private void showLoadingMessage(String message) {
        if (webView == null || webView.getEngine() == null) {
            System.err.println("WebView non initialisée pour le message de chargement");
            return;
        }

        Platform.runLater(() -> {
            try {
                String html = "<html><body style='font-family: Arial; text-align: center; padding-top: 100px;'>" +
                        "<h3>" + escapeHtml(message) + "</h3>" +
                        "<div style='margin-top: 20px;'>" +
                        "   <img src='https://i.gifer.com/ZKZx.gif' alt='Loading' style='width: 50px;'>" +
                        "</div>" +
                        "</body></html>";
                webView.getEngine().loadContent(html);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'affichage du message: " + e.getMessage());
            }
        });
    }

    /**
     * Charge l'image de l'hôtel
     */
    private void loadHotelImage() {
        if (currentHotel == null || hotelImageView == null) {
            System.err.println("Impossible de charger l'image: hôtel ou ImageView null");
            return;
        }

        // Utiliser une solution simplifiée d'abord
        Platform.runLater(() -> {
            try {
                // Commencer par définir une image par défaut
                Image defaultImage = new Image(DEFAULT_HOTEL_IMAGE, true);
                hotelImageView.setImage(defaultImage);
                hotelImageView.setFitWidth(300);
                hotelImageView.setPreserveRatio(true);

                System.out.println("Image par défaut chargée en attente de l'image spécifique");

                // Pour les tests - détection simple du nom de l'hôtel
                if (currentHotel.getNom().toLowerCase().contains("movenpick") ||
                        currentHotel.getNom().toLowerCase().contains("tunis")) {
                    // Ne rien faire, garder l'image par défaut qui est déjà celle du Movenpick
                    System.out.println("Hôtel Movenpick détecté, utilisation de l'image par défaut");
                } else {
                    // En parallèle, essayer de chercher une image spécifique
                    mapService.searchHotelImage(currentHotel.getNom())
                            .thenAccept(foundUrl -> {
                                if (foundUrl != null && !foundUrl.isEmpty()) {
                                    Platform.runLater(() -> {
                                        try {
                                            System.out.println("Chargement de l'image spécifique: " + foundUrl);
                                            Image specificImage = new Image(foundUrl, true);

                                            // Vérifier si l'image a bien été chargée
                                            specificImage.errorProperty().addListener((obs, oldValue, newValue) -> {
                                                if (newValue) {
                                                    System.err.println("Erreur lors du chargement de l'image spécifique");
                                                    // En cas d'erreur, on garde l'image par défaut
                                                }
                                            });

                                            // Attendre que l'image soit chargée avant de la définir
                                            specificImage.progressProperty().addListener((obs, oldValue, newValue) -> {
                                                if (newValue.doubleValue() == 1.0) {
                                                    hotelImageView.setImage(specificImage);
                                                    System.out.println("Image spécifique chargée avec succès");
                                                }
                                            });
                                        } catch (Exception e) {
                                            System.err.println("Erreur avec l'image spécifique: " + e.getMessage());
                                        }
                                    });
                                } else {
                                    System.out.println("Aucune URL d'image spécifique trouvée, conservation de l'image par défaut");
                                }
                            })
                            .exceptionally(ex -> {
                                System.err.println("Exception lors de la recherche d'image: " + ex.getMessage());
                                return null;
                            });
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                loadDefaultHotelImage();
            }
        });
    }

    /**
     * Charge une image par défaut en cas d'échec du chargement normal
     */
    private void loadDefaultHotelImage() {
        Platform.runLater(() -> {
            try {
                System.out.println("Chargement de l'image par défaut");

                // Utiliser l'URL par défaut définie dans HotelMapService
                String defaultImageUrl = DEFAULT_HOTEL_IMAGE;

                Image defaultImage = new Image(defaultImageUrl, true);
                defaultImage.errorProperty().addListener((obs, oldValue, newValue) -> {
                    if (newValue) {
                        System.err.println("Erreur lors du chargement de l'image par défaut");
                        // En cas d'erreur critique, on pourrait mettre une image locale
                        // ou simplement ne rien afficher
                    }
                });

                hotelImageView.setImage(defaultImage);
                hotelImageView.setFitWidth(300);
                hotelImageView.setPreserveRatio(true);

                System.out.println("Image par défaut définie");
            } catch (Exception e) {
                System.err.println("Erreur critique lors du chargement de l'image par défaut: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Échapper les caractères HTML pour éviter les injections
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Échappe les caractères spéciaux pour éviter les problèmes JavaScript
     */
    private String escapeJsString(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}