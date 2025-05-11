package services;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import models.Trajet;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service pour gérer les notifications vocales Twilio
 */
public class TwilioService {
    // ExecutorService pour gérer les appels asynchrones
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Clés de configuration - À charger depuis un fichier de configuration
    private static final String ACCOUNT_SID_KEY = "twilio.account.sid";
    private static final String AUTH_TOKEN_KEY = "twilio.auth.token";
    private static final String PHONE_NUMBER_KEY = "twilio.phone.number";
    private static final String DESTINATION_NUMBER_KEY = "twilio.destination.number";

    // Valeurs par défaut (à remplacer par votre système de configuration)
    private static String ACCOUNT_SID = "ACae0d4fe85da40aa0eabe47ac3d711d40";
    private static String AUTH_TOKEN = "a3e2063e19107748af787ecdfd8e7f5e";
    private static String TWILIO_PHONE_NUMBER = "+14176373881";
    private static String DESTINATION_PHONE_NUMBER = "+21652348380";

    // Statut d'initialisation
    private static boolean isInitialized = false;

    /**
     * Initialise le service Twilio de manière sécurisée
     */
    public static void initialize() {
        // Tenter de charger les configurations depuis un fichier externe ou des variables d'environnement
        loadConfiguration();

        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            isInitialized = true;
            System.out.println("Service Twilio initialisé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du service Twilio: " + e.getMessage());
            isInitialized = false;
        }
    }

    /**
     * Charge la configuration depuis un fichier ou des variables d'environnement
     */
    private static void loadConfiguration() {
        // Exemple: charger depuis les variables d'environnement
        String envAccountSid = System.getenv("TWILIO_ACCOUNT_SID");
        if (envAccountSid != null && !envAccountSid.isEmpty()) {
            ACCOUNT_SID = envAccountSid;
        }

        String envAuthToken = System.getenv("TWILIO_AUTH_TOKEN");
        if (envAuthToken != null && !envAuthToken.isEmpty()) {
            AUTH_TOKEN = envAuthToken;
        }

        String envPhoneNumber = System.getenv("TWILIO_PHONE_NUMBER");
        if (envPhoneNumber != null && !envPhoneNumber.isEmpty()) {
            TWILIO_PHONE_NUMBER = envPhoneNumber;
        }

        String envDestinationNumber = System.getenv("TWILIO_DESTINATION_NUMBER");
        if (envDestinationNumber != null && !envDestinationNumber.isEmpty()) {
            DESTINATION_PHONE_NUMBER = envDestinationNumber;
        }
    }

    /**
     * Envoie une notification vocale de manière asynchrone
     * @param trajet Le trajet à notifier
     * @return Un CompletableFuture avec le résultat de l'opération
     */
    public static CompletableFuture<NotificationResult> sendVoiceNotification(Trajet trajet) {
        return CompletableFuture.supplyAsync(() -> {
            // Vérifier si le service est initialisé
            if (!isInitialized) {
                try {
                    initialize();
                } catch (Exception e) {
                    return new NotificationResult(false, "Impossible d'initialiser le service Twilio: " + e.getMessage());
                }
            }

            if (!isInitialized) {
                return new NotificationResult(false, "Le service Twilio n'est pas initialisé");
            }

            try {
                // Créer le message à prononcer
                String message = String.format(
                        "Un nouveau trajet a été ajouté. Destination: %s. Transport: %s. Date: %s. Heure: %d heures. Durée: %d heures.",
                        trajet.getDestination(),
                        trajet.getTransport(),
                        trajet.getDate().toLocalDate().toString(),
                        trajet.getHeure(),
                        trajet.getDuree()
                );

                // Construire le TwiML pour la réponse vocale
                Say say = new Say.Builder(message)
                        .language(Say.Language.FR_FR)  // Français
                        .voice(Say.Voice.POLLY_LEA)    // Voix femme en français
                        .build();

                VoiceResponse response = new VoiceResponse.Builder()
                        .say(say)
                        .build();

                String twimlString = response.toXml();
                System.out.println("TwiML généré: " + twimlString);

                // Effectuer l'appel téléphonique avec TwiML inline
                Call call = Call.creator(
                                new PhoneNumber(DESTINATION_PHONE_NUMBER),
                                new PhoneNumber(TWILIO_PHONE_NUMBER),
                                new Twiml(twimlString))
                        .create();

                System.out.println("Appel vocal envoyé avec succès, SID: " + call.getSid());
                return new NotificationResult(true, "Notification vocale envoyée avec succès (SID: " + call.getSid() + ")");

            } catch (ApiException apiEx) {
                System.err.println("Erreur API Twilio: " + apiEx.getMessage());
                return new NotificationResult(false, "Erreur API Twilio: " + apiEx.getMessage());
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de la notification vocale: " + e.getMessage());
                e.printStackTrace();
                return new NotificationResult(false, "Erreur lors de l'envoi de la notification: " + e.getMessage());
            }
        }, executor);
    }

    /**
     * Ferme proprement les ressources du service
     */
    public static void shutdown() {
        executor.shutdown();
    }

    /**
     * Classe pour représenter le résultat d'une notification
     */
    public static class NotificationResult {
        private final boolean success;
        private final String message;

        public NotificationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}