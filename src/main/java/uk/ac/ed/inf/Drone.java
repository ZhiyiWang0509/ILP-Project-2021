package uk.ac.ed.inf;

/* an instance of this class represent a drone object

 */
public class Drone {
    public static int moves;  // count the moves made by the drone on a single day
    public static final int MOVES_LIMIT = 1500; // the maximum moves allowed for a drone on a single day

    // return true if the drone still have moves available for a day
    public boolean checkMoves() {
        return moves < MOVES_LIMIT;
    }


}
