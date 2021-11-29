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
 * this class is used to output the flight path, coordinates and orders as a result of
 * deliveries made by the drone.
 */
public class Result {
    /**
     * this is the drone that made the deliveries
     */
    public Drone drone;

    /**
     * this is the constructor of the class
     * @param drone this is the drone that made the deliveries and needs to get the results output.
     */
    public Result(Drone drone) {
        this.drone = drone;
    }

    /**
     * this method would export the results obtained from makeDelivery(),
     * it would export a geojson file for the drone's flight path and write two tables
     * in the database, one called "deliveries" the other called "flightpath"
     */
    public void outPutResult() {
        try {
            List<Point> flightPathPoints = new ArrayList<>();
            for (LongLat location : drone.geoJsonList) {
                flightPathPoints.add(Point.fromLngLat(location.longitude, location.latitude));
            }
            LineString lineString = LineString.fromLngLats(flightPathPoints);
            Feature feature = Feature.fromGeometry(lineString);
            FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
            // initiate a file writer
            String fileName = "drone-" + drone.day + "-" + drone.month + "-" + drone.year;
            try {
                FileWriter geojsonFile = new FileWriter(fileName);
                geojsonFile.write(featureCollection.toJson());
                geojsonFile.close();
                System.out.println("File write successfully!");
            } catch (IOException e) {
                System.err.println("Failed to generate the Geo json file");
                System.exit(1);
            }
            // store 'deliveries' and 'flightpath' table in the database
            DataBase database = new DataBase(drone.dataBasePort);
            database.createDeliveriesDb(drone.orderDataBase, drone.webServerPort);
            database.createFlightPathDb(drone.flightPathDataBase);

        } catch (NullPointerException e) {
            System.err.println("Drone.makeDelivery, has to be called first");
            System.exit(1);
        }
    }
}
