package org.example;

import Modules.Depensse;
import Service.Depensseservice;

import java.sql.SQLException;

public class Textjdbc {

    public static void main(String[] args) {
        Depensseservice d = new Depensseservice();
        Depensse d1 = new Depensse("paypal", "22/02/2024", "full", 500, "vip");

        try {



            System.out.println(d.getAll());
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
