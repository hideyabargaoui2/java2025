package Service;

import Modules.Depensse;
import Utils.Maconecxion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Depensseservice implements Iservice<Depensse> {

    private Connection connection;

    public Depensseservice() {
        connection = Maconecxion.getInstance().getConnection();
    }

    @Override
    public void add(Depensse depensse) throws SQLException {
        String sql = "INSERT INTO depens (modepay, datepay, categorie, montant, description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, depensse.getModdepay());
            pstmt.setString(2, depensse.getDatepay());
            pstmt.setString(3, depensse.getCategories());
            pstmt.setInt(4, depensse.getMontant());
            pstmt.setString(5, depensse.getDescripiton());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la dépense : " + e.getMessage());
            throw e;
        }
    }


    public Depensse getById(int idvoy) {
        Depensse depensse = null;
        String sql = "SELECT * FROM depens WHERE idvoy = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idvoy);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                depensse = new Depensse();
                depensse.setIdvoy(rs.getInt("idvoy"));
                depensse.setModdepay(rs.getString("modepay"));
                depensse.setDatepay(rs.getString("datepay"));
                depensse.setCategories(rs.getString("categorie"));
                depensse.setMontant(rs.getInt("montant"));
                depensse.setDescripiton(rs.getString("description"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la dépense par ID : " + e.getMessage());
        }
        return depensse;
    }


    @Override
    public void update(Depensse depensse) {
        String sql = "UPDATE depens SET modepay = ?, datepay = ?, categorie = ?, montant = ?, description = ? WHERE idvoy = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, depensse.getModdepay());
            pstmt.setString(2, depensse.getDatepay());
            pstmt.setString(3, depensse.getCategories());
            pstmt.setInt(4, depensse.getMontant());
            pstmt.setString(5, depensse.getDescripiton());
            pstmt.setInt(6, depensse.getIdvoy());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la dépense : " + e.getMessage());
        }
    }


    @Override
    public void delete(Depensse depensse) {
        String sql = "DELETE FROM depens WHERE idvoy = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, depensse.getIdvoy()); // Assumes you have getId()
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la dépense : " + e.getMessage());
        }
    }

    @Override
    public List<Depensse> getAll() throws SQLException {
        String sql = "SELECT * FROM depens";
        List<Depensse> depensses = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Depensse dep = new Depensse();
                dep.setIdvoy(rs.getInt("idvoy"));  // Important
                dep.setModdepay(rs.getString("modepay"));
                dep.setDatepay(rs.getString("datepay"));
                dep.setCategories(rs.getString("categorie"));
                dep.setMontant(rs.getInt("montant"));
                dep.setDescripiton(rs.getString("description"));
                depensses.add(dep);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des dépenses : " + e.getMessage());
            throw e;
        }

        return depensses;
    }
}
