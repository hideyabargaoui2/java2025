package models;

import javafx.scene.image.Image;
import utils.QRCodeGenerator;
import utils.QRCodeGenerator ;
import java.time.LocalDateTime;
import java.util.Date;

public class Trajet {
    private int id;
    private LocalDateTime date;
    private int heure;
    private String destination;
    private  String transport;
    private int duree ;
    private Image qrCodeImage;
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


    // Nouvelle méthode pour générer le QR code à la demande
    public Image getQRCodeImage() {
        if (qrCodeImage == null) {
            try {
                qrCodeImage = QRCodeGenerator.generateQRCodeImage(this);
            } catch (Exception e) {
                System.err.println("Erreur lors de la génération du QR code: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return qrCodeImage;
    }

    // Méthode pour forcer la régénération du QR code
    public void regenerateQRCode() {
        qrCodeImage = null;
        getQRCodeImage();
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
