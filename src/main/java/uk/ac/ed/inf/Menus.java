package uk.ac.ed.inf;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Menus {

    public String server;
    public String port;

    //class constructor
    public Menus(String server, String port) {
        this.server = server;
        this.port = port;
    }

    public int getDeliveryCost(String item1) {
        return 0;
    }

    public int getDeliveryCost(String item1, String item2) {
        return 0;
    }

    public int getDeliveryCost(String item1, String item2, String item3) {
        return 0;
    }

    public int getDeliveryCost(String item1, String item2, String item3, String item4) {
        return 0;
    }

    public static void main(String[] args){
        Menus menu1 = new Menus("localhost","80");
        WebAccess newAccess = new WebAccess(menu1.server, menu1.port,"menus","menus");
        Type listType = new TypeToken<ArrayList<Resturant>>(){}.getType();
        ArrayList<Resturant> resturantList = new Gson().fromJson(String.valueOf(newAccess.getResponse()), listType);
        System.out.println(resturantList);
        //System.out.println(newAccess.getResponse());


    }
}
