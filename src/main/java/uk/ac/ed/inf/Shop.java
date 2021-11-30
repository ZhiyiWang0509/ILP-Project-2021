package uk.ac.ed.inf;

import java.util.HashMap;
import java.util.List;

/**
 *  This class act as a json parser for the Menus.json file stored on the web server
 *  it also contains methods to transform vital attributes: location, menu,into a format that's
 *  easier to apply in other classes
 *
 *  fields name in this class match exactly to the attribute names in the json file.
 *
 */
public class Shop {

    /**
     * this is the name of the shop
     */
    private String name;
    /**
     * this is the location of the shop encoded in W3words format
     */
    public String location;
    /**
     * this is the menu of the shop
     * a menu contain information of the items sell in the shop
     * and their prices
     */
    public List<MenuDetails> menu;

    /**
     * MenuDetails is an inner class of the Shop class
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
     * this method is used when there's needs to lookup item's price using item name
     *
     * @return a hashmap of the items on the menu with item names corresponding to
     * items' prices
     */
    public HashMap<String, Integer> getItemsPrice(){
        HashMap<String, Integer> itemPriceMap = new HashMap<>();
        for (MenuDetails singleItem : menu) {
            itemPriceMap.put(singleItem.item, singleItem.pence);
        }
        return itemPriceMap;
    }

    /**
     * this method is used when there's needs to lookup item's shop location using item name
     *
     * @return a hashmap of item names corresponding to the shop location
     */
    public HashMap<String, String> getShopLocation(){
        HashMap<String, String> itemLocationMap = new HashMap<>();
        for (MenuDetails singleItem : menu) {
            itemLocationMap.put(singleItem.item, location);
        }
        return itemLocationMap;
    }



}
