package uk.ac.ed.inf;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Menus {

    public String server;
    public String port;

    //class constructor
    public Menus(String server, String port) {
        this.server = server;
        this.port = port;
    }

    // The method would extract items from every restaurant as a hashmap with the item name as the key and item price as the value
    // Hashmap is considered since it's a quick way to lookup price of item.
    public HashMap<String, Integer> getItemList(){
        WebAccess newAccess = new WebAccess(server, port,"menus","menus");

        // create an arrayList contain restaurants information as json object
        Type listType = new TypeToken<ArrayList<Resturant>>(){}.getType();
        ArrayList<Resturant> resturantList = new Gson().fromJson(String.valueOf(newAccess.getResponse()), listType);


        HashMap<String, Integer> items = new HashMap<>();
        for (Resturant resturant : resturantList){
            HashMap<String, Integer> localItems = resturant.getItemsHash(); // generate a hashmap using the getItemHash() method from the restaurant class for each restaurant
            items.putAll(localItems);  // combine the information of each to a large hashmap
        }
        return items;
    }

    public int getDeliveryCost(String item1) {
        HashMap<String, Integer> itemList = getItemList();
        return itemList.get(item1) + 50;
    }

    public int getDeliveryCost(String item1, String item2) {
        HashMap<String, Integer> itemList = getItemList();
        return (itemList.get(item1) + itemList.get(item2) + 50);
    }

    public int getDeliveryCost(String item1, String item2, String item3) {
        HashMap<String, Integer> itemList = getItemList();
        return (itemList.get(item1) + itemList.get(item2) + itemList.get(item3) + 50);
    }

    public int getDeliveryCost(String item1, String item2, String item3, String item4) {
        HashMap<String, Integer> itemList = getItemList();
        return (itemList.get(item1) + itemList.get(item2) + itemList.get(item3)+ itemList.get(item4) + 50);
    }


}
