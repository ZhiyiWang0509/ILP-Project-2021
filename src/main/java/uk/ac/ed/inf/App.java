package uk.ac.ed.inf;


import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.*;
import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * this is the class where the arguments from commend line are taken in
 * the first argument of the input is the day of the delivery date
 * the second argument is the month of the date
 * the third is the year of the date
 * the forth is the portal of the web server
 * the last is the portal of the database
 *
 * note that the portal of the webserver and database must match the ones
 * been activated before the start of this program.
 *
 * in this class a Drone object will be created using the date built by the input
 * argument, the web server portal and the database portal.
 * the drone created would then make the deliveries of the day using its 'makeDelivery' method
 * the Result object returned by the method would be written in according files.
 * there are three outputs for this program, one is a geojson file recording the coordinates
 * visited by the drone during its delivery.
 * and two database tables, one is the 'deliveries' table and the other is the 'flightPaths' table.
 *
 * the outputted geojson file can be presented in geojson.io and the database tables will be stored in
 * derby database.
 *
 * the terminal will have messages as a feedback of successful generation of the files.
 *
 * this whole process of initiating a drone till all files are generated and stored will be timed for
 * run time. The overall run time in second will be reported from the terminal.
 *
 */
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
            String dataBasePort = "9876";

            long startTime = System.nanoTime(); // time the application
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
            database.createDeliveriesDb(result.orderDataBase,webServerPort);
            database.createFlightPathDb(result.flightPathDataBase);

            long finishTime = System.nanoTime();
            long duration = finishTime - startTime; // calculate the run time
            double secondDuration = duration/1E9; // converting the run time from nano-second to second
            System.out.println("The time taken to generate the path is: " + secondDuration + " second");
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println("Invalid input");
            System.exit(1);
        }


    }
}
