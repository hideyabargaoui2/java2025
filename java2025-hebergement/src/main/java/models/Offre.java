package models;

import javafx.beans.value.ObservableValue;

import java.time.LocalDate;

public class Offre {
    private int id;
    private String lieu;
    private LocalDate dateDepart;
    private LocalDate dateRetour;
    private int capacite;
    private double prixTotal;
    private String description;

    public Offre() {
    }

    public Offre(int id, String lieu, LocalDate dateDepart, LocalDate dateRetour,
                 int capacite, double prixTotal, String description) {
        this.id = id;
        this.lieu = lieu;
        this.dateDepart = dateDepart;
        this.dateRetour = dateRetour;
        this.capacite = capacite;
        this.prixTotal = prixTotal;
        this.description = description;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public LocalDate getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDate dateDepart) {
        this.dateDepart = dateDepart;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Offre :" +

                "  lieu = '" + lieu + '\'' +
                ", dateDepart =" + dateDepart +
                ", dateRetour =" + dateRetour +
                ", capacite =" + capacite +
                ", prixTotal =" + prixTotal +
                ", description ='" + description + '\'' +
                '}';
    }

}