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
        String day = "11";
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
        String fileName = day + "-" + month + "-" + year;
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



        /* test the travelTo method in Drone class
        Database ordersDb = new Database("1527");
        ArrayList<Order> orders = ordersDb.getOrders("2022-04-11");
        LongLat longLat = newWord.toLongLat(orders.get(0).deliverTo);
        System.out.println(newDrone.currentLocation.getMoves(longLat));
        System.out.println(newDrone.travelTo(orders.get(0).orderNO,longLat).size());
        */

        /*
        double distanceTo = newDrone.currentLocation.distanceTo(businessSchool);
        System.out.println(distanceTo);
        System.out.println(Math.round(distanceTo/0.00015));
        System.out.println("the drone need 10 steps to fly from AT to business school.");

        int angle = newDrone.currentLocation.getAngle(businessSchool);
        System.out.println(angle);
        //angle += 180;  // need to modify the angle with the direction of travelling
       // System.out.println(newDrone.currentLocation.nextPosition(angle).formatLongLat());
       // DecimalFormat f = new DecimalFormat("##.000000");
        LongLat noflypoint1 = new LongLat(-3.1892189, 55.9454105);
        int i = 0;
        while(!newDrone.currentLocation.closeTo(businessSchool)){
            newDrone.updateLocation(newDrone.currentLocation.nextPosition(angle));
            System.out.println(newDrone.currentLocation.formatLongLat());
            i++;
        }
        System.out.println(i);
        //System.out.println(newDrone.getLocations());
        //System.out.println(newDrone.getEntirePath());
        */
        /*Database ordersDb = new Database("1527");
        ArrayList<Order> orders = ordersDb.getOrders("2022-04-11");
        int moveSum = 0;
        for(Order order : orders){
            moveSum += newDrone.getRouteMovesCount(order);
            System.out.println(newDrone.getRouteMovesCount(order));
        }
        System.out.println(moveSum);  */








    }
}
