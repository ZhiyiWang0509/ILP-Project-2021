package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.ArrayList;
import java.util.List;

// Class to deal with geojson files include methods related to non-fly zone and landmarks
public class Buildings {
    public String webPort;  // the port number of the website
    public String fileName;  // the filename want to access

    public Buildings(String webPort, String fileName) {
        this.webPort = webPort;
        this.fileName = fileName;
    }

    public List<Feature> getFeatures(){
        WebAccess newBuildings = new WebAccess(webPort, "buildings", fileName);
        FeatureCollection featureCollection = FeatureCollection.fromJson(newBuildings.getResponse());
        assert featureCollection.features() != null;
        return featureCollection.features();
    }

    // get the lists of coordinates that define the no-fly zones as a list of LongLat objects for each area
    public List<List<LongLat>> getNoFlyCoordinates(){
        assert fileName.equals("no-fly-zones");
        List<Feature> features = getFeatures();
        List<List<LongLat>> coordinates = new ArrayList<>();
        for(Feature fc : features) {
            assert fc.geometry() != null;
            Polygon polygon = Polygon.fromJson(fc.geometry().toJson()); // cast the feature to polygon object
            List<LongLat> localPoints = new ArrayList<>();
            for(Point point : polygon.coordinates().get(0)){
                LongLat newLoc = new LongLat(point.longitude(), point.latitude());
                localPoints.add(newLoc);
            }
            coordinates.add(localPoints);
        }
        return coordinates;
    }

    // get the landmarks for the drone to take detour when it would cross the no-fly zones.
    public List<LongLat> getLandMarks(){
        assert fileName.equals("landmarks");
        List<Feature> features = getFeatures();
        List<LongLat> coordinates = new ArrayList<>();
        for(Feature fc : features) {
            assert fc.geometry() != null;
            Point point = Point.fromJson(fc.geometry().toJson());
            LongLat location = new LongLat(point.longitude(), point.latitude());
            coordinates.add(location);
        }
        return coordinates;
    }


}
