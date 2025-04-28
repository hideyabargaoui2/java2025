package models;

import java.util.Date;

public class reshotel {
    private int idReservation;
    private String dateReservation;
    private String hotel;
    private String statutReservation;

    public reshotel() {
    }

    public reshotel(int idReservation, String dateReservation, String hotel, String statutReservation) {
        this.idReservation = idReservation;
        this.dateReservation = dateReservation;
        this.hotel = hotel;
        this.statutReservation = statutReservation;
    }

    public reshotel(String dateReservation, String hotel, String statutReservation) {
        this.dateReservation = dateReservation;
        this.hotel = hotel;
        this.statutReservation = statutReservation;
    }

    public String getStatutReservation() {
        return statutReservation;
    }

    public void setStatutReservation(String statutReservation) {
        this.statutReservation = statutReservation;
    }

    public String gethotel() {
        return hotel;
    }

    public void sethotel(String hotel) {
        this.hotel = hotel;
    }

    public String getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(String dateReservation) {
        this.dateReservation = dateReservation;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    @Override
    public String toString() {
        return "reshotel{" +
                "idReservation=" + idReservation +
                ", dateReservation='" + dateReservation + '\'' +
                ", hotel=" + hotel +
                ", statutReservation='" + statutReservation + '\'' +
                '}';
    }
}


