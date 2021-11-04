package uk.ac.ed.inf;


import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import javax.xml.crypto.Data;

public class App
{
    public static void main( String[] args ){
        LongLat appletonTower = new LongLat(-3.186874, 55.944494);
        LongLat businessSchool = new LongLat(-3.1873,55.9430);
        LongLat greyfriarsKirkyard = new LongLat(-3.1928,55.9469);
        String w3Test = "army.monks.grapes";
        W3words newWord = new W3words("9898");
       // System.out.println(newWord.toLongLat().isConfined());
       // WebAccess newWord = new WebAccess("localhost", "80", "words", "army.monks.grapes");

        WebAccess newBuilding = new WebAccess( "80", "buildings", "no-fly-zones");
        //System.out.println(newBuilding.getResponse());
        Buildings building = new Buildings( "9898","no-fly-zones");
        Menus menu = new Menus("9898");
        //System.out.println(building.getNoFlyCoordinates());


        Drone newDrone = new Drone("2022-04-11", "9898","1527");
        System.out.println(newDrone.checkNoFlyZones(businessSchool));
        double distanceTo = newDrone.currentLocation.distanceTo(businessSchool);
        System.out.println(distanceTo);
        System.out.println(Math.round(distanceTo/0.00015));
        System.out.println("the drone need 10 steps to fly from AT to business school.");

        int angle = newDrone.currentLocation.getAngle(businessSchool);
        if(angle%10 !=0){
            angle = newDrone.roundAngle(angle);
        }
        angle += 180;  // need to modify the angle with the direction of travelling
        System.out.println(newDrone.currentLocation.nextPosition(angle).formatLongLat());
        DecimalFormat f = new DecimalFormat("##.000000");
        LongLat noflypoint1 = new LongLat(-3.1892189, 55.9454105);
        int i = 0;
        while(!newDrone.currentLocation.closeTo(businessSchool)){
            newDrone.updateLocation(newDrone.currentLocation.nextPosition(angle));
            System.out.println(newDrone.currentLocation.formatLongLat());
            i++;
        }
        System.out.println(i);
        //System.out.println(newDrone.getLocations());
        //System.out.println(newDrone.getEntirePath());








    }
}
