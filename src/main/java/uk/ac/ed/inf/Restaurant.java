package uk.ac.ed.inf;

/* an instance of this class act as a json parser for the json content from the Menus.json
 * the local fields here matched the corresponding keys in the json file
 */

import java.util.HashMap;
import java.util.List;

public class Restaurant {

    public String name;  // the restaurant name
    public String location;  // the restaurant's location
    List<MenuDetails> menu;  // the menu of the restaurant

    public static class MenuDetails {  // the inner class is created to map the content inside the menu field
        String item;
        Integer pence;
    }

    // return every item's price stored in Hash map
    // the item's name is the key and price is the value for each pair in the map
    public HashMap<String, Integer> getItemsPrice(){
        HashMap<String, Integer> itemList = new HashMap<>();
        for (MenuDetails singleItem : menu) {
            itemList.put(singleItem.item, singleItem.pence);
        }
        return itemList;
    }

    // return the restaurant location correspond to the items in a hash map
    public HashMap<String, String> getShopLocation(){
        HashMap<String, String> itemList = new HashMap<>();
        for (MenuDetails singleItem : menu) {
            itemList.put(singleItem.item, location);
        }
        return itemList;
    }



}
