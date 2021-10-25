package uk.ac.ed.inf;


import java.util.List;
import org.javatuples.Pair;

public class App
{
    public static void main( String[] args ){
        LongLat appletonTower = new LongLat(-3.191594,55.943658);
        String w3Test = "army.monks.grapes";
        W3words newWord = new W3words(w3Test);
       // System.out.println(newWord.toLongLat().isConfined());
       // WebAccess newWord = new WebAccess("localhost", "80", "words", "army.monks.grapes");
        WebAccess newMenu = new WebAccess("localhost", "80", "menus", "menus");
        WebAccess newBuilding = new WebAccess("localhost", "80", "buildings", "no-fly-zones");
        //System.out.println(newBuilding.getResponse());


        Buildings building = new Buildings("localhost", "80","landmarks");
        System.out.println(building.getNoFlyCoordinates());









    }
}
