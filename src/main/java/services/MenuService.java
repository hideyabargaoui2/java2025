package services;
import models.Menu;
import models.Restaurant;
import util.MyConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class MenuService implements IService<Menu> {
    private Connection cnx;

    public MenuService() {
        cnx = MyConnection.getInstance().getConnection();
    }
    @Override
    public void add(Menu menu) throws SQLException {

        String query = "INSERT INTO menu (id, nom, prix, description, restaurant_id) VALUES ('"
                + menu.getId() + "', '"
                + menu.getName() + "', '"
                + menu.getPrix() + "', '"
                + menu.getDescription() + "', '"
                + menu.getRestaurant().getId() + "')";
        try (Statement stmt = cnx.createStatement()) {
            stmt.executeUpdate(query);
        }
    }

    @Override
    public void delete(Menu menu) {
        String query = "UPDATE menu SET "
                + "id = '" + menu.getId() + "', "
                + "name = '" + menu.getName() + "', "
                + "prix = '" + menu.getPrix() + "', "
                + "description = '" + menu.getDescription() + "', "
                + "restaurant_id = '" + menu.getRestaurant().getId() + "'";
        try (Statement stmt = cnx.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du menu : " + e.getMessage());
        }


    }

    @Override
    public void update(Menu menu) {
    }
    @Override
    public List<Menu> getAll() throws SQLException {
        List<Menu> menus = new ArrayList<>();
        String query = "SELECT * FROM menu";
        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prix = rs.getString("prix");
                String description = rs.getString("description");
                int restaurantId = rs.getInt("restaurant_id");
                Restaurant restaurant = new Restaurant();
                restaurant.setId(restaurantId);
                Menu menu = new Menu(id, nom, prix, description, restaurant);
                menus.add(menu);
            }
        }
        return menus;
    }
}
