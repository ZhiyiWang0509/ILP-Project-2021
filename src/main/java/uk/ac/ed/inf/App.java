package uk.ac.ed.inf;


import java.util.List;
import org.javatuples.Pair;

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

       /* List<List<List<Double>>> coordinates = new ArrayList<>();
        for(Feature fc : noFlyZones.features()) {
            Polygon polygon = Polygon.fromJson(fc.geometry().toJson()); // cast the feature to polygon object
            List<List<Double>> localPoints = new ArrayList<>();
            for(Point point : polygon.coordinates().get(0)){
                localPoints.add(point.coordinates());
            }
            coordinates.add(localPoints);
        }
        System.out.println(coordinates.get(0));
        */
        Buildings building = new Buildings("localhost", "80");
        List<LongLat> list1 = building.getNoFlyCoordinates().get(0);
        int length = list1.size();
        for(int i=1; i < length; i++){
            int j = i - 1;
            System.out.println(Pair.with(list1.get(j).longitude, list1.get(j).latitude));
        }

        // these locations shouldn't cross the no-fly zones
        LongLat businessSchool = new LongLat(-3.1873,55.9430);
        LongLat greyfriarsKirkyard = new LongLat(-3.1928,55.9469);

        // test1 and test2 are two points on the no-fly zones border lines
        LongLat test1 = new LongLat(-3.1892189, 55.9454105);
        LongLat test2 = new LongLat(-3.1891868, 55.9452948);
        Drone newDrone = new Drone();
        System.out.println(newDrone.checkNoFlys(test2));






    }
}
