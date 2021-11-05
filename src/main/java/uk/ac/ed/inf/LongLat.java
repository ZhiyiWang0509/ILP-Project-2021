package uk.ac.ed.inf;

/* an instance of this class represents a location of the drone in (longitude, latitude)
 * it also contains methods: isConfined, distanceTo, closeTo and nextPosition, which are all related to the drone's location.
 * to create an instance, the constructor need two doubles: one is longitude, the other is latitude
 */

import java.lang.Math;
import java.text.DecimalFormat;
import java.util.*;
import org.javatuples.Pair;

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

    //  check if the given location is close to drone's current location
    //  two locations are said to be close to each other if the distance in between is less than a single move
    public boolean closeTo(LongLat location) {
        return (distanceTo(location) <= SINGLE_MOVE);
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


    // return the angle between the two location in int degree and modify the angle to the nearest multiple of 10
    public int getAngle(LongLat location){
        double dY = Math.abs(location.latitude - latitude);
        double dX = Math.abs(location.longitude - longitude);
        if(dX == 0 && location.latitude > latitude){  // the drone is heading North
            return 90;
        }else if(dX == 0 && location.latitude < latitude){
            return 270;
        }
        double arcAngle = Math.atan(dY/dX);
        int degree = (int) Math.toDegrees(arcAngle);
        int remainder = degree % 10;
        if (remainder < 5){
            degree -= remainder;
        }
        else{
            degree += (10 - remainder);
        }
        if(location.longitude>longitude && location.latitude>latitude){  // the drone is flying northeast
            return degree;
        }else if(location.longitude<longitude && location.latitude>latitude){  // the drone is flying northwest
            return (180-degree);
        }else if(location.longitude<longitude && location.latitude<latitude){  // the drone is flying southwest
            return (180+degree);
        }else{  // the drone is flying southeast
            return (360-degree);
        }
    }

    public double getMoves(LongLat destination){  // return the move count between the current location and the give location
        int move = 0;
        double distance = distanceTo(destination);
        return Math.round(distance/SINGLE_MOVE);
    }

    // return the LongLat object in a more presentable way for human inspection
    public Pair<Double, Double>formatLongLat(){
        return new Pair<>(longitude, latitude);
    }

}
