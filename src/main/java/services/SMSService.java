package services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class SMSService {

    // API Twilio pour envoyer des SMS
    private static final String TWILIO_API_URL = "https://api.twilio.com/2010-04-01/Accounts/ACCOUNT_SID/Messages.json";
    private static final String ACCOUNT_SID = "YOUR_ACCOUNT_SID"; // À remplacer par votre SID
    private static final String AUTH_TOKEN = "YOUR_AUTH_TOKEN"; // À remplacer par votre token
    private static final String FROM_PHONE = "+YOUR_TWILIO_PHONE"; // Numéro Twilio à remplacer

    /**
     * Envoie un SMS au numéro spécifié via Twilio
     * @param phoneNumber Le numéro de téléphone du destinataire
     * @param message Le message à envoyer
     * @return true si l'envoi est réussi, false sinon
     */
    public static boolean sendSMS(String phoneNumber, String message) {
        System.out.println("Envoi de SMS à " + phoneNumber + ": " + message);

        // Créer une tâche en arrière-plan pour ne pas bloquer l'interface utilisateur
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    // Utiliser l'API Twilio
                    String auth = ACCOUNT_SID + ":" + AUTH_TOKEN;
                    String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
                    String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());

                    // Préparer l'URL
                    String apiUrl = TWILIO_API_URL.replace("ACCOUNT_SID", ACCOUNT_SID);
                    URL url = new URL(apiUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Préparer les données
                    String data = "From=" + URLEncoder.encode(FROM_PHONE, StandardCharsets.UTF_8.toString()) +
                            "&To=" + URLEncoder.encode(phoneNumber, StandardCharsets.UTF_8.toString()) +
                            "&Body=" + encodedMessage;

                    // Envoyer la requête
                    try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                        writer.write(data);
                        writer.flush();
                    }

                    // Vérifier la réponse
                    int responseCode = conn.getResponseCode();
                    if (responseCode >= 200 && responseCode < 300) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                            String line;
                            StringBuilder response = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            System.out.println("Réponse Twilio: " + response.toString());
                            return true;
                        }
                    } else {
                        // En cas d'erreur, lire la réponse
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                            String line;
                            StringBuilder response = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            System.err.println("Erreur Twilio: " + response.toString());
                        }
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };

        // Gérer le résultat
        task.setOnSucceeded(event -> {
            boolean result = task.getValue();
            if (result) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("SMS Envoyé");
                    alert.setHeaderText(null);
                    alert.setContentText("Un SMS de confirmation a été envoyé à " + phoneNumber);
                    alert.showAndWait();
                });
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Impossible d'envoyer le SMS à " + phoneNumber);
                    alert.showAndWait();
                });
            }
        });

        // Gérer les erreurs
        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de l'envoi du SMS: " + task.getException().getMessage());
                alert.showAndWait();
            });
        });

        // Démarrer la tâche
        new Thread(task).start();

        return true;
    }
}