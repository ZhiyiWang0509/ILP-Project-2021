package uk.ac.ed.inf;

/**
 * this class is mainly used as a parser for json files stored in the 'words' folder on the webserver.
 * fields name in this class match exactly to the attribute names in the json file.
 *
 */
public class W3wordDetails {
    /**
     * this is the country of the location located in
     */
    private String country;
    /**
     * this is to match the 'square' field in the json
     */
    private Square square;
    /**
     * this is the nearest city relative to the location
     */
    private String nearestPlace;
    /**
     * this is the coordinates of the location as an Coordinate object
     */
    public Coordinate coordinates;
    /**
     * this is the w3words format of the location
     */
    private String words;
    /**
     * this is to match the 'language' field in json
     */
    private String language;
    /**
     * this is a representation of the location on 'https://w3w.co' website
     */
    private String map;
    /**
     * this is an inner class created to match the field: coordinates on the json file
     */
    private static class Coordinate {
        Double lng;
        Double lat;
    }
    /**
     * this is an inner class to match the field square on the json file
     */
    private static class Square {
        Coordinate southwest;
        Coordinate northeast;
    }
    /**
     * this method would return the LongLat location corresponding to the w3words location
     * as a LongLat object
     *
     * @return the location corresponding to te w3words location as an LongLat object
     */
    public LongLat getLocation(){
        return new LongLat(coordinates.lng, coordinates.lat);
    }


}
