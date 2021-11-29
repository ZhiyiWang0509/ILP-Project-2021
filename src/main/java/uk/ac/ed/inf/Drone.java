package uk.ac.ed.inf;

import org.javatuples.Pair;

import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.*;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * an instance of this class represent the drone on the date specified.
 * this class require the portal number of database and webserver to access relative information.
 *
 * note that the portal number here MUST match the number used to activate the database and webserver
 * through commend line before the start of this program.
 *
 * at the beginning of a day, the drone would depart from the Appleton Tower, the daily moves limit is
 * 1500 moves, the drone have to return to Appleton Tower after finishing the day's orders.
 * or it would return to Appleton Tower if the moves left is not enough for delivering the next order
 * before the start of delivering any order, it's mandatory to check if the move left after making the order
 * is greater than or equal to the moves needed to return to Appleton Tower from the destination of the delivery.
 * if yes, then the order can be delivered, otherwise the drone would return to the Appleton Tower.
 *
 * during a day of delivery, the drone MUST always remain in the confined area, which is defined by specifying four
 * corners of the area. The method to check this is in the LongLat class, and it will be called in this class when
 * the drone is making the next move.
 *
 * before making any order, the orders of the day needs to be checked if it's valid.
 * by a valid order, the number of items it contains must not be more than four and the number of shops needs to travel
 * to must be no more than two.
 *
 * in order to maximise the percentage monetary value delivered on the day, the drone would prioritize the order with higher
 * monetary return per move. Orders with higher returns and lower moves are preferred.
 *
 * when the drone is making a delivery order, it would first fly to the shops to collect the items required in the order,
 * if multiple items are from the same shop in an order, the drone only need to travel to the shop once.
 * after picking up all the items needed, the drone would fly to the destination of delivery, which is specified by the
 * 'deliverTo' location in an order.
 *
 * the drone would need to 'hover' for a move when it's located 'close to' a shop location or a deliverTo location.
 * by 'hover' it means a unit reduction in moves count with no change in location.
 *
 * on the drone's journey to any location, it has to check if the direct path to the destination cross the borders of
 * no-fly-zones.
 * if cross, then the drone would need to travel to an appropriate landmark to avoid crossing no-fly-zones, there are
 * two landmarks available to choose, one is located in the top right of the confined area, and the other is located at
 * relatively bottom left of the map. The path from the drone's location to the landmark must not cross no-fly-borders,
 * if the journey to either one of the landmarks cross, the drone would travel to the other one. If the path to both landmarks
 * don't cross, then the drone would travel to the one that's closest to the drone's location.
 *
 * there are three outcomes from the drone's deliveries made on the date.
 * the first one is the order made on the day, which is recorded as a list of Order objects,
 * this would be written in the database in the applying stage.
 * the second one is the flightpath of the delivery on the day, which is recorded as a list of FlightPath objects,
 * this would be written in the database in the applying stage.
 * the last one is a list of coordinates recording all the location travelled by the drone on the day, which is represented as
 * a list of LongLat objects.
 * this would be transformed to a geojson file in the applying stage, so that when putting the file in geojson.io, the flight
 * path can be illustrated.
 *
 *
 */
public class Drone {
    /**
     * this is the year of the date the deliveries are made
     */
    public String year;
    /**
     * this is the month of the date the deliveries are made
     */
    public String month;
    /**
     * this is the day of the date the deliveries are made
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
     * this is a count on the moves left on the day
     * at the beginning the move left is the upper limit 1500.
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
     * the value is passed when an instance is created
     */
    private final List<Line2D> noFlyZones;
    /**
     * this is a list of landmarks locations
     * the value is passed when an instance is created
     */
    private final List<LongLat> landmarks;
    /**
     * this the date of making the delivery
     */
    private final String date;
    /**
     * this field store a list of LongLat object
     * this would be parsed in to GeoJson file in the application
     */
    private static final List<LongLat> geoJsonList = new ArrayList<>();
    /**
     * this field store a list of Order object
     * this would be stored in to the 'deliveries' database created in the application
     */
    private static final List<Order> orderDataBase = new ArrayList<>();
    /**
     * this field store a list of FlightPath object
     * this would be stored in to the 'filghtpath' database created in the application
     */
    private static final List<FlightPath> flightPathDataBase = new ArrayList<>();

    /**
     * this is the constructor of the Drone class
     *
     * @param day           this is the day of making the deliveries
     * @param month         this is the month of making the deliveries
     * @param year          this is the year of making deliveries
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
        // this is the date of making the deliveries
        this.date = year + "-" + month + "-" + day;
    }

    /**
     * this method return the valid orders to deliver on the day as a list of Order objects
     * a valid order contains items no more than four and the shops need to travel to is no
     * more than two.
     *
     * @return a list of valid orders as Order objects.
     */
    private List<Order> getValidOrders() {
        Database ordersDb = new Database(dataBasePort);
        ArrayList<Order> orders = ordersDb.getOrders(date);
        List<Order> validOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.itemList.size() <= 4 && order.getOrderShops(webServerPort).size() <= 2) {
                validOrders.add(order);
            }
        }
        if (validOrders.isEmpty()) {
            System.out.println("There's no valid order to deliver today");
            System.exit(0);
        }
        return validOrders;
    }


    /**
     * used to get the next order that has the maximum order value per move from the input list
     * this is used to maximise the percentage monetary value of each day's delivery.
     *
     * @param orderList this is the input list of orders
     * @return the Order instance in the input list that got the highest ratio of monetary return over moves.
     */
    private Order getNextOrder(List<Order> orderList){
        HashMap<Double, Order> comparator = new HashMap<>();
        List<Double> valueMoveRatios = new ArrayList<>();
        for(Order order : orderList){
            comparator.put(order.getOrderCost(webServerPort)/(double)getRouteMovesCount(order),order);
            valueMoveRatios.add(order.getOrderCost(webServerPort)/(double)getRouteMovesCount(order));

        }
        valueMoveRatios.sort(Collections.reverseOrder());
        Double max_key = valueMoveRatios.get(0);
        return comparator.get(max_key);
    }

    /**
     * this method return the closest shop relative to the drone's current location.
     * if there are two shops need to travel to pick up items, the drone would go to the nearest one first.
     * in the case there's only one shop, the method would return the shop directly.
     *
     * @param shopList a list of shops in w3words form
     * @return the closest shop to visit in w3words form
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
     * it needs to obey the rules of not crossing the no-fly-zones borders and fly out of the confined area.
     * if the drone would cross no-fly-zones borders, it would need to travel to the nearest appropriate landmark
     * to avoid no-fly-area.
     * once the drone is close to any shop or deliverTo location it needs to hover for a move.
     * it would update the orders actually made, the flightpath took and the coordinates visited in the fields:
     * orderDatabase, flightPathDatabase and geoJsonList respectively.
     *
     */
    public void makeDelivery() {
        W3words w3words = new W3words(webServerPort);
        List<Order> validOrders = getValidOrders();
        // use the order number of the last order as the orderNo for drone's return to the Appleton
        int LAST_ORDER_INDEX = validOrders.size() - 1;
        Order lastOrder = validOrders.get(LAST_ORDER_INDEX);

        geoJsonList.add(currentLocation);
        while(!validOrders.isEmpty()){
            Order order = getNextOrder(validOrders);
            LongLat deliverTo = w3words.toLongLat(order.deliverTo);
            String orderNo = order.orderNO;
            // this is the total moves needed for this order
            int orderMoves = getRouteMovesCount(order);
            // this is the moves needed to return to Appleton after the order's finished
            int returnMoves = getMovesToAT(deliverTo);
            // this is to check if the drone still have moves left for returning after finishing this order
            if ((MOVE_LEFT - orderMoves) >= returnMoves) {
                List<String> shops = order.getOrderShops(webServerPort);
                while(!shops.isEmpty()){
                    String shop = getNextShop(shops);
                    LongLat shopLngLat = w3words.toLongLat(shop);
                    // this is to check if the path to the shop cross the non-fly zone
                    if (checkNoFlyZones(currentLocation, shopLngLat)) {
                        LongLat landmark = getLandMark(currentLocation, shopLngLat);
                        travelTo(orderNo, landmark);
                    }
                    travelTo(orderNo,shopLngLat);
                    shops.remove(shop);
                    // the drone hovers for 1 move wh it reaches shop
                    MOVE_LEFT -= 1;
                }
                // this is to check if the path to the delivery spot cross the non-fly zone
                if (checkNoFlyZones(currentLocation, deliverTo)) {
                    LongLat landmark = getLandMark(currentLocation, deliverTo);
                    travelTo(orderNo,landmark);
                }
                // at this point, an order is made
                travelTo(orderNo,deliverTo);
                orderDataBase.add(order);
                validOrders.remove(order);
                MOVE_LEFT -= 1;
            }
            // this is the case if the drone doesn't have enough move to fulfill the next delivery journey
            else {
                // this is to check if the path to Appleton Tower cross the non-fly zone
                if (checkNoFlyZones(currentLocation, APPLETON_TOWER)) {
                    LongLat landmark = getLandMark(currentLocation, APPLETON_TOWER);
                    travelTo(orderNo,landmark);
                }
                travelTo(orderNo,APPLETON_TOWER);
                break;
            }
        }
        // this is to check if the path to Appleton Tower cross the non-fly zone
        if (checkNoFlyZones(currentLocation, APPLETON_TOWER)) {
            LongLat landmark = getLandMark(currentLocation, APPLETON_TOWER);
            travelTo(lastOrder.orderNO, landmark);
        }
        // at this point, the drone has finished all the orders and returned to the Appleton Tower
        travelTo(lastOrder.orderNO, APPLETON_TOWER);
    }

    /**
     * this method would export the results obtained from makeDelivery(),
     * it would export a geojson file for the drone's flight path and write two tables
     * in the database, one called "deliveries" the other called "flightpath"
     */
    public void outPutResult(){
        try{
            List<Point> flightPathPoints = new ArrayList<>();
            for(LongLat location : geoJsonList){
                flightPathPoints.add(Point.fromLngLat(location.longitude,location.latitude));
            }
            LineString lineString = LineString.fromLngLats(flightPathPoints);
            Feature feature = Feature.fromGeometry(lineString);
            FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
            // initiate a file writer
            String fileName = "drone-" + day + "-" + month + "-" + year;
            try {
                FileWriter geojsonFile = new FileWriter(fileName);
                geojsonFile.write(featureCollection.toJson());
                geojsonFile.close();
                System.out.println("File write successfully!");
            } catch (IOException e) {
                System.err.println("Failed to generate the Geo json file");
                System.exit(1);
            }
            // store 'deliveries' and 'flightpath' table in the database
            Database database = new Database(dataBasePort);
            database.createDeliveriesDb(orderDataBase,webServerPort);
            database.createFlightPathDb(flightPathDataBase);

        }catch (NullPointerException e){
            System.err.println("method: makeDelivery, has to be called first");
            System.exit(1);
        }

    }
    /**
     * this method would renew the drone's current location with the given new location
     * this method would also check if the drone's new location is within the confined area
     * if yes, then update location
     * if no, then the program will be terminated
     *
     * @param newLocation the drone's new location
     */
    private void updateLocation(LongLat newLocation) {
        // this is to check if the drone is located in the confined area
        if (newLocation.isConfined()) {
            currentLocation = newLocation;
        } else {
            System.err.println("The new location is unconfined");
            System.exit(1);
        }

    }
    /**
     * this method would make the drone travel to a location that is close to the given LongLat location
     * by taking a move in the direction between the given location and the current location.
     * after taking a move, the flight path is recorded as well as the updated current location
     * the move count need to be reduced by 1 unit after taking a move
     * after the drone is close to the destination, the local fields store the coordinates visited and flightpath made
     * will be updated.
     *
     * @param orderNo this is the order number of the order the drone is delivering on this path
     * @param ToLoc this is the location the drone is heading to
     */
    private void travelTo(String orderNo, LongLat ToLoc) {
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
    }

    /**
     * this method would calculate the number of moves required for the drone to complete the order
     * provided as a parameter.
     * the moves also take into account the detour to landmarks if any path in delivering the order
     * cross the no-fly-zones borders.
     * when the drone travel to a shop or deliver location, it needs to 'hover' for a move.
     *
     * @param order this is the order to count for the moved needed.
     * @return the number of moves needed to complete the order provided.
     */
    private int getRouteMovesCount(Order order) {
        int moves = 0;
        List<LongLat> routeBuilder = new ArrayList<>();
        // the route start from the drone's current location
        routeBuilder.add(currentLocation);
        W3words w3words = new W3words(webServerPort);
        for (String shop : order.getOrderShops(webServerPort)) {
            routeBuilder.add(w3words.toLongLat(shop));
        }
        List<String> shopList = order.getOrderShops(webServerPort);
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
                // drone needs to hover at shop and deliver location.
                moves += 1;
            }
        }
        return moves;
    }

    /**
     * this method would calculate the number of moves needed to get to Appleton tower
     * from the given LongLat location.
     * if the direct path cross no-fly-zones, the moves to take detour would also be counted.
     *
     * @param location this is the location the drone is travelling from to the Appleton Tower
     * @return the number of moves needed to travel to Appleton Tower from the given LongLat location.
     */
    private int getMovesToAT(LongLat location) {
        int moves = 0;
        List<LongLat> path = new ArrayList<>();
        path.add(location);
        if (checkNoFlyZones(location, APPLETON_TOWER)) {
            LongLat landmark = getLandMark(location, APPLETON_TOWER);
            path.add(landmark);
        }
        path.add(APPLETON_TOWER);
        for (int i = 1; i < path.size(); i++) {
            // this is the index of the previous location
            int j = i - 1;
            moves += path.get(j).getMoves(path.get(i));
        }
        return moves;
    }

    /**
     * this method would check if the path formed by the two LongLat locations provided
     * would cross any of the no-fly-zones borders.
     *
     * @param startLoc  this is the starting coordinate of forming the path line segment.
     * @param finishLoc this is the finishing coordinate of forming the path line segment.
     * @return true if the path formed cross with any of the no-fly-zones borders.
     */
    private Boolean checkNoFlyZones(LongLat startLoc, LongLat finishLoc) {
        Line2D dronePath = new Line2D.Double(startLoc.longitude, startLoc.latitude,
                                            finishLoc.longitude, finishLoc.latitude);
        boolean isCrossed = false;
        for (Line2D border : noFlyZones) {
            isCrossed = dronePath.intersectsLine(border);
            if (isCrossed) {
                return isCrossed;
            }
        }
        return isCrossed;
    }

    /**
     * this is method return the appropriate landmark to travel to if the path formed by the two LongLat locations
     * cross any of the no-fly-zones borders.
     * the paths to the landmarks from the starting location and from the landmarks to the finishing location would be
     * checked whether no-fly-zones borders are crossed.
     * if one of the paths to landmarks crosses, then the other landmark would be return.
     * if both don't cross, the landmark that's closest to the drone's current location would be returned.
     *
     * @param startLoc  this is the starting coordinate of forming the path line segment.
     * @param finishLoc this is the finishing coordinate of forming the path line segment.
     * @return the location of the landmark for the drone to travel to as a LongLat object.
     */
    private LongLat getLandMark(LongLat startLoc, LongLat finishLoc) {
        // this hashmap store the distance corresponding to each landmark
        HashMap<Double, LongLat> distanceToLMs = new HashMap<>();
        // this list store the distances to each landmark
        // used to find the landmark with the minimum distance
        ArrayList<Double> distances = new ArrayList<>();
        for (LongLat landmark : landmarks) {
            // this is to check if both the path from starting location to the landmark
            // and from the landmark to the finish location won't cross the no-fly-zone borders.
            if (!checkNoFlyZones(startLoc, landmark) && !checkNoFlyZones(landmark,finishLoc)) {
                Double distance = startLoc.distanceTo(landmark);
                distanceToLMs.put(distance, landmark);
                distances.add(distance);
            }
        }
        // return the landmark with the minimum distance
        return distanceToLMs.get(Collections.min(distances));
    }

    // delete this main before submit!!
    /*public static void main(String[] args) {
        //Drone testBot = new Drone("2022-04-15", "9898", "9876");
        W3words w3words = new W3words("9898");
        LongLat from = new LongLat(-3.191257, 55.945626);
        LongLat to = new LongLat(-3.188512, 55.944036);
        LongLat testLM = testBot.getLandMark(to, from);
        System.out.println(testLM.formatLongLat());
        System.out.println(testBot.checkNoFlyZones(from, testLM));

        List<Order> orderList = testBot.getValidOrders();
        Order order = orderList.get(1);
       // Pair<List<FlightPath>, List<LongLat>> pair = testBot.travelTo(order.orderNO, w3words.toLongLat(order.deliverTo));
        System.out.println(pair.getValue0());
        System.out.println(pair.getValue1());
    } */


}

