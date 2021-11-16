package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.*;

/**
 * this class is used to define the characteristics and movements made by a drone.
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
 * in order to maximise the percentage monetary value delivered on the day,the valid orders would be sorted in a way that
 * prioritise the orders with higher monetary return.
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
     * this the date of making the delivery
     */
    public String date;
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
     * at the beginning the move is the upper limit 1500.
     */
    private static int MOVE_LEFT = 1500;
    /**
     * this is the location of the Appleton Tower
     */
    private final LongLat APPLETON_TOWER = new LongLat(-3.186874, 55.944494);
    /**
     * this is the drone's current location
     * the drone's initial location is always at the Appleton Tower
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
     * this is the constructor of the Drone class
     *
     * @param date          this is the date of making deliveries
     * @param webServerPort this is the portal number of the webserver
     * @param dataBasePort  this is the portal number of the database
     */
    public Drone(String date, String webServerPort, String dataBasePort) {
        this.date = date;
        this.webServerPort = webServerPort;
        this.dataBasePort = dataBasePort;

        String NO_FLY_ZONES = "no-fly-zones"; // this is the file name of the 'no-fly-zones.geojson' file without file extension.
        this.noFlyZones = new Buildings(webServerPort, NO_FLY_ZONES).getNoFlyBorders();
        String LANDMARKS = "landmarks"; //this is the file name of the 'landmarks.geojson' file without file extension.
        this.landmarks = new Buildings(webServerPort, LANDMARKS).getLandMarks();
    }

    /**
     * this method return the valid orders to deliver on the day as a list of Order objects
     * a valid order contains items no more than four and the shops need to travel to is no
     * more than two.
     *
     * @return a list of valid orders as Order objects.
     */
    public List<Order> getValidOrders() {
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
     * this method would sort input list of Orders by the monetary return of delivering
     * the orders in descending manner.
     *
     * @param orders this is a list of Order objects to sort.
     * @return a sorted list of Order objects by each order's monetary return in a descending manner.
     */
    public List<Order> sortOrdersByValue(List<Order> orders) {
        List<Order> sortedOrders = new ArrayList<>();
        List<Integer> sortedPrice = new ArrayList<>();
        HashMap<Integer, Order> comparator = new HashMap<>();
        for (Order order : orders) {
            comparator.put(order.getOrderCost(webServerPort), order);
            sortedPrice.add(order.getOrderCost(webServerPort));
        }
        sortedPrice.sort(Collections.reverseOrder());
        for (Integer price : sortedPrice) {
            for (Map.Entry<Integer, Order> orderEntry : comparator.entrySet()) {
                if (orderEntry.getKey().equals(price)) {
                    sortedOrders.add(orderEntry.getValue());
                }
            }
        }
        return sortedOrders;
    }

    /**
     * this method would make the drone deliver the sorted valid orders of the day until it runs out
     * of moves.
     * it needs to obey the rules of not crossing the no-fly-zones borders and fly out of the confined area.
     * if the drone would cross no-fly-zones borders, it would need to travel to the nearest appropriate landmark
     * to avoid no-fly-area.
     * once the drone is close to any shop or deliverTo location it needs to hover for a move.
     * it would record the orders actually made, the flightpath took and the coordinates visited.
     *
     * @return a Result object representing the outcome of making the day's delivery.
     */
    public Result makeDelivery() {
        W3words w3words = new W3words(webServerPort);
        List<LongLat> flightCoordinates = new ArrayList<>();  // the list to store the coordinates visited by the drone
        List<Order> orderMadeList = new ArrayList<>();  // store all the orders made
        List<FlightPath> flightPaths = new ArrayList<>();  // store all the flight path took

        List<Order> validOrders = sortOrdersByValue(getValidOrders());
        flightCoordinates.add(currentLocation);
        for (Order order : validOrders) {
            LongLat deliverTo = w3words.toLongLat(order.deliverTo);
            String orderNo = order.orderNO;
            int orderMoves = getRouteMovesCount(order); // the total moves needed for this order
            int returnMoves = getMovesToAT(deliverTo);  // the moves needed to return to Appleton after the order's finished
            if ((MOVE_LEFT - orderMoves) >= returnMoves) {  // check if the drone still have moves left for returning after finishing this order
                Set<String> shops = order.getOrderShops(webServerPort);
                for (String shop : shops) {
                    LongLat shopLngLat = w3words.toLongLat(shop);
                    if (checkNoFlyZones(currentLocation, shopLngLat)) {  // if the path to the shop cross the non-fly zone
                        LongLat landmark = getLandMark(currentLocation, shopLngLat);
                        flightPaths.addAll(travelTo(orderNo, landmark));
                        flightCoordinates.add(currentLocation);
                    }
                    flightPaths.addAll(travelTo(orderNo, shopLngLat));
                    flightCoordinates.add(currentLocation);
                    MOVE_LEFT -= 1; // hover for 1 move
                }
                if (checkNoFlyZones(currentLocation, deliverTo)) { // need to check if the route would pass the non-fly zone as well
                    LongLat landmark = getLandMark(currentLocation, deliverTo);
                    flightPaths.addAll(travelTo(orderNo, landmark));
                    flightCoordinates.add(currentLocation);
                }
                flightPaths.addAll(travelTo(orderNo, deliverTo));
                flightCoordinates.add(currentLocation);  // an order is made
                orderMadeList.add(order);
                MOVE_LEFT -= 1;
            } else {  // if the drone doesn't have enough move to fulfill the next delivery journey
                if (checkNoFlyZones(currentLocation, APPLETON_TOWER)) {
                    LongLat landmark = getLandMark(currentLocation, APPLETON_TOWER);
                    flightPaths.addAll(travelTo(orderNo, landmark));
                    flightCoordinates.add(currentLocation);
                }
                flightPaths.addAll(travelTo(orderNo, APPLETON_TOWER));
                flightCoordinates.add(currentLocation);
                break;
            }
        }
        Order lastOrder = validOrders.get(validOrders.size() - 1); // use the order number of the last order as the orderNo for drone's return to the Appleton
        if (checkNoFlyZones(currentLocation, APPLETON_TOWER)) {
            LongLat landmark = getLandMark(currentLocation, APPLETON_TOWER);
            flightPaths.addAll(travelTo(lastOrder.orderNO, landmark));
            flightCoordinates.add(currentLocation);
        }
        flightPaths.addAll(travelTo(lastOrder.orderNO, APPLETON_TOWER));
        flightCoordinates.add(currentLocation);
        return new Result(flightCoordinates, orderMadeList, flightPaths);
    }

    /**
     * this method would renew the drone's current location with the given new location
     * this method would also check if the drone's new location is within the confined area
     * if yes, then update location
     * if no, then the program will be terminated
     *
     * @param newLocation the drone's new location
     */
    public void updateLocation(LongLat newLocation) {
        if (newLocation.isConfined()) {  // the drone must always be in the confined area
            currentLocation = newLocation;
        } else {
            System.err.println("The new location is unconfined");
            System.exit(0);
        }

    }

    /**
     * this method would make the drone travel to a location that is close to the given LongLat location
     * by taking a move in the direction between the given location and the current location.
     * after taking a move, the flight path is recorded and the move count need to be reduced by 1 unit.
     *
     * @param orderNo this is the order number of the order the drone is delivering on this path
     * @param ToLoc   this is the location the drone is heading to
     * @return a list of paths made by the drone as it travels from its current location to a location that's
     * close to the given location as a list of FlightPath objects.
     */
    public List<FlightPath> travelTo(String orderNo, LongLat ToLoc) {
        List<FlightPath> flightPaths = new ArrayList<>();
        while (!currentLocation.closeTo(ToLoc)) {
            int angle = currentLocation.getAngle(ToLoc);
            LongLat nextLocation = currentLocation.nextPosition(angle);
            FlightPath flightPath = new FlightPath(orderNo, currentLocation.longitude, currentLocation.latitude, angle, nextLocation.longitude, nextLocation.latitude);
            flightPaths.add(flightPath);
            updateLocation(nextLocation);
            MOVE_LEFT -= 1;
        }
        return flightPaths;
    }

    /**
     * this method would calculate the number of moves required for the drone to complete the order
     * provided as a parameter.
     * the moves also take into account the detour to landmarks if any path in delivering the order
     * cross the no-fly-zones borders.
     *
     * @param order this is the order to count for the moved needed.
     * @return the number of moves needed to complete the order provided.
     */
    public int getRouteMovesCount(Order order) {
        int moves = 0;
        List<LongLat> routeBuilder = new ArrayList<>();
        routeBuilder.add(currentLocation); // the route start from the drone's current location
        W3words w3words = new W3words(webServerPort);
        for (String shop : order.getOrderShops(webServerPort)) {
            routeBuilder.add(w3words.toLongLat(shop));
        }
        routeBuilder.add(w3words.toLongLat(order.deliverTo));
        for (int i = 1; i < routeBuilder.size(); i++) {
            int j = i - 1; // index of the previous location
            LongLat fromLoc = routeBuilder.get(j);
            LongLat ToLoc = routeBuilder.get(i);
            if (checkNoFlyZones(fromLoc, ToLoc)) {
                LongLat landmark = getLandMark(fromLoc, ToLoc);
                moves += fromLoc.getMoves(landmark);
                moves += landmark.getMoves(ToLoc);
            } else {
                moves += fromLoc.getMoves(ToLoc);
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
    public int getMovesToAT(LongLat location) {
        int moves = 0;
        List<LongLat> path = new ArrayList<>();
        path.add(location);
        if (checkNoFlyZones(location, APPLETON_TOWER)) {
            LongLat landmark = getLandMark(location, APPLETON_TOWER);
            path.add(landmark);
        }
        path.add(APPLETON_TOWER);
        for (int i = 1; i < path.size(); i++) {
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
    public Boolean checkNoFlyZones(LongLat startLoc, LongLat finishLoc) {
        Line2D dronePath = new Line2D.Double(startLoc.longitude, startLoc.latitude, finishLoc.longitude, finishLoc.latitude);
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
    public LongLat getLandMark(LongLat startLoc, LongLat finishLoc) {
        HashMap<Double, LongLat> distanceToLMs = new HashMap<>(); // store the distance to each landmark
        ArrayList<Double> distances = new ArrayList<>();

        for (LongLat landmark : landmarks) {
            if (!checkNoFlyZones(startLoc, landmark)) {
                Double distance = startLoc.distanceTo(landmark);
                distanceToLMs.put(distance, landmark);
                distances.add(distance);
            }
        }
        LongLat closetLM = distanceToLMs.get(Collections.min(distances));
        return closetLM;
    }

    public static void main(String[] args) {
        Drone testBot = new Drone("2022-04-15", "9898", "9876");
        LongLat from = new LongLat(-3.191257, 55.945626);
        LongLat to = new LongLat(-3.188512, 55.944036);
        LongLat testLM = testBot.getLandMark(to, from);
        System.out.println(testLM.formatLongLat());
        System.out.println(testBot.checkNoFlyZones(from, testLM));
    }


    // Buildings buildings = new Buildings("9898", "no-fly-zones");

}

