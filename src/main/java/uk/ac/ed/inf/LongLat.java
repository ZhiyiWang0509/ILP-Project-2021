package uk.ac.ed.inf;

public class LongLat {
    // two public fields in the class
    public double longitude;
    public double latitude;

    /* the four corners of the confined area
       moves beyond the four locations are not allowed.

    public final LongLat forrestHill = new LongLat(-3.192473,55.946233);
    public final LongLat kfc = new LongLat(-3.184319,55.946233);
    public final LongLat topMeadows = new LongLat(-3.192473,55.942617);
    public final LongLat buccleuchBusStop = new LongLat(-3.184319,55.942617);
    */

    // the constructor of the class LongLat has longitude and latitude as inputs.
    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // method to check if the drone is within the confined area
    public boolean isConfined() {
        boolean latCheck = (latitude < 55.946233) && (latitude > 55.942617);
        boolean longCheck = (longitude > -3.192473) && (longitude < -3.184319);
        return (latCheck && longCheck);
    }

    public double distanceTo(LongLat businessSchool) {
        return 0;
    }

    public boolean closeTo(LongLat alsoAppletonTower) {
        return true;
    }

    public LongLat nextPosition(int i) {
        LongLat x = new LongLat(i, i);
        return x;
    }
}
