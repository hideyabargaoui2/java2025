package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {

    private final String URL = "jdbc:mysql://localhost:3306/gestion de resto"; // ou "gestion_resto" si renommée
    private final String USER = "root";
    private final String PASS = ""; // mets ici ton mot de passe root si besoin

    private Connection connection;
    private static MyConnection instance;

    private MyConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connexion établie avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }

    public static MyConnection getInstance() {
        if (instance == null)
            instance = new MyConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
