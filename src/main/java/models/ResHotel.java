package models;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;




public class ResHotel {
    private int idres;
    private String hotel;
    private String startres;
    private LocalDateTime dateres;


    public ResHotel() {
    }

    public ResHotel(int idres, String hotel, String startres, LocalDateTime dateres) {
        this.idres = idres;
        this.hotel = hotel;
        this.startres = startres;
        this.dateres = dateres;
    }

    public ResHotel(String hotel, String startres, LocalDateTime dateres) {
        this.hotel = hotel;
        this.startres = startres;
        this.dateres = dateres;
    }


    public int getIdres() {
        return idres;
    }

    public void setIdres(int idres) {
        this.idres = idres;
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

    @Override
    public String toString() {
        return "ResHotel{" +
                "idres=" + idres +
                ", hotel='" + hotel + '\'' +
                ", startres='" + startres + '\'' +
                ", dateres=" + dateres +
                '}';
    }
}
