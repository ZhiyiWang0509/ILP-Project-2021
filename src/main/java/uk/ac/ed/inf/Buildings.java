package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * this class is used to help to transform information stored in geojson files in to more accessible format
 * this class could only allow access to either no-fly-zones.geojson or landmarks.geojson
 * incorrect filename could cause the termination of the program
 *
 */
public class Buildings {
    /**
     * this is the portal of the web server that store the files needed
     */
    public String webPort;
    /**
     * this is the name of the file to access on the web server
     * this field could only be "no-fly-zones" or "landmarks"
     */
    public String fileName;

    /**
     * the constructor of the Buildings class
     *
     * @param webPort the portal of the webserver where the geojson files are stored
     * @param fileName the name of the file to access on the web
     *                 the name could only be "no-fly-zones" or "landmarks"
     */
    public Buildings(String webPort, String fileName) {
        this.webPort = webPort;
        this.fileName = fileName;
    }

    /**
     * this method get access to the web server and extract information from the file specified
     * by the file name as a list of Feature object.
     * the access to the webserver is achieved by creating an instance of WebAccess class and the
     * content in the file is obtained by calling the getResponse method on that instance.
     *
     * @return a list of Features in the geojson file accessed
     */
    private List<Feature> getFeatures(){
        WebAccess newBuildings = new WebAccess(webPort, "buildings", fileName);
        FeatureCollection featureCollection = FeatureCollection.fromJson(newBuildings.getResponse());
        return featureCollection.features();
    }

    /**
     * this method only accept "no-fly-zones.geojson" as file name
     * this method would return a list of all the no-fly zones
     * the border of each no-fly zone is represented by a list of
     * LongLat objects
     *
     * @return a list of no-fly-zones with each zone be a list of LongLat
     * object which represent the coordinates of the zone's borders on
     * a map
     */
    private List<List<LongLat>> getNoFlyCoordinates(){
        if(!fileName.equals("no-fly-zones")){
            System.err.println("Invalid File name input");
            System.exit(1);
        }
        List<Feature> features = getFeatures();
        List<List<LongLat>> coordinates = new ArrayList<>();
        try{
            for(Feature fc : features) {
                assert fc.geometry() != null;
                // cast the feature to polygon object
                Polygon polygon = Polygon.fromJson(fc.geometry().toJson());
                List<LongLat> localPoints = new ArrayList<>();
                for(Point point : polygon.coordinates().get(0)){
                    LongLat newLoc = new LongLat(point.longitude(), point.latitude());
                    localPoints.add(newLoc);
                }
                coordinates.add(localPoints);
        }
        }catch(ArrayIndexOutOfBoundsException|NullPointerException e){
            System.exit(1);
        }
        return coordinates;
    }

    /**
     *  this method transform the no-fly-zones in to a list of 2D lines
     *  each line represent a part of the border for a no-fly-zone
     *  since a border is defined as a complete outline of an area
     *  therefore after the looping of all the points in each no-fly-zone the route from
     *  the last point to the first point is also considered
     *
     * @return a list of no-fly-zones' borderlines represented by Line2D object
     */
    public List<Line2D> getNoFlyBorders(){
        List<Line2D> borders =  new ArrayList<>();
        List<List<LongLat>> noFlyCoordinates = getNoFlyCoordinates();
        try{
            for(List<LongLat> area : noFlyCoordinates){
                for(int i = 1; i < area.size(); i++) {
                    int j = i - 1;
                    LongLat noFlyBorder1 = area.get(j);
                    LongLat noFlyBorder2 = area.get(i);
                    Line2D border = new Line2D.Double(noFlyBorder1.longitude, noFlyBorder1.latitude, noFlyBorder2.longitude, noFlyBorder2.latitude);
                    borders.add(border);
                }
                // the first coordinate
                LongLat head = area.get(0);
                // the last coordinate
                LongLat tail = area.get(area.size()-1);
                Line2D lastBorder = new Line2D.Double(head.longitude, head.latitude, tail.longitude, tail.latitude);
                borders.add(lastBorder);
            }
        }catch(ArrayIndexOutOfBoundsException|NullPointerException e){
            System.exit(1);
        }
        return borders;
    }

    /**
     * this method only accept "landmarks" as file name
     * this method return all the landmarks the drone is able to travel to when it would cross no-fly-zones
     * during delivery
     *
     * @return a list of landmark location as LongLat objects
     */
    public List<LongLat> getLandMarks(){
        if(!fileName.equals("landmarks")){
            System.exit(1);
        }
        List<Feature> features = getFeatures();
        List<LongLat> coordinates = new ArrayList<>();
        try{
            for(Feature fc : features) {
                assert fc.geometry() != null;
                Point point = Point.fromJson(fc.geometry().toJson());
                LongLat location = new LongLat(point.longitude(), point.latitude());
                coordinates.add(location);
            }
        }catch(ArrayIndexOutOfBoundsException|NullPointerException e){
            System.exit(1);
        }
        return coordinates;
    }


}
