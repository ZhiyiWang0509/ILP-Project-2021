package uk.ac.ed.inf;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import javax.xml.crypto.Data;

public class App
{
    public static void main( String[] args ){
        LongLat appletonTower = new LongLat(-3.186874, 55.944494);
        String w3Test = "army.monks.grapes";
        W3words newWord = new W3words("9898");
       // System.out.println(newWord.toLongLat().isConfined());
       // WebAccess newWord = new WebAccess("localhost", "80", "words", "army.monks.grapes");

        WebAccess newBuilding = new WebAccess( "80", "buildings", "no-fly-zones");
        //System.out.println(newBuilding.getResponse());
        Buildings building = new Buildings( "80","landmarks");
        Menus menu = new Menus("9898");
        //System.out.println(building.getNoFlyCoordinates());


        Drone newDrone = new Drone("2022-04-11", "9898","1527");
        System.out.println(newDrone.getLocations());








    }
}
