package services;

import models.hotel;
import utils.Maconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service unifié pour la gestion des hôtels combinant les fonctionnalités des deux versions
 */
public class HotelService implements iServicee<hotel> {
    private Connection connection;

    /**
     * Constructeur initialisant la connexion à la base de données et vérifiant le schéma
     */
    public HotelService() {
        try {
            connection = Maconnection.getInstance().getConnection();
            verifierEtMettreAJourSchema(); // Appel de la méthode pour vérifier et mettre à jour le schéma
        } catch (Exception e) {
            System.err.println("Erreur avec la connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si la colonne nombre_chambres_reservees existe et l'ajoute si nécessaire
     */
    private void verifierEtMettreAJourSchema() {
        try {
            // Vérifier si la colonne existe déjà
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "hotel", "nombre_chambres_reservees");

            if (!columns.next()) {
                // La colonne n'existe pas, donc on l'ajoute
                String sql = "ALTER TABLE hotel ADD COLUMN nombre_chambres_reservees INT DEFAULT 0";
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Colonne 'nombre_chambres_reservees' ajoutée avec succès à la table 'hotel'");
                }
            }
            columns.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du schéma : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ajoute un nouvel hôtel à la base de données
     * @param hotel L'hôtel à ajouter
     * @return true si l'ajout a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    @Override
    public boolean ajouter(hotel hotel) throws SQLException {
        String sql = "INSERT INTO hotel(nom, prixnuit, nombrenuit, standing, adresse, nombre_chambres_reservees) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, hotel.getNom());
            ps.setDouble(2, hotel.getPrixnuit());
            ps.setInt(3, hotel.getNombrenuit());
            ps.setString(4, hotel.getStanding());
            ps.setString(5, hotel.getAdresse());
            ps.setInt(6, hotel.getNombreChambresReservees());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        hotel.setId(rs.getInt(1));
                    }
                }
                System.out.println("Hôtel ajouté avec succès. ID: " + hotel.getId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'hôtel : " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * Met à jour un hôtel dans la base de données
     * @param hotel L'hôtel à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    @Override
    public boolean modifier(hotel hotel) throws SQLException {
        String sql = "UPDATE hotel SET nom = ?, prixnuit = ?, nombrenuit = ?, standing = ?, adresse = ?, nombre_chambres_reservees = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hotel.getNom());
            ps.setDouble(2, hotel.getPrixnuit());
            ps.setInt(3, hotel.getNombrenuit());
            ps.setString(4, hotel.getStanding());
            ps.setString(5, hotel.getAdresse());
            ps.setInt(6, hotel.getNombreChambresReservees());
            ps.setInt(7, hotel.getId());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Hôtel modifié avec succès. Lignes affectées: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'hôtel : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Supprime un hôtel de la base de données
     * @param hotel L'hôtel à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    @Override
    public boolean supprimer(hotel hotel) throws SQLException {
        return supprimerById(hotel.getId());
    }

    /**
     * Supprime un hôtel de la base de données par son ID
     * @param id ID de l'hôtel à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean supprimerById(int id) throws SQLException {
        String sql = "DELETE FROM hotel WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Hôtel supprimé avec succès. Lignes affectées: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'hôtel : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Récupère tous les hôtels de la base de données
     * @return Liste des hôtels
     * @throws SQLException en cas d'erreur SQL
     */
    @Override
    public List<hotel> getA() throws SQLException {
        String sql = "SELECT * FROM hotel";
        List<hotel> hotels = new ArrayList<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                hotel h = new hotel();
                h.setId(rs.getInt("id"));
                h.setNom(rs.getString("nom"));
                h.setPrixnuit(rs.getDouble("prixnuit"));
                h.setNombrenuit(rs.getInt("nombrenuit"));
                h.setStanding(rs.getString("standing"));
                h.setAdresse(rs.getString("adresse"));

                // Récupérer nombre_chambres_reservees s'il existe dans la table
                try {
                    h.setNombreChambresReservees(rs.getInt("nombre_chambres_reservees"));
                } catch (SQLException e) {
                    // Si la colonne n'existe pas, on met 0 par défaut
                    h.setNombreChambresReservees(0);
                }

                hotels.add(h);
            }

            System.out.println("Nombre total d'hôtels récupérés: " + hotels.size());
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans getA() : " + e.getMessage());
            throw e;
        }

        return hotels;
    }

    /**
     * Méthode statique pour récupérer tous les hôtels
     * @return Liste des hôtels
     */
    public static List<hotel> getAllHotels() {
        try {
            HotelService service = new HotelService();
            return service.getA();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des hôtels : " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Récupère un hôtel spécifique par son ID
     * @param id ID de l'hôtel à récupérer
     * @return L'hôtel trouvé ou null si non trouvé
     * @throws SQLException en cas d'erreur SQL
     */
    public hotel getById(int id) throws SQLException {
        String sql = "SELECT * FROM hotel WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hotel h = new hotel();
                    h.setId(rs.getInt("id"));
                    h.setNom(rs.getString("nom"));
                    h.setPrixnuit(rs.getDouble("prixnuit"));
                    h.setNombrenuit(rs.getInt("nombrenuit"));
                    h.setStanding(rs.getString("standing"));
                    h.setAdresse(rs.getString("adresse"));

                    // Récupérer nombre_chambres_reservees s'il existe
                    try {
                        h.setNombreChambresReservees(rs.getInt("nombre_chambres_reservees"));
                    } catch (SQLException e) {
                        // Si la colonne n'existe pas, on met 0 par défaut
                        h.setNombreChambresReservees(0);
                    }

                    return h;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'hôtel par ID : " + e.getMessage());
            throw e;
        }

        return null;
    }

    /**
     * Récupère les hôtels par standing
     * @param standing Le standing recherché
     * @return Liste des hôtels correspondants
     * @throws SQLException en cas d'erreur SQL
     */
    public List<hotel> findByStanding(String standing) throws SQLException {
        String sql = "SELECT * FROM hotel WHERE standing = ?";
        List<hotel> hotels = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, standing);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hotel h = new hotel();
                    h.setId(rs.getInt("id"));
                    h.setNom(rs.getString("nom"));
                    h.setPrixnuit(rs.getDouble("prixnuit"));
                    h.setNombrenuit(rs.getInt("nombrenuit"));
                    h.setStanding(rs.getString("standing"));
                    h.setAdresse(rs.getString("adresse"));

                    // Récupérer nombre_chambres_reservees s'il existe
                    try {
                        h.setNombreChambresReservees(rs.getInt("nombre_chambres_reservees"));
                    } catch (SQLException e) {
                        h.setNombreChambresReservees(0);
                    }

                    hotels.add(h);
                }
            }

            System.out.println("Nombre d'hôtels de standing '" + standing + "' trouvés: " + hotels.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'hôtels par standing : " + e.getMessage());
            throw e;
        }

        return hotels;
    }

    /**
     * Récupère les hôtels avec un prix par nuit maximum
     * @param prixMax Le prix maximum par nuit
     * @return Liste des hôtels correspondants
     * @throws SQLException en cas d'erreur SQL
     */
    public List<hotel> findByPrixMaximum(double prixMax) throws SQLException {
        String sql = "SELECT * FROM hotel WHERE prixnuit <= ?";
        List<hotel> hotels = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, prixMax);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hotel h = new hotel();
                    h.setId(rs.getInt("id"));
                    h.setNom(rs.getString("nom"));
                    h.setPrixnuit(rs.getDouble("prixnuit"));
                    h.setNombrenuit(rs.getInt("nombrenuit"));
                    h.setStanding(rs.getString("standing"));
                    h.setAdresse(rs.getString("adresse"));

                    try {
                        h.setNombreChambresReservees(rs.getInt("nombre_chambres_reservees"));
                    } catch (SQLException e) {
                        h.setNombreChambresReservees(0);
                    }

                    hotels.add(h);
                }
            }

            System.out.println("Nombre d'hôtels avec un prix par nuit <= " + prixMax + " trouvés: " + hotels.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'hôtels par prix maximum : " + e.getMessage());
            throw e;
        }

        return hotels;
    }

    /**
     * Recherche des hôtels par nom (recherche partielle)
     * @param keyword Le mot-clé recherché dans le nom
     * @return Liste des hôtels correspondants
     * @throws SQLException en cas d'erreur SQL
     */
    public List<hotel> searchByName(String keyword) throws SQLException {
        String sql = "SELECT * FROM hotel WHERE nom LIKE ?";
        List<hotel> hotels = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hotel h = new hotel();
                    h.setId(rs.getInt("id"));
                    h.setNom(rs.getString("nom"));
                    h.setPrixnuit(rs.getDouble("prixnuit"));
                    h.setNombrenuit(rs.getInt("nombrenuit"));
                    h.setStanding(rs.getString("standing"));
                    h.setAdresse(rs.getString("adresse"));

                    try {
                        h.setNombreChambresReservees(rs.getInt("nombre_chambres_reservees"));
                    } catch (SQLException e) {
                        h.setNombreChambresReservees(0);
                    }

                    hotels.add(h);
                }
            }

            System.out.println("Nombre d'hôtels trouvés avec le mot-clé '" + keyword + "': " + hotels.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'hôtels par nom : " + e.getMessage());
            throw e;
        }

        return hotels;
    }

    /**
     * Vérifie si le nombre de chambres demandé est disponible pour un hôtel
     * @param hotelId ID de l'hôtel
     * @param nombreChambres Nombre de chambres demandé
     * @return true si le nombre de chambres est disponible, false sinon
     */
    public boolean isChambresDisponibles(int hotelId, int nombreChambres) {
        try {
            hotel h = getById(hotelId);
            if (h != null) {
                return h.isDisponible(nombreChambres);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de disponibilité : " + e.getMessage());
        }
        return false;
    }

    /**
     * Réserve un nombre de chambres pour un hôtel spécifié
     * @param hotelId ID de l'hôtel
     * @param nombreChambres Nombre de chambres à réserver
     * @return true si la réservation a réussi, false sinon
     */
    public boolean reserverChambres(int hotelId, int nombreChambres) {
        try {
            hotel h = getById(hotelId);
            if (h != null && h.isDisponible(nombreChambres)) {
                h.reserverChambres(nombreChambres);
                return modifier(h);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la réservation : " + e.getMessage());
        }
        return false;
    }
}