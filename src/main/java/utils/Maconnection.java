package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Maconnection {
    private final String username="root";
    private final String  password ="";
    private final String  url ="jdbc:mysql://localhost:3306/hebergementmanager";

    private Connection connection ;

    static Maconnection instance;

    public static Maconnection getInstance(){
        if(instance==null){
            instance=new Maconnection();
        }
        return instance;
    }


    private Maconnection() {
        try{
            connection = DriverManager.getConnection(url,username,password);
            System.out.println("connection établie avec succés");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }




    }

    public Connection getConnection() {
        return connection;
    }
}
