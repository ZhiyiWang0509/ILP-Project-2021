package uk.ac.ed.inf;

/**
 * this class is used when information related to a flightpath is needed to be stored and processed
 *
 */
public class FlightPath {
    /**
     * this is the order number of the order that's delivered on this flight path.
     */
    private final String orderNo;
    /**
     * this is the longitude of the location coordinate the drone is travelling from
     */
    private final double fromLongitude;
    /**
     * this is the latitude of the location coordinate the drone is travelling from
     */
    private final double fromLatitude;
    /**
     * this is the angle between the traveling from location and the traveling to location
     */
    private final int angle;
    /**
     * this is the longitude of the location coordinate the drone is travelling to
     */
    private final double tolongitude;
    /**
     * this is the latitude of the location coordinate the drone is travelling to
     */
    private final double toLatitude;

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

    /**
     * this is the getter of the order number
     * @return the order number
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * this is the getter of the from-longitude
     * @return the from-longitude
     */
    public double getFromLongitude() {
        return fromLongitude;
    }
    /**
     * this is the getter of the from-longitude
     * @return the from-longitude
     */
    public double getFromLatitude() {
        return fromLatitude;
    }
    /**
     * this is the getter of the angle
     * @return the angle
     */
    public int getAngle() {
        return angle;
    }
    /**
     * this is the getter of the to-longitude
     * @return the to-longitude
     */
    public double getTolongitude() {
        return tolongitude;
    }
    /**
     * this is the getter of the to-latitude
     * @return the to-latitude
     */
    public double getToLatitude() {
        return toLatitude;
    }
}
