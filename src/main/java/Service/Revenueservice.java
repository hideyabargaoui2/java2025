package Service;

import Modules.Revenue;
import Utils.Maconecxion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Revenueservice implements  Iservice<Revenue> {


    public Revenueservice() {
        connection = Maconecxion.getInstance().getConnection();
    }

    private Connection connection;

    @Override
    public void add(Revenue revenu) throws SQLException {
        String sql = "INSERT INTO revenu (daterevenue, modereception, Rmontant, devise) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, revenu.getDaterevenue());
            pstmt.setString(2, revenu.getModereception());
            pstmt.setInt(3, revenu.getRmontant());
            pstmt.setString(4, revenu.getDevise());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la revenu : " + e.getMessage());
            throw e;
        }
    }

    public Revenue getById(int idvoy) {
        Revenue revenue = null;
        String sql = "SELECT * FROM revenu WHERE idvoy = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idvoy);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                revenue = new Revenue();
                revenue.setIdvoy(rs.getInt("idvoy"));
                revenue.setDaterevenue(rs.getString("daterevenue"));
                revenue.setModereception(rs.getString("modereception"));
                revenue.setRmontant(rs.getInt("Rmontant"));
                revenue.setDevise(rs.getString("devise"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du revenu par ID : " + e.getMessage());
        }
        return revenue;
    }








    @Override
    public void update(Revenue revenu) {
        String sql = "UPDATE revenu SET daterevenue = ?, modereception = ?, Rmontant = ?, devise = ? WHERE idvoy = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, revenu.getDaterevenue());
            pstmt.setString(2, revenu.getModereception());
            pstmt.setInt(3, revenu.getRmontant());
            pstmt.setString(4, revenu.getDevise());
            pstmt.setInt(5, revenu.getIdvoy());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la revenu : " + e.getMessage());
        }
    }


    @Override
    public void delete(Revenue revenu) {
        String sql = "DELETE FROM revenu WHERE idvoy = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, revenu.getIdvoy());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du revenu : " + e.getMessage());
        }
    }


    @Override
    public List<Revenue> getAll() throws SQLException {
        String sql = "SELECT * FROM revenu";
        List<Revenue> revenues = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Revenue rev = new Revenue();
                rev.setIdvoy(rs.getInt("idvoy"));
                rev.setDaterevenue(rs.getString("daterevenue"));
                rev.setModereception(rs.getString("modereception"));
                rev.setRmontant(rs.getInt("Rmontant"));
                rev.setDevise(rs.getString("devise"));
                revenues.add(rev);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des revenus : " + e.getMessage());
            throw e;
        }

        return revenues;
    }
}








