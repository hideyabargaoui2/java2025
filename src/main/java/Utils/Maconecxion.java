package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton Design Pattern
public class Maconecxion {

    private final String URL = "jdbc:mysql://localhost:3306/3b3java";
    private final String USER = "root";
    private final String PASS = "";

    private Connection connection;
    private static Maconecxion instance;

    // Constructeur privé
    private Maconecxion() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connexion établie avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }

    // Accès à l'instance unique
    public static Maconecxion getInstance() {
        if (instance == null) {
            instance = new Maconecxion();
        }
        return instance;
    }

    // Accès à l'objet Connection
    public Connection getConnection() {
        return connection;
    }
}
