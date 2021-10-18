package uk.ac.ed.inf;

import java.util.List;

/* an instance of this class act as a json parser for the json content related to a w3words details json file
 * the local fields here matched the corresponding keys in the json file
 */
public class Location {
    String country;
    Square square;
    String nearestPlace;
    Coordinate coordinates;
    String words;
    String language;
    String map;

    public static class Coordinate {
        Double lng;
        Double lat;
    }
    public static class Square {
        Coordinate southwest;
        Coordinate northeast;
    }

    // return the geo-location according to the w3words location encode.
    public LongLat getLocation(){
        return new LongLat(coordinates.lng, coordinates.lat);
    }


}
