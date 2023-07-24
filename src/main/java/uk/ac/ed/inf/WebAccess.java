package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * this class is used to establish connection toS and get response from the web server.
 * instance of this class can access files in any one of the folders: 'menus', 'words' and 'buildings'
 * by specifying the file name and the folder name when the instance is created.
 *
 */
public class WebAccess {
    /**
     * this is the machine name of the webserver
     */
    private static final String server = "localhost";
    /**
     * this is the HTTP client that's used to access the web server
     */
    private static final HttpClient client = HttpClient.newHttpClient();
    /**
     * this is the portal of the webserver
     */
    private final String port;
    /**
     * this is the folder name of where the file is located
     */
    private final String folderName;
    /**
     * this is the name of the file wanted to access without file extension been specified
     */
    private String fileName;

    /**
     * this is the constructor of WebAccess class
     *
     * @param port this is the portal of the webserver
     * @param folderName this is the name of the folder that stores the file
     * @param fileName this is the name of the file wanted to access without the file extension
     */
    public WebAccess(String port, String folderName, String fileName) {
        this.port = port;
        this.folderName = folderName;
        this.fileName = fileName;
    }

    /**
     * this method transforms a w3word location into a valid file location in order to
     * access the correct file stored in the 'words' folder.
     *
     * @param location this is the w3words location needs to get formatted.
     * @return a valid file location for a file stored under 'words' folder.
     *
     */
    private String formatLocation(String location){
        String[] locSplit = location.split("[.]");
        String newLoc = "";
        for (String loc : locSplit) newLoc = newLoc.concat(loc + "/");
        return (newLoc + "details");
    }

    /**
     * this method would establish connection to and get response from the webserver for the file content stored
     * in the JSON files.
     *
     * @return the content in the JSON file as a JSON string
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
        HttpResponse<String> response = null;
        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println("Issue connecting to webserver");
            System.exit(1);
        } catch(NullPointerException e){
            System.err.println("The response from the Webserver is empty");
            System.exit(1);
        }catch (Exception e){
            System.exit(1);
        }
        return response.body();
    }
}
