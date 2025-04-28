package models;

public class Voiturelocation extends Transport{
    private String modele ;
    private int nbrjours;
    private boolean Avec_chauffeur ;



    public Voiturelocation() {
    }

    public Voiturelocation(int id, String type, String compagnie, double prix, String modele, int nbrjours, boolean avec_chauffeur) {
        super(id, type, compagnie, prix);
        this.modele = modele;
        this.nbrjours = nbrjours;
        Avec_chauffeur = avec_chauffeur;
    }

    public Voiturelocation(String type, String compagnie, double prix, String modele, int nbrjours, boolean avec_chauffeur) {
        super(type, compagnie, prix);
        this.modele = modele;
        this.nbrjours = nbrjours;
        Avec_chauffeur = avec_chauffeur;
    }


    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public int getNbrjours() {
        return nbrjours;
    }

    public void setNbrjours(int nbrjours) {
        this.nbrjours = nbrjours;
    }

    public boolean isAvec_chauffeur() {
        return Avec_chauffeur;
    }

    public void setAvec_chauffeur(boolean avec_chauffeur) {
        Avec_chauffeur = avec_chauffeur;
    }

    @Override
    public String toString() {
        return "Voiturelocation{" +
                "modele='" + modele + '\'' +
                ", nbrjours=" + nbrjours +
                ", Avec_chauffeur=" + Avec_chauffeur +
                '}';
    }
}
