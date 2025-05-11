package services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

public class PlatSuggereAPI {

    public static String getPlatSuggere(String adresse) {
        try {
            String encodedAdresse = URLEncoder.encode(adresse, "UTF-8");
            String apiUrl = "https://api.mocky.io/v2/5ecfd5dc3200006200e3d64b?adresse=" + encodedAdresse;

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            int status = con.getResponseCode();
            if (status != 200) {
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();
            con.disconnect();

            // Exemple simple de réponse : {"plat":"Pizza Margherita"}
            String json = response.toString();
            int start = json.indexOf(":\"") + 2;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);

        } catch (Exception e) {
            System.out.println("Erreur API Suggestion : " + e.getMessage());
            return null;
        }
    }
}
