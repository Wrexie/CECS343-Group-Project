package com.company;

import java.sql.*;

public class CustomerDB {
    private Connection conn;
    static final String local_format = "%-25s%-25s%-25s%-25s%-25s%-25s";

    public CustomerDB(Connection conn) {
        this.conn = conn;
    }

    public void save(Customer customer) {
        try {
            String query = "insert into customers (fullname, address, status, phone, salestax) values (?, ?, ?, ?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, customer.getFullName());
            pStmt.setString(2, customer.getShipAddress());
            pStmt.setString(3, customer.getStatus().toString());
            pStmt.setString(4, customer.getPhone());
            pStmt.setDouble(5, customer.getTaxRate());

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
        String phone;
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
                phone = rs.getString("PHONE");
                return new Customer(resultID, fullName, shipAddress, phone, status, taxRate);
            }

        } catch(SQLException e) {
            e.printStackTrace();;
        }
        return null;
    }

    public void update(Customer customer) {
        try {
            String query = "update customers set taxrate = ?, status = ? where customerid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setDouble(1, customer.getTaxRate());
            pStmt.setString(2, customer.getStatus().toString());
            pStmt.setInt(3, customer.getId());

            pStmt.executeUpdate();

            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printAll() {
        try {
            String query = "select * from customers";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Customers:");
            System.out.printf(local_format, "CustomerID", "Fullname", "TaxRate",
                    "Address", "Status", "Phone");
            System.out.println();
            while(rs.next()) {
                int customerid = rs.getInt("CUSTOMERID");
                String fullname = rs.getString("FULLNAME");
                double taxrate = rs.getDouble("SALESTAX");
                String address = rs.getString("ADDRESS");
                boolean status = rs.getBoolean("STATUS");
                String phone = rs.getString("PHONE");
                System.out.printf(local_format, customerid, fullname, taxrate, address, status, phone);
                System.out.println();
            }
            System.out.println("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        try {
            String query = "delete from customers";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmail() {
        try {
            String query = "alter table customers drop column email";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
