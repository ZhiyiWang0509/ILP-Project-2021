package uk.ac.ed.inf;


import com.google.gson.Gson;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Polygon;
import java.util.Arrays;
import java.util.List;

public class App
{
    public static void main( String[] args ){
        LongLat appletonTower = new LongLat(-3.191594,55.943658);
        String w3Test = "army.monks.grapes";
        W3words newWord = new W3words(w3Test);
       // System.out.println(newWord.toLongLat().isConfined());
       // WebAccess newWord = new WebAccess("localhost", "80", "words", "army.monks.grapes");
        WebAccess newMenu = new WebAccess("localhost", "80", "menus", "menus");
        WebAccess newBuilding = new WebAccess("localhost", "80", "buildings", "no-fly-zones");
        //System.out.println(newBuilding.getResponse());

        // turn the geojson string to a feature collection
        FeatureCollection noFlyZones = FeatureCollection.fromJson(newBuilding.getResponse());
        assert noFlyZones.features() != null;
        for(Feature fc : noFlyZones.features()) {
            Polygon polygon = Polygon.fromJson(fc.geometry().toJson()); // cast the feature to polygon object
            System.out.println(polygon.coordinates());
        }

       // System.out.println(noFlyZones.features());






    }
}
