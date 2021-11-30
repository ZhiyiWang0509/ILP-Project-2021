package uk.ac.ed.inf;

import com.google.gson.Gson;

/**
 * this class acts as a translator of location from W3words string to LongLat object
 *
 */
public class W3words {
    /**
     * this is the portal of the web server to access.
     */
    public String webPort;

    /**
     * this is the constructor of W3words class
     *
     * @param webPort this is the portal of the webserver to access
     */
    public W3words(String webPort) {
        this.webPort = webPort;
    }

    /**
     * this method would return the LongLat location corresponding to the w3words
     * location provided as the parameter by accessing the information stored on the webserver
     *
     * @param w3wLocation this is the location in w3words format.
     * @return the location provided as a LongLat object.
     */
    public LongLat toLongLat(String w3wLocation){
        WebAccess newWords = new WebAccess(webPort, "words", w3wLocation);
        W3wordDetails newW3wordDetails = new Gson().fromJson(String.valueOf(newWords.getResponse()), W3wordDetails.class);
        return newW3wordDetails.getLocation();
    }


}
