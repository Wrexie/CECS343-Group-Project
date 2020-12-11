package com.company;
import java.sql.*;

//WarehouseDB class
//Handles all required interactions with database for warehouse data
public class WarehouseDB {
    //Private connection variable
    private Connection conn;
    //Print format for printing warehouse data
    static final String local_format = "%-25s%-25s%-25s";
    //Print format for printing product data
    static final String product_format = "%-25s%-25s%-25s%-25s%-25s%-25s";

    //Constructor. Takes in database connection
    public WarehouseDB(Connection conn) { this.conn = conn; }

    //Saves new warehouse entity object to database
    public void save(Warehouse warehouse) {
        try {
            //Insert new warehouse object into database
            String query = "insert into warehouses (warehousename, address, phone)" +
                    "values (?, ?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, warehouse.getName());
            pStmt.setString(2, warehouse.getAddress());
            pStmt.setString(3, warehouse.getPhone());

            //Execute statement
            pStmt.executeUpdate();

            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Returns requested warehouse entity object from DB
    public Warehouse getPOJO(String warehousename) {
        //Attribute variables
        String resultName;
        String address;
        String phone;

        try {
            //Selects all data from specified warehouse
            String query = "select * from warehouses where warehousename = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, warehousename);
            ResultSet rs = pStmt.executeQuery();

            //Iterates through data returned
            if (rs.next()) {
                resultName = rs.getString("WAREHOUSENAME");
                address = rs.getString("ADDRESS");
                phone = rs.getString("PHONE");

                //Creates and returned requested warehouse entity object
                return new Warehouse(resultName, address, phone);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Returns null by default if requested data is not found
        return null;
    }

    //Checks if table is empty
    public boolean isEmpty() {
        try {
            //Selects all data from warehouse table
            String query = "select * from warehouses";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            //If no data is returned then table is empty
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

    //Updates existing database entry
    public void update(Warehouse warehouse) {
        try {
            //Update the address and phone for specified warehouse
            String query = "update warehouses set address = ?, phone = ? where warehousename = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, warehouse.getAddress());
            pStmt.setString(2, warehouse.getPhone());
            pStmt.setString(3, warehouse.getName());

            //Execute update statement
            pStmt.executeUpdate();

            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Print all data from warehouse table
    public void printAll() {
        try {
            //Select all data from warehouse table
            String query = "select * from warehouses";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Warehouses: ");
            System.out.printf(local_format, "Warehouse Name", "Address", "Phone");
            System.out.println();

            //Iterate through all rows returned
            while(rs.next()) {
                String warehousename = rs.getString("WAREHOUSENAME");
                String address = rs.getString("ADDRESS");
                String phone = rs.getString("PHONE");

                //Print data
                System.out.printf(local_format, warehousename, address, phone);
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Print all products belonging to specified warehouse
    public void printProducts(String warehousename) {
        try {
            //Select all products belonging to specified warehouse
            String query = "select * from products where warehousename = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, warehousename);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Products in " + warehousename + " :");
            System.out.printf(product_format, "Product ID", "Product Name", "Sell Price", "Buy Price", "Stock", "Warehouse Name");
            System.out.println();

            //Iterates through all rows returned from the database
            while(rs.next()) {
                int productid = rs.getInt("PRODUCTID");
                String productname = rs.getString("PRODUCTNAME");
                double sellprice = rs.getDouble("SELLPRICE");
                double buyprice = rs.getDouble("BUYPRICE");
                int stock = rs.getInt("STOCK");

                //Print data
                System.out.printf(product_format, productid, productname, sellprice, buyprice, stock, warehousename);
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
