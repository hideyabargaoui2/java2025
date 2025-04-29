package services;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Restaurant;

import static java.awt.SystemColor.menu;

public class RestaurantServices implements IService<Restaurant> {
    private Connection cnx;

    public RestaurantServices() {
        cnx = util.MyConnection.getInstance().getConnection();
    }

    @Override
    public void add(Restaurant restaurant) throws SQLException {
        String sql = "INSERT INTO restaurant (id, nom, adresse, type, horaire_ouv, horaire_ferm, classement) VALUES ('"
                + restaurant.getId() + "', '"
                + restaurant.getNom() + "', '"
                + restaurant.getAdresse() + "', '"
                + restaurant.getType() + "', '"
                + restaurant.getHeure_ouv() + "', '"
                + restaurant.getHeure_ferm() + "', '"
                + restaurant.getClassement() + "')";
        try {
            Statement stmt = cnx.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override

    public void update(Restaurant restaurant) {
        String query = "UPDATE restaurant SET "
                + "nom = '" + restaurant.getNom() + "', "
                + "adresse = '" + restaurant.getAdresse() + "', "
                + "type = '" + restaurant.getType() + "', "
                + "classement = " + restaurant.getClassement() + ", "
                + "horaire_ouv = '" + restaurant.getHeure_ouv() + "', "
                + "horaire_ferm = '" + restaurant.getHeure_ferm() + "' "
                + "WHERE id = " + restaurant.getId();

        try (Statement stmt = cnx.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du restaurant : " + e.getMessage());
        }
    }



    @Override
    public void delete(Restaurant restaurant) {
        String sql = "DELETE FROM restaurant WHERE id = " + restaurant.getId();
        try {
            Statement stmt = cnx.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Restaurant> getAll() throws SQLException {
        String req = "SELECT * FROM restaurant";
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Restaurant restaurant = new Restaurant(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("type"),
                        rs.getInt("classement"),
                        rs.getTime("horaire_ouv"),
                        rs.getTime("horaire_ferm")
                );
                restaurants.add(restaurant);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return restaurants;
    }
}
