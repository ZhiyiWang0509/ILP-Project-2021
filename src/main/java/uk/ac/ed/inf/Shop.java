package uk.ac.ed.inf;

import java.util.HashMap;
import java.util.List;

/**
 *  The Shop class act as a json parser for the Menus.json file stored on the web server
 */
public class Shop {

    /**
     * this is the name of the shop
     */
    public String name;
    /**
     * this is the location of the shop encoded in W3words format
     */
    public String location;
    /**
     * this is the menu of the shop
     * a menu contain information of the items sell in the shop
     * and their prices
     */
    List<MenuDetails> menu;  // the menu of the restaurant

    /**
     * MenuDetails is a inner class of the Shop class
     * it's used to parse the MenuDetails in the json file
     *
     * item field store the name of the item sell
     * pence field store the price of the item sell in pence
     *
     */
    public static class MenuDetails {
        String item;
        Integer pence;
    }


    /**
     * this method return a hashmap of the item sell in the shop
     * the name of each item is the key and the price of the item is the value for
     * each pair
     *
     * @return a hashmap of the items on the menu with item names corresponding to
     * items' prices
     */
    public HashMap<String, Integer> getItemsPrice(){
        HashMap<String, Integer> itemList = new HashMap<>();
        for (MenuDetails singleItem : menu) {
            itemList.put(singleItem.item, singleItem.pence);
        }
        return itemList;
    }

    // return the restaurant location correspond to the items in a hash map

    /**
     * this method return a hashmap of the items in the shop
     * the name of each item is the key and the location of the shop the item
     * belongs to is the value for each pair
     *
     * @return a hashmap of item names corresponding to the shop location
     */
    public HashMap<String, String> getShopLocation(){
        HashMap<String, String> itemList = new HashMap<>();
        for (MenuDetails singleItem : menu) {
            itemList.put(singleItem.item, location);
        }
        return itemList;
    }



}
