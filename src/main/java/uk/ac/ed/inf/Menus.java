package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

/**
 * This class would access the menu.json file on the webserver
 * This class contain methods to return necessary information related to the items available
 * to order from the application.
 *
 */

public class Menus {

    /**
     * the portal of the webserver to access the menu.json file
     */
    public String webPort;

    /**
     * this is the constructor of the Menu class
     * @param webPort this is the portal of the webserver to access
     */
    public Menus(String webPort) {
        this.webPort = webPort;
    }

    /**
     * this method would access the menu.json file on the webserver and store all the shops
     * as Shop objects in a list and return it.
     * @return an Array list of all the shops available for the delivery service
     */
    public ArrayList<Shop> getShops(){
        WebAccess newAccess = new WebAccess(webPort,"menus","menus");  // create an instance of WebAccess class to get the content in menus.json
        Type listType = new TypeToken<ArrayList<Shop>>(){}.getType();
        ArrayList<Shop> shopList = new Gson().fromJson(String.valueOf(newAccess.getResponse()), listType);
        return shopList;
    }

    /**
     * this method would gather the price information for items sell in every single shops
     * as a HashMap, with the item name as the key and item price as the value for each
     * pair in the HashMap.
     * @return a HashMap of (item name, item price) pair for every item available to order
     * in this delivery service
     */
    public HashMap<String, Integer> getAllItemsPrice(){
        ArrayList<Shop> shopList = getShops();
        HashMap<String, Integer> allItems = new HashMap<>();
        for (Shop shop : shopList){
            HashMap<String, Integer> localItems = shop.getItemsPrice(); // get a HashMap of (item name, item price) pairs for this shop
            allItems.putAll(localItems);  // combine information for each shop
        }
        return allItems;
    }

    /**
     * this method would store the shop location for each item available to order from the service
     * as a HashMap with each pair made up as item name as key and shop location the item belongs
     * to as value.
     * the shop location returned by this method is in W3word format, the location needs to be
     * transformed in to LongLat object if necessary.
     * @return a HashMap of (item name, shop location) pair for each item available to order.
     */
    public HashMap<String, String> getAllItemsLocation() {
        ArrayList<Shop> shopList = getShops();
        HashMap<String, String> allItems = new HashMap<>();
        for (Shop shop : shopList){
            HashMap<String, String> localItems = shop.getShopLocation(); // get a HashMap of (item name, restaurant name) pairs for this shop
            allItems.putAll(localItems);  // combine information for each shop
        }
        return allItems;
    }

    /**
     * this method return the shop location of the item belongs to
     * the location returned is in W3words format, hence needs to
     * be transformed into LongLat object if necessary
     * @param item this parameter is the name of the item
     * @return the shop location of which the item belongs to in w3word format.
     */
    public String getItemShop(String item){
        HashMap<String, String> itemRestaurants = getAllItemsLocation();
        return itemRestaurants.get(item);
    }



}
