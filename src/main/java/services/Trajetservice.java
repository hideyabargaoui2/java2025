package services;

import models.Trajet;
import utils.Maconnexion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Trajetservice implements Iservice<Trajet>{
    private Connection connection;
    public Trajetservice() {
        connection = Maconnexion.getInstance().getConnection();
    }

    @Override
    public void ajouter(Trajet trajet) {
        String sql="INSERT INTO `trajet`(`date`, `heure`, `destination`, `transport`, `duree`) " +
                "VALUES ('"+trajet.getDate()+"','"+trajet.getHeure()+"','"+trajet.getDestination()+"','"+trajet.getTransport()+"','"+trajet.getDuree()+"')";
        try {
            Statement statement = connection.createStatement();
            int lines = statement.executeUpdate(sql);
            System.out.println("Trajet ajouter avec succes!");
            System.out.println(lines);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Trajet trajet) {

    }

    @Override
    public void supprimer(Trajet trajet) {

    }

    @Override
    public List<Trajet> getA() {
        String sql = "SELECT * FROM `trajet`";
        List<Trajet> trajet = new ArrayList<Trajet>();
        try {
            Statement st =connection.createStatement() ;
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Trajet t = new Trajet();
                t.setDate(rs.getString("date"));
                t.setHeure(rs.getString("heure"));
                t.setDestination(rs.getString("destination"));
                t.setTransport(rs.getString("transport"));
                t.setDuree(rs.getInt("duree"));
                trajet.add(t);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return trajet;

    }
}
