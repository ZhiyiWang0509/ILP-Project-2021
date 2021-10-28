package uk.ac.ed.inf;


import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import javax.xml.crypto.Data;

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
        //System.out.println(building.getNoFlyCoordinates());


        // get access to the two databases
        Database db = new Database("localhost", "1527","orders");
        Database detailsDb = new Database("localhost", "1527", "orderDetails");
        //System.out.println(db.getOrders("2022-04-11"));
        ArrayList<Database.Order> orders = db.getOrders("2022-04-11");
        for(Database.Order order : orders){
            System.out.println(order.getDeliverTo());
            System.out.println(detailsDb.getOrderDetails(order.getOrderNO()));
        }









    }
}
