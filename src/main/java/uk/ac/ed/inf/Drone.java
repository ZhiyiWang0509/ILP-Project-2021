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

    public static int moveLeft = 1500; // at the start of the journey the drone have 1500 valid moves
    private final double SINGLE_MOVE = 0.00015;  // a single move of the drone

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494); //the starting and ending point of the drone
    public LongLat currentLocation = appletonTower;  // the drone's initial location is Appleton Tower by definition

    public static final int hover = -999;  // dummy angle used when the drone is hovering, indicating not moving

    public Drone(String date, String webServerPort, String dataBasePort) {
        this.date = date;
        this.webServerPort = webServerPort;
        this.dataBasePort = dataBasePort;
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
        return validOrders;
    }


    // return a Result object as a result of the delivery made by the drone onn the day
    public Result makeDelivery(){
        W3words w3words = new W3words(webServerPort);
        List<LongLat> flightCoordinates = new ArrayList<>();  // the list to store the flight path
        List<Order> orderMadeList = new ArrayList<>();  // store all the orders made
        List<FlightPath> flightPaths = new ArrayList<>();  // store all the flight path took

        List<Order> validOrders = getValidOrders(); // get the list of locations that's valid as an order
        flightCoordinates.add(currentLocation);
        for(Order order : validOrders){
            // first need to check moves required for completing this delivery
            int orderMoves = getRouteMovesCount(order); // the total moves needed for this order
            int returnMoves = getMovesToAT(w3words.toLongLat(order.deliverTo));  // the moves needed to return to Appleton after the order's finished
            if((moveLeft - orderMoves) >= returnMoves){  // check if the drone still have moves left for returning after finishing this order
                Set<String> shops = order.getOrderShops(webServerPort);
                for (String shop : shops){
                    LongLat shopLngLat = w3words.toLongLat(shop);
                    if(checkNoFlyZones(shopLngLat)) {  // if the path to the shop cross the non-fly zone
                        flightPaths.addAll(travelTo(order.orderNO,closestLandMark()));
                        flightCoordinates.add(currentLocation);
                    }
                    flightPaths.addAll(travelTo(order.orderNO,shopLngLat));
                    flightCoordinates.add(currentLocation);
                    moveLeft -= 1; // hover for 1 move
                }
                // after visit all the shops need to visit the delivery address to fulfill the order
                LongLat deliverTo = w3words.toLongLat(order.deliverTo);
                if(checkNoFlyZones(deliverTo)){ // need to check if the route would pass the non-fly zone as well
                    flightPaths.addAll(travelTo(order.orderNO, closestLandMark()));
                    flightCoordinates.add(currentLocation);
                }
                flightPaths.addAll(travelTo(order.orderNO, deliverTo));
                flightCoordinates.add(currentLocation);  // an order is made
                orderMadeList.add(order);
                moveLeft -= 1;
            }else{  // if the drone doesn't have enough move to fulfill the next delivery journey
                if(checkNoFlyZones(appletonTower)){
                    flightPaths.addAll(travelTo(order.orderNO, closestLandMark()));
                    flightCoordinates.add(currentLocation);
                }
                flightPaths.addAll(travelTo(order.orderNO, appletonTower));
                flightCoordinates.add(currentLocation);
                break;
            }
        }
        Order lastOrder = validOrders.get(validOrders.size()-1); // use the order number of the last order as the orderNo for drone's return to the Appleton
        if(checkNoFlyZones(appletonTower)){
            flightPaths.addAll(travelTo(lastOrder.orderNO, closestLandMark()));
            flightCoordinates.add(currentLocation);
        }
        flightPaths.addAll(travelTo(lastOrder.orderNO, appletonTower));
        flightCoordinates.add(currentLocation);
        return new Result(flightCoordinates,orderMadeList,flightPaths);
    }

    // update the drone's current location if the drone made a move
    public void updateLocation(LongLat newLocation){
        currentLocation = newLocation;
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
            moveLeft -= 1;
        }
        return flightPaths;
    }

    // return true if the drone still have moves available for a day
    public boolean checkMoveCount() {
        return moveLeft != 0;
    }

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
            int j = i - 1;
            if(checkNoFlyZones(routeBuilder.get(j), routeBuilder.get(i))){
                routeBuilder.add(i,closestLandMark()); // if the path cross the non-fly zone add the closest landmark in between
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
            path.add(closestLandMark());
        }
        path.add(appletonTower);
        for(int i = 1; i < path.size(); i++){
            int j = i - 1;
            moves += path.get(j).getMoves(path.get(i));
        }
        return moves;
    }

    // check if the drone's route pass the no-fly zone return false if don't cross
    public Boolean checkNoFlyZones(LongLat destination){
        List<List<LongLat>> noFlyZones = new Buildings(webServerPort,"no-fly-zones").getNoFlyCoordinates();
        boolean isCrossed = false; // assume the drone's route doesn't cross the no-fly zones;
        for(List<LongLat> border : noFlyZones){
            int length = border.size();
            for(int i = 1; i < length; i++){
                int j = i - 1;
                isCrossed = isCrossed || checkIntersect(destination, border.get(j), border.get(i));
            }
        }
        return isCrossed;
    }

    // method override
    public Boolean checkNoFlyZones(LongLat current, LongLat destination){
        List<List<LongLat>> noFlyZones = new Buildings(webServerPort,"no-fly-zones").getNoFlyCoordinates();
        boolean isCrossed = false; // assume the drone's route doesn't cross the no-fly zones;
        for(List<LongLat> zone : noFlyZones){
            int length = zone.size();
            for(int i = 1; i < length; i++){
                int j = i - 1;
                isCrossed = isCrossed || checkIntersect(current, destination, zone.get(j), zone.get(i));
            }
        }
        return isCrossed;
    }


    // check if the two lines intersect, return true if they intersect
    public Boolean checkIntersect(LongLat current, LongLat destination, LongLat noFlyBorder1, LongLat noFlyBorder2){
        Line2D dronePath = new Line2D.Double(current.longitude,current.latitude,destination.longitude,destination.latitude);
        Line2D noFlyBorder = new Line2D.Double(noFlyBorder1.longitude,noFlyBorder1.latitude,noFlyBorder2.longitude,noFlyBorder2.latitude);
        return dronePath.intersectsLine(noFlyBorder);
    }

    // method override
    public Boolean checkIntersect(LongLat destination, LongLat noFlyBorder1, LongLat noFlyBorder2){
        Line2D dronePath = new Line2D.Double(currentLocation.longitude,currentLocation.latitude,destination.longitude,destination.latitude);
        Line2D noFlyBorder = new Line2D.Double(noFlyBorder1.longitude,noFlyBorder1.latitude,noFlyBorder2.longitude,noFlyBorder2.latitude);
        return dronePath.intersectsLine(noFlyBorder);
    }

    // return the landmark that's closest to the current drone's location
    public LongLat closestLandMark(){
        Buildings building = new Buildings( webServerPort,"landmarks");
        List<LongLat> landmarks = building.getLandMarks();
        HashMap<Double,LongLat> distanceComparator = new HashMap<>();
        ArrayList<Double> distances = new ArrayList<>();
        for(LongLat landmark : landmarks){
            Double distance = appletonTower.distanceTo(landmark);
            distanceComparator.put(distance,landmark);
            distances.add(distance);
        }
        return distanceComparator.get(Collections.min(distances));
    }
}
