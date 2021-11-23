package uk.ac.ed.inf;

import com.google.gson.Gson;

/**
 * this class acts as a translator of location from W3words string to LongLat object,
 * since LongLat is more widely used in other classes.
 * this class would need access to the web server, hence the portal of the webserver needs
 * to be provided when an instance of this class is created.
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
     * location provided as the parameter.
     * the access to the webserver is achieved by creating an instance of WebAccess class and the
     * content in the file is obtained by calling the getResponse method on that instance.
     * the parameter would be rephrased to a proper file location where the information
     * related to the w3words location is stored in the WebAccess class.
     *
     * @param w3wLocation this is the location in w3words format.
     * @return the location provided as a LongLat object.
     */
    public LongLat toLongLat(String w3wLocation){
        WebAccess newWords = new WebAccess(webPort, "words", w3wLocation);
        Location newLocation = new Gson().fromJson(String.valueOf(newWords.getResponse()),Location.class);
        return newLocation.getLocation();
    }


}
