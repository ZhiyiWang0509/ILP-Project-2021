package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isConfined() {
        return true;
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
