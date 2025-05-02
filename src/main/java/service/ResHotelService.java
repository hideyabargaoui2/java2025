package service;

import models.ResHotel;
import utils.Maconnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResHotelService implements iService<ResHotel> {
    private Connection connection;

    public ResHotelService() {
        connection = Maconnection.getInstance().getConnection();
    }

    @Override
    public boolean ajouter(ResHotel resHotel) {
        String sql = "INSERT INTO reshotel(hotel, startres, dateres) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, resHotel.getHotel());
            ps.setString(2, resHotel.getStartres());
            ps.setTimestamp(3, Timestamp.valueOf(resHotel.getDateres()));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        resHotel.setIdres(rs.getInt(1));
                    }
                }
                System.out.println("Réservation d'hôtel ajoutée avec succès. ID: " + resHotel.getIdres());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la réservation : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean modifier(ResHotel resHotel) {
        String sql = "UPDATE reshotel SET hotel = ?, startres = ?, dateres = ? WHERE idres = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, resHotel.getHotel());
            ps.setString(2, resHotel.getStartres());
            ps.setTimestamp(3, Timestamp.valueOf(resHotel.getDateres()));
            ps.setInt(4, resHotel.getIdres());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Réservation modifiée avec succès. Lignes affectées: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de la réservation : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimer(ResHotel resHotel) {
        String sql = "DELETE FROM reshotel WHERE idres = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, resHotel.getIdres());
            int rowsAffected = ps.executeUpdate();
            System.out.println("Réservation supprimée avec succès. Lignes affectées: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la réservation : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ResHotel> getA() {
        String sql = "SELECT * FROM reshotel";
        List<ResHotel> reservations = new ArrayList<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ResHotel res = new ResHotel();
                res.setIdres(rs.getInt("idres"));
                res.setHotel(rs.getString("hotel"));
                res.setStartres(rs.getString("startres"));

                try {
                    Timestamp timestamp = rs.getTimestamp("dateres");
                    if (timestamp != null) {
                        res.setDateres(timestamp.toLocalDateTime());
                    } else {
                        // Si la date est null, utiliser la date actuelle
                        res.setDateres(LocalDateTime.now());
                    }
                } catch (Exception e) {
                    System.out.println("Erreur date pour réservation ID " + rs.getInt("idres") + ": " + e.getMessage());
                    res.setDateres(LocalDateTime.now());
                }

                reservations.add(res);
            }

            System.out.println("Nombre total de réservations récupérées: " + reservations.size());

        } catch (SQLException e) {
            System.out.println("Erreur SQL dans getA() : " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }
}