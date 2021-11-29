package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
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
    /**
     * this is the name of the 'orders' database.
     */
    private static final String ORDERS = "orders";
    /**
     * this is the name of the 'orderDetails' database.
     */
    private static final String ORDER_DETAILS = "orderDetails";

    /**
     * this is the constructor of the Database class
     *
     * @param dataBasePort this is the portal number of the database to access.
     */
    public DataBase(String dataBasePort) {
        this.dataBasePort = dataBasePort;
    }

    /**
     * this is the location of the database to access
     *
     * @return the location of the database to access the derby database.
     */
    public String getJdbcString(){
        return "jdbc:derby://" + server + ":" + dataBasePort + "/derbyDB";
    }

    /**
     * this method would access the database for information related to an order on a given day
     * the parameter would be used to select orders from the 'orders' table on the delivery date specified.
     * 'orderDetails' table will also be accessed for item names in each order.
     * the information regarding a single order obtained from the tables will be used to create an Order object
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
            while (rs.next()){
                String orderNo = rs.getString("orderNo");
                String deliverTo = rs.getString("deliverTo");
                ArrayList<String> itemList = new ArrayList<>();
                final String dataQuery2 = "select * from " + ORDER_DETAILS + " where " + "orderNo" + "=(?)";
                try {
                    PreparedStatement psDataQuery2 = conn.prepareStatement(dataQuery2);
                    psDataQuery2.setString(1, orderNo);
                    ResultSet rs2 = psDataQuery2.executeQuery();
                    while (rs2.next()) {
                        String itemName = rs2.getString("item");
                        itemList.add(itemName);
                    }
                    psDataQuery2.close();
                } catch (SQLException e){
                    System.err.println("Failed in connecting to orderDetails database");
                    System.exit(1);
                }
                Order order = new Order(orderNo, deliverTo,itemList);
                orderList.add(order);
            }
             psDataQuery.close();
             conn.close(); // shutdown the database
        } catch (SQLException e){
            System.err.println("Failed in connecting to orders database");
            System.exit(1);
        }
        return orderList;

    }

    /**
     * this method would create a 'deliveries' table in the derby database and write data in.
     * this method would first check the existence of 'deliveries' table, if there's one such table
     * in the database, the old one would be dropped and replaced by the latest created one.
     * the data write in is related to the orders made on the specified day.
     * the data includes order number, the location of delivery and the monetary value of making this
     * order.
     * in order to get the monetary value of the order, the method require access to the web server
     * hence, web server is required.
     *
     * note that after making a database query, the statement of creating the connection needs to be closed
     * and at the end of the method, as well as the connection to the database.
     *
     * @param orderList this is a list of Order objects, corresponding to the orders made on the specified day.
     * @param webServerPort this is the portal number of the web server to access the order's delivery cost.
     */
    public void createDeliveriesDb(List<Order> orderList, String webServerPort){
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
            PreparedStatement psOrder = conn.prepareStatement(
                    "insert into deliveries values (?,?,?)");
            for(Order order : orderList){
                psOrder.setString(1, order.orderNO);
                psOrder.setString(2,order.deliverTo);
                psOrder.setInt(3,order.getOrderCost(webServerPort));
                psOrder.execute();
            }
            System.out.println("Database generated successfully");
            statement.close();  //shutdown the database
            conn.close();
        } catch (SQLException e) {
            System.err.println("The connection failed in deliveries table");
            System.exit(1);
        }
    }

    /**
     * this method would create a 'flightpath' table in the derby database and write data in.
     * this method would first check the existence of 'flightpath' table, if there's one such table
     * in the database, the old one would be dropped and replaced by the latest created one.
     * the data write in is related to the paths taken by the drone in making the deliveries on the day specified.
     * each flight path includes: the longitude and latitude of where the drone is from, the longitude and latitude
     * of where the drone is flying to and the angle between the two location.
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
            PreparedStatement psFlightPath = conn.prepareStatement(
                    "insert into flightpath values (?,?,?,?,?,?)");
            for(FlightPath flightPath : flightPaths){
                psFlightPath.setString(1, flightPath.orderNo);
                psFlightPath.setDouble(2,flightPath.fromLongitude);
                psFlightPath.setDouble(3,flightPath.fromLatitude);
                psFlightPath.setInt(4,flightPath.angle);
                psFlightPath.setDouble(5,flightPath.tolongitude);
                psFlightPath.setDouble(6,flightPath.toLatitude);
                psFlightPath.execute();
            }
            System.out.println("Database generated successfully");
            statement.close();
            conn.close(); //shutdown the database
        } catch (SQLException e) {
            System.err.println("The connection failed in flightpath table");
            System.exit(1);
        }
    }

}
