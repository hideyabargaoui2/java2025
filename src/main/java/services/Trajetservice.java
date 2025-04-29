package services;

import models.Trajet;
import utils.Maconnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Trajetservice implements Iservice<Trajet>{
    private Connection connection;

    public Trajetservice() {
        connection = Maconnexion.getInstance().getConnection();
    }

    @Override
    public boolean ajouter(Trajet trajet) {
        String sql = "INSERT INTO `trajet`(`date`, `heure`, `destination`, `transport`, `duree`) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(trajet.getDate()));
            ps.setInt(2, trajet.getHeure());
            ps.setString(3, trajet.getDestination());
            ps.setString(4, trajet.getTransport());
            ps.setInt(5, trajet.getDuree());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        trajet.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du trajet : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void modifier(Trajet trajet) {
        String sql = "UPDATE trajet SET date = ?, heure = ?, destination = ?, transport = ?, duree = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(trajet.getDate()));
            ps.setInt(2, trajet.getHeure());
            ps.setString(3, trajet.getDestination());
            ps.setString(4, trajet.getTransport());
            ps.setInt(5, trajet.getDuree());
            ps.setInt(6, trajet.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Trajet modifié avec succès.");
            } else {
                System.out.println("Aucun trajet modifié (ID non trouvé ?).");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du trajet : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Trajet trajet) {
        String sql = "DELETE FROM trajet WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, trajet.getId());

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Trajet supprimé avec succès.");
            } else {
                System.out.println("Aucun trajet supprimé (ID non trouvé ?).");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du trajet : " + e.getMessage());
        }
    }

    @Override
    public List<Trajet> getA() {
        String sql = "SELECT * FROM `trajet`";
        List<Trajet> trajets = new ArrayList<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Trajet t = new Trajet();

                // Récupérer l'ID
                t.setId(rs.getInt("id"));

                // Récupération et conversion de la date
                try {
                    Timestamp timestamp = rs.getTimestamp("date");
                    if (timestamp != null) {
                        t.setDate(timestamp.toLocalDateTime());
                    } else {
                        // Si la date est null, utiliser la date actuelle
                        t.setDate(LocalDateTime.now());
                    }
                } catch (Exception e) {
                    System.out.println("Erreur date pour trajet ID " + rs.getInt("id") + ": " + e.getMessage());
                    t.setDate(LocalDateTime.now());
                }

                t.setHeure(rs.getInt("heure"));
                t.setDestination(rs.getString("destination"));
                t.setTransport(rs.getString("transport"));
                t.setDuree(rs.getInt("duree"));

                trajets.add(t);
            }

            System.out.println("Nombre total de trajets récupérés: " + trajets.size());

        } catch (SQLException e) {
            System.out.println("Erreur SQL dans getA() : " + e.getMessage());
            e.printStackTrace();
        }

        return trajets;
    }
}