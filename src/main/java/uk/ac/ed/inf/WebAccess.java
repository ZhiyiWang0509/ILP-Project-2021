package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

// WebAccess class is built to get the content stored in the web server.
// The server has to be started before any object in the WebAccess class to be created.
// Clients have to provide the server and port to access the web address, also the folder name and the file name.
// The most important method in this class is the getResponse, which would return the content in the file in String format.
public class WebAccess {

    String server;
    String port;
    String folderName;
    String fileName;

    public WebAccess(String server, String port, String folderName, String fileName) {
        this.server = server;
        this.port = port;
        this.folderName = folderName;
        this.fileName = fileName;
    }

    // the method would return the file content in string format
    public String getResponse(){
        String fileExtension = ".json";  // files in different folders might have different extension.
        if(folderName.equals("buildings")) fileExtension = ".geojson";

        String url = "http://" + server + ":" + port + "/" + folderName + "/" + fileName + fileExtension;

        final HttpClient client = HttpClient.newHttpClient(); // set client to be final type to ensure only one is created
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept",(folderName + ".json") )
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = null;
        {
            try {
                response = client.send(request, BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        assert response != null; // Check if the content is empty
        return response.body();

    }

}
