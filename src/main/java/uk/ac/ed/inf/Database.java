package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String server = "localhost"; // machine name of the database: localhost
    public String dataBasePort; // the portal, default is 1527
    private static final String ORDERS = "orders";
    private static final String ORDER_DETAILS = "orderDetails";

    public Database(String dataBasePort) {
        this.dataBasePort = dataBasePort;
    }

    // return the database location
    private String getJdbcString(){
        return "jdbc:derby://" + server + ":" + dataBasePort + "/derbyDB";
    }

    // return a list of orders as Order objects with a given date
    public ArrayList<Order> getOrders(String date){
        ArrayList<Order> orderList = new ArrayList<>();
        String jdbcString = getJdbcString();
        // the name of the column to access the table orders
        String deliveryDate = "deliveryDate";
        final String dataQuery = "select * from " + ORDERS + " where " + deliveryDate + "=(?)";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psDataQuery = conn.prepareStatement(dataQuery);
            psDataQuery.setString(1, date);

            ResultSet rs = psDataQuery.executeQuery();
            while (rs.next()){
                String orderNo = rs.getString("orderNo");
                String deliverTo = rs.getString("deliverTo");
                // get a list of all the items placed in an order by looking in to the order details table
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
                    System.err.println("The connection failed in orderDetails database"); // need to work on error handling later
                }
                Order order = new Order(orderNo, deliverTo,itemList);
                orderList.add(order);
            }
             psDataQuery.close(); // shutdown the database
             conn.close();
        } catch (SQLException e){
            System.err.println("The connection failed in order database"); // need to work on error handling later
            System.exit(1);
           // e.printStackTrace();
        }
        return orderList;  // the list could be empty if no order is found with the given date.
    }

    // create the deliveries table in the derbyDB database
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
                    "costInPence int)");  // create an empty deliveries table
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
        }
    }

    // create the deliveries table in the derbyDB database
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
                    "toLatitude double)");  // create an empty flightpath table
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
            statement.close();  //shutdown the database
            conn.close();
        } catch (SQLException e) {
            System.err.println("The connection failed in flightpath table");
        }
    }

}
