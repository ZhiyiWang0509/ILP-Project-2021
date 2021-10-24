package uk.ac.ed.inf;

import org.javatuples.Pair;

import java.util.List;

/* an instance of this class represent a drone object

 */
public class Drone {
    public static int moves;  // count the moves made by the drone on a single day
    public static final int MOVES_LIMIT = 1500; // the maximum moves allowed for a drone on a single day
    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494); //the starting and ending point of the drone
    public static int fly; // an angle indicate the direction the drone will fly to in the next move
    public static final int hover = -999;  // dummy angle used when the drone is hovering, indicating not moving
    public LongLat currentLocation = appletonTower;  // the drone's initial location is Appleton Tower by definition

    // update the drone's current location if the drone made a move
    public void updateLocation(LongLat newLocation){
        currentLocation = newLocation;
    }

    // return true if the drone still have moves available for a day
    public boolean checkMoveCount() {
        return moves < MOVES_LIMIT;
    }


    // check if the drone's route pass the no-fly zone
    public Boolean checkNoFlys(LongLat destination){
        // first need to get the line details of the line segment of the route from the drone's current location to its destination
        Pair<Double, Double> route = currentLocation.getLineDetails(destination);
        List<List<LongLat>> noFlyZones = new Buildings("localhost", "80").getNoFlyCoordinates();
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

    // check if the two lines intersect, return true if they intersect, otherwise false.
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
