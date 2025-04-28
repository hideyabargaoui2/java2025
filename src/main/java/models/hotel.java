package models;

public class hotel {
    private int id;
    private String nom;
    private double prixParNuit;
    private int nombreNuits;
    private String standing;
    private String adresse;



    public hotel() {
    }


    public hotel(int id, String nom, double prixParNuit, int nombreNuits, String standing, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prixParNuit = prixParNuit;
        this.nombreNuits = nombreNuits;
        this.standing = standing;
        this.adresse = adresse;
    }


    public hotel(String nom, double prixParNuit, int nombreNuits, String standing, String adresse) {
        this.nom = nom;
        this.prixParNuit = prixParNuit;
        this.nombreNuits = nombreNuits;
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

    public double getPrixParNuit() {
        return prixParNuit;
    }

    public void setPrixParNuit(double prixParNuit) {
        this.prixParNuit = prixParNuit;
    }

    public int getNombreNuits() {
        return nombreNuits;
    }

    public void setNombreNuits(int nombreNuits) {
        this.nombreNuits = nombreNuits;
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
                ", prixParNuit=" + prixParNuit +
                ", nombreNuits=" + nombreNuits +
                ", standing='" + standing + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}

