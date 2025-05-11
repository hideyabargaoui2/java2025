package models;

import java.time.LocalDateTime;

public class ResHotel {
    private int id;
    private String hotel;
    private String startres;
    private LocalDateTime dateres;
    private int nombreChambres; // Ajout de cet attribut pour stocker le nombre de chambres réservées

    public ResHotel() {
    }

    public ResHotel(String hotel, String startres, LocalDateTime dateres) {
        this.hotel = hotel;
        this.startres = startres;
        this.dateres = dateres;
        this.nombreChambres = 1; // Par défaut, une chambre est réservée
    }

    public ResHotel(String hotel, String startres, LocalDateTime dateres, int nombreChambres) {
        this.hotel = hotel;
        this.startres = startres;
        this.dateres = dateres;
        this.nombreChambres = nombreChambres;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getStartres() {
        return startres;
    }

    public void setStartres(String startres) {
        this.startres = startres;
    }

    public LocalDateTime getDateres() {
        return dateres;
    }

    public void setDateres(LocalDateTime dateres) {
        this.dateres = dateres;
    }

    public int getNombreChambres() {
        return nombreChambres;
    }

    public void setNombreChambres(int nombreChambres) {
        this.nombreChambres = nombreChambres;
    }

    @Override
    public String toString() {
        return "ResHotel{" +
                "id=" + id +
                ", hotel='" + hotel + '\'' +
                ", startres='" + startres + '\'' +
                ", dateres=" + dateres +
                ", nombreChambres=" + nombreChambres +
                '}';
    }
}