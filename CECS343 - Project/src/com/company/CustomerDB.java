package com.company;

import java.sql.*;

public class CustomerDB {
    private Connection connection;

    public CustomerDB(Connection connection) {
        this.connection = connection;
    }


    public void save(Customer customer) {
        try {
            String query = "insert into customers(fullname, address, status, taxRate) values (?, ?, ?, ?)";
            PreparedStatement pStmt = connection.prepareStatement(query);
            pStmt.setString(1, customer.getFullName());
            pStmt.setString(2, customer.getShipAddress());
            pStmt.setString(3, customer.getStatus().toString());
            pStmt.setDouble(4, customer.getTaxRate());

        } catch(SQLException e) {
            e.printStackTrace();;
        }
    }
}
