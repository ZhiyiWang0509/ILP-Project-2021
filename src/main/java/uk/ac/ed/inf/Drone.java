package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * an instance of this class represent the drone on the date specified.
 * the main method of making deliveries flightpath is implemented with
 * helper functions in this class.
 *
 */
public class Drone {
    /**
     * this is the year of the date when the deliveries are made
     */
    public String year;
    /**
     * this is the month of the date when the deliveries are made
     */
    public String month;
    /**
     * this is the day of the date when the deliveries are made
     */
    public String day;
    /**
     * this is the portal of the webserver to access
     */
    public String webServerPort;
    /**
     * this is the portal of the database to access
     */
    public String dataBasePort;
    /**
     * this is a count on the moves left in the drone
     * at the beginning the move left is at the upper limit 1500.
     */
    private static int MOVE_LEFT = 1500;
    /**
     * this is the location of the Appleton Tower
     */
    private final LongLat APPLETON_TOWER = new LongLat(-3.186874, 55.944494);
    /**
     * this is the drone's current location
     * the drone's initial location is at the Appleton Tower
     */
    private LongLat currentLocation = APPLETON_TOWER;
    /**
     * this is a list of borders of no-fly-zones
     * the value is passed in the constructor
     */
    private final List<Line2D> noFlyZones;
    /**
     * this is a list of landmarks locations
     * the value is passed in the constructor
     */
    private final List<LongLat> landmarks;
    /**
     * this is the map used to look up an item's price with the item's name
     */
    private final HashMap<String, Integer> allItemPrices;
    /**
     * this is the map used to look up an item's location with the item's name
     */
    private final HashMap<String, String> allItemShops;
    /**
     * this the date of making the delivery
     */
    private final String date;
    /**
     * this is a list of coordinates visited by the drone during the day's delivery journey
     */
    public List<LongLat> geoJsonList = new ArrayList<>();
    /**
     * this is a list of orders made by the drone during the day's delivery journey
     */
    public List<Order> orderDataBase = new ArrayList<>();
    /**
     * this is a list of flightpath made by the drone during the day's delivery journey
     */
    public List<FlightPath> flightPathDataBase = new ArrayList<>();

    /**
     * this is the constructor of the Drone class
     *
     * @param day           this is the day of making the deliveries
     * @param month         this is the month of making the deliveries
     * @param year          this is the year of making the deliveries
     * @param webServerPort this is the portal number of the webserver
     * @param dataBasePort  this is the portal number of the database
     */
    public Drone(String day, String month,String year,String webServerPort, String dataBasePort) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.webServerPort = webServerPort;
        this.dataBasePort = dataBasePort;

        // this is the file name of the 'no-fly-zones.geojson' file without file extension.
        String NO_FLY_ZONES = "no-fly-zones";
        this.noFlyZones = new Buildings(webServerPort, NO_FLY_ZONES).getNoFlyBorders();

        //this is the file name of the 'landmarks.geojson' file without file extension.
        String LANDMARKS = "landmarks";
        this.landmarks = new Buildings(webServerPort, LANDMARKS).getLandMarks();
        this.date = year + "-" + month + "-" + day;

        Menus menu = new Menus(webServerPort);
        this.allItemPrices = menu.getAllItemsPrice();
        this.allItemShops = menu.getAllItemsLocation();
    }

    /**
     * this method return the valid orders to deliver on the day as a list of Order objects
     * a valid order contains no more than four items and the number of shops need to visit is no
     * more than two.
     *
     * @return a list of valid orders as Order objects.
     */
    private List<Order> getValidOrders() {
        DataBase ordersDb = new DataBase(dataBasePort, webServerPort);
        ArrayList<Order> orders = ordersDb.getOrders(date);
        List<Order> validOrders = new ArrayList<>();
        try{
            for (Order order : orders) {
                if (order.itemList.size() <= 4 && order.getOrderShops(allItemShops).size() <= 2) {
                    validOrders.add(order);
                }
            }
        }catch (NullPointerException e){
            System.err.println("There's no valid order to deliver today");
            System.exit(1);
        } catch (Exception e){
            System.exit(1);
        }
        return validOrders;
    }


    /**
     * used to get the next order that has the maximum order value per move from the input list
     * this is used to maximise the percentage monetary value of each day's delivery.
     *
     * @param orderList this is the input list of orders
     * @return the order that has the highest ratio of monetary return over moves.
     */
    private Order getNextOrder(List<Order> orderList){
        HashMap<Double, Order> comparator = new HashMap<>();
        List<Double> valueMoveRatios = new ArrayList<>();
        try{
            for(Order order : orderList){
                comparator.put(order.getOrderCost(allItemPrices)/(double)getRouteMovesCount(order),order);
                valueMoveRatios.add(order.getOrderCost(allItemPrices)/(double)getRouteMovesCount(order));

            }
        }catch(NullPointerException|ArrayIndexOutOfBoundsException e){
            System.err.println("The input list is empty");
            System.exit(1);
        }
        valueMoveRatios.sort(Collections.reverseOrder());
        Double max_key = valueMoveRatios.get(0);
        return comparator.get(max_key);

    }

    /**
     * this method return the closest shop relative to the drone's current location.
     *
     * @param shopList a list of shops in w3words form
     * @return the w3words location of the closest shop to visit
     */
    private String getNextShop(List<String> shopList){
        W3words w3words = new W3words(webServerPort);
        int SINGLE_SHOP = 1;
        int FIRST_SHOP_iNDEX = 0;
        int SECOND_SHOP_INDEX = 1;
        if(shopList.size() == SINGLE_SHOP){
            return shopList.get(FIRST_SHOP_iNDEX);
        } else{
            LongLat shop1 = w3words.toLongLat(shopList.get(FIRST_SHOP_iNDEX));
            LongLat shop2 = w3words.toLongLat(shopList.get(SECOND_SHOP_INDEX));
            if(currentLocation.distanceTo(shop1) < currentLocation.distanceTo(shop2)){
                return shopList.get(FIRST_SHOP_iNDEX);
            } else{
                return shopList.get(SECOND_SHOP_INDEX);
            }
        }

    }

    /**
     * this method would make the drone deliver the valid orders of the day until it runs out
     * of moves.
     *
     * this method would keep update the orders actually made, the flightpath took and the coordinates visited in
     * the fields: orderDatabase, flightPathDatabase and geoJsonList respectively.
     *
     */
    public void makeDelivery() {
        try {
            W3words w3words = new W3words(webServerPort);
            List<Order> validOrders = getValidOrders();
            // use the order number of the last order as the orderNo for drone's return to the Appleton
            int LAST_ORDER_INDEX = validOrders.size() - 1;
            Order lastOrder = validOrders.get(LAST_ORDER_INDEX);
            geoJsonList.add(currentLocation);
            while (!validOrders.isEmpty()) {
                Order order = getNextOrder(validOrders);
                LongLat deliverTo = w3words.toLongLat(order.deliverTo);
                String orderNo = order.orderNO;

                int orderMoves = getRouteMovesCount(order);
                int returnMoves = getMovesToAT(deliverTo);
                // this is to check if the drone still have moves left for returning after finishing this order
                if ((MOVE_LEFT - orderMoves) >= returnMoves) {
                    List<String> shops = order.getOrderShops(allItemShops);
                    try{
                        while (!shops.isEmpty()) {
                            String shop = getNextShop(shops);
                            LongLat shopLngLat = w3words.toLongLat(shop);
                            if (checkNoFlyZones(currentLocation, shopLngLat)) {
                                LongLat landmark = getLandMark(currentLocation, shopLngLat);
                                travelTo(orderNo, landmark);
                            }
                            travelTo(orderNo, shopLngLat);
                            shops.remove(shop);
                            // the drone hovers for 1 move when it reaches a shop
                            MOVE_LEFT -= 1;
                        }
                    }catch(NullPointerException|ArrayIndexOutOfBoundsException e){
                        System.err.println("Shop Collection is empty");
                        System.exit(1);
                    }
                    if (checkNoFlyZones(currentLocation, deliverTo)) {
                        LongLat landmark = getLandMark(currentLocation, deliverTo);
                        travelTo(orderNo, landmark);
                    }
                    // at this point, an order is made
                    travelTo(orderNo, deliverTo);
                    orderDataBase.add(order);
                    validOrders.remove(order);
                    MOVE_LEFT -= 1;
                }
                // drone give up this order and return to the order-move check
                else {
                    validOrders.remove(order);
                }
            }
            if (checkNoFlyZones(currentLocation, APPLETON_TOWER)) {
                LongLat landmark = getLandMark(currentLocation, APPLETON_TOWER);
                travelTo(lastOrder.orderNO, landmark);
            }
            // at this point, the drone has finished all the orders and returned to the Appleton Tower
            travelTo(lastOrder.orderNO, APPLETON_TOWER);
        }catch(NullPointerException|ArrayIndexOutOfBoundsException e){
            System.err.println("Order collection is empty");
            System.exit(1);
        }catch(Exception e){
            System.exit(1);
        }
    }


    /**
     * this method would update the drone's current location to the given new location if
     * the new location is within the confined area
     *
     * @param newLocation the drone's new location
     */
    private void updateLocation(LongLat newLocation) {
        if (newLocation.isConfined()) {
            currentLocation = newLocation;
        } else {
            System.err.println("The new location is unconfined");
            System.exit(1);
        }

    }
    /**
     * this method would make the drone travel to a location that is close to the given LongLat location
     * after the drone is close to the destination, the local fields store the coordinates visited and flightpath made
     * will be updated.
     *
     * @param orderNo this is the order number of the order the drone is delivering on this path
     * @param ToLoc this is the location the drone is heading to
     */
    private void travelTo(String orderNo, LongLat ToLoc) {
        try{
            List<FlightPath> flightPaths = new ArrayList<>();
            List<LongLat> coordinates = new ArrayList<>();
            while (!currentLocation.closeTo(ToLoc)) {
                int angle = currentLocation.getAngle(ToLoc);
                LongLat nextLocation = currentLocation.nextPosition(angle);
                FlightPath flightPath = new FlightPath(orderNo, currentLocation.longitude, currentLocation.latitude, angle,
                        nextLocation.longitude, nextLocation.latitude);
                flightPaths.add(flightPath);
                updateLocation(nextLocation);
                coordinates.add(currentLocation);
                MOVE_LEFT -= 1;
            }
            geoJsonList.addAll(coordinates);
            flightPathDataBase.addAll(flightPaths);
        } catch(Exception e){
            System.exit(1);
        }

    }

    /**
     * this method would calculate the number of moves that would take for the drone to complete the given order
     *
     * @param order this is the order to count for the moved needed.
     * @return the number of moves needed to complete the order provided.
     */
    private int getRouteMovesCount(Order order) {
        int moves = 0;
        List<LongLat> routeBuilder = new ArrayList<>();
        routeBuilder.add(currentLocation);
        W3words w3words = new W3words(webServerPort);
        for (String shop : order.getOrderShops(allItemShops)) {
            routeBuilder.add(w3words.toLongLat(shop));
        }
        List<String> shopList = order.getOrderShops(allItemShops);
        try{
            while(!shopList.isEmpty()){
                String nextShop = getNextShop(shopList);
                routeBuilder.add(w3words.toLongLat(nextShop));
                shopList.remove(nextShop);
            }
            routeBuilder.add(w3words.toLongLat(order.deliverTo));
            for (int i = 1; i < routeBuilder.size(); i++) {
                // this is the index of the previous location
                int j = i - 1;
                LongLat fromLoc = routeBuilder.get(j);
                LongLat ToLoc = routeBuilder.get(i);
                if (checkNoFlyZones(fromLoc, ToLoc)) {
                    LongLat landmark = getLandMark(fromLoc, ToLoc);
                    moves += fromLoc.getMoves(landmark);
                    moves += landmark.getMoves(ToLoc);
                } else {
                    moves += fromLoc.getMoves(ToLoc);
                    // drone needs to hover at a shop or deliver location.
                    moves += 1;
                }
            }
        } catch(Exception e){
            System.exit(1);
        }
        return moves;
    }

    /**
     * this method would calculate the number of moves that would take for the drone to travel to Appleton Tower
     * from the given location.
     *
     * @param location this is the starting point of the drone's path to the Appleton Tower
     * @return the number of moves needed to travel to Appleton Tower from the given location.
     */
    private int getMovesToAT(LongLat location) {
        List<LongLat> path = new ArrayList<>();
        path.add(location);
        if (checkNoFlyZones(location, APPLETON_TOWER)) {
            LongLat landmark = getLandMark(location, APPLETON_TOWER);
            path.add(landmark);
        }
        path.add(APPLETON_TOWER);
        int moves = 0;
        try{
            for (int i = 1; i < path.size(); i++) {
                // this is the index of the previous location
                int j = i - 1;
                moves += path.get(j).getMoves(path.get(i));
            }
        }catch(Exception e){
            System.exit(1);
        }
        return moves;
    }

    /**
     * this method would check if the path jointed by the two locations provided would
     * cross any of the no-fly-zones borders.
     *
     * @param startLoc  this is the starting point of the path
     * @param finishLoc this is the finishing point of the path
     * @return true if the path would cross with any of the no-fly-zones borders.
     */
    private Boolean checkNoFlyZones(LongLat startLoc, LongLat finishLoc) {
        Line2D dronePath = new Line2D.Double(startLoc.longitude, startLoc.latitude,
                                            finishLoc.longitude, finishLoc.latitude);
        boolean isCrossed;
        try{
            for (Line2D border : noFlyZones) {
                isCrossed = dronePath.intersectsLine(border);
                if (isCrossed) {
                    return true;
                }
            }
        }catch (NullPointerException|ArrayIndexOutOfBoundsException e){
            System.err.println("The no-fly-zone borders aren't obtained successfully");
            System.exit(1);
        }catch (Exception e){
            System.exit(1);
        }
        return false;

    }

    /**
     * this method return the appropriate landmark for the drone to travel to from the drone's current location
     *
     * @param startLoc  this is the starting coordinate of forming the path line segment.
     * @param finishLoc this is the finishing coordinate of forming the path line segment.
     * @return the location of the landmark for the drone to travel to as a LongLat object.
     */
    private LongLat getLandMark(LongLat startLoc, LongLat finishLoc) {
        HashMap<Double, LongLat> distanceToLMs = new HashMap<>();
        ArrayList<Double> distances = new ArrayList<>();
        try{
            for (LongLat landmark : landmarks) {
                if (!checkNoFlyZones(startLoc, landmark) && !checkNoFlyZones(landmark,finishLoc)) {
                    Double distance = startLoc.distanceTo(landmark);
                    distanceToLMs.put(distance, landmark);
                    distances.add(distance);
                }
            }
        }catch (NullPointerException|ArrayIndexOutOfBoundsException e){
            System.err.println("The landmarks aren't obtained successfully");
            System.exit(1);
        }catch(Exception e){
            System.exit(1);
        }
        // the closest landmark to the drone's current location
        return distanceToLMs.get(Collections.min(distances));
    }


}

