package models;

import java.time.LocalDateTime;
import java.util.Date;

public class Trajet {
    private int id;
    private LocalDateTime date;
    private int heure;
    private String destination;
    private  String transport;
    private int duree ;

    public Trajet() {
    }

    public Trajet(int id, LocalDateTime date, int heure, String destination, String transport, int duree) {
        this.id = id;
        this.date = date;
        this.heure = heure;
        this.destination = destination;
        this.transport = transport;
        this.duree = duree;
    }

    public Trajet(LocalDateTime date, int heure, String destination, String transport, int duree) {
        this.date = date;
        this.heure = heure;
        this.destination = destination;
        this.transport = transport;
        this.duree = duree;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getHeure() {
        return heure;
    }

    public void setHeure(int heure) {
        this.heure = heure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    @Override
    public String toString() {
        return "Trajet{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", heure='" + heure + '\'' +
                ", destination='" + destination + '\'' +
                ", transport='" + transport + '\'' +
                ", duree=" + duree +
                '}';
    }
}
