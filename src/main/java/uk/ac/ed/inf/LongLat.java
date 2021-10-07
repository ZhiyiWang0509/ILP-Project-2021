package uk.ac.ed.inf;
import java.lang.Math;

/* an instance of this class represents a location of the drone in (longitude, latitude)
 * it also contains relevant methods related to a drone's location
 * to create an instance, the constructor need two doubles: one is longitude, the other is latitude
 */

public class LongLat {

    public double longitude;  // the longitude of the location
    public double latitude;  // the latitude of the location
    private final double SINGLE_MOVE = 0.00015;  // a single move of the drone

    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // check if the drone is within the confined area
    public boolean isConfined() {
        double MAX_LATITUDE = 55.946233;  // the latitude for KFC and Forest Hill
        double MIN_LATITUDE = 55.942617;  // the latitude for Buccleuch St bus stop and Top of Meadows
        double MAX_LONGITUDE = -3.184319;  // the longitude for KFC and Buccleuch St bus stop
        double MIN_LONGITUDE = -3.192473;  // the longitude for Forest Hill and Top of Meadows
        boolean latCheck = (latitude < MAX_LATITUDE) && (latitude > MIN_LATITUDE);
        boolean longCheck = (longitude > MIN_LONGITUDE) && (longitude < MAX_LONGITUDE);
        return (latCheck && longCheck);
    }

    // return the Pythagorean distance between current and the given location
    public double distanceTo(LongLat location) {
        return (Math.sqrt(Math.pow((latitude - location.latitude), 2) + Math.pow((longitude - location.longitude), 2)));
    }

    /*  check if the given location is close to drone's current location
     *  two locations are said to be close to each other if the distance between them is less than a single move
     */
    public boolean closeTo(LongLat location) {
        return (distanceTo(location) < SINGLE_MOVE);
    }

    // return the next location for the drone to fly
    public LongLat nextPosition(int i) {
        double newLongitude = longitude;
        double newLatitude = latitude;
        if ((i % 10 != 0) || (i < 0) || (i > 350)) {  // check if the given angle is valid: the angle should be a multiple of 10 and within range (0,350)
            System.out.println("The given input angle is a invalid direction.");
        }
        else {
            double diffLongitude = SINGLE_MOVE * Math.cos(Math.toRadians(i));  // the distance to the next position in longitude
            double diffLatitude = SINGLE_MOVE * Math.sin(Math.toRadians(i));  // the distance to the next position in latitude
            newLatitude += diffLatitude;
            newLongitude += diffLongitude;
        }
        return new LongLat(newLongitude, newLatitude);
    }
}
