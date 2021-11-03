package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

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
                String jdbcString2 = getJdbcString();
                final String dataQuery2 = "select * from " + ORDER_DETAILS + " where " + "orderNo" + "=(?)";
                try {
                    Connection conn2 = DriverManager.getConnection(jdbcString2);
                    PreparedStatement psDataQuery2 = conn2.prepareStatement(dataQuery2);
                    psDataQuery2.setString(1, orderNo);

                    ResultSet rs2 = psDataQuery2.executeQuery();
                    while (rs2.next()){
                        String itemName = rs2.getString("item");
                        itemList.add(itemName);
                    }
                } catch (SQLException e){
                    System.out.println("The connection failed"); // need to work on error handling later
                }
                Order order = new Order(orderNo, deliverTo,itemList);
                orderList.add(order);
            }
        } catch (SQLException e){
            System.out.println("The connection failed"); // need to work on error handling later
           // e.printStackTrace();
        }
        return orderList;  // the list could be empty if no order is found with the given date.
    }

}
