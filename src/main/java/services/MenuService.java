package services;

import models.Menu;
import models.Restaurant;
import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private Connection con = MyConnection.getInstance().getConnection();
    private Connection cnx;

    public MenuService() {
        cnx = MyConnection.getInstance().getConnection();
    }

    public void add(Menu m) {
        String sql = "INSERT INTO `menu` (`nom`, `prix`, `description`, `restaurant_id`) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, m.getName());
            pst.setInt(2, m.getPrix());
            pst.setString(3, m.getDescription());
            pst.setInt(4, m.getRestaurant().getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Menu menu) {
        String sql = "UPDATE menu SET nom = ?, description = ?, prix = ?, restaurant_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setString(1, menu.getName());
            pstmt.setString(2, menu.getDescription());
            pstmt.setInt(3, menu.getPrix());
            pstmt.setInt(4, menu.getRestaurant().getId());
            pstmt.setInt(5, menu.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Restaurant getRestaurantById(int restaurantId) {
        Restaurant restaurant = null;
        String query = "SELECT * FROM restaurant WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, restaurantId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    restaurant = new Restaurant();
                    restaurant.setId(rs.getInt("id"));
                    restaurant.setNom(rs.getString("nom"));
                    restaurant.setAdresse(rs.getString("adresse"));
                    restaurant.setType(rs.getString("type"));
                    restaurant.setHeure_ouv(rs.getTime("heure_ouv"));
                    restaurant.setHeure_ferm(rs.getTime("heure_ferm"));
                    restaurant.setClassement(rs.getInt("classement"));
                    restaurant.setLatitude(rs.getDouble("latitude"));
                    restaurant.setLongitude(rs.getDouble("longitude"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return restaurant;
    }

    public List<Menu> getAll() {
        List<Menu> menus = new ArrayList<>();
        String query = "SELECT * FROM menu";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nom");
                int prix = rs.getInt("prix");
                String description = rs.getString("description");
                int restaurantId = rs.getInt("restaurant_id");

                Restaurant restaurant = getRestaurantById(restaurantId);

                // Correction ici : instantiation du Menu avec les bonnes valeurs
                Menu menu = new Menu(restaurant, name, prix, description);
                menu.setId(id);

                menus.add(menu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menus;
    }

    public void delete(Menu menu) {
        String query = "DELETE FROM menu WHERE id = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, menu.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du menu : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Menu> getAllSafe() {
        try {
            return getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
