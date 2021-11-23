package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    private List<LongLat> geoJsonList;
    /**
     * this field store a list of Order object
     * this would be stored in to the 'deliveries' database created in the application
     */
    private List<Order> orderDataBase;
    /**
     * this field store a list of FlightPath object
     * this would be stored in to the 'filghtpath' database created in the application
     */
    private List<FlightPath> flightPathDataBase;

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

    /**
     * this method would export the results from the application to relevant locations,
     * it would export a geojson file for the drone's flight path and write two tables
     * in the database, one called "deliveries" the other called "flightpath"
     * this method require information of the day the delivery is made in order to name
     * the geojson file, and information of the database and webserver portal in order
     * to write in tables.
     *
     * @param day this is the day of the order made
     * @param month this is the month the order is made
     * @param year this is the year the order is made
     * @param dataBasePort this is the portal of the database server
     * @param webServerPort this is the portal of the web server
     */
    public void outputResults(String day, String month, String year, String dataBasePort, String webServerPort){
        List<Point> flightPathPoints = new ArrayList<>();
        for(LongLat location : geoJsonList){
            flightPathPoints.add(Point.fromLngLat(location.longitude,location.latitude));
        }
        LineString lineString = LineString.fromLngLats(flightPathPoints);
        Feature feature = Feature.fromGeometry(lineString);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
        // initiate a file writer
        String fileName = "drone-" + day + "-" + month + "-" + year;
        try {
            FileWriter geojsonFile = new FileWriter(fileName);
            geojsonFile.write(featureCollection.toJson());
            geojsonFile.close();
            System.out.println("File write successfully!");
        } catch (IOException e) {
            System.err.println("Failed to generate the Geo json file due to error occurs");
        }
        // store 'deliveries' and 'flightpath' table in the database
        Database database = new Database(dataBasePort);
        database.createDeliveriesDb(orderDataBase,webServerPort);
        database.createFlightPathDb(flightPathDataBase);
    }
}
