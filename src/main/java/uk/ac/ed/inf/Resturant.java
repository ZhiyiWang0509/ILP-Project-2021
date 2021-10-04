package uk.ac.ed.inf;


import java.util.HashMap;
import java.util.List;


// Parsed class from menus.json
public class Resturant {

    String name;
    String location;
    List<MenuDetails> menu;

    public static class MenuDetails {  // the Menu inner class is for each single item
        String item;
        Integer pence;
    }

    // the method would generate a hash map of all the items in the restaurant's menu
    // for a quicker lookup of the price.
    public HashMap<String, Integer> getItemsHash(){
        HashMap<String, Integer> itemList = new HashMap<>();
        for (MenuDetails singleItem : menu){
            itemList.put(singleItem.item, singleItem.pence);
        }
        return itemList;
    }
}
