package Modules;

public class Depensse {
    private int idvoy;
    private String moddepay;
    private String datepay;
    private String categories;
    private int montant;
    private String descripiton ;


    public Depensse() {
    }


    public Depensse(int idvoy, String moddepay, String datepay, String categories, int montant, String descripiton) {
        this.idvoy = idvoy;
        this.moddepay = moddepay;
        this.datepay = datepay;
        this.categories = categories;
        this.montant = montant;
        this.descripiton = descripiton;
    }

    public Depensse(String moddepay, String datepay, String categories, int montant, String descripiton) {
        this.moddepay = moddepay;
        this.datepay = datepay;
        this.categories = categories;
        this.montant = montant;
        this.descripiton = descripiton;
    }

    public int getIdvoy() {
        return idvoy;
    }

    public void setIdvoy(int idvoy) {
        this.idvoy = idvoy;
    }

    public String getModdepay() {
        return moddepay;
    }

    public void setModdepay(String moddepay) {
        this.moddepay = moddepay;
    }

    public String getDatepay() {
        return datepay;
    }

    public void setDatepay(String datepay) {
        this.datepay = datepay;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public String getDescripiton() {
        return descripiton;
    }

    public void setDescripiton(String descripiton) {
        this.descripiton = descripiton;
    }

    @Override
    public String toString() {
        return "Depensse{" +
                "idvoy=" + idvoy +
                ", moddepay='" + moddepay + '\'' +
                ", datepay='" + datepay + '\'' +
                ", categories='" + categories + '\'' +
                ", montant=" + montant +
                ", descripiton='" + descripiton + '\'' +
                '}';
    }



}
