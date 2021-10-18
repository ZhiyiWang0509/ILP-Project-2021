package uk.ac.ed.inf;

/* an instance of this class represent a drone object

 */
public class Drone {
    public static int moves;  // count the moves made by the drone on a single day
    public static final int MOVES_LIMIT = 1500; // the maximum moves allowed for a drone on a single day
    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494); //the starting and ending point of the drone

    // return true if the drone still have moves available for a day
    public boolean checkMoves() {
        return moves < MOVES_LIMIT;
    }


}
