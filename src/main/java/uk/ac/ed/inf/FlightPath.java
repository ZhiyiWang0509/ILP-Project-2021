package uk.ac.ed.inf;

// an instance of this class represent as a row in flightPath database
public class FlightPath {
    public String orderNo;
    public double fromLongitude;
    public double fromLatitude;
    public int angle;
    public double tolongitude;
    public double toLatitude;

    public FlightPath(String orderNo, double fromLongitude, double fromLatitude, int angle, double tolongitude, double toLatitude) {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.tolongitude = tolongitude;
        this.toLatitude = toLatitude;
    }
}
