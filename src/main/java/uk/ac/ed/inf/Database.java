package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    private static final String server = "localhost"; // machine name of the database: localhost
    public String dataBasePort; // the portal, default is 1527
    public String dataBaseName; // the name of the database we want to access

    public Database(String dataBasePort, String dataBaseName) {
        this.dataBasePort = dataBasePort;
        this.dataBaseName = dataBaseName;
    }
    // return the database location
    private String getJdbcString(){
        return "jdbc:derby://" + server + ":" + dataBasePort + "/derbyDB";
    }

    // need a method to get the orders of the day based on the data
    // it will return a list of tuples as a record of all the orders made in that day
    // each tuple is in the form (orderNo, customer, deliverTo)
    public ArrayList<Order> getOrders(String date){
        ArrayList<Order> orderList = new ArrayList<>();
        String jdbcString = getJdbcString();
        // the name of the column to access the table orders
        String ordersAccessCol = "deliveryDate";
        final String dateQuery = "select * from " + dataBaseName + " where " + ordersAccessCol + "=(?)";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psDateQuery = conn.prepareStatement(dateQuery);
            psDateQuery.setString(1, date);

            ResultSet rs = psDateQuery.executeQuery();
            while (rs.next()){
                String orderNo = rs.getString("orderNo");
                String customer = rs.getString("customer");
                String deliverTo = rs.getString("deliverTo");
                Order order = new Order(orderNo, customer, deliverTo);
                orderList.add(order);
            }
        } catch (SQLException e){
            System.out.println("The connection failed"); // need to work on error handling later
           // e.printStackTrace();
        }
        return orderList;  // the list could be empty if no order is found with the given date.
    }

    // get a list of all the items placed in an order by looking in to the order details table
    public ArrayList<String> getOrderDetails(String orderNo){
        ArrayList<String> itemList = new ArrayList<>();
        String jdbcString = getJdbcString();
        // the name of the column to access the orderDetails table
        String orderDetailsAccessCol = "orderNo";
        final String dateQuery = "select * from " + dataBaseName + " where " + orderDetailsAccessCol + "=(?)";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psDateQuery = conn.prepareStatement(dateQuery);
            psDateQuery.setString(1, orderNo);

            ResultSet rs = psDateQuery.executeQuery();
            while (rs.next()){
                String itemName = rs.getString("item");
                itemList.add(itemName);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return itemList;
    }

    // inner class to help phrase the content in the orders database
    public static class Order {
        private final String orderNO;
        private final String customer;
        private final String deliverTo;

        public Order(String orderNO, String customer, String deliverTo) {
            this.orderNO = orderNO;
            this.customer = customer;
            this.deliverTo = deliverTo;
        }

        // get the orderNo of the order
        public String getOrderNO() {
            return orderNO;
        }

        // get the customer id associated with the order
        public String getCustomer() {
            return customer;
        }

        // get the address to deliver to
        public String getDeliverTo() {
            return deliverTo;
        }
    }


}
