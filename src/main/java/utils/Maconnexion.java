package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Maconnexion {
    private final String username="root";
    private final String password="";
    private final String URL="jdbc:mysql://localhost:3306/java";
    static Maconnexion instance;
    Connection connection;


    public static Maconnexion getInstance(){
        if (instance==null){
            instance=new Maconnexion();
        }
        return instance;

    }
    private Maconnexion() {
        try {
            connection = DriverManager.getConnection(URL,username,password);
            System.out.println("connection etablie avec succes! ");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public Connection getConnection() {
        return connection;
    }


}
