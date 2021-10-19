package uk.ac.ed.inf;

import com.google.gson.Gson;

// A simple class to decode the What3Words location into LongLat location
public class W3words {
    public String w3wordString;

    public W3words(String w3wordString) {
        this.w3wordString = w3wordString;
    }

    // return the LongLat location by searching on the web server
    public LongLat toLongLat(){
        // access the web server by the w3words string
        // the w3word string will be formatted in to a proper file location in the WebAccess class
        WebAccess newWords = new WebAccess("localhost", "80", "words", w3wordString);
        //pass the json string to the parser for Location
        Location newLocation = new Gson().fromJson(String.valueOf(newWords.getResponse()),Location.class);
        // return the location as a LongLat object
        return newLocation.getLocation();
    }


}
