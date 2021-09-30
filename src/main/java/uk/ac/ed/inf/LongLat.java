package uk.ac.ed.inf;
import java.lang.Math;

public class LongLat {
    // two public fields in the class
    public double longitude;
    public double latitude;

    /* the four corners of the confined area
       moves beyond the four locations are not allowed.

    public final LongLat forrestHill = new LongLat(-3.192473,55.946233);
    public final LongLat kfc = new LongLat(-3.184319,55.946233);
    public final LongLat topMeadows = new LongLat(-3.192473,55.942617);
    public final LongLat buccleuchBusStop = new LongLat(-3.184319,55.942617);
    */

    // the constructor of the class LongLat has longitude and latitude as inputs.
    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // method to check if the drone is within the confined area
    //
    public boolean isConfined() {
        double max_latitude = 55.946233;
        double min_latitude = 55.942617;
        double max_longitude = -3.184319;
        double min_longitude = -3.192473;
        boolean latCheck = (latitude < max_latitude) && (latitude > min_latitude);
        boolean longCheck = (longitude > min_longitude) && (longitude < max_longitude);
        return (latCheck && longCheck);
    }

    // method to calculate the Pythagorean distance between current point and the given location
    public double distanceTo(LongLat location) {
        return (Math.sqrt(Math.pow((latitude - location.latitude), 2) + Math.pow((longitude - location.longitude), 2)));
    }

    // method to assess whether the given location is close to drone's current location
    // we defined distance less than 0.00015 degree as close distance between the two locations
    public boolean closeTo(LongLat location) {
        return (distanceTo(location) < 0.00015);
    }

    // return the next location for the drone to fly.
    // need to first check if the given angle is valid, which is a multiple of 10, in a range [0,350].
    public LongLat nextPosition(int i) {
        double move = 0.00015;  // a single move of the drone
        double new_longitude = longitude;
        double new_latitude = latitude;
        if ((i % 10 != 0) || (i < 0) || (i > 350)){
            System.out.println("The given input angle is a invalid direction.");}
        else{
            double diff_longitude = move * Math.cos(Math.toRadians(i));  // the distance to the next position in longitude
            double diff_latitude = move * Math.sin(Math.toRadians(i));  // the distance to the next position in latitude
            new_latitude += diff_latitude;
            new_longitude += diff_longitude;
        }
        return new LongLat(new_longitude, new_latitude);
    }
}
