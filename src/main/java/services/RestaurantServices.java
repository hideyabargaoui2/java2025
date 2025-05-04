package services;

import models.Restaurant;
import util.MyConnection;

import java.sql.*;
import java.util.*;

public class RestaurantServices {
    private Connection con = MyConnection.getInstance().getConnection();

    public void add(Restaurant r) {
        // Vérifie que les champs obligatoires ne sont pas vides
        if (r.getNom() == null || r.getNom().trim().isEmpty()) {
            System.out.println("Le nom du restaurant ne peut pas être vide");
            return;
        }
        if (r.getAdresse() == null || r.getAdresse().trim().isEmpty()) {
            System.out.println("L'adresse du restaurant ne peut pas être vide");
            return;
        }

        String sql = "INSERT INTO restaurant (nom, adresse, type, horaire_ouv, horaire_ferm, classement) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, r.getNom());
            pst.setString(2, r.getAdresse());  // Vérifie que cette valeur n'est pas vide ou null
            pst.setString(3, r.getType());
            pst.setTime(4, r.getHeureOuv());
            pst.setTime(5, r.getHeureFerm());
            pst.setInt(6, r.getClassement());
            pst.executeUpdate();
            System.out.println("Restaurant ajouté avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }


    public List<Restaurant> afficher() {
        List<Restaurant> liste = new ArrayList<>();
        String sql = "SELECT * FROM restaurant";
        try (Statement ste = con.createStatement(); ResultSet rs = ste.executeQuery(sql)) {
            while (rs.next()) {
                Time heureOuv = rs.getTime("horaire_ouv");
                Time heureFerm = rs.getTime("horaire_ferm");
                int classement = rs.getInt("classement");

                Restaurant r = new Restaurant(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("type"),
                        heureOuv,
                        heureFerm,
                        classement
                );
                liste.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM restaurant WHERE id=?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifier(Restaurant r) {
        String sql = "UPDATE restaurant SET nom=?, adresse=?, type=? WHERE id=?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, r.getNom());
            pst.setString(2, r.getAdresse());
            pst.setString(3, r.getType());
            pst.setInt(4, r.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Restaurant> getAll() throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        String query = "SELECT * FROM restaurant";  // Assure-toi que la table restaurant existe

        // Pas besoin de recréer une connexion, utilise la connexion existante
        try (PreparedStatement statement = con.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Restaurant restaurant = new Restaurant();
                restaurant.setId(resultSet.getInt("id"));
                restaurant.setNom(resultSet.getString("nom"));
                restaurants.add(restaurant);
                System.out.println("Restaurant loaded: " + restaurant.getNom());  // Affichage pour débogage
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des restaurants : " + e.getMessage());
            throw e;  // Re-throw exception pour la gestion de l'erreur à un autre niveau
        }

        return restaurants;
    }



}
