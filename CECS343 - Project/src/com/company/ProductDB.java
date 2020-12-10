package com.company;

import java.sql.*;
import java.text.DecimalFormat;

//ProductDB class
//Handles all of the required interaction with the database for product data
public class ProductDB {
    //local database connection variable
    private Connection conn;
    //Print format for printing product data retrieved from database
    static final String local_format = "%-25s%-25s%-25s%-25s%-25s%-25s";
    //Print format for printing profit data retrieved from database
    static final String profit_format = "%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s";
    //Formatting for decimal values
    private static final DecimalFormat df = new DecimalFormat("0.00");

    //Constructor. Takes in DB connection
    public ProductDB(Connection conn) { this.conn = conn; };

    //Save method that saves entity object to database
    public void save(Product product) {
        try {
            //Statement to insert entity object into database
            String query = "insert into products (productname, sellprice," +
                    "buyprice, stock, warehousename) values (?, ?, ?, ?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, product.getProductName());
            pStmt.setDouble(2, product.getSellPrice());
            pStmt.setDouble(3, product.getBuyPrice());
            pStmt.setInt(4, product.getStock());
            pStmt.setString(5, product.getWarehouse().getName());

            //Execute update
            pStmt.executeUpdate();

            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Returns entity object created from data retrieved from database
    public Product getPOJO(int id) {
        //Attribute variables
        int resultID;
        String productName;
        double sellPrice;
        double buyPrice;
        int stock;

        //Warehouse object
        String warehousename;
        Warehouse warehouse;
        //WarehouseDB object created to select required warehouse object from DB
        WarehouseDB warehouseDB = new WarehouseDB(conn);

        try {
            //Selects all product data from the product specified by user
            String query = "select * from products where productid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);

            //Execute query and store retrieved data in result set
            ResultSet rs = pStmt.executeQuery();

            //Iterate through all returned rows from DB if any
            if(rs.next()) {
                //Set all attribute variables to respective data retrieved from DB
                resultID = rs.getInt("PRODUCTID");
                productName = rs.getString("PRODUCTNAME");
                sellPrice = rs.getDouble("SELLPRICE");
                buyPrice = rs.getDouble("BUYPRICE");
                stock = rs.getInt("STOCK");
                warehousename = rs.getString("WAREHOUSENAME");

                //Retrieve appropriate warehouse object from DB
                warehouse = warehouseDB.getPOJO(warehousename);

                //Create and return new entity object from data retrieved from DB
                return new Product(resultID, productName, sellPrice, buyPrice, stock, warehouse);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Return null by default if data requested is not found
        return null;
    }

    //Checks if table is empty
    public boolean isEmpty() {
        try {
            //Select all data from table
            String query = "select * from products";
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

    //Update an existing entry in the database
    public void update(Product product) {
        try {
            //Statement to update stock for specified product
            String query = "update products set stock = ? where productid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, product.getStock());
            pStmt.setInt(2, product.getProductID());

            //Execute update
            pStmt.executeUpdate();

            pStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Print all data from table
    public void printAll() {
        try {
            //Select all data from table
            String query = "select * from products";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Products: ");
            System.out.printf(local_format, "Product ID", "Product Name", "Sell Price", "Buy Price", "Stock", "Warehouse Name");
            System.out.println();

            //Iterate through all rows in table
            while(rs.next()) {
                //Set all attribute variables to respective data returned from DB
                int productid = rs.getInt("PRODUCTID");
                String productname = rs.getString("PRODUCTNAME");
                double sellprice = rs.getDouble("SELLPRICE");
                double buyprice = rs.getDouble("BUYPRICE");
                int stock = rs.getInt("STOCK");
                String warehousename = rs.getString("WAREHOUSENAME");

                //Print all data
                System.out.printf(local_format, productid, productname, df.format(sellprice), df.format(buyprice), stock, warehousename);
                System.out.println();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Prints all products with stock <= 5
    public void printNeedRestock() {
        try {
            //Select all products where stock <= 5
            String query = "select * from products where stock <= 5 order by stock asc";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Products that need to be restocked: ");
            System.out.printf(local_format, "Product ID", "Product Name", "Sell Price", "Buy Price", "Stock", "Warehouse Name");
            System.out.println();

            //Iterate through all rows returned from DB
            while(rs.next()) {
                //Set all attribute variables to respective data returned from DB
                int productid = rs.getInt("PRODUCTID");
                String productname = rs.getString("PRODUCTNAME");
                double sellprice = rs.getDouble("SELLPRICE");
                double buyprice = rs.getDouble("BUYPRICE");
                int stock = rs.getInt("STOCK");
                String warehousename = rs.getString("WAREHOUSENAME");

                //Print all data
                System.out.printf(local_format, productid, productname, df.format(sellprice), df.format(buyprice), stock, warehousename);
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Prints all product data and their total cost, sales and profit
    public void printProfits() {
        try {
            //Selects all data from products and calculates total sales $, total cost $ and total profit percentage
            String query = "select productname, sellprice, buyprice, sum(stock), sum(quantityordered)," +
                    " sum(buyprice * quantityordered), sum(sellprice * quantityordered)," +
                    " sum(100-((buyprice * quantityordered)/(sellprice * quantityordered))*100) from products" +
                    " inner join orderdetails on orderdetails.productid = products.PRODUCTID" +
                    " group by productname, sellprice, buyprice order by 8 asc";

            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Product Profits: ");
            System.out.printf(profit_format, "Product Name", "Sell Price", "Buy Price", "Total Stock",
                    "Total Ordered", "Total Sales $", "Total Cost $", "Profit Percentage");
            System.out.println();

            //Iterate through all rows returned
            while(rs.next()) {
                //Set attribute variables to respective data returned from DB
                String productname = rs.getString("PRODUCTNAME");
                double sellprice =  rs.getDouble("SELLPRICE");
                double buyprice = rs.getDouble("BUYPRICE");
                int totalStock = rs.getInt("4");
                int totalOrdered = rs.getInt("5");
                double totalCost = rs.getDouble("6");
                double totalSales = rs.getDouble("7");
                double profitPercent = 100-((totalCost/totalSales)*100);

                //Print all data
                System.out.printf(profit_format, productname, df.format(sellprice), df.format(buyprice), totalStock, totalOrdered,
                        df.format(totalSales), df.format(totalCost), df.format(profitPercent));
                System.out.println();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
