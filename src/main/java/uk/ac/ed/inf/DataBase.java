package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * this class is used to access information stored in the tables: "orders" and "orderdetails" on the derby database
 * as well as creating and writing tables: "deliveries" and "flightpath" in to the database.
 * into the database.
 *
 */
public class DataBase {
    /**
     * this is the machine name of the database.
     */
    private static final String server = "localhost";
    /**
     * this the portal number of the database.
     */
    private final String dataBasePort;
    /**
     * this is the name of the 'orders' database.
     */
    private static final String ORDERS = "orders";
    /**
     * this is the name of the 'orderDetails' database.
     */
    private static final String ORDER_DETAILS = "orderDetails";

    /**
     * this is used to store values of (item name, item price) HashMap obtained by calling on menu.
     */
    private final HashMap<String, Integer> allItemPrices;

    /**
     * this is the constructor of the Database class
     *
     * @param dataBasePort this is the portal number of the database to access.
     */
    public DataBase(String dataBasePort, String webServerPort) {
        this.dataBasePort = dataBasePort;


        Menus menu = new Menus(webServerPort);
        this.allItemPrices = menu.getAllItemsPrice();
    }

    /**
     * this method is used to construct the location of the database
     *
     * @return the location of the derby database
     */
    private String getJdbcString(){
        return "jdbc:derby://" + server + ":" + dataBasePort + "/derbyDB";
    }

    /**
     * this method is used to collect and store all the information related to the orders of the day.
     *
     * @param date this is the date when the orders are made
     * @return a list of Order objects that store information of all the orders on the date provided.
     */
    public ArrayList<Order> getOrders(String date){
        ArrayList<Order> orderList = new ArrayList<>();
        String jdbcString = getJdbcString();
        String DELIVERY_DATE = "deliveryDate";
        final String dataQuery = "select * from " + ORDERS + " where " + DELIVERY_DATE + "=(?)";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psDataQuery = conn.prepareStatement(dataQuery);
            psDataQuery.setString(1, date);

            ResultSet rs = psDataQuery.executeQuery();
            try{
                while (rs.next()){
                    String orderNo = rs.getString("orderNo");
                    String deliverTo = rs.getString("deliverTo");
                    ArrayList<String> itemList = new ArrayList<>();
                    final String dataQuery2 = "select * from " + ORDER_DETAILS + " where " + "orderNo" + "=(?)";
                    try {
                        PreparedStatement psDataQuery2 = conn.prepareStatement(dataQuery2);
                        psDataQuery2.setString(1, orderNo);
                        ResultSet rs2 = psDataQuery2.executeQuery();
                        try{
                            while (rs2.next()) {
                                String itemName = rs2.getString("item");
                                itemList.add(itemName);
                            }
                        }catch(NullPointerException e){
                            System.err.println("There's no orders in the database under this date");
                            System.exit(1);
                        }
                        psDataQuery2.close();
                    } catch (SQLException e){
                        System.err.println("Failed in connecting to orderDetails database");
                        System.exit(1);
                    }
                    Order order = new Order(orderNo, deliverTo,itemList);
                    orderList.add(order);
                }
            } catch(NullPointerException e){
                System.err.println("There's no orders in the database under this date");
                System.exit(1);
            }
             psDataQuery.close();
            // shutdown the database
             conn.close();
        } catch (SQLException e){
            System.err.println("Failed in connecting to orders database");
            System.exit(1);
        } catch(Exception e){
            System.exit(1);
        }
        return orderList;

    }

    /**
     * this method would create a 'deliveries' table in the derby database and write in the information provided.
     *
     * @param orderList this is a collection of all the orders that's successfully delivered by the drone on the
     *                  specified day.
     *
     */
    public void createDeliveriesDb(List<Order> orderList){
        try {
            String jdbcString = getJdbcString();
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null,null,"DELIVERIES",null);
            if(resultSet.next()){
                statement.execute("drop table deliveries");
            }
            statement.execute("create table deliveries(" +
                    "orderNo char(8), " +
                    "deliveredTo varchar(19), " +
                    "costInPence int)");

            String dataBaseQuery = "INSERT INTO deliveries VALUES ";
            StringBuilder dataBaseQueryBody = new StringBuilder();
            try{
                for(Order order:orderList){
                    String entry = "('"+order.getOrderNO()+"','"+order.getDeliverTo()+"',"+order.getOrderCost(allItemPrices)+")"+",";
                    dataBaseQueryBody.append(entry);
                }
            }catch(NullPointerException|ArrayIndexOutOfBoundsException e){
                System.err.println("No order is made!");
                System.exit(1);
            }

            dataBaseQueryBody.setLength(dataBaseQueryBody.length()-1);
            statement.execute(dataBaseQuery+ dataBaseQueryBody);
            System.out.println("deliveries table written successfully");
            statement.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Failed creation in deliveries table");
            System.exit(1);
        } catch (Exception e){
            System.exit(1);
        }
    }

    /**
     * this method would create a 'flightpath' table in the derby database and write in the information provided.
     *
     * @param flightPaths this is a collection of every single flight path made by the drone in the process of
     *                    delivering the day's order.
     */
    public void createFlightPathDb(List<FlightPath> flightPaths){
        try {
            String jdbcString = getJdbcString();
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null,null,"FLIGHTPATH",null);
            if(resultSet.next()){
                statement.execute("drop table flightpath");
            }
            statement.execute("create table flightpath(" +
                    "orderNo char(8), " +
                    "fromLongitude double, " +
                    "fromLatitude double, " +
                    "angle integer, " +
                    "toLongitude double, " +
                    "toLatitude double)");

            String dataBaseQuery = "INSERT INTO flightpath VALUES ";
            StringBuilder dataBaseQueryBody = new StringBuilder();
            try{
                for(FlightPath flightPath:flightPaths){
                    String entry = "('"+flightPath.orderNo+"',"+flightPath.fromLongitude+","
                            +flightPath.fromLatitude+","+flightPath.angle+","
                            +flightPath.tolongitude +","+flightPath.toLatitude+"),";
                    dataBaseQueryBody.append(entry);
                }
            }catch(NullPointerException|ArrayIndexOutOfBoundsException e){
                System.err.println("No order is made!");
                System.exit(1);
            }
            dataBaseQueryBody.setLength(dataBaseQueryBody.length()-1);
            statement.execute(dataBaseQuery+dataBaseQueryBody);
            System.out.println("flightpath written successfully");
            statement.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Failed creation of flightpath table");
            System.exit(1);
        }catch(Exception e){
            System.exit(1);
        }
    }

}
