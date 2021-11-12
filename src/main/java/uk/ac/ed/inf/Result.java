package uk.ac.ed.inf;

import java.util.List;

/**
 * A Result contains all the information needed as the output for
 * this application.
 *
 */
public class Result {
    /**
     * this field store a list of LongLat object
     * this would be parsed in to GeoJson file in the application
     */
    public List<LongLat> geoJsonList;
    /**
     * this field store a list of Order object
     * this would be stored in to the 'deliveries' database created in the application
     */
    public List<Order> orderDataBase;
    /**
     * this field store a list of FlightPath object
     * this would be stored in to the 'filghtpath' database created in the application
     */
    public List<FlightPath> flightPathDataBase;

    /**
     * This is the constructor of the Result class
     *
     * @param geoJsonList a list of coordinates as LongLat objects which the drone travelled to during it's delivery
     * @param orderDataBase a list of orders as Order objects which are the orders made by the drone during the day
     * @param flightPathDataBase a list of flight path as FlightPath object which are every move made by the drone during the day
     */
    public Result(List<LongLat> geoJsonList, List<Order> orderDataBase, List<FlightPath> flightPathDataBase) {
        this.geoJsonList = geoJsonList;
        this.orderDataBase = orderDataBase;
        this.flightPathDataBase = flightPathDataBase;
    }
}
