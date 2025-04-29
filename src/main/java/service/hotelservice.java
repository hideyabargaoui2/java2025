package service;

import models.hotel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class hotelservice implements iService<hotel> {
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;

    public hotelservice() {
        // Connexion à la base de données (à adapter selon votre configuration)
        try {
            // Chargement du pilote JDBC
            Class.forName("com.mysql.jdbc.Driver");

            // Établissement de la connexion
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hebergementmanager", "root", "");

            // Vérification que la connexion est établie
            if (connection != null) {
                statement = connection.createStatement();
            } else {
                throw new SQLException("La connexion n'a pas pu être établie");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Pilote JDBC introuvable : " + e.getMessage());
            throw new RuntimeException("Pilote JDBC introuvable", e);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }

    @Override
    public void ajouter(hotel h) throws SQLException {
        String requete = "INSERT INTO hotel (nom, prixnuit, nombrenuit, standing, adresse) VALUES (?, ?, ?, ?, ?)";
        preparedStatement = connection.prepareStatement(requete);
        preparedStatement.setString(1, h.getNom());
        preparedStatement.setDouble(2, h.getPrixParNuit());
        preparedStatement.setInt(3, h.getNombreNuits());
        preparedStatement.setString(4, h.getStanding());
        preparedStatement.setString(5, h.getAdresse());
        preparedStatement.executeUpdate();
    }

    @Override
    public void modifier(hotel h) throws SQLException {
        String requete = "UPDATE hotel SET nom = ?, prixnuit = ?, nombrenuit = ?, standing = ?, adresse = ? WHERE id = ?";
        preparedStatement = connection.prepareStatement(requete);
        preparedStatement.setString(1, h.getNom());
        preparedStatement.setDouble(2, h.getPrixParNuit());
        preparedStatement.setInt(3, h.getNombreNuits());
        preparedStatement.setString(4, h.getStanding());
        preparedStatement.setString(5, h.getAdresse());
        preparedStatement.setInt(6, h.getId());
        preparedStatement.executeUpdate();
    }

    @Override
    public void supprimer(hotel h) throws SQLException {
        // Implémentation de la méthode supprimer avec un objet hotel comme paramètre
        supprimer(h.getId());
    }

    // Conserver la méthode supprimer par ID comme méthode auxiliaire
    public void supprimer(int id) throws SQLException {
        String requete = "DELETE FROM hotel WHERE id = ?";
        preparedStatement = connection.prepareStatement(requete);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<hotel> getA() throws SQLException {
        List<hotel> hotels = new ArrayList<>();
        String requete = "SELECT * FROM hotel";
        ResultSet resultSet = statement.executeQuery(requete);

        while (resultSet.next()) {
            hotel h = new hotel();
            h.setId(resultSet.getInt("id"));
            h.setNom(resultSet.getString("nom"));
            h.setPrixParNuit(resultSet.getDouble("prixnuit"));
            h.setNombreNuits(resultSet.getInt("nombrenuit"));
            h.setStanding(resultSet.getString("standing"));
            h.setAdresse(resultSet.getString("adresse"));
            hotels.add(h);
        }

        return hotels;
    }

    public hotel getById(int id) throws SQLException {
        String requete = "SELECT * FROM hotel WHERE id = ?";
        preparedStatement = connection.prepareStatement(requete);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            hotel h = new hotel();
            h.setId(resultSet.getInt("id"));
            h.setNom(resultSet.getString("nom"));
            h.setPrixParNuit(resultSet.getDouble("prixnuit"));
            h.setNombreNuits(resultSet.getInt("nombrenuit"));
            h.setStanding(resultSet.getString("standing"));
            h.setAdresse(resultSet.getString("adresse"));
            return h;
        }

        return null;
    }

    public List<hotel> rechercherParNom(String nomRecherche) throws SQLException {
        List<hotel> hotels = new ArrayList<>();
        String requete = "SELECT * FROM hotel WHERE nom LIKE ?";
        preparedStatement = connection.prepareStatement(requete);
        preparedStatement.setString(1, "%" + nomRecherche + "%");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            hotel h = new hotel();
            h.setId(resultSet.getInt("id"));
            h.setNom(resultSet.getString("nom"));
            h.setPrixParNuit(resultSet.getDouble("prixnuit"));
            h.setNombreNuits(resultSet.getInt("nombrenuit"));
            h.setStanding(resultSet.getString("standing"));
            h.setAdresse(resultSet.getString("adresse"));
            hotels.add(h);
        }

        return hotels;
    }
}