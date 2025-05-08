package service;

import models.Reservation;
import models.Offre;
import service.OffreService;
import utils.Maconnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private Connection connection;
    private OffreService offreService;

    public ReservationService() {
        this.connection = Maconnection.getInstance().getConnection();
        this.offreService = new OffreService();
    }

    // Add a new reservation
    public boolean addReservation(Reservation reservation) {
        String query = "INSERT INTO reservation (offre_id, client_nom, client_email, " +
                "date_reservation, nombre_personnes, statut) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reservation.getOffre().getId());
            pst.setString(2, reservation.getClientNom());
            pst.setString(3, reservation.getClientEmail());
            pst.setDate(4, Date.valueOf(reservation.getDateReservation()));
            pst.setInt(5, reservation.getNombrePersonnes());
            pst.setString(6, reservation.getStatut());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update a reservation
    public boolean updateReservation(Reservation reservation) {
        String query = "UPDATE reservation SET offre_id = ?, client_nom = ?, client_email = ?, " +
                "date_reservation = ?, nombre_personnes = ?, statut = ? WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, reservation.getOffre().getId());
            pst.setString(2, reservation.getClientNom());
            pst.setString(3, reservation.getClientEmail());
            pst.setDate(4, Date.valueOf(reservation.getDateReservation()));
            pst.setInt(5, reservation.getNombrePersonnes());
            pst.setString(6, reservation.getStatut());
            pst.setInt(7, reservation.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a reservation
    public boolean deleteReservation(int id) {
        String query = "DELETE FROM reservation WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all reservations
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                reservations.add(createReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // Get reservation by ID
    public Reservation getReservationById(int id) {
        String query = "SELECT * FROM reservation WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return createReservationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get reservations by offer ID
    public List<Reservation> getReservationsByOffre(int offreId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation WHERE offre_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, offreId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    reservations.add(createReservationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // Get reservations by client email
    public List<Reservation> getReservationsByClientEmail(String email) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation WHERE client_email = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, email);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    reservations.add(createReservationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // Helper method to create Reservation object from ResultSet
    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));

        // Get associated offer
        int offreId = rs.getInt("offre_id");
        Offre offre = offreService.getOffreById(offreId);
        reservation.setOffre(offre);

        reservation.setClientNom(rs.getString("client_nom"));
        reservation.setClientEmail(rs.getString("client_email"));
        reservation.setDateReservation(rs.getDate("date_reservation").toLocalDate());
        reservation.setNombrePersonnes(rs.getInt("nombre_personnes"));
        reservation.setStatut(rs.getString("statut"));

        return reservation;
    }
}