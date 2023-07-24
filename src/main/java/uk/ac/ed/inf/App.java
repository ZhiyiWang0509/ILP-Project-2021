package uk.ac.ed.inf;
/**
 * this is the class where the program gets executed with the user's input
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

            Drone newDrone = new Drone(day,month,year,webServerPort, dataBasePort);
            Result result = new Result(newDrone);
            newDrone.makeDelivery();
            result.outPutResult();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }
}
