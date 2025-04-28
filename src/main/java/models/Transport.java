package models;

public class Transport {
    private int id;
    private String type;
    private String compagnie;
    private double prix ;

    public Transport() {
    }

    public Transport(int id, String type, String compagnie, double prix) {
        this.id = id;
        this.type = type;
        this.compagnie = compagnie;
        this.prix = prix;

    }

    public Transport(String type, String compagnie, double prix) {
        this.type = type;
        this.compagnie = compagnie;
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompagnie() {
        return compagnie;
    }

    public void setCompagnie(String compagnie) {
        this.compagnie = compagnie;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }


    @Override
    public String toString() {
        return "Transport{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", compagnie='" + compagnie + '\'' +
                ", prix=" + prix +
                '}';
    }
}

