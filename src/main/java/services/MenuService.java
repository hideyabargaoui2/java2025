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
            pst.setString(1, m.getName());  // Nom du menu
            pst.setInt(2, m.getPrix());     // Prix du menu
            pst.setString(3, m.getDescription()); // Description du menu
            pst.setInt(4, m.getRestaurant().getId()); // ID du restaurant associé

            pst.executeUpdate();  // Insertion dans la base
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void update(Menu menu) {
        String query = "UPDATE menu SET nom = ?, prix = ?, description = ?, restaurant_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, menu.getName());
            pstmt.setInt(2, menu.getPrix());
            pstmt.setString(3, menu.getDescription());
            pstmt.setInt(4, menu.getRestaurant().getId());
            pstmt.setInt(5, menu.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du menu : " + e.getMessage());
            e.printStackTrace();
        }
    }
    private Restaurant getRestaurantById(int restaurantId) {
        Restaurant restaurant = null;
        String query = "SELECT * FROM restaurant WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, restaurantId); // On associe le paramètre à la requête
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Si un restaurant est trouvé, on le crée et on l'initialise
                    restaurant = new Restaurant();
                    restaurant.setId(rs.getInt("id"));
                    restaurant.setNom(rs.getString("nom"));
                    // Ajouter d'autres champs si nécessaire, par exemple:
                    // restaurant.setAdresse(rs.getString("adresse"));
                    // restaurant.setTelephone(rs.getString("telephone"));
                } else {
                    // Si aucun restaurant n'est trouvé pour cet ID, afficher un message ou gérer autrement
                    System.out.println("Aucun restaurant trouvé avec l'ID : " + restaurantId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionnel : loguer ou afficher un message d'erreur plus spécifique pour aider au diagnostic
        }

        return restaurant; // Retourne null si aucun restaurant n'est trouvé
    }

    public List<Menu> getAll() {
        List<Menu> menus = new ArrayList<>();
        String query = "SELECT * FROM menu";  // Ou une requête filtrée par restaurant_id si nécessaire
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Menu menu = new Menu();
                menu.setId(rs.getInt("id"));
                menu.setName(rs.getString("nom"));
                menu.setPrix(rs.getInt("prix"));
                menu.setDescription(rs.getString("description"));

                // Charger le restaurant associé au menu (par exemple, en récupérant par l'ID du restaurant)
                int restaurantId = rs.getInt("restaurant_id");
                Restaurant restaurant = getRestaurantById(restaurantId); // Assurez-vous d'avoir cette méthode dans votre code
                menu.setRestaurant(restaurant);

                menus.add(menu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menus;
    }

    public static void updateMenu(Menu menu) throws SQLException {
        String query = "UPDATE menu SET nom = ?, prix = ?, description = ? WHERE id = ?";
        // Exemple d'initialisation de la connexion
        Connection cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/nom_base", "utilisateur", "motdepasse");

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, menu.getName());
            pst.setDouble(2, menu.getPrix());
            pst.setString(3, menu.getDescription());
            pst.setInt(4, menu.getId()); // Assurez-vous que l'id est valide et correspond au menu

            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour du menu", e);
        }
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
