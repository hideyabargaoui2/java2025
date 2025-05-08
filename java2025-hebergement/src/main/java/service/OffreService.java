package service;

import models.Offre;
import utils.Maconnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OffreService {
    private Connection connection;

    public OffreService() {
        this.connection = Maconnection.getInstance().getConnection();
    }

    // Ajouter une offre
    public void addOffre(Offre offre) throws SQLException {
        String query = "INSERT INTO offre (lieu, date_depart, date_retour, capacite, prix_total, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, offre.getLieu());
            pst.setDate(2, Date.valueOf(offre.getDateDepart()));
            pst.setDate(3, Date.valueOf(offre.getDateRetour()));
            pst.setInt(4, offre.getCapacite());
            pst.setDouble(5, offre.getPrixTotal());
            pst.setString(6, offre.getDescription());

            pst.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    offre.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Modifier une offre
    public void updateOffre(Offre offre) throws SQLException {
        String query = "UPDATE offre SET lieu = ?, date_depart = ?, date_retour = ?, " +
                "capacite = ?, prix_total = ?, description = ? WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, offre.getLieu());
            pst.setDate(2, Date.valueOf(offre.getDateDepart()));
            pst.setDate(3, Date.valueOf(offre.getDateRetour()));
            pst.setInt(4, offre.getCapacite());
            pst.setDouble(5, offre.getPrixTotal());
            pst.setString(6, offre.getDescription());
            pst.setInt(7, offre.getId());

            pst.executeUpdate();
        }
    }

    // Supprimer une offre
    public void deleteOffre(int id) throws SQLException {
        String query = "DELETE FROM offre WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    // Récupérer toutes les offres
    public List<Offre> getAllOffres() throws SQLException {
        List<Offre> offres = new ArrayList<>();
        String query = "SELECT * FROM offre";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Offre offre = new Offre();
                offre.setId(rs.getInt("id"));
                offre.setLieu(rs.getString("lieu"));
                offre.setDateDepart(rs.getDate("date_depart").toLocalDate());
                offre.setDateRetour(rs.getDate("date_retour").toLocalDate());
                offre.setCapacite(rs.getInt("capacite"));
                offre.setPrixTotal(rs.getDouble("prix_total"));
                offre.setDescription(rs.getString("description"));

                offres.add(offre);
            }
        }

        return offres;
    }

    // Récupérer une offre par ID
    public Offre getOffreById(int id) throws SQLException {
        String query = "SELECT * FROM offre WHERE id = ?";
        Offre offre = null;

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    offre = new Offre();
                    offre.setId(rs.getInt("id"));
                    offre.setLieu(rs.getString("lieu"));
                    offre.setDateDepart(rs.getDate("date_depart").toLocalDate());
                    offre.setDateRetour(rs.getDate("date_retour").toLocalDate());
                    offre.setCapacite(rs.getInt("capacite"));
                    offre.setPrixTotal(rs.getDouble("prix_total"));
                    offre.setDescription(rs.getString("description"));
                }
            }
        }

        return offre;
    }
}