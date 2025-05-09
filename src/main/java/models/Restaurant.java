package models;
import java.sql.Time;

public class Restaurant {
    private int id;
    private String nom;
    private String adresse;
    private String type;
    private Time heure_ouv;
    private Time heure_ferm;
    private int classement;
    private double latitude;  // Nouvelle propriété pour la latitude
    private double longitude;

    public Restaurant() {
        // Constructeur vide nécessaire pour certains cas
    }

    public Restaurant(int id, String nom, String adresse, String type, Time heure_ouv, Time heure_ferm, int classement) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.type = type;
        this.heure_ouv = heure_ouv;
        this.heure_ferm = heure_ferm;
        this.classement = classement;

    }

    // Getters et setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getAdresse() { return adresse; }
    public String getType() { return type; }
    public Time getHeureOuv() { return heure_ouv; }
    public Time getHeureFerm() { return heure_ferm; }
    public int getClassement() { return classement; }

    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setType(String type) { this.type = type; }
    public void setHeure_ouv(Time heure_ouv) { this.heure_ouv = heure_ouv; }
    public void setHeure_ferm(Time heure_ferm) { this.heure_ferm = heure_ferm; }
    public void setClassement(int classement) { this.classement = classement; }

    // Méthodes pour obtenir l'heure au format String
    public String getHoraireOuvert() {
        return heure_ouv != null ? heure_ouv.toString() : null;
    }

    public String getHoraireFerme() {
        return heure_ferm != null ? heure_ferm.toString() : null;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
