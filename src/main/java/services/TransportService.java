package services;

import models.Transport;
import utils.Maconnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransportService implements Iservice<Transport> {
    private Connection connection;

    public TransportService() {
        connection = Maconnexion.getInstance().getConnection();
    }

    @Override
    public boolean ajouter(Transport transport) {
        String sql = "INSERT INTO `transport`(`type`, `compagnie`, `prix`) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, transport.getType());
            ps.setString(2, transport.getCompagnie());
            ps.setDouble(3, transport.getPrix());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        transport.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du transport : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void modifier(Transport transport) {
        String sql = "UPDATE transport SET type = ?, compagnie = ?, prix = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, transport.getType());
            ps.setString(2, transport.getCompagnie());
            ps.setDouble(3, transport.getPrix());
            ps.setInt(4, transport.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Transport modifié avec succès.");
            } else {
                System.out.println("Aucun transport modifié (ID non trouvé ?).");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du transport : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Transport transport) {
        String sql = "DELETE FROM transport WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transport.getId());

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Transport supprimé avec succès.");
            } else {
                System.out.println("Aucun transport supprimé (ID non trouvé ?).");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du transport : " + e.getMessage());
        }
    }

    @Override
    public List<Transport> getA() {
        String sql = "SELECT * FROM `transport`";
        List<Transport> transports = new ArrayList<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Transport t = new Transport();

                t.setId(rs.getInt("id"));
                t.setType(rs.getString("type"));
                t.setCompagnie(rs.getString("compagnie"));
                t.setPrix(rs.getDouble("prix"));

                transports.add(t);
            }

            System.out.println("Nombre total de transports récupérés: " + transports.size());

        } catch (SQLException e) {
            System.out.println("Erreur SQL dans getA() : " + e.getMessage());
            e.printStackTrace();
        }

        return transports;
    }
}