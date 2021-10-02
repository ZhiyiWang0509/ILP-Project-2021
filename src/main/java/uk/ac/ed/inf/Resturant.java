package uk.ac.ed.inf;


import java.util.List;
import com.google.gson.Gson;

// Parsed class from menus.json
public class Resturant {

    private String name;
    private String location;
    private List<MenuDetails> menu;

    public static class MenuDetails {  // the Menu inner class is for each single item
        private String item;
        private int pence;
    }

}
