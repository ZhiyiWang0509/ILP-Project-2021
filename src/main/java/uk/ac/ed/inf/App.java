package uk.ac.ed.inf;
/**
 * this is the class where the arguments from commend line are taken in
 * and the program would be executed based on the input arguments.
 *
 */
public class App
{
    public static void main( String[] args ){
        try{

          /*  String day = args[0];
            String month = args[1];
            String year = args[2];
            String webServerPort = args[3];
            String dataBasePort = args[4]; */

            String day = "08";
            String month = "08";
            String year = "2023";
            String webServerPort = "9898";
            String dataBasePort = "9876";

            // comment out these sections after the runtime results are out
            long startTime = System.nanoTime(); // time the application
            // parse the flight path into json FeatureCollection
            Drone newDrone = new Drone(day,month,year,webServerPort, dataBasePort);
            Result result = new Result(newDrone);
            newDrone.makeDelivery();
            result.outPutResult();
           // result.outputResults(day,month,year,dataBasePort,webServerPort);
            long finishTime = System.nanoTime();
            long duration = finishTime - startTime; // calculate the run time
            double secondDuration = duration/1E9; // converting the run time from nano-second to second
            System.out.println("The time taken to generate the path is: " + secondDuration + " second");
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }


    }
}
