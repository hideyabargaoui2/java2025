package models;

public class Train extends Transport{
    private String numerotrain ;
    private String garedepart ;
    private String garearrivee ;

    public Train() {
    }

    public Train(int id, String type, String compagnie, double prix, String numerotrain, String garedepart, String garearrivee) {
        super(id, type, compagnie, prix);
        this.numerotrain = numerotrain;
        this.garedepart = garedepart;
        this.garearrivee = garearrivee;
    }

    public Train(String type, String compagnie, double prix, String numerotrain, String garedepart, String garearrivee) {
        super(type, compagnie, prix);
        this.numerotrain = numerotrain;
        this.garedepart = garedepart;
        this.garearrivee = garearrivee;
    }

    public String getNumerotrain() {
        return numerotrain;
    }

    public void setNumerotrain(String numerotrain) {
        this.numerotrain = numerotrain;
    }

    public String getGaredepart() {
        return garedepart;
    }

    public void setGaredepart(String garedepart) {
        this.garedepart = garedepart;
    }

    public String getGarearrivee() {
        return garearrivee;
    }

    public void setGarearrivee(String garearrivee) {
        this.garearrivee = garearrivee;
    }

    @Override
    public String toString() {
        return "Train{" +
                "numerotrain='" + numerotrain + '\'' +
                ", garedepart='" + garedepart + '\'' +
                ", garearrivee='" + garearrivee + '\'' +
                '}';
    }
}
