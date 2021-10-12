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

    public String server;  // the server name of the website
    public String port;  // the port number of the website
    private final int DELIVERY_COST = 50; // the standard delivery cost for each delivery

    public Menus(String server, String port) {
        this.server = server;
        this.port = port;
    }

    // return every restaurant in the menus.json file as a list of Restaurant objects
    public ArrayList<Restaurant> getRestaurants(){
        WebAccess newAccess = new WebAccess(server, port,"menus","menus");  // create an instance of WebAccess class to get the content in menus.json

        Type listType = new TypeToken<ArrayList<Restaurant>>(){}.getType();
        ArrayList<Restaurant> resturantList = new Gson().fromJson(String.valueOf(newAccess.getResponse()), listType);  // create an arrayList of Restaurant objects
        return resturantList;
    }

    // return the items price for every item in each restaurant in the form of HashMap
    public HashMap<String, Integer> getItemsPrice(){
        ArrayList<Restaurant> resturantList = getRestaurants();  // get the list of Restaurant objects

        HashMap<String, Integer> allItems = new HashMap<>();
        for (Restaurant resturant : resturantList){
            HashMap<String, Integer> localItems = resturant.getItemsPrice(); // get a HashMap of (item name, item price) pairs
            allItems.putAll(localItems);  // combine each restaurant's information
        }
        return allItems;
    }

    // return the item's restaurant name for each item in each restaurant in the form of hash map
    public HashMap<String, String> getItemsRestaurants() {
        ArrayList<Restaurant> resturantList = getRestaurants();  // get the list of Restaurant objects

        HashMap<String, String> allItems = new HashMap<>();
        for (Restaurant resturant : resturantList){
            HashMap<String, String> localItems = resturant.getItemsRestaurant(); // get a HashMap of (item name, restaurant name) pairs
            allItems.putAll(localItems);  // combine each restaurant's information
        }
        return allItems;
    }

    // return the overall delivery cost for delivery one item
    public int getDeliveryCost(String item1) {
        HashMap<String, Integer> itemList = getItemsPrice();
        return itemList.get(item1) + DELIVERY_COST;
    }

    // return the overall delivery cost for delivery two items
    public int getDeliveryCost(String item1, String item2) {
        HashMap<String, Integer> itemList = getItemsPrice();
        return (itemList.get(item1) + itemList.get(item2) + DELIVERY_COST);
    }

    // return the overall delivery cost for delivery three items
    // need to check the restaurants of the items in this method
    // since each order can't take items from more than 2 places
    public int getDeliveryCost(String item1, String item2, String item3) {
        HashMap<String, Integer> itemsPrice = getItemsPrice();
        HashMap<String, String> itemsRestaurants = getItemsRestaurants();
        String[] restaurants = {itemsRestaurants.get(item1), itemsRestaurants.get(item2), itemsRestaurants.get(item3)};
        Set<String> restaurantsUnique = new HashSet<>(List.of(restaurants));
        if(restaurantsUnique.size() > 2){   // check the restaurants the items belongs to
            System.out.println("Can't take items from more than 2 restaurants for a single order.");
            return 999;  // return a dummy value if the condition is met
        }else{
            return (itemsPrice.get(item1) + itemsPrice.get(item2) + itemsPrice.get(item3) + DELIVERY_COST);
        }
    }

    // return the overall delivery cost for delivery four items
    // need to check the restaurants of the items in this method
    // since each order can't take items from more than 2 places
    public int getDeliveryCost(String item1, String item2, String item3, String item4) {
        HashMap<String, Integer> itemList = getItemsPrice();
        HashMap<String, String> itemsRestaurants = getItemsRestaurants();
        String[] restaurants = {itemsRestaurants.get(item1), itemsRestaurants.get(item2), itemsRestaurants.get(item3), itemsRestaurants.get(item4)};
        Set<String> restaurantsUnique = new HashSet<>(List.of(restaurants));
        if(restaurantsUnique.size() > 2){   // check the restaurants the items belongs to
            System.out.println("Can't take items from more than 2 restaurants for a single order.");
            return 999;  // return a dummy value if the condition is met
        }else {
            return (itemList.get(item1) + itemList.get(item2) + itemList.get(item3)+ itemList.get(item4) + DELIVERY_COST);
        }
    }


}
