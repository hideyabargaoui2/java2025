package org.example;

import Modules.Depensse;
import Service.Depensseservice;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Création du service et d’un exemple de dépense
        Depensseservice depensseService = new Depensseservice();
        Depensse depensse = new Depensse("paypal", "22/02/2024", "full", 500, "vip");

        // Ajout de la dépense
        try {
            depensseService.add(depensse);
            System.out.println("Dépense ajoutée avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de la dépense : " + e.getMessage());
        }

        // Affichage de toutes les dépenses
        try {
            System.out.println(depensseService.getAll());
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des dépenses : " + e.getMessage());
        }
    }
}
