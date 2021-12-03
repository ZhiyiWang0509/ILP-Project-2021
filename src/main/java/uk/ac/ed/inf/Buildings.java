package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * this class is used to process information stored in GeoJSON files located in the "building" folder
 *
 */
public class Buildings {
    /**
     * this is the portal of the web server
     */
    private final String webPort;
    /**
     * this is the name of the file to access on the web server
     * this field could only be "no-fly-zones" or "landmarks"
     */
    private final String fileName;

    /**
     * the constructor of the Buildings class
     *
     * @param webPort the portal of the webserver
     * @param fileName the name of the file that's located in the buildings folder
     */
    public Buildings(String webPort, String fileName) {
        this.webPort = webPort;
        this.fileName = fileName;
    }

    /**
     * this method obtain the file content in the GeoJSON files on the webserver
     *
     * @return the content of the file as a collection of Features.
     */
    private List<Feature> getFeatures(){
        WebAccess newBuildings = new WebAccess(webPort, "buildings", fileName);
        FeatureCollection featureCollection = FeatureCollection.fromJson(newBuildings.getResponse());
        return featureCollection.features();
    }

    /**
     * this method would collect every coordinate that defines the no-fly-zone area.
     *
     * @return a list collections of coordinates with each collection specify one part of the no-fly-zone.
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
                Polygon polygon = Polygon.fromJson(fc.geometry().toJson());
                List<LongLat> localPoints = new ArrayList<>();
                for(Point point : polygon.coordinates().get(0)){
                    LongLat newLoc = new LongLat(point.longitude(), point.latitude());
                    localPoints.add(newLoc);
                }
                coordinates.add(localPoints);
        }
        } catch(Exception e){
            System.exit(1);
        }
        return coordinates;
    }

    /**
     *  this method organize the collections of no-fly-zone coordinates into a collection of no-fly-zone border lines.
     *
     * @return a list of no-fly-zone borderlines
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
                    Line2D border = new Line2D.Double(noFlyBorder1.getLongitude(), noFlyBorder1.getLatitude(), noFlyBorder2.getLongitude(), noFlyBorder2.getLatitude());
                    borders.add(border);
                }

                int FIRST_INDEX = 0;
                int LAST_INDEX = area.size()-1;
                LongLat head = area.get(FIRST_INDEX);
                LongLat tail = area.get(LAST_INDEX);
                Line2D lastBorder = new Line2D.Double(head.getLongitude(), head.getLatitude(), tail.getLongitude(), tail.getLatitude());
                borders.add(lastBorder);
            }
        }catch(Exception e){
            System.exit(1);
        }
        return borders;
    }

    /**
     * this method collect all the landmark locations
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
        }catch(Exception e){
            System.exit(1);
        }
        return coordinates;
    }


}
