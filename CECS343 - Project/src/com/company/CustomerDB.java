package com.company;

import java.sql.*;

public class CustomerDB {
    private Connection conn;

    public CustomerDB(Connection conn) {
        this.conn = conn;
    }


    public void save(Customer customer) {
        try {
            String query = "insert into customers(fullname, address, status, taxRate) values (?, ?, ?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, customer.getFullName());
            pStmt.setString(2, customer.getShipAddress());
            pStmt.setString(3, customer.getStatus().toString());
            pStmt.setDouble(4, customer.getTaxRate());

            pStmt.executeUpdate();

            pStmt.close();
        } catch(SQLException e) {
            e.printStackTrace();;
        }

    }

    public Customer getPOJO(int id) {
        int resultID;
        String fullName;
        String shipAddress;
        CustomerStatus status;
        double taxRate;
        try {
            String query = "select * from customers where customerid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);
            ResultSet rs = pStmt.executeQuery();

            if(rs.next()) {
                resultID = rs.getInt("CUSTOMERID");
                fullName = rs.getString("FULLNAME");
                shipAddress = rs.getString("ADDRESS");
                status = CustomerStatus.valueOf(rs.getString("STATUS"));
                taxRate = rs.getDouble("TAXRATE");
                return new Customer(resultID, fullName, shipAddress, status, taxRate);
            }

        } catch(SQLException e) {
            e.printStackTrace();;
        }
        return null;
    }
}
