package uk.ac.ed.inf;

import java.lang.Math;

/**
 * this class is used to define a coordinate on a geographical map in this application
 */
public class LongLat {

    /**
     * this is the longitude of the coordinate
     */
    private final double longitude;
    /**
     * this is the latitude of the coordinate
     */
    private final double latitude;
    /**
     * this is a single move made by the drone in degree
     */
    private final double SINGLE_MOVE = 0.00015;

    /**
     * this is the constructor of the LongLat class
     *
     * @param longitude this is the longitude of the location
     * @param latitude this is the latitude of the location
     */
    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * this is the longitude of the coordinate
     * @return the longitude of the location
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * this is the latitude of the coordinate
     * @return the latitude of the location
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * this method check if the location defined is within the confined area
     * the confined area is a Square enclosed by the four corners: MAX_LONGITUDE, MIN_LONGITUDE,
     * MAX_LATITUDE AND MIN_LONGITUDE.
     *
     * @return true if the location is within the area and false otherwise.
     */
    public boolean isConfined() {
        double MAX_LATITUDE = 55.946233;
        double MIN_LATITUDE = 55.942617;
        double MAX_LONGITUDE = -3.184319;
        double MIN_LONGITUDE = -3.192473;
        boolean latCheck = (latitude < MAX_LATITUDE) && (latitude > MIN_LATITUDE);
        boolean longCheck = (longitude > MIN_LONGITUDE) && (longitude < MAX_LONGITUDE);
        return (latCheck && longCheck);
    }

    /**
     * this method calculate the Pythagorean distance between this LongLat location and the location provided
     *
     * @param location this is the other coordinate which form the line segment with this.LongLat
     * @return the distance between this LongLat location and the given location.
     */
    public double distanceTo(LongLat location) {
        return (Math.sqrt(Math.pow((latitude - location.latitude), 2) + Math.pow((longitude - location.longitude), 2)));
    }

    /**
     * this method check if this location is within a move distance to the given location
     *
     * @param location this is the location we want to compare-to using the local location
     * @return true if this location is within a move to the given location.
     */
    public boolean closeTo(LongLat location) {
        return (distanceTo(location) <= SINGLE_MOVE);
    }

    /**
     * this method calculate drone's next location after taking a move in the direction specified as the angle provided
     *
     * @param angle this is the direction the drone is flying to
     * @return the position of the drone after taken a move in the given direction
     */
    public LongLat nextPosition(int angle) {
        double newLongitude = longitude;
        double newLatitude = latitude;
        if ((angle % 10 != 0) || (angle < 0) || (angle > 350)) {
            System.out.println("The given input angle is a invalid direction.");
            System.exit(0);
        }
        else {
            double diffLongitude = SINGLE_MOVE * Math.cos(Math.toRadians(angle));
            double diffLatitude = SINGLE_MOVE * Math.sin(Math.toRadians(angle));
            newLatitude += diffLatitude;
            newLongitude += diffLongitude;
        }
        return new LongLat(newLongitude, newLatitude);
    }


    /**
     * this method would calculate the angle between this LongLat location and the given LongLat location, which
     * indicate the direction of the drone's move
     *
     * @param destination this is the location the drone is heading from the local location
     * @return the angle between the given location and this LongLat location
     */
    public int getAngle(LongLat destination){
        double dY = Math.abs(destination.latitude - latitude);
        double dX = Math.abs(destination.longitude - longitude);
        // the drone is heading North
        if(dX == 0 && destination.latitude > latitude){
            return 90;
        }
        // the drone is heading South
        else if(dX == 0 && destination.latitude < latitude){
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
        // the drone is flying northeast
        if(destination.longitude>longitude && destination.latitude>latitude){
            return degree;
        }
        // the drone is flying northwest
        else if(destination.longitude<longitude && destination.latitude>latitude){
            return (180-degree);
        }
        // the drone is flying southwest
        else if(destination.longitude<longitude && destination.latitude<latitude){
            return (180+degree);
        }
        // the drone is flying southeast
        else{
            return (350-degree);
        }
    }

    /**
     * this method calculate the number of moves the drone needs to travel from this location to the given location.
     *
     * @param destination this is the location the drone is travelling to
     * @return a count of the moves needed to fly from this location to the given destination
     */
    public double getMoves(LongLat destination){
        double distance = distanceTo(destination);
        return Math.round(distance/SINGLE_MOVE);
    }



}
