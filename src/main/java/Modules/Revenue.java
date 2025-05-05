package Modules;

public class Revenue {
    private int idvoy;
    private String daterevenue;
    private String modereception;
    private int Rmontant;
    private String devise;

    public Revenue() {
    }

    public Revenue(int idvoy, String daterevenue, String modereception, int rmontant, String devise) {
        this.idvoy = idvoy;
        this.daterevenue = daterevenue;
        this.modereception = modereception;
        this.Rmontant = rmontant;
        this.devise = devise;
    }

    public Revenue(String daterevenue, String modereception, int rmontant, String devise) {
        this.daterevenue = daterevenue;
        this.modereception = modereception;
        this.Rmontant = rmontant;
        this.devise = devise;
    }

    // Getters et setters
    public int getIdvoy() {
        return idvoy;
    }

    public void setIdvoy(int idvoy) {
        this.idvoy = idvoy;
    }

    public String getDaterevenue() {
        return daterevenue;
    }

    public void setDaterevenue(String daterevenue) {
        this.daterevenue = daterevenue;
    }

    public String getModereception() {
        return modereception;
    }

    public void setModereception(String modereception) {
        this.modereception = modereception;
    }

    public int getRmontant() {
        return Rmontant;
    }

    public void setRmontant(int rmontant) {
        Rmontant = rmontant;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    @Override
    public String toString() {
        return "Revenue{" +
                "idvoy=" + idvoy +
                ", daterevenue='" + daterevenue + '\'' +
                ", modereception='" + modereception + '\'' +
                ", Rmontant=" + Rmontant +
                ", devise='" + devise + '\'' +
                '}';
    }
}
