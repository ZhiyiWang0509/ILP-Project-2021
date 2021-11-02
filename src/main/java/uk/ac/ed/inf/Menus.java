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
    private final int DELIVERY_COST = 50; // the standard delivery cost for each delivery

    public Menus(String webPort) {
        this.webPort = webPort;
    }

    // return every restaurant in the menus.json file as a list of Restaurant objects
    public ArrayList<Restaurant> getRestaurants(){
        WebAccess newAccess = new WebAccess(webPort,"menus","menus");  // create an instance of WebAccess class to get the content in menus.json

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

    // return the item's restaurant locations for each item in each restaurant in the form of hash map
    public HashMap<String, String> getItemsLocation() {
        ArrayList<Restaurant> resturantList = getRestaurants();  // get the list of Restaurant objects
        HashMap<String, String> allItems = new HashMap<>();
        for (Restaurant resturant : resturantList){
            HashMap<String, String> localItems = resturant.getShopLocation(); // get a HashMap of (item name, restaurant name) pairs
            allItems.putAll(localItems);  // combine each restaurant's information
        }
        return allItems;
    }

    // return the restaurant location of where the item belongs to
    public String getItemRestaurant(String item){
        HashMap<String, String> itemRestaurants = getItemsLocation();
        return itemRestaurants.get(item);
    }

    // return the overall delivery cost for delivery one item
    public int getDeliveryCost(String item1) {
        HashMap<String, Integer> itemsPrice = getItemsPrice();
        try{
            return itemsPrice.get(item1) + DELIVERY_COST;
        } catch (NullPointerException e){  // to catch the case when the input item name doesn't exist
            System.out.println("The item isn't found in the menu.");
            return 9999;  // return a dummy value
        }
    }

    // return the overall delivery cost for delivery two items
    public int getDeliveryCost(String item1, String item2) {
        HashMap<String, Integer> itemsPrice = getItemsPrice();
        try{
            return (itemsPrice.get(item1) + itemsPrice.get(item2) + DELIVERY_COST);
        } catch (NullPointerException e){  // to catch the case when the input item name doesn't exist
            System.out.println("The item isn't found in the menu.");
            return 9999;  // return a dummy value
        }
    }

    // return the overall delivery cost for delivery three items
    public int getDeliveryCost(String item1, String item2, String item3) {
        HashMap<String, Integer> itemsPrice = getItemsPrice();
        try{
            return (itemsPrice.get(item1) + itemsPrice.get(item2) + itemsPrice.get(item3) + DELIVERY_COST);
        } catch (NullPointerException e){  // to catch the case when the input item name doesn't exist
            System.out.println("The item isn't found in the menu.");
            return 9999;  // return a dummy value
        }
    }

    // return the overall delivery cost for delivery four items
    public int getDeliveryCost(String item1, String item2, String item3, String item4) {
        HashMap<String, Integer> itemList = getItemsPrice();
        try{
            return (itemList.get(item1) + itemList.get(item2) + itemList.get(item3)+ itemList.get(item4) + DELIVERY_COST);
        } catch (NullPointerException e){  // to catch the case when the input item name doesn't exist
            System.out.println("The item isn't found in the menu.");
            return 9999;  // return a dummy value
        }

    }

}
