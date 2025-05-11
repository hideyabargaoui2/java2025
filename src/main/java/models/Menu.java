package models;

public class Menu {
    private int id;
    private String name;
    private int prix;
    private String description;
    private Restaurant restaurant;

    public Menu() {
    }

    public Menu(Restaurant restaurant, String name, int prix, String description) {
        this.restaurant = restaurant;
        this.name = name;
        this.prix = prix;
        this.description = description;
    }

    public Menu(int id, String name, int prix, String description, Restaurant restaurant) {
        this.id = id;
        this.name = name;
        this.prix = prix;
        this.description = description;
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

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
                ", prix=" + prix +
                ", description='" + description + '\'' +
                ", restaurant=" + (restaurant != null ? restaurant.getNom() : "null") +
                '}';
    }
}
