package models;

public class hotel {
    private int id;
    private String nom;
    private double prixnuit;
    private int nombrenuit;
    private String standing;
    private String adresse;

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

    @Override
    public String toString() {
        return "hotel{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prixnuit=" + prixnuit +
                ", nombrenuit=" + nombrenuit +
                ", standing='" + standing + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
