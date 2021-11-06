package uk.ac.ed.inf;

import java.util.List;

// an instance of this class stores all the result needed
public class Result {
    public List<LongLat> geoJsonList;
    public List<Order> orderDataBase;
    public List<FlightPath> flightPathDataBase;

    public Result(List<LongLat> geoJsonList, List<Order> orderDataBase, List<FlightPath> flightPathDataBase) {
        this.geoJsonList = geoJsonList;
        this.orderDataBase = orderDataBase;
        this.flightPathDataBase = flightPathDataBase;
    }
}
