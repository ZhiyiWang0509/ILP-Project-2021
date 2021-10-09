package uk.ac.ed.inf;

/* an instance of the menu class will have the menu information stored on the website
 * the server name and the port number is required for the class constructor
 * getDeliveryCost method will return the total price been charged for a single time delivery service
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Menus {

    public String server;  // the server name of the website
    public String port;  // the port number of the website
    private final int DELIVERY_COST = 50; // the standard delivery cost for each delivery

    public Menus(String server, String port) {
        this.server = server;
        this.port = port;
    }

    // return the items in each restaurant in the form of HashMap
    public HashMap<String, Integer> getItemList(){
        WebAccess newAccess = new WebAccess(server, port,"menus","menus");  // create an instance of WebAccess class to get the content in menus.json

        Type listType = new TypeToken<ArrayList<Restaurant>>(){}.getType();
        ArrayList<Restaurant> resturantList = new Gson().fromJson(String.valueOf(newAccess.getResponse()), listType);  // create an arrayList of Restaurant objects

        HashMap<String, Integer> allItems = new HashMap<>();  // create a new HashMap to store all item information from every single restaurants in the array list
        for (Restaurant resturant : resturantList){
            HashMap<String, Integer> localItems = resturant.getItemsHash(); // call the method on every Restaurant object to get a HashMap for each restaurant
            allItems.putAll(localItems);  // adds the restaurant's items to the overall 'allItems'
        }
        return allItems;
    }

    // return the overall delivery cost for delivery one item
    public int getDeliveryCost(String item1) {
        HashMap<String, Integer> itemList = getItemList();
        return itemList.get(item1) + DELIVERY_COST;
    }

    // return the overall delivery cost for delivery two items
    public int getDeliveryCost(String item1, String item2) {
        HashMap<String, Integer> itemList = getItemList();
        return (itemList.get(item1) + itemList.get(item2) + DELIVERY_COST);
    }

    // return the overall delivery cost for delivery three items
    public int getDeliveryCost(String item1, String item2, String item3) {
        HashMap<String, Integer> itemList = getItemList();
        return (itemList.get(item1) + itemList.get(item2) + itemList.get(item3) + DELIVERY_COST);
    }

    // return the overall delivery cost for delivery four items
    public int getDeliveryCost(String item1, String item2, String item3, String item4) {
        HashMap<String, Integer> itemList = getItemList();
        return (itemList.get(item1) + itemList.get(item2) + itemList.get(item3)+ itemList.get(item4) + DELIVERY_COST);
    }
}
