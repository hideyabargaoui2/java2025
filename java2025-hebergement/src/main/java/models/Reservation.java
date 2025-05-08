package models;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private Offre offre;
    private String clientNom;
    private String clientEmail;
    private LocalDate dateReservation;
    private int nombrePersonnes;
    private String statut;

    public Reservation() {
    }

    public Reservation(int id, Offre offre, String clientNom, String clientEmail,
                       LocalDate dateReservation, int nombrePersonnes, String statut) {
        this.id = id;
        this.offre = offre;
        this.clientNom = clientNom;
        this.clientEmail = clientEmail;
        this.dateReservation = dateReservation;
        this.nombrePersonnes = nombrePersonnes;
        this.statut = statut;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Offre getOffre() {
        return offre;
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(int nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", offre=" + offre +
                ", clientNom='" + clientNom + '\'' +
                ", clientEmail='" + clientEmail + '\'' +
                ", dateReservation=" + dateReservation +
                ", nombrePersonnes=" + nombrePersonnes +
                ", statut='" + statut + '\'' +
                '}';
    }
}