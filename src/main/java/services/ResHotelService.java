package services;

import models.ResHotel;
import models.hotel;
import utils.Maconnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResHotelService implements iServicee<ResHotel> {
    private Connection connection;

    public ResHotelService() {
        try {
            connection = Maconnection.getInstance().getConnection();
            verifierEtMettreAJourSchema(); // Vérifier et mettre à jour le schéma si nécessaire
        } catch (Exception e) {
            System.err.println("Erreur avec la connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si la colonne nombre_chambres existe et l'ajoute si nécessaire
     */
    private void verifierEtMettreAJourSchema() {
        try {
            // Vérifier si la colonne existe déjà
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "reshotel", "nombre_chambres");

            if (!columns.next()) {
                // La colonne n'existe pas, donc on l'ajoute
                String sql = "ALTER TABLE reshotel ADD COLUMN nombre_chambres INT DEFAULT 1";
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Colonne 'nombre_chambres' ajoutée avec succès à la table 'reshotel'");
                }
            }
            columns.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du schéma : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean ajouter(ResHotel resHotel) throws SQLException {
        String sql = "INSERT INTO reshotel (hotel, startres, dateres, nombre_chambres) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, resHotel.getHotel());
            ps.setString(2, resHotel.getStartres());
            ps.setTimestamp(3, Timestamp.valueOf(resHotel.getDateres()));
            ps.setInt(4, resHotel.getNombreChambres());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        resHotel.setId(rs.getInt(1));
                    }
                }
                System.out.println("Réservation ajoutée avec succès. ID: " + resHotel.getId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réservation : " + e.getMessage());
            throw e;
        }
        return false;
    }

    @Override
    public boolean modifier(ResHotel resHotel) throws SQLException {
        String sql = "UPDATE reshotel SET hotel = ?, startres = ?, dateres = ?, nombre_chambres = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, resHotel.getHotel());
            ps.setString(2, resHotel.getStartres());
            ps.setTimestamp(3, Timestamp.valueOf(resHotel.getDateres()));
            ps.setInt(4, resHotel.getNombreChambres());
            ps.setInt(5, resHotel.getId());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Réservation modifiée avec succès. Lignes affectées: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la réservation : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean supprimer(ResHotel resHotel) throws SQLException {
        // Avant de supprimer la réservation, on doit libérer les chambres réservées
        try {
            // Récupérer l'hôtel correspondant à partir de son nom
            HotelService hotelService = new HotelService();
            List<hotel> hotels = hotelService.searchByName(resHotel.getHotel());

            if (!hotels.isEmpty()) {
                // Prendre le premier hôtel correspondant au nom
                hotel h = hotels.get(0);

                // Mettre à jour le nombre de chambres réservées
                int nouveauNombreReservees = h.getNombreChambresReservees() - resHotel.getNombreChambres();
                if (nouveauNombreReservees < 0) nouveauNombreReservees = 0; // Sécurité

                h.setNombreChambresReservees(nouveauNombreReservees);
                hotelService.modifier(h);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la libération des chambres : " + e.getMessage());
            // Ne pas empêcher la suppression de la réservation
        }

        String sql = "DELETE FROM reshotel WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, resHotel.getId());
            int rowsAffected = ps.executeUpdate();
            System.out.println("Réservation supprimée avec succès. Lignes affectées: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réservation : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<ResHotel> getA() throws SQLException {
        String sql = "SELECT * FROM reshotel";
        List<ResHotel> reservations = new ArrayList<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ResHotel res = new ResHotel();
                res.setId(rs.getInt("id"));
                res.setHotel(rs.getString("hotel"));
                res.setStartres(rs.getString("startres"));
                res.setDateres(rs.getTimestamp("dateres").toLocalDateTime());

                // Récupérer nombre_chambres s'il existe
                try {
                    res.setNombreChambres(rs.getInt("nombre_chambres"));
                } catch (SQLException e) {
                    // Si la colonne n'existe pas, on met 1 par défaut
                    res.setNombreChambres(1);
                }

                reservations.add(res);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans getA() : " + e.getMessage());
            throw e;
        }

        return reservations;
    }

    // Autres méthodes utiles...
}