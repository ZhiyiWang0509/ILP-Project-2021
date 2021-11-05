package uk.ac.ed.inf;

import java.util.*;

/* An instance of this class represent an order, which include the information to be written in to the database

 */
public class Order {

    public String orderNO;
    public String deliverTo;
    public ArrayList<String> itemList;

    public Order(String orderNO, String deliverTo, ArrayList<String> itemList) {
        this.orderNO = orderNO;
        this.deliverTo = deliverTo;
        this.itemList = itemList;
    }
    // get the orderNo of the order
    public String getOrderNO() {
        return orderNO;
    }

    // get the address to deliver to
    public String getDeliverTo() {return deliverTo;}

    // get the itemList of the order
    public ArrayList<String> getItemList() {
        return itemList;
    }

    // return the total cost of the order by given the list of items
    public int getOrderCost(String webPort){
        Menus menus = new Menus(webPort);
        HashMap<String, Integer> itemsPrice = menus.getItemsPrice();
        int price = 0;
        try{
            for(String item : itemList){
                price += itemsPrice.get(item);
            }
        } catch(NullPointerException e){
            System.err.println("The item isn't found in the menu.");
        }
        // the standard delivery cost for each delivery
        int DELIVERY_COST = 50;
        return price + DELIVERY_COST;
    }

    // return a set of shops need to visit for this order
    public Set<String> getOrderShops(String webPort){
        Menus menus = new Menus(webPort);
        Set<String> shopLocations = new HashSet<>();
        for(String item : itemList){
            try{
                String location = menus.getItemRestaurant(item);  // need to catch NullPointerException if the item isn't found?
                shopLocations.add(location);
            } catch (NullPointerException e){
                System.err.println("Item is not found in the menu");
            }
        }
        return shopLocations;
    }

}
