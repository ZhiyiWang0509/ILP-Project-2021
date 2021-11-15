package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * this class is used to establish connection to the web server.
 * there are three folders stored in the web server: 'menus', 'words' and 'buildings'.
 * instance of this class can access files in any one of the folders by specifying
 * the file name and the folder name when the instance is created.
 * 'buildings' folders contain 'landmarks.geojson' and 'no-fly-zones.geojson'
 * 'menus folder' contain 'menus.json'
 * 'words' folder contain json files corresponding to w3words format location,
 * for instance, information about a location with w3word format: 'word1.word2.word3'
 * is stored in a file location: 'word1/word2/word3/details.json'
 * the portal of the webserver must match the portal initiated through commend line before the program
 * starts.
 *
 */
public class WebAccess {

    /**
     * this is the machine name of the webserver
     * in this programme, files are stored on local host
     */
    private static final String server = "localhost";
    /**
     * this is the HTTP client to access the web server
     * only one client is created since it is a heavy-weight object
     */
    private static final HttpClient client = HttpClient.newHttpClient();
    /**
     * this is the portal of the webserver to access
     */
    public String port;
    /**
     * this is the folder name which stores the file
     */
    public String folderName;
    /**
     * this is the name of the file needed to access
     * note the filename doesn't require the file extension
     */
    public String fileName;

    /**
     * this is the constructor of WebAccess class
     * @param port this is the portal of the webserver to access
     * @param folderName this is the name of the folder that store the file
     * @param fileName this is the name of the file wanted to access, the name doesn't include file extension
     */
    public WebAccess(String port, String folderName, String fileName) {
        this.port = port;
        this.folderName = folderName;
        this.fileName = fileName;
    }

    /**
     * this method would transform a w3word location into a valid file location in order to
     * access the correct file stored in the 'words' folder.
     * corresponding to a w3word location: 'word1.word2.word3', a valid file location in the
     * 'words' folder is: 'word1/word2/word3/details.json'.
     *
     * @param location this is the w3words location needs to get formatted.
     * @return a valid file location where the detailed information corresponding to the w3words
     * location is stored in the 'words' folder.
     *
     */
    public String formatLocation(String location){
        String[] locSplit = location.split("[.]");
        String newLoc = "";
        for (String loc : locSplit) newLoc = newLoc.concat(loc + "/");
        return (newLoc + "details");
    }

    /**
     * this method would access the file on the web server by first rebuild the actual file name
     * with file extension added depending on the folder name.
     * if the folder is 'buildings' the extension is '.geojson', otherwise the extension is '.json'.
     * also, the file name will be modified if the folder name is 'words': the file name will be transformed
     * using the 'formatLocation' method previously defined.
     *
     * then the modified file name and the folder name will be combined with server name and portal number
     * to make a complete url address to access.
     * with the url address, the method send a request to the web server, IOException and InterruptedException
     * are catch while sending the request.
     * if any of the exceptions is caught, the system will be terminated.
     *
     * @return the result of sending the request to the webserver, which is the content of the json file as a json string
     *
     */
    public String getResponse(){
        String fileExtension = ".json";
        if(folderName.equals("buildings")) {
            fileExtension = ".geojson";
        }
        if(folderName.equals("words")) {
            fileName = formatLocation(fileName);
        }
        String url = "http://" + server + ":" + port + "/" + folderName + "/" + fileName + fileExtension;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept",(folderName + ".json") )
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = null;  // receive the response from the request made
        {
            try {
                response = client.send(request, BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                System.err.println("Issue connectinf to webserver");
                System.exit(0);  // exit the system when there's connection issue with the web server
            }
        }
        assert response != null;
        return response.body();
    }
}
