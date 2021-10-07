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

    // return every item in the menu in the form of Hash map
    // the item's name is the key and price is the value for each pair in the map
    public HashMap<String, Integer> getItemsHash(){
        HashMap<String, Integer> itemList = new HashMap<>();
        for (MenuDetails singleItem : menu) {
            itemList.put(singleItem.item, singleItem.pence);
        }
        return itemList;
    }
}
