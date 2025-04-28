package models;

public class Avion extends Transport{
    private String classecabine;
    private String numerovol;

    public Avion() {

    }

    public Avion(int id, String type, String compagnie, double prix, String classecabine, String numerovol) {
        super(id, type, compagnie, prix);
        this.classecabine = classecabine;
        this.numerovol = numerovol;
    }

    public Avion(String type, String compagnie, double prix, String classecabine, String numerovol) {
        super(type, compagnie, prix);
        this.classecabine = classecabine;
        this.numerovol = numerovol;
    }

    public String getClassecabine() {
        return classecabine;
    }

    public void setClassecabine(String classecabine) {
        this.classecabine = classecabine;
    }

    public String getNumerovol() {
        return numerovol;
    }

    public void setNumerovol(String numerovol) {
        this.numerovol = numerovol;
    }

    @Override
    public String toString() {
        return "Avion{" +
                "classecabine='" + classecabine + '\'' +
                ", numerovol='" + numerovol + '\'' +
                '}';
    }
}
