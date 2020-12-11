package com.company;

import java.sql.*;

//CustomerDB class
//Handles all required interactions with the database for customer data
public class CustomerDB {
    //Private connection variable
    private Connection conn;
    //Print format for printing customer data retrieved from database
    static final String local_format = "%-25s%-25s%-25s%-25s%-25s%-25s";

    //Constructor. Takes in database connection
    public CustomerDB(Connection conn) {
        this.conn = conn;
    }

    //Saves customer object to database
    public void save(Customer customer) {
        try {
            //Statement to insert new customer object to database
            String query = "insert into customers (fullname, address, status, phone, salestax) values (?, ?, ?, ?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, customer.getFullName());
            pStmt.setString(2, customer.getShipAddress());
            pStmt.setString(3, customer.getStatus().toString());
            pStmt.setString(4, customer.getPhone());
            pStmt.setDouble(5, customer.getTaxRate());

            //Execute statement
            pStmt.executeUpdate();

            pStmt.close();
        } catch(SQLException e) {
            e.printStackTrace();;
        }

    }

    //Returns requested entity object from database
    public Customer getPOJO(int id) {
        //Attribute variables
        int resultID;
        String fullName;
        String shipAddress;
        CustomerStatus status;
        double taxRate;
        String phone;

        try {
            //Selects all data for specified customer
            String query = "select * from customers where customerid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);
            //Saves data returned to result set
            ResultSet rs = pStmt.executeQuery();

            //Iterate through rows returned if any
            if(rs.next()) {
                resultID = rs.getInt("CUSTOMERID");
                fullName = rs.getString("FULLNAME");
                shipAddress = rs.getString("ADDRESS");
                status = CustomerStatus.valueOf(rs.getString("STATUS"));
                taxRate = rs.getDouble("SALESTAX");
                phone = rs.getString("PHONE");

                //Create and return requested customer entity object
                return new Customer(resultID, fullName, shipAddress, phone, status, taxRate);
            }

        } catch(SQLException e) {
            e.printStackTrace();;
        }
        //Return null by default if requested data is not found
        return null;
    }

    //Updates existing database entry with new data provided by user
    public void update(Customer customer) {
        try {
            //Update salestax for specified user
            String query = "update customers set salestax = ?, status = ? where customerid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setDouble(1, customer.getTaxRate());
            pStmt.setString(2, customer.getStatus().toString());
            pStmt.setInt(3, customer.getId());

            //Execute statement
            pStmt.executeUpdate();

            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Print all data from customers table
    public void printAll() {
        try {
            //Select all data from customers table
            String query = "select * from customers";
            PreparedStatement pStmt = conn.prepareStatement(query);
            //Store returned data in result set
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Customers:");
            System.out.printf(local_format, "CustomerID", "Fullname", "TaxRate",
                    "Address", "Status", "Phone");
            System.out.println();

            //Iterate through all rows of returned data
            while(rs.next()) {
                int customerid = rs.getInt("CUSTOMERID");
                String fullname = rs.getString("FULLNAME");
                double taxrate = rs.getDouble("SALESTAX");
                String address = rs.getString("ADDRESS");
                String status = rs.getString("STATUS");
                String phone = rs.getString("PHONE");

                //Print data
                System.out.printf(local_format, customerid, fullname, taxrate, address, status, phone);
                System.out.println();
            }
            System.out.println("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Checks if table is empty
    public boolean isEmpty() {
        try {
            //Select all data from table
            String query = "select * from customers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            //If nothing is returned then table is empty. If not then table is not empty
            if(rs.next() == false) {
                return true;
            }
            else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
    //Deletes all entries from customers table
    public void deleteAll() {
        try {
            String query = "delete from customers";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

}
