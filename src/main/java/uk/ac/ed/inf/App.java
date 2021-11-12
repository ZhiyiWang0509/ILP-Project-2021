package uk.ac.ed.inf;


import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.*;
import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;

public class App
{
    public static void main( String[] args ){
        try{
            /*
            String day = args[0];
            String month = args[1];
            String year = args[2];
            String date = year + "-" + month + "-" + day;
            String webServerPort = args[3];
            String dataBasePort = args[4]; */

            String day = "15";
            String month = "04";
            String year = "2022";
            String date = year + "-" + month + "-" + day;
            String webServerPort = "9898";
            String dataBasePort = "1527";

            // parse the flight path into json FeatureCollection
            Drone newDrone = new Drone(date, webServerPort, dataBasePort);
            Result result = newDrone.makeDelivery();
            List<Point> flightPathPoints = new ArrayList<>();
            for(LongLat location : result.geoJsonList){
                flightPathPoints.add(Point.fromLngLat(location.longitude,location.latitude));
            }
            LineString lineString = LineString.fromLngLats(flightPathPoints);
            Feature feature = Feature.fromGeometry(lineString);
            FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
            // initiate a file writter
            String fileName = "drone-" + day + "-" + month + "-" + year;
            try {
                FileWriter geojsonFile = new FileWriter(fileName);
                geojsonFile.write(featureCollection.toJson());
                geojsonFile.close();
                System.out.println("File write successfully!");
            } catch (IOException e) {
                System.err.println("Failed to generate the Geo json file due to error occurs");
            }

            // create databases required to store orders and flightpath made by the drone
            Database database = new Database(dataBasePort);
            database.createDeliveriesDb(result.orderDataBase,webServerPort);
            database.createFlightPathDb(result.flightPathDataBase);

        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println("Invalid input");
            System.exit(1);
        }


    }
}
