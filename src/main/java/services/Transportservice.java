package services;
import models.Transport;
import utils.Maconnexion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



public class Transportservice implements Iservice<Transport>{
    private Connection connection;
    public Transportservice() {
        connection = Maconnexion.getInstance().getConnection();
    }
    @Override
    public void ajouter(Transport transport) {
        String sql = "INSERT INTO `transport`(`type`, `compagnie`, `prix`) " +
                "VALUES ('" + transport.getType() + "','" + transport.getCompagnie() + "','" + transport.getPrix() + "')";

        try {
            Statement statement = connection.createStatement();
            int lines = statement.executeUpdate(sql);
            System.out.println("Transport ajouter avec succes!");
            System.out.println(lines);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void modifier(Transport transport) {

    }

    @Override
    public void supprimer(Transport transport) {

    }

    @Override
    public List<Transport> getA() {
        String sql = "SELECT * FROM `transport`";
        List<Transport> transport = new ArrayList<Transport>();
        try {
            Statement st= connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Transport t = new Transport();
                t.setId(rs.getInt("id"));
                t.setType(rs.getString("type"));
                t.setCompagnie(rs.getString("compagnie"));
                t.setPrix(rs.getDouble("prix"));
                transport.add(t);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return transport;
    }


}


