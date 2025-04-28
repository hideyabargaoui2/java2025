package service;
import models.reshotel;
import utils.Maconnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class reshotelservice implements iService <reshotel> {

    private Connection connection ;

    public reshotelservice() {
        connection = Maconnection.getInstance().getConnection();
    }

    @Override
    public void ajouter(reshotel reshotel) {
        String SQL="INSERT INTO `reshotel`(`dateres`, `hotel`, `statres`)" +
                " VALUES ('"+reshotel.getDateReservation()+"','"+reshotel.gethotel()+"','"+reshotel.getStatutReservation()+"')";
        try {
            Statement stmt = connection.createStatement();
            int lines = stmt.executeUpdate(SQL);
            System.out.println("Reshotel ajout  avec succes!");
            System.out.println(lines);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(reshotel reshotel) {

    }

    @Override
    public void supprimer(reshotel reshotel) {

    }

    @Override
    public List<reshotel> getA() {
        String SQL="SELECT * FROM `reshotel`";
        List<reshotel> reshotels = new ArrayList<reshotel>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                reshotel reshotel = new reshotel();
                reshotel.setDateReservation(rs.getString("dateres"));
                reshotel.sethotel(rs.getString("hotel"));
                reshotel.setStatutReservation(rs.getString("statres"));
                reshotels.add(reshotel);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reshotels;
    }
}
