package org.example;

import javafx.scene.control.Menu;
import models.Restaurant;
import services.MenuService;
import services.RestaurantServices;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Création des objets nécessaires
        RestaurantServices restaurantServices = new RestaurantServices();
        Restaurant restaurant = new Restaurant();
        MenuService menuService = new MenuService();
        Menu menu = new Menu();

        // Bloc try-catch pour récupérer et afficher les restaurants
        try {
            System.out.println(restaurantServices.getAll());
            System.out.println(menuService.getAll());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des restaurants : " + e.getMessage());
            System.err.println("Erreur lors de la récupération du menu : " + e.getMessage());
        }
        /*try {
            System.out.println(menuService.getAll());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des menus : " + e.getMessage());
        }*/
    }
}
