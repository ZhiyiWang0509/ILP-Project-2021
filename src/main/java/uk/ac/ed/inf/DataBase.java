package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * this class is used to access information stored in the derby database as well as write outcomes
 * into the database.
 * the name of the tables that's able to access is 'orders' and 'orderDetails'.
 * 'orders' table store information on lunch orders, it contains fields: 'orderNo', 'deliveryDate', 'customer' and 'deliverTo'.
 * 'orderDetails' table store detailed information of each order, it contains fields: 'orderNo' and 'item'.
 * the tables that's able to generated via this class are 'deliveries' and 'flightPath' which are required as part of the
 * final outcome of this program.
 *
 */
public class DataBase {
    /**
     * this is the machine name of the database.
     */
    private static final String server = "localhost";
    /**
     * this the portal number of the database to access.
     */
    public String dataBasePort;

    public String webServerPort;
    /**
     * this is the name of the 'orders' database.
     */
    private static final String ORDERS = "orders";
    /**
     * this is the name of the 'orderDetails' database.
     */
    private static final String ORDER_DETAILS = "orderDetails";

    private Menus menu;
    private final HashMap<String, Integer> allItemPrices;

    /**
     * this is the constructor of the Database class
     *
     * @param dataBasePort this is the portal number of the database to access.
     */
    public DataBase(String dataBasePort, String webServerPort) {
        this.dataBasePort = dataBasePort;
        this.webServerPort = webServerPort;
        this.menu = new Menus(webServerPort);
        this.allItemPrices = menu.getAllItemsPrice();
    }

    /**
     * this is the location of the database to access
     *
     * @return the location of the database to access the derby database.
     */
    private String getJdbcString(){
        return "jdbc:derby://" + server + ":" + dataBasePort + "/derbyDB";
    }

    /**
     * this method would access the database for information related to an order on a given day
     * overall, every order made on the date specified would be collected and returned as a list of Order object.
     *
     * note that after making a database query, the query of creating the connection needs to be closed and at
     * the end of the method, as well as the connection to the database.
     *
     * @param date this is the date the order is made which corresponding to the 'deliveryDate' column in the 'orders' table
     * @return a list of Order objects corresponding to the order made on the date specified.
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
     * this method would create a 'deliveries' table in the derby database and write data in.
     * in order to get the monetary value of the order, the method require access to the web server

     * note that after making a database query, the statement of creating the connection needs to be closed
     * and at the end of the method, as well as the connection to the database.
     *
     * @param orderList this is a list of Order objects, corresponding to the orders made on the specified day.
     *
     */
    public void createDeliveriesDb(List<Order> orderList){
        String jdbcString = getJdbcString();
        try {
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
                    String entry = "('"+order.orderNO+"','"+order.deliverTo+"',"+order.getOrderCost(allItemPrices)+")"+",";
                    dataBaseQueryBody.append(entry);
                }
            }catch(NullPointerException|ArrayIndexOutOfBoundsException e){
                System.err.println("No order is made!");
                System.exit(1);
            }

            dataBaseQueryBody.setLength(dataBaseQueryBody.length()-1);
            statement.execute(dataBaseQuery+ dataBaseQueryBody);
            System.out.println("Database generated successfully");
            statement.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("The connection failed in deliveries table");
            System.exit(1);
        } catch (Exception e){
            System.exit(1);
        }
    }

    /**
     * this method would create a 'flightpath' table in the derby database and write data in.
     *
     * note that after making a database query, the statement of creating the connection needs to be closed
     * and at the end of the method, as well as the connection to the database.
     *
     * @param flightPaths this is a list of FlightPath objects, with each corresponding to a single path taken by
     *                    the drone in making the day's deliveries.
     */
    public void createFlightPathDb(List<FlightPath> flightPaths){
        String jdbcString = getJdbcString();
        try {
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
                    String entry = "('"+flightPath.orderNo+"',"+flightPath.fromLongitude+","+flightPath.fromLatitude+","+flightPath.angle+","
                            +flightPath.tolongitude +","+flightPath.toLatitude+"),";
                    dataBaseQueryBody.append(entry);
                }
            }catch(NullPointerException|ArrayIndexOutOfBoundsException e){
                System.err.println("No order is made!");
                System.exit(1);
            }
            dataBaseQueryBody.setLength(dataBaseQueryBody.length()-1);
            statement.execute(dataBaseQuery+dataBaseQueryBody);
            System.out.println("Database generated successfully");
            statement.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("The connection failed in flightpath table");
            System.exit(1);
        }catch(Exception e){
            System.exit(1);
        }
    }

}
