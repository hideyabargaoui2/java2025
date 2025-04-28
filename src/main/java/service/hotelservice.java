package service;
import models.hotel;
import utils.Maconnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class hotelservice implements iService<hotel> {

    private Connection connection;

    public hotelservice() {
        connection = Maconnection.getInstance().getConnection();
    }

    @Override
    public void ajouter(hotel hotel) throws SQLException {

        String SQL ="INSERT INTO `hotel`(`nom`, `prixnuit`, `nombrenuit`, `standing`, `adresse`) VALUES ('"+hotel.getNom()+"','"+hotel.getPrixParNuit()+"','"+hotel.getNombreNuits()+"','"+hotel.getStanding()+"','"+hotel.getAdresse()+"')";
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void modifier(hotel hotel) {

    }

    @Override
    public void supprimer(hotel hotel) {

    }

    @Override
    public List<hotel> getA() {
        String SQL ="SELECT * FROM `hotel`";
        List<hotel> hotels = new ArrayList<hotel>();
        try {
            Statement stmt =connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                hotel hotel = new hotel();
                hotel.setNom(rs.getString("nom"));
                hotel.setPrixParNuit(rs.getInt("prixnuit"));
                hotel.setNombreNuits(rs.getInt("nombrenuit"));
                hotels.add(hotel);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return hotels;


    }
}
