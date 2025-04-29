package models;

import java.sql.Time;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
public class Restaurant {
    private int id,classement;
    private String nom;
    private String adresse,type;
    private Time heure_ouv,heure_ferm;

    public Restaurant() {}

    public Restaurant(int id, String nom, String adresse, String type, int classement, Time heure_ouv, Time heure_ferm) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.type = type;
        this.classement = classement;
        this.heure_ouv = heure_ouv;
        this.heure_ferm = heure_ferm;
    }


    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", type='" + type + '\'' +
                ", classement=" + classement +
                ", heure_ouv=" + heure_ouv +
                ", heure_ferm=" + heure_ferm +
                '}';
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getClassement() {
        return classement;
    }

    public void setClassement(int classement) {
        this.classement = classement;
    }

    public Time getHeure_ouv() {
        return heure_ouv;
    }

    public void setHeure_ouv(Time heure_ouv) {
        this.heure_ouv = heure_ouv;
    }

    public Time getHeure_ferm() {
        return heure_ferm;
    }

    public void setHeure_ferm(Time heure_ferm) {
        this.heure_ferm = heure_ferm;
    }



}


