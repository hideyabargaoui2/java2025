package org.example;

import models.Trajet;
import models.Transport;
import services.Trajetservice;
import services.Transportservice;
import utils.Maconnexion;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Transportservice transportservice = new Transportservice();
        Transport Tr = new Transport("avion","tunisaire",2568.124);
        transportservice.ajouter(Tr);

        Trajetservice trajetservice = new Trajetservice();
        Trajet trajet = new Trajet("12/5","5h","paris","avion",2);
        trajetservice.ajouter(trajet);
        System.out.println(trajetservice.getA());
        System.out.println(transportservice.getA());
    }
}