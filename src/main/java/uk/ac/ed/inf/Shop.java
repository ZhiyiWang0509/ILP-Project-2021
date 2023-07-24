package uk.ac.ed.inf;

import java.util.HashMap;
import java.util.List;

/**
 *  this class is used as a GSON parser for the file located in the 'menus' folder on the webserver.
 *
 */
public class Shop {

    /**
     * this is the name of the shop
     */
    private final String name;
    /**
     * this is the location of the shop encoded in W3words format
     */
    private final String location;
    /**
     * this is the menu of the shop
     */
    private final List<MenuDetails> menu;
    /**
     * this is an inner class created for the field: menu in the JSON string
     */
    private static class MenuDetails {
        String item;
        Integer pence;
    }

    /**
     * this is the constructor of the class
     * @param name this is the name of the shop
     * @param location this is the location of the shop encoded in W3words format
     * @param menu this is the menu of the sho
     */
    public Shop(String name, String location, List<MenuDetails> menu) {
        this.name = name;
        this.location = location;
        this.menu = menu;
    }

    /**
     * this method organize price information for items in this shop
     *
     * @return a hashmap of the (item name, item price) pairs for items in this shop
     */
    public HashMap<String, Integer> getItemsPrice(){
        HashMap<String, Integer> itemPriceMap = new HashMap<>();
        for (MenuDetails singleItem : menu) {
            itemPriceMap.put(singleItem.item, singleItem.pence);
        }
        return itemPriceMap;
    }

    /**
     * this method organize shop location information for items in this shop
     *
     * @return a hashmap of (item name, shop location) pairs for items in this shop
     */
    public HashMap<String, String> getShopLocation(){
        HashMap<String, String> itemLocationMap = new HashMap<>();
        for (MenuDetails singleItem : menu) {
            itemLocationMap.put(singleItem.item, location);
        }
        return itemLocationMap;
    }



}
