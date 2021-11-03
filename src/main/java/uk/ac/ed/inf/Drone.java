package uk.ac.ed.inf;

import org.javatuples.Pair;

import java.util.*;

/* an instance of this class represent a drone object

 */
public class Drone {
    public String date;  // the date of the delivery
    public String webServerPort; // the port of the webserver to access
    public String dataBasePort; //  the port of the database to access
    public static int moveCount;  // count the moves made by the drone on a single day
    public static final int MOVES_LIMIT = 1500; // the maximum moves allowed for a drone on a single day
    private final double SINGLE_MOVE = 0.00015;  // a single move of the drone

    private static final String ordersDB = "orders"; // the name of one of the database need to access
    private static final String orderDetailsDB = "orderDetails"; // the name of the other database need to access

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494); //the starting and ending point of the drone
    public LongLat currentLocation = appletonTower;  // the drone's initial location is Appleton Tower by definition

    public static int fly; // an angle indicate the direction the drone will fly to in the next move
    public static final int hover = -999;  // dummy angle used when the drone is hovering, indicating not moving

    public Drone(String date, String webServerPort, String dataBasePort) {
        this.date = date;
        this.webServerPort = webServerPort;
        this.dataBasePort = dataBasePort;
    }

    // return the locations the drone will travel to as a linked hashmap of pairs: deliverLocation: [shopLocations]
    // each location is in the w3words format
    public LinkedHashMap<String, Set<String>> getLocations(){
        Database ordersDb = new Database(dataBasePort,ordersDB);
        Database detailsDb = new Database(dataBasePort, orderDetailsDB);
        Menus menus = new Menus(webServerPort);
        ArrayList<Database.Order> orders = ordersDb.getOrders(date);
        LinkedHashMap<String, Set<String>> locations = new LinkedHashMap<>();
        for(Database.Order order : orders){
            String deliverTo = order.getDeliverTo();
            Set<String> shopLocations = new HashSet<>();
            List<String> orderDetails = detailsDb.getOrderDetails(order.getOrderNO()); // get the orders details of the day
            if(orderDetails.size() <= 4){  // check the number of items in the order, each order can take up to 4 items
                for(String item : orderDetails){
                    String location = menus.getItemRestaurant(item);  // need to catch NullPointerException if the item isn't found?
                    shopLocations.add(location);
                }
                if (shopLocations.size() <= 2){  // check the shops the drone would need to travel (max: 2 shops)
                    locations.put(deliverTo,shopLocations);
                }
            }
        }
        return locations;
    }


    // return the coordinates the drone travelled as a list of LongLat
    // note the drone have not make actual move here
    // first iterate through the shop positions then the deliverTo location,
    // need to check the distance to Appleton with the moves left, to ensure the drone have moves to return
    // pick the shop that's closer to the current location as the first to go
    // need to check whether the path cross the no-fly zones, if so, need to fly to the closest landmark
    // recalculate the route involved the detour to landmark if needed
    // as the path been set, calculate the angle in between
    // pass the angle to nextPosition to: 1. take count of the drone's moveCount 2. update drone's currentPosition
    // once reach the position (isReached) hover: pass -999 to the nextPosition method
    // after the shop locations, fly to deliverTo location
    // once deliverTo location is reached, write in to database
    // move on to the next order if conditions apply
    public List<LongLat> getFlightPath(){
        List<LongLat> flightPath = new ArrayList<>();
        LinkedHashMap<String, Set<String>> locations = getLocations(); // get the list of locations that's valid as an order
        Buildings buildings = new Buildings(webServerPort,"landmarks");
        List<LongLat> landmarks = buildings.getLandMarks(); // get the list of landmarks
        Set<String> deliverToList = locations.keySet();
        W3words w3words = new W3words(webServerPort);  // need to decode the addresses to LongLat in this method
        for(String deliverTo : deliverToList){
            LongLat deliverLngLat = w3words.toLongLat(deliverTo);  // get the delivery address as a LongLat
            List<LongLat> deliverRoute = new ArrayList<>(); // to store the flight path for this delivery
            for(String shop :locations.get(deliverTo)){  // iterate through the shop list
                LongLat shopLngLat = w3words.toLongLat(shop);
                double distanceToShop = currentLocation.distanceTo(shopLngLat); // the distance from the current location to the shop
                if(shopLngLat.isConfined() && checkNoFlyZones(shopLngLat)){
                    deliverRoute.add(shopLngLat); // if the shop can be travelled directly, simply add it in the list
                } else{
                    List<Double> distanceToLandMarks = (List<Double>) landmarks.stream().map(x -> currentLocation.distanceTo(x));
                }

            }
        }
        return flightPath;
    }


    // update the drone's current location if the drone made a move
    public void updateLocation(LongLat newLocation){
        currentLocation = newLocation;
    }

    // return true if the drone still have moves available for a day
    public boolean checkMoveCount() {
        return moveCount < MOVES_LIMIT;
    }


    // check if the drone's route pass the no-fly zone
    public Boolean checkNoFlyZones(LongLat destination){
        // first need to get the line details of the line segment of the route from the drone's current location to its destination
        Pair<Double, Double> route = currentLocation.getLineDetails(destination);
        List<List<LongLat>> noFlyZones = new Buildings(webServerPort,"no-fly-zones").getNoFlyCoordinates();
        boolean isCrossed = false; // assume the drone's route doesn't cross the no-fly zones;
        for(List<LongLat> zone : noFlyZones){
            int length = zone.size();
            for(int i = 1; i < length; i++){
                int j = i - 1;
                isCrossed = isCrossed || checkIntersect(destination, zone.get(j), zone.get(i));
            }
        }
        return isCrossed;
    }

    // check if the two lines intersect, return true if they intersect
    public Boolean checkIntersect(LongLat droneDestination, LongLat noFlyBorder1, LongLat noFlyBorder2){
        Pair<Double, Double> route = currentLocation.getLineDetails(droneDestination);
        Pair<Double, Double> border = noFlyBorder1.getLineDetails(noFlyBorder2);
        double intersectX = (border.getValue1() - route.getValue1())/(route.getValue0() - border.getValue0());
        boolean isParallel = route.getValue0().equals(border.getValue0()); // return true if the borders are parallel
        boolean isIntersect = (intersectX >= Math.max(Math.min(currentLocation.longitude, droneDestination.longitude), //return true if the two segment intersect
                                Math.min(noFlyBorder1.longitude, noFlyBorder2.longitude)) &
                                intersectX <= Math.min(Math.max(currentLocation.longitude, droneDestination.longitude),
                                Math.max(noFlyBorder1.longitude, noFlyBorder2.longitude)));
        return (!isParallel) & isIntersect;
    }


    // need a method to determine whether the drone need to fly or hover at its current location



}
