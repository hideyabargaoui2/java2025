package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import models.hotel;
import services.HotelService;
import services.HotelMapService;
import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import org.json.JSONObject;

public class AjouterHotelController {

    @FXML private TextField nomField;
    @FXML private TextField prixNuitField;
    @FXML private TextField nombreNuitField;
    @FXML private TextField adresseField;
    @FXML private TextField nombreChambresField;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;
    @FXML private ComboBox<String> standingComboBox;
    @FXML private Label searchStatusLabel; // Nouveau label pour afficher le statut de la recherche

    private final HotelService hotelService = new HotelService();
    private final HotelMapService mapService = new HotelMapService();
    private AfficherHotelController parentController;

    // Pour stocker l'hôtel sélectionné si on est en mode réservation
    private hotel hotelSelectionne;
    private boolean modeReservation = false;

    // Variable pour suivre la recherche en cours
    private CompletableFuture<Void> currentSearch;

    // Variable pour empêcher les recherches multiples simultanées
    private String lastSearchTerm = "";
    private boolean isSearching = false;

    // Constantes pour les requêtes HTTP
    private static final String USER_AGENT = "HotelApplication/1.0";
    private static final int CONNECT_TIMEOUT = 5000; // 5 secondes
    private static final int READ_TIMEOUT = 5000; // 5 secondes

    @FXML
    public void initialize() {
        System.out.println("Initialisation du AjouterHotelController");
        // Vérification que les champs et boutons sont correctement injectés
        if (nomField == null || prixNuitField == null || nombreNuitField == null ||
                adresseField == null || nombreChambresField == null ||
                btnConfirmer == null || btnAnnuler == null) {
            System.err.println("Erreur: un ou plusieurs éléments FXML n'ont pas été injectés correctement");
        }

        // Configuration explicite des événements boutons
        if (btnConfirmer != null) {
            btnConfirmer.setOnAction(event -> confirmerAjout());
        }

        if (btnAnnuler != null) {
            btnAnnuler.setOnAction(event -> annulerAjout());
        }

        // Ajouter le contrôle de saisie pour le champ de prix nuit (accepte uniquement les chiffres)
        configurerChampNumerique(prixNuitField);

        // Également configurer le champ de nombre de nuits pour coherence
        configurerChampNumerique(nombreNuitField);

        // Configurer le champ nombre de chambres pour n'accepter que des entiers
        configurerChampEntier(nombreChambresField);

        // Initialiser la ComboBox avec les options d'étoiles sous forme de symboles
        standingComboBox.getItems().addAll(
                "⭐",
                "⭐⭐",
                "⭐⭐⭐",
                "⭐⭐⭐⭐",
                "⭐⭐⭐⭐⭐"
        );

        // S'assurer que le label de statut est vide au début
        if (searchStatusLabel != null) {
            searchStatusLabel.setText("");
            searchStatusLabel.setVisible(false);
        }

        // Configurer la recherche automatique d'adresse lors de la saisie du nom d'hôtel
        configureHotelSearch();
    }

    /**
     * Configure la recherche automatique d'adresse d'hôtel basée sur le nom
     * Implémentation améliorée avec feedback visuel et prévention des recherches multiples
     */
    private void configureHotelSearch() {
        // Ajouter un écouteur au champ de nom pour rechercher l'adresse automatiquement
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty() && newValue.trim().length() > 3
                    && !newValue.equals(oldValue) && !isSearching) {
                // Une recherche simple après que l'utilisateur a saisi au moins 4 caractères
                String hotelName = newValue.trim();
                // Éviter de rechercher plusieurs fois le même terme
                if (!hotelName.equals(lastSearchTerm)) {
                    lastSearchTerm = hotelName;
                    searchHotelAddressWithDelay(hotelName);
                }
            }
        });

        // Configurer l'action lorsque l'utilisateur appuie sur Entrée dans le champ de nom
        nomField.setOnAction(event -> {
            String hotelName = nomField.getText().trim();
            if (hotelName != null && !hotelName.isEmpty() && !isSearching) {
                lastSearchTerm = hotelName;
                searchHotelAddressWithoutDelay(hotelName);
            }
        });

        // Configurer le champ d'adresse pour indiquer qu'il peut être rempli automatiquement
        adresseField.setPromptText("Sera rempli automatiquement ou saisissez manuellement");
    }

    /**
     * Effectue une recherche d'adresse avec un délai pour éviter trop de requêtes
     */
    private void searchHotelAddressWithDelay(String hotelName) {
        // Annuler toute recherche précédente en cours
        if (currentSearch != null && !currentSearch.isDone()) {
            currentSearch.cancel(true);
        }

        isSearching = true;
        updateSearchStatus("Recherche d'adresse en cours...", true);

        // Utiliser un Thread pour ne pas bloquer l'interface utilisateur
        currentSearch = CompletableFuture.runAsync(() -> {
            try {
                // Attendre un peu pour laisser à l'utilisateur le temps de finir de taper
                Thread.sleep(800);

                // Vérifier à nouveau le contenu du champ (il peut avoir changé)
                Platform.runLater(() -> {
                    if (hotelName.equals(nomField.getText().trim())) {
                        searchHotelAddressWithoutDelay(hotelName);
                    } else {
                        // Si le texte a changé, nous annulons cette recherche
                        isSearching = false;
                        updateSearchStatus("", false);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isSearching = false;
                Platform.runLater(() -> updateSearchStatus("", false));
            }
        });
    }

    /**
     * Effectue une recherche immédiate d'adresse pour un hôtel
     */
    private void searchHotelAddressWithoutDelay(String hotelName) {
        updateSearchStatus("Recherche d'adresse pour: " + hotelName, true);

        // Ajouter "hotel" au terme de recherche pour améliorer les résultats
        String searchQuery = hotelName + " hotel";

        mapService.getCoordinatesFromAddress(searchQuery)
                .thenAccept(coordinates -> {
                    if (coordinates != null) {
                        // Coordonnées trouvées, maintenant essayer d'obtenir l'adresse complète
                        mapService.getAddressFromCoordinates(coordinates)
                                .thenAccept(address -> {
                                    Platform.runLater(() -> {
                                        if (address != null && !address.isEmpty()) {
                                            // Adresse trouvée, la mettre dans le champ
                                            adresseField.setText(address);
                                            updateSearchStatus("Adresse trouvée!", false);
                                        } else {
                                            // Si l'adresse n'a pas été trouvée mais que nous avons des coordonnées
                                            String adresse = adresseField.getText();
                                            if (adresse == null || adresse.isEmpty()) {
                                                adresseField.setText("Coordonnées trouvées pour: " + hotelName);
                                            }
                                            updateSearchStatus("Impossible de trouver l'adresse complète", false);
                                        }
                                        isSearching = false;
                                    });
                                })
                                .exceptionally(ex -> {
                                    Platform.runLater(() -> {
                                        updateSearchStatus("Erreur lors de la récupération de l'adresse", false);
                                        isSearching = false;
                                    });
                                    return null;
                                });
                    } else {
                        Platform.runLater(() -> {
                            updateSearchStatus("Aucune adresse trouvée pour: " + hotelName, false);
                            isSearching = false;
                        });
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Erreur lors de la recherche d'adresse: " + ex.getMessage());
                    Platform.runLater(() -> {
                        updateSearchStatus("Erreur de recherche: " + ex.getMessage(), false);
                        isSearching = false;
                    });
                    return null;
                });
    }

    /**
     * Met à jour le label de statut de recherche
     * @param message Le message à afficher
     * @param isSearching Si true, indique que la recherche est en cours
     */
    private void updateSearchStatus(String message, boolean isSearching) {
        if (searchStatusLabel != null) {
            searchStatusLabel.setText(message);
            searchStatusLabel.setVisible(!message.isEmpty());

            // Optionnel: changer le style en fonction du statut
            if (isSearching) {
                searchStatusLabel.setStyle("-fx-text-fill: blue;");
            } else if (message.startsWith("Erreur") || message.startsWith("Aucune")) {
                searchStatusLabel.setStyle("-fx-text-fill: red;");
            } else if (message.startsWith("Adresse trouvée")) {
                searchStatusLabel.setStyle("-fx-text-fill: green;");
            }
        }
    }

    /**
     * Configure un champ de texte pour n'accepter que des valeurs numériques
     * @param textField Le champ de texte à configurer
     */
    private void configurerChampNumerique(TextField textField) {
        // Définir un filtre qui n'accepte que les chiffres et un point décimal
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Permet les chiffres, un point décimal et champ vide
            if (newText.matches("^\\d*\\.?\\d*$")) {
                return change;
            }
            return null; // Rejette tous les autres caractères
        };

        // Appliquer le TextFormatter avec notre filtre au champ
        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    /**
     * Configure un champ de texte pour n'accepter que des valeurs entières
     * @param textField Le champ de texte à configurer
     */
    private void configurerChampEntier(TextField textField) {
        // Définir un filtre qui n'accepte que les chiffres
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Permet uniquement les chiffres et champ vide
            if (newText.matches("^\\d*$")) {
                return change;
            }
            return null; // Rejette tous les autres caractères
        };

        // Appliquer le TextFormatter avec notre filtre au champ
        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    public void setParentController(AfficherHotelController controller) {
        this.parentController = controller;
    }

    /**
     * Configure le formulaire pour la réservation de chambres d'un hôtel existant
     * @param hotel L'hôtel pour lequel on veut réserver des chambres
     */
    public void setModeReservation(hotel hotel) {
        this.hotelSelectionne = hotel;
        this.modeReservation = true;

        // Préremplir les champs et les rendre non-modifiables
        nomField.setText(hotel.getNom());
        nomField.setEditable(false);

        prixNuitField.setText(String.valueOf(hotel.getPrixnuit()));
        prixNuitField.setEditable(false);

        nombreNuitField.setText(String.valueOf(hotel.getNombrenuit()));
        nombreNuitField.setEditable(false);

        standingComboBox.setValue(hotel.getStanding());
        standingComboBox.setDisable(true);

        adresseField.setText(hotel.getAdresse());
        adresseField.setEditable(false);

        // Afficher le nombre de chambres disponibles
        nombreChambresField.setPromptText("Disponibles: " + hotel.getNombreChambresDisponibles() + "/" + hotel.getNombreChambresTotal());

        // Modifier le texte des boutons
        btnConfirmer.setText("Réserver");

        // Cacher le label de statut en mode réservation
        if (searchStatusLabel != null) {
            searchStatusLabel.setVisible(false);
        }
    }

    @FXML
    public void confirmerAjout() {
        try {
            if (modeReservation) {
                // Mode réservation de chambres
                confirmerReservation();
            } else {
                // Mode ajout d'hôtel
                ajouterNouvelHotel();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte("Erreur SQL: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur inattendue: " + e.getMessage());
        }
    }

    /**
     * Traite la réservation de chambres pour un hôtel existant
     * @throws SQLException en cas d'erreur lors de l'accès à la base de données
     */
    private void confirmerReservation() throws SQLException {
        // Vérifier que le champ nombre de chambres est rempli
        if (nombreChambresField.getText().isEmpty()) {
            afficherAlerte("Veuillez entrer le nombre de chambres à réserver.");
            return;
        }

        // Convertir en entier
        int nombreChambres;
        try {
            nombreChambres = Integer.parseInt(nombreChambresField.getText());
            if (nombreChambres <= 0) {
                afficherAlerte("Le nombre de chambres doit être supérieur à zéro.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Veuillez entrer un nombre entier valide pour le nombre de chambres.");
            return;
        }

        // Vérifier si le nombre de chambres demandé est disponible
        if (!hotelSelectionne.isDisponible(nombreChambres)) {
            afficherAlerte("Impossible de réserver " + nombreChambres + " chambres. " +
                    "Il reste seulement " + hotelSelectionne.getNombreChambresDisponibles() +
                    " chambres disponibles sur " + hotelSelectionne.getNombreChambresTotal() + ".");
            return;
        }

        // Réserver les chambres
        hotelSelectionne.reserverChambres(nombreChambres);

        // Mettre à jour l'hôtel dans la base de données
        boolean success = hotelService.modifier(hotelSelectionne);

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText(nombreChambres + " chambres réservées avec succès à l'hôtel " + hotelSelectionne.getNom() + ".");
            alert.showAndWait();

            // Mettre à jour l'affichage dans le contrôleur parent
            if (parentController != null) {
                parentController.loadData();
            }

            // Fermer la fenêtre
            fermerFenetre();
        } else {
            afficherAlerte("Erreur lors de la mise à jour de l'hôtel.");
        }
    }

    /**
     * Traite l'ajout d'un nouvel hôtel
     * @throws SQLException en cas d'erreur lors de l'accès à la base de données
     */
    private void ajouterNouvelHotel() throws SQLException {
        System.out.println("Ajout d'un nouvel hôtel");

        // Récupérer les valeurs des champs
        String nom = nomField.getText();

        // Validation des valeurs numériques avec messages d'erreur spécifiques
        double prixNuit;
        try {
            prixNuit = Double.parseDouble(prixNuitField.getText());
            if (prixNuit <= 0) {
                afficherAlerte("Le prix par nuit doit être supérieur à zéro.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Veuillez entrer un nombre valide pour le prix par nuit.");
            return;
        }

        int nombreNuit;
        try {
            nombreNuit = Integer.parseInt(nombreNuitField.getText());
            if (nombreNuit <= 0) {
                afficherAlerte("Le nombre de nuits doit être supérieur à zéro.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Veuillez entrer un nombre entier valide pour le nombre de nuits.");
            return;
        }

        // Vérifier le nombre de chambres réservées (si fourni)
        int nombreChambresReservees = 0;
        if (!nombreChambresField.getText().isEmpty()) {
            try {
                nombreChambresReservees = Integer.parseInt(nombreChambresField.getText());
                if (nombreChambresReservees < 0) {
                    afficherAlerte("Le nombre de chambres ne peut pas être négatif.");
                    return;
                }
                if (nombreChambresReservees > 25) {
                    afficherAlerte("Le nombre de chambres réservées ne peut pas dépasser 25 (le maximum par hôtel).");
                    return;
                }
            } catch (NumberFormatException e) {
                afficherAlerte("Veuillez entrer un nombre entier valide pour le nombre de chambres.");
                return;
            }
        }

        String standing = standingComboBox.getValue();
        String adresse = adresseField.getText();

        // Validation des champs
        if (nom.isEmpty() || standing == null || adresse.isEmpty()) {
            afficherAlerte("Veuillez remplir tous les champs obligatoires (nom, standing, adresse).");
            return;
        }

        // Créer un objet hôtel sans le nombre de chambres réservées
        hotel nouveauHotel = new hotel(0, nom, prixNuit, nombreNuit, standing, adresse);

        try {
            // Ajouter l'hôtel sans le nombre de chambres réservées pour éviter l'erreur
            boolean success = ajouterHotelSansChambresReservees(nouveauHotel);
            System.out.println("Résultat de l'ajout: " + success);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Hôtel ajouté avec succès.");
                alert.showAndWait();

                // Mettre à jour l'affichage dans le contrôleur parent
                if (parentController != null) {
                    parentController.loadData();
                } else {
                    System.err.println("Erreur: parentController est null");
                }

                // Fermer la fenêtre
                fermerFenetre();
            } else {
                afficherAlerte("Erreur lors de l'ajout de l'hôtel.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            afficherAlerte("Erreur SQL: " + e.getMessage());
        }
    }

    /**
     * Ajoute un hôtel sans utiliser la colonne nombre_chambres_reservees
     * Utilise une requête SQL directe pour éviter le problème de colonne manquante
     */
    private boolean ajouterHotelSansChambresReservees(hotel hotel) throws SQLException {
        // Utiliser directement une connexion à la base de données pour exécuter notre propre requête
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Obtenir une connexion via la classe utilitaire Maconnection
            connection = utils.Maconnection.getInstance().getConnection();

            // Requête d'insertion sans la colonne problématique
            String sql = "INSERT INTO hotel(nom, prixnuit, nombrenuit, standing, adresse) VALUES (?, ?, ?, ?, ?)";

            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, hotel.getNom());
            ps.setDouble(2, hotel.getPrixnuit());
            ps.setInt(3, hotel.getNombrenuit());
            ps.setString(4, hotel.getStanding());
            ps.setString(5, hotel.getAdresse());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    hotel.setId(rs.getInt(1));
                }
                System.out.println("Hôtel ajouté avec succès sans la colonne nombre_chambres_reservees. ID: " + hotel.getId());
                return true;
            }
            return false;
        } finally {
            // Fermer les ressources (mais pas la connexion qui est partagée)
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignore */ }
            if (ps != null) try { ps.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    /**
     * Récupère l'adresse correspondant à des coordonnées géographiques (géocodage inverse)
     * @param coordinates Les coordonnées (latitude, longitude)
     * @return CompletableFuture contenant l'adresse trouvée ou null en cas d'échec
     */
    public CompletableFuture<String> getAddressFromCoordinates(Pair<Double, Double> coordinates) {
        return CompletableFuture.supplyAsync(() -> {
            if (coordinates == null) {
                System.err.println("Coordonnées nulles, impossible de récupérer l'adresse");
                return null;
            }
            double lat = coordinates.getKey();
            double lon = coordinates.getValue();
            System.out.println("Recherche d'adresse pour les coordonnées: " + lat + ", " + lon);

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Utiliser l'API de géocodage inverse de Nominatim
                String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;

                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    System.err.println("Erreur HTTP: " + responseCode);
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Analyser la réponse JSON
                JSONObject jsonObject = new JSONObject(response.toString());
                if (jsonObject.has("display_name")) {
                    String address = jsonObject.getString("display_name");
                    System.out.println("Adresse trouvée: " + address);
                    return address;
                } else {
                    System.err.println("Aucune adresse trouvée dans la réponse");
                    return null;
                }

            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération de l'adresse: " + e.getMessage());
                return null;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la fermeture du lecteur: " + e.getMessage());
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    @FXML
    public void annulerAjout() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    private void afficherAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}