package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

/**
 * This class is used to process content in the menu.json file on the webserver
 *
 */

public class Menus {
    /**
     * this is the portal of the webserver
     */
    private final String webPort;

    /**
     * this is the constructor of the Menu class
     *
     * @param webPort this is the portal of the webserver
     */
    public Menus(String webPort) {
        this.webPort = webPort;
    }

    /**
     * this method obtain the file content in the menu.json file on the webserver
     *
     * @return an Array list of all the shops available for the day's delivery service
     */
    private ArrayList<Shop> getShops(){
        WebAccess newAccess = new WebAccess(webPort,"menus","menus");
        Type listType = new TypeToken<ArrayList<Shop>>(){}.getType();
        return new Gson().fromJson(String.valueOf(newAccess.getResponse()), listType);
    }

    /**
     * this method organize prices for all the items available to order in a HashMap so that an item's price can be
     * lookup using the item's name.
     *
     * @return a HashMap of (item name, item price) pair for every item available to order
     */
    public HashMap<String, Integer> getAllItemsPrice(){
        ArrayList<Shop> shopList = getShops();
        HashMap<String, Integer> allItems = new HashMap<>();
        for (Shop shop : shopList){
            HashMap<String, Integer> localItems = shop.getItemsPrice();
            allItems.putAll(localItems);
        }
        return allItems;
    }

    /**
     * this method organize shop locations for all the items available to order in a HashMap so that
     * an item's shop location can be lookup using the item's name.
     *
     * the shop location returned by this method is in W3word format, the location needs to be
     * transformed in to LongLat object if necessary.
     *
     * @return a HashMap of (item name, shop location) pair for each item available to order.
     */
    public HashMap<String, String> getAllItemsLocation() {
        ArrayList<Shop> shopList = getShops();
        HashMap<String, String> allItems = new HashMap<>();
        for (Shop shop : shopList){
            HashMap<String, String> localItems = shop.getShopLocation();
            allItems.putAll(localItems);
        }
        return allItems;
    }


}
