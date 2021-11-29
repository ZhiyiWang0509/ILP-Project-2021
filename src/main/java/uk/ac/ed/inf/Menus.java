package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

/**
 * This class would access the menu.json file on the webserver
 * This class contain methods to process and return necessary information related to items sold in each shop,
 * such as the items' price and shop location.
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
     * the access to the webserver is achieved by creating an instance of WebAccess class and the
     * content in the file is obtained by calling the getResponse method on that instance.
     *
     * @return an Array list of all the shops available for the delivery service
     */
    private ArrayList<Shop> getShops(){
        // create an instance of WebAccess class to get the content in menus.json
        WebAccess newAccess = new WebAccess(webPort,"menus","menus");
        Type listType = new TypeToken<ArrayList<Shop>>(){}.getType();
        return new Gson().fromJson(String.valueOf(newAccess.getResponse()), listType);
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
            HashMap<String, Integer> localItems = shop.getItemsPrice();
            // combine information for each shop
            allItems.putAll(localItems);
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
            HashMap<String, String> localItems = shop.getShopLocation();
            // combine information for each shop
            allItems.putAll(localItems);
        }
        return allItems;
    }




}
