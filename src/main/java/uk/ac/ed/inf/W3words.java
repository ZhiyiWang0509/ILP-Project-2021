package uk.ac.ed.inf;

import com.google.gson.Gson;

/**
 * this class is used as a translator of location from W3words string to LongLat object
 *
 */
public class W3words {
    /**
     * this is the portal of the web server
     */
    public String webPort;

    /**
     * this is the constructor of W3words class
     *
     * @param webPort this is the portal of the webserver
     */
    public W3words(String webPort) {
        this.webPort = webPort;
    }

    /**
     * this method translates a w3words format location to a LongLat location.
     *
     * @param w3wLocation this is the location in w3words format.
     * @return the LongLat form of the location provided
     */
    public LongLat toLongLat(String w3wLocation){
        WebAccess newWords = new WebAccess(webPort, "words", w3wLocation);
        W3wordDetails newW3wordDetails = new Gson().fromJson(String.valueOf(newWords.getResponse()), W3wordDetails.class);
        return newW3wordDetails.getLocation();
    }


}
