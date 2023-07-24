package uk.ac.ed.inf;

/**
 * this class is used as a GSON parser for files located in the 'words' folder on the webserver.
 *
 */
public class W3wordDetails {
    /**
     * this is the country of the location
     */
    private final String country;
    /**
     * this is the 'square' field in the JSON string
     */
    private final Square square;
    /**
     * this is the nearest city relative to the location
     */
    private final String nearestPlace;
    /**
     * this is the coordinates of the location as a Coordinate object
     */
    private final Coordinate coordinates;
    /**
     * this is the w3words format of the location
     */
    private final String words;
    /**
     * this is the 'language' field in the JSON string
     */
    private final String language;
    /**
     * this is a representation of the location on 'https://w3w.co' website
     */
    private final String map;
    /**
     * this is an inner class created for the field: coordinates in the JSON string
     */
    private static class Coordinate {
        Double lng;
        Double lat;
    }
    /**
     * this is the inner class for the field "square" in the JSON string
     */
    private static class Square {
        Coordinate southwest;
        Coordinate northeast;
    }

    public W3wordDetails(String country, Square square, String nearestPlace, Coordinate coordinates, String words, String language, String map) {
        this.country = country;
        this.square = square;
        this.nearestPlace = nearestPlace;
        this.coordinates = coordinates;
        this.words = words;
        this.language = language;
        this.map = map;
    }

    /**
     * this method is used to get the location's coordinate
     *
     * @return the coordinate corresponding to the w3words location as an LongLat object
     */
    public LongLat getLocation(){
        return new LongLat(coordinates.lng, coordinates.lat);
    }


}
