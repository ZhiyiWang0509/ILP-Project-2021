package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.ArrayList;
import java.util.List;

// Class to deal with geojson files include methods related to non-fly zone and landmarks
public class Buildings {
    public String server;  // the server name of the website
    public String port;  // the port number of the website
    private static final String noFlyFileName = "no-fly-zones"; // the name of the file which store the no-fly zones

    public Buildings(String server, String port) {
        this.server = server;
        this.port = port;
    }

    public List<Feature> getFeatures(String fileName){
        WebAccess newBuildings = new WebAccess(server, port, "buildings", fileName);
        FeatureCollection noFlyZones = FeatureCollection.fromJson(newBuildings.getResponse());
        assert noFlyZones.features() != null;
        return noFlyZones.features();
    }

    // get the lists of coordinates that define the no-fly zones as a list of LongLat objects for each area
    public List<List<LongLat>> getNoFlyCoordinates(){
        List<Feature> features = getFeatures(noFlyFileName);
        List<List<LongLat>> coordinates = new ArrayList<>();
        for(Feature fc : features) {
            assert fc.geometry() != null;
            Polygon polygon = Polygon.fromJson(fc.geometry().toJson()); // cast the feature to polygon object
            List<LongLat> localPoints = new ArrayList<>();
            for(Point point : polygon.coordinates().get(0)){
                LongLat newLoc = new LongLat(point.coordinates().get(0), point.coordinates().get(1));
                localPoints.add(newLoc);
            }
            coordinates.add(localPoints);
        }
        return coordinates;
    }

    // need a method to get the landmarks for the drone to take detour when it would cross the no-fly zones.



}
