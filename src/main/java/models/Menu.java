package models;

public class Menu {
    private int id;
    private String name;
    private int prix;  // Prix modifié pour être de type int
    private String description;
    private Restaurant restaurant; // Association avec Restaurant

    public Menu() {
    }

    public Menu(int id, String name, int prix, String description, Restaurant restaurant) {
        this.id = id;
        this.name = name;
        this.prix = prix;
        this.description = description;
        this.restaurant = restaurant;
    }

    // + Getter & Setter pour restaurant
    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    // Getters et Setters existants
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", prix=" + prix +  // Affichage du prix en tant qu'int
                ", description='" + description + '\'' +
                ", restaurant=" + (restaurant != null ? restaurant.getNom() : "null") +
                '}';
    }
}
