package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.*;

/* an instance of this class represent a drone object
 information like date, web port and database port need to be provided to the constructor
 the class will output the information needed for the final flightPath GeoJson file
 as well as parse the data needed for creating two databases.
 */
public class Drone {
    public String date;  // the date of the delivery
    public String webServerPort; // the port of the webserver to access
    public String dataBasePort; //  the port of the database to access

    public static int MOVE_LEFT = 1500; // at the start of the journey the drone have 1500 valid moves
    private final double SINGLE_MOVE = 0.00015;  // a single move of the drone

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494); //the starting and ending point of the drone
    public LongLat currentLocation = appletonTower;  // the drone's initial location is Appleton Tower by definition

    private final List<List<LongLat>> noFlyZones;
    private final List<LongLat> landmarks;

    public Drone(String date, String webServerPort, String dataBasePort) {
        this.date = date;
        this.webServerPort = webServerPort;
        this.dataBasePort = dataBasePort;
        // the file name of the file store no-fly-zones information
        String noFlyFileName = "no-fly-zones";
        this.noFlyZones = new Buildings(webServerPort, noFlyFileName).getNoFlyCoordinates();
        String landmarksFileName = "landmarks";
        this.landmarks = new Buildings( webServerPort, landmarksFileName).getLandMarks();
    }

    // return the locations of the valid orders the drone may visit as a linked hashmap of pairs: deliverLocation: [shopLocations]
    public List<Order> getValidOrders(){
        Database ordersDb = new Database(dataBasePort);
        ArrayList<Order> orders = ordersDb.getOrders(date);
        List<Order> validOrders = new ArrayList<>();
        for(Order order : orders){
            // check the number of items in the order, each order can take up to 4 items
            // and the shops number of shops the drone need to visit, up to 2 shops
            if(order.getItemList().size() <= 4 && order.getOrderShops(webServerPort).size() <= 2){
                validOrders.add(order);
            }
        }
        if(validOrders.isEmpty()){
            System.out.println("There's no valid order to deliver today");
            System.exit(1);
        }
        return validOrders;
    }


    // return a Result object as a result of the delivery made by the drone onn the day
    public Result makeDelivery(){
        W3words w3words = new W3words(webServerPort);
        List<LongLat> flightCoordinates = new ArrayList<>();  // the list to store the coordinates visited by the drone
        List<Order> orderMadeList = new ArrayList<>();  // store all the orders made
        List<FlightPath> flightPaths = new ArrayList<>();  // store all the flight path took

        List<Order> validOrders = getValidOrders(); // get the list of locations that's valid as an order
        flightCoordinates.add(currentLocation);
        for(Order order : validOrders){
            LongLat deliverTo = w3words.toLongLat(order.deliverTo);
            // first need to check moves required for completing this delivery
            int orderMoves = getRouteMovesCount(order); // the total moves needed for this order
            int returnMoves = getMovesToAT(deliverTo);  // the moves needed to return to Appleton after the order's finished
            if((MOVE_LEFT - orderMoves) >= returnMoves){  // check if the drone still have moves left for returning after finishing this order
                Set<String> shops = order.getOrderShops(webServerPort);
                for (String shop : shops){
                    LongLat shopLngLat = w3words.toLongLat(shop);
                    if(checkNoFlyZones(currentLocation,shopLngLat)) {  // if the path to the shop cross the non-fly zone
                        flightPaths.addAll(travelTo(order.orderNO, getLandMark(currentLocation,shopLngLat)));
                        flightCoordinates.add(currentLocation);
                    }
                    flightPaths.addAll(travelTo(order.orderNO,shopLngLat));
                    flightCoordinates.add(currentLocation);
                    MOVE_LEFT -= 1; // hover for 1 move
                }
                // after visit all the shops need to visit the delivery address to fulfill the order
                if(checkNoFlyZones(currentLocation, deliverTo)){ // need to check if the route would pass the non-fly zone as well
                    flightPaths.addAll(travelTo(order.orderNO, getLandMark(currentLocation,deliverTo)));
                    flightCoordinates.add(currentLocation);
                }
                flightPaths.addAll(travelTo(order.orderNO, deliverTo));
                flightCoordinates.add(currentLocation);  // an order is made
                orderMadeList.add(order);
                MOVE_LEFT -= 1;
            } else {  // if the drone doesn't have enough move to fulfill the next delivery journey
                if(checkNoFlyZones(currentLocation, appletonTower)){
                    flightPaths.addAll(travelTo(order.orderNO, getLandMark(currentLocation,appletonTower)));
                    flightCoordinates.add(currentLocation);
                }
                flightPaths.addAll(travelTo(order.orderNO, appletonTower));
                flightCoordinates.add(currentLocation);
                break;
            }
        }
        Order lastOrder = validOrders.get(validOrders.size()-1); // use the order number of the last order as the orderNo for drone's return to the Appleton
        if(checkNoFlyZones(currentLocation, appletonTower)){
            flightPaths.addAll(travelTo(lastOrder.orderNO, getLandMark(currentLocation,appletonTower)));
            flightCoordinates.add(currentLocation);
        }
        flightPaths.addAll(travelTo(lastOrder.orderNO, appletonTower));
        flightCoordinates.add(currentLocation);
        return new Result(flightCoordinates,orderMadeList,flightPaths);
    }

    // update the drone's current location if the drone made a move
    public void updateLocation(LongLat newLocation){
        if(newLocation.isConfined()){  // the drone must always be in the confined area
            currentLocation = newLocation;
        }else{
            System.exit(0);
        }

    }

    // return a list recording every move made by the drone on its way to the given location under the given order
    public List<FlightPath> travelTo(String orderNo, LongLat location){
        List<FlightPath> flightPaths = new ArrayList<>();
        while(!currentLocation.closeTo(location)){
            int angle = currentLocation.getAngle(location);
            LongLat nextLocation = currentLocation.nextPosition(angle);
            FlightPath flightPath = new FlightPath(orderNo,currentLocation.longitude, currentLocation.latitude,angle, nextLocation.longitude, nextLocation.latitude);
            flightPaths.add(flightPath);
            updateLocation(nextLocation);
            MOVE_LEFT -= 1;
        }
        return flightPaths;
    }


    // return the moves needed to deliver the input order
    public int getRouteMovesCount(Order order){
        int moves = 0;
        List<LongLat> routeBuilder = new ArrayList<>();
        routeBuilder.add(currentLocation); // the route start from the drone's current location
        W3words w3words = new W3words(webServerPort);
        for(String shop : order.getOrderShops(webServerPort)){
            routeBuilder.add(w3words.toLongLat(shop));
        }
        routeBuilder.add(w3words.toLongLat(order.deliverTo));
        for(int i = 1; i < routeBuilder.size(); i++){ // iterate through the route to check if any of the path cross the non-fly zone
            int j = i - 1; // index of the previous location
            if(checkNoFlyZones(routeBuilder.get(j), routeBuilder.get(i))){
                routeBuilder.add(i, getLandMark(routeBuilder.get(j),routeBuilder.get(i))); // if the path cross the non-fly zone add the closest landmark in between
            }
            moves += routeBuilder.get(j).getMoves(routeBuilder.get(i));
        }
        return moves;
    }

    // return the moves needed to travel to Appleton Tower from the given location
    public int getMovesToAT(LongLat location){
        int moves = 0;
        List<LongLat> path = new ArrayList<>();
        path.add(location);
        if(checkNoFlyZones(location,appletonTower)){
            path.add(getLandMark(location,appletonTower));
        }
        path.add(appletonTower);
        for(int i = 1; i < path.size(); i++){
            int j = i - 1;
            moves += path.get(j).getMoves(path.get(i));
        }
        return moves;
    }


    // method override
    public Boolean checkNoFlyZones(LongLat location1, LongLat location2){
       // String noFlyFileName = "no-fly-zones"; // the file name of the file store no-fly-zones information
       // List<List<LongLat>> noFlyZones = new Buildings(webServerPort,noFlyFileName).getNoFlyCoordinates();
        boolean isCrossed = false; // assume the drone's route doesn't cross the no-fly zones;
        for(List<LongLat> zone : noFlyZones){
            int length = zone.size();
            for(int i = 1; i < length; i++){
                int j = i - 1;
                isCrossed = isCrossed || checkIntersect(location1, location2, zone.get(j), zone.get(i));
            }
        }
        return isCrossed;
    }


    // check if the two lines intersect, return true if they intersect
    public Boolean checkIntersect(LongLat location1, LongLat location2, LongLat noFlyBorder1, LongLat noFlyBorder2){
        Line2D dronePath = new Line2D.Double(location1.longitude,location1.latitude,location2.longitude,location2.latitude);
        Line2D noFlyBorder = new Line2D.Double(noFlyBorder1.longitude,noFlyBorder1.latitude,noFlyBorder2.longitude,noFlyBorder2.latitude);
        return dronePath.intersectsLine(noFlyBorder);
    }


    // get the landmark the drone need to travel to if the path from startLoc to finishLoc cross the no-fly zone
    public LongLat getLandMark(LongLat startLoc, LongLat finishLoc){
       // String landmarksFileName = "landmarks";
       // Buildings building = new Buildings( webServerPort,landmarksFileName);
       // List<LongLat> landmarks = building.getLandMarks();
        HashMap<Double,LongLat> distanceToLMs = new HashMap<>(); // store the distance to each landmark
        ArrayList<Double> distances = new ArrayList<>();

        for(LongLat landmark : landmarks){
            if(!checkNoFlyZones(startLoc,landmark) && !checkNoFlyZones(landmark,finishLoc)){  // check if the drone would cross the non-fly as it travel to the landmark
                Double distance = startLoc.distanceTo(landmark);
                distanceToLMs.put(distance,landmark);
                distances.add(distance);
            }
        }
        LongLat closetLM = distanceToLMs.get(Collections.min(distances));
        return closetLM;
    }

    public static void main(String args[]){
        Drone testBot = new Drone("2022-04-15","9898","9876");
        LongLat from = new LongLat(-3.191257,55.945626);
        LongLat to = new LongLat(-3.188512,55.944036);
        LongLat testLM = testBot.getLandMark(to,from);
        System.out.println(testLM.formatLongLat());
    }
}
