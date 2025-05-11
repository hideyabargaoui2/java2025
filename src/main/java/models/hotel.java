package models;

public class hotel {
    private int id;
    private String nom;
    private double prixnuit;
    private int nombrenuit;
    private String standing;
    private String adresse;
    private int nombreChambresTotal = 25; // Nombre total de chambres disponibles
    private int nombreChambresReservees = 0; // Nombre de chambres déjà réservées

    public hotel() {
    }

    public hotel(int id, String nom, double prixnuit, int nombrenuit, String standing, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prixnuit = prixnuit;
        this.nombrenuit = nombrenuit;
        this.standing = standing;
        this.adresse = adresse;
    }

    public hotel(int id, String nom, double prixnuit, int nombrenuit, String standing, String adresse, int nombreChambresReservees) {
        this.id = id;
        this.nom = nom;
        this.prixnuit = prixnuit;
        this.nombrenuit = nombrenuit;
        this.standing = standing;
        this.adresse = adresse;
        this.nombreChambresReservees = nombreChambresReservees;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrixnuit() {
        return prixnuit;
    }

    public void setPrixnuit(double prixnuit) {
        this.prixnuit = prixnuit;
    }

    public int getNombrenuit() {
        return nombrenuit;
    }

    public void setNombrenuit(int nombrenuit) {
        this.nombrenuit = nombrenuit;
    }

    public String getStanding() {
        return standing;
    }

    public void setStanding(String standing) {
        this.standing = standing;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getNombreChambresTotal() {
        return nombreChambresTotal;
    }

    public void setNombreChambresTotal(int nombreChambresTotal) {
        this.nombreChambresTotal = nombreChambresTotal;
    }

    public int getNombreChambresReservees() {
        return nombreChambresReservees;
    }

    public void setNombreChambresReservees(int nombreChambresReservees) {
        this.nombreChambresReservees = nombreChambresReservees;
    }

    public int getNombreChambresDisponibles() {
        return nombreChambresTotal - nombreChambresReservees;
    }

    /**
     * Vérifie si le nombre de chambres demandées est disponible
     * @param nombreChambres Nombre de chambres à réserver
     * @return true si le nombre est disponible, false sinon
     */
    public boolean isDisponible(int nombreChambres) {
        return nombreChambresReservees + nombreChambres <= nombreChambresTotal;
    }

    /**
     * Réserve un certain nombre de chambres si disponible
     * @param nombreChambres Nombre de chambres à réserver
     * @return true si la réservation a réussi, false sinon
     */
    public boolean reserverChambres(int nombreChambres) {
        if (isDisponible(nombreChambres)) {
            nombreChambresReservees += nombreChambres;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "hotel{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prixnuit=" + prixnuit +
                ", nombrenuit=" + nombrenuit +
                ", standing='" + standing + '\'' +
                ", adresse='" + adresse + '\'' +
                ", chambresDisponibles=" + getNombreChambresDisponibles() +
                '}';
    }
}