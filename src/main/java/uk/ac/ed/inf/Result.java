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
 * this class is used to output the flight path, coordinates and orders made by the drone as a result
 * of a day's deliveries
 */
public class Result {
    /**
     * this is the drone that has finished the day's deliveries
     */
    private final Drone drone;

    /**
     * this is the constructor of the class
     *
     * @param drone this is the drone that has made the day's deliveries and needs to have its results get outputted.
     */
    public Result(Drone drone) {
        this.drone = drone;
    }

    /**
     * this method would export the results obtained from makeDelivery() method in the Drone class.
     * it would export a GeoJSON file for the drone's flight path as well as create and write
     * "deliveries" and "flightpaths" tables in the database
     */
    public void outPutResult() {
        try {
            List<Point> flightPathPoints = new ArrayList<>();
            for (LongLat location : drone.getGeoJsonList()) {
                flightPathPoints.add(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
            }
            LineString lineString = LineString.fromLngLats(flightPathPoints);
            Feature feature = Feature.fromGeometry(lineString);
            FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
            String fileName = "drone-" + drone.getDay() + "-" + drone.getMonth() + "-" + drone.getYear();

            try {
                FileWriter geojsonFile = new FileWriter(fileName);
                geojsonFile.write(featureCollection.toJson());
                geojsonFile.close();
                System.out.println("File write successfully!");
            } catch (IOException e) {
                System.err.println("Failed to generate the Geo json file");
                System.exit(1);
            }

            DataBase database = new DataBase(drone.getDataBasePort(), drone.getWebServerPort());
            database.createDeliveriesDb(drone.getOrderDataBase());
            database.createFlightPathDb(drone.getFlightPathDataBase());

        } catch (ArrayIndexOutOfBoundsException|NullPointerException e) {
            System.err.println("Drone.makeDelivery, has to be called first");
            System.exit(1);
        }catch (Exception e){
            System.exit(1);
        }
    }
}
