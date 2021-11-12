package uk.ac.ed.inf;

/* an instance of the menu class will have the menu information stored on the website
 * the server name and the port number is required for the class constructor
 * getDeliveryCost method will return the total price been charged for a single time delivery service
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

public class Menus {

    public String webPort;  // the port number of the website

    public Menus(String webPort) {
        this.webPort = webPort;
    }

    // return every restaurant in the menus.json file as a list of Restaurant objects
    public ArrayList<Shop> getRestaurants(){
        WebAccess newAccess = new WebAccess(webPort,"menus","menus");  // create an instance of WebAccess class to get the content in menus.json

        Type listType = new TypeToken<ArrayList<Shop>>(){}.getType();
        ArrayList<Shop> resturantList = new Gson().fromJson(String.valueOf(newAccess.getResponse()), listType);  // create an arrayList of Restaurant objects
        return resturantList;
    }

    // return the items price for every item in each restaurant in the form of HashMap
    public HashMap<String, Integer> getItemsPrice(){
        ArrayList<Shop> shopList = getRestaurants();  // get the list of Restaurant objects
        HashMap<String, Integer> allItems = new HashMap<>();
        for (Shop shop : shopList){
            HashMap<String, Integer> localItems = shop.getItemsPrice(); // get a HashMap of (item name, item price) pairs
            allItems.putAll(localItems);  // combine each restaurant's information
        }
        return allItems;
    }

    // return the item's restaurant locations for each item in each restaurant in the form of hash map
    public HashMap<String, String> getItemsLocation() {
        ArrayList<Shop> shopList = getRestaurants();  // get the list of Restaurant objects
        HashMap<String, String> allItems = new HashMap<>();
        for (Shop shop : shopList){
            HashMap<String, String> localItems = shop.getShopLocation(); // get a HashMap of (item name, restaurant name) pairs
            allItems.putAll(localItems);  // combine each restaurant's information
        }
        return allItems;
    }

    // return the restaurant location of where the item belongs to
    public String getItemRestaurant(String item){
        HashMap<String, String> itemRestaurants = getItemsLocation();
        return itemRestaurants.get(item);
    }



}
