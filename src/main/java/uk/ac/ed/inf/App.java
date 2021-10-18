package uk.ac.ed.inf;


import com.google.gson.Gson;

import java.util.Arrays;

public class App
{
    public static void main( String[] args ){
        LongLat appletonTower = new LongLat(-3.191594,55.943658);
        WebAccess newWord = new WebAccess("localhost", "80", "words", "army.monks.grapes");
        WebAccess newMenu = new WebAccess("localhost", "80", "menus", "menus");

        // the algorithm pass the json string to the parser for Location, by calling the method in the class return the
        // location of the point in LongLat object.
        Location newLocation = new Gson().fromJson(String.valueOf(newWord.getResponse()),Location.class);
        System.out.println(newLocation.getLocation());


    }
}
