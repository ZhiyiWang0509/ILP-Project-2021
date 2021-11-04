package uk.ac.ed.inf;

import com.google.gson.Gson;

// A simple class to decode the What3Words location into LongLat location
public class W3words {
    public String webPort;

    public W3words(String webPort) {
        this.webPort = webPort;
    }

    // return the LongLat location by searching on the web server
    public LongLat toLongLat(String w3wordString){
        WebAccess newWords = new WebAccess(webPort, "words", w3wordString);
        Location newLocation = new Gson().fromJson(String.valueOf(newWords.getResponse()),Location.class);
        return newLocation.getLocation();// return the location as a LongLat object
    }


}
