package uk.ac.ed.inf;

/* an instance of this class is used to access the content stored on the web server
 * the server has to be started before the access of the website
 * to create an instance of the class, the constructor needs the server name, port number,
 * folder name of the file located and the file name without file extension.
 */

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class WebAccess {

    public String server;  // the server name of the website
    public String port;  // the port number of the website
    public String folderName;  // the folder where the file is located
    public String fileName;  // the actual file name without the file type extension

    public WebAccess(String server, String port, String folderName, String fileName) {
        this.server = server;
        this.port = port;
        this.folderName = folderName;
        this.fileName = fileName;
    }

    // return the file content in string format
    public String getResponse(){
        String fileExtension = ".json";
        if(folderName.equals("buildings")) {  // files in different folders might have different extension.
            fileExtension = ".geojson";
        }
        // the file location to access the file on the website
        String url = "http://" + server + ":" + port + "/" + folderName + "/" + fileName + fileExtension;

        final HttpClient client = HttpClient.newHttpClient(); // set client to be final type to ensure only one is created
        HttpRequest request = HttpRequest.newBuilder()  // made a web request
                .GET()
                .header("accept",(folderName + ".json") )
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = null;  // receive the response from the request made
        {
            try {
                response = client.send(request, BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                System.exit(0);  // exit the system when there's connection issue with the web server
            }
        }
        assert response != null;
        return response.body();
    }
}
