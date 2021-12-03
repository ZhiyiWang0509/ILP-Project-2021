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

            String day = args[0];
            String month = args[1];
            String year = args[2];
            String webServerPort = args[3];
            String dataBasePort = args[4];


            // parse the flight path into json FeatureCollection
            Drone newDrone = new Drone(day,month,year,webServerPort, dataBasePort);
            Result result = new Result(newDrone);
            long time1 = System.nanoTime();
            newDrone.makeDelivery();
            long time2 = System.nanoTime();
            result.outPutResult();
            long time3 = System.nanoTime();
            long duration_1 = time2 - time1; // calculate the run time
            long duration_2 = time3-time2;

            System.out.println("The time taken to generate the results is: " + duration_1/1E9 + " second");
            System.out.println("The time taken to output the results is: " + duration_2/1E9 + " second");
            System.out.println("Overall runtime is: " + ((duration_1 + duration_2)/1E9) + " second");
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }


    }
}
