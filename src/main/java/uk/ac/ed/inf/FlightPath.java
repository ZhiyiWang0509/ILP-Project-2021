package uk.ac.ed.inf;

/**
 * this class is used when information related to a flightpath is needed to be stored and processed
 *
 */
public class FlightPath {
    /**
     * this is the order number of the order that's delivered on this flight path.
     */
    public String orderNo;
    /**
     * this is the longitude of the location coordinate the drone is travelling from
     */
    public double fromLongitude;
    /**
     * this is the latitude of the location coordinate the drone is travelling from
     */
    public double fromLatitude;
    /**
     * this is the angle between the traveling from location and the traveling to location
     */
    public int angle;
    /**
     * this is the longitude of the location coordinate the drone is travelling to
     */
    public double tolongitude;
    /**
     * this is the latitude of the location coordinate the drone is travelling to
     */
    public double toLatitude;

    /**
     * this is the constructor of FlightPath class
     *
     * @param orderNo this is the order number of the order delivered on this path
     * @param fromLongitude this is the longitude of the location the drone is travelling from
     * @param fromLatitude this is the latitude of the location the drone is travelling from
     * @param angle this is the angle between the travelling from location and the travelling to location
     * @param tolongitude this is the longitude of the location the drone is travelling to
     * @param toLatitude this is the latitude of the location the drone is travelling to
     */
    public FlightPath(String orderNo, double fromLongitude, double fromLatitude, int angle, double tolongitude, double toLatitude) {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.tolongitude = tolongitude;
        this.toLatitude = toLatitude;
    }
}
