package uk.ac.ed.inf;

import java.util.*;

/**
 * An instance of Order class represent a single order for the drone to deliver
 * this class contain essential information to be written into the
 * 'deliveries' database as an output of the application
 *
 */
public class Order {

    /**
     * this field records the order number of the order
     * it's a unique identified of each order in the database
     */
    public String orderNO;
    /**
     * this field stores the location of the delivery destination
     * the location is encoded in w3words format
     */
    public String deliverTo;
    /**
     * this field stored a list of names of all the items included in this order
     */
    public ArrayList<String> itemList;

    /**
     * this is a constructor of the Order class
     *
     * @param orderNO this is the order number of the order
     * @param deliverTo this is the location of the order to be delivered
     * @param itemList this is the list of items included in this order
     */
    public Order(String orderNO, String deliverTo, ArrayList<String> itemList) {
        this.orderNO = orderNO;
        this.deliverTo = deliverTo;
        this.itemList = itemList;
    }

    /**
     * this method return the total amount of money made by doing this order
     * the total amount include a fixed 50 pence of delivery charge which is
     * compulsory for each order
     *
     * @param webPort this is the portal of the webserver where the menu of the
     *                shop is stored
     * @return the total price of this order included the 50p delivery charge
     */
    public int getOrderCost(String webPort){
        Menus menus = new Menus(webPort);
        HashMap<String, Integer> itemsPrice = menus.getAllItemsPrice();
        int price = 0;
        try{
            for(String item : itemList){
                price += itemsPrice.get(item);
            }
        } catch(NullPointerException e){
            System.err.println("The item isn't found in the menu.");
        }
        int DELIVERY_COST = 50;
        return price + DELIVERY_COST;
    }

    /**
     * this method return a set of all the shops needs to visit to pick up all the items
     * required in the order.
     * a set is used since it's unnecessary to travel to a shop multiple times even if
     * there are multiple items in the order that are from the same shop
     *
     * @param webPort this is the portal of the web server where the menu of the shop is stored
     * @return a set of all the shops need to travel to, a set is used since there's no necessary
     * to travel to a shop multiple times even if there are multiple items from the same shop in
     * the order
     */
    public Set<String> getOrderShops(String webPort){
        Menus menus = new Menus(webPort);
        Set<String> shopLocations = new HashSet<>();
        for(String item : itemList){
            try{
                String location = menus.getItemShop(item);  // need to catch NullPointerException if the item isn't found?
                shopLocations.add(location);
            } catch (NullPointerException e){
                System.err.println("Item is not found in the menu");
                System.exit(0);
            }
        }
        return shopLocations;
    }

}
