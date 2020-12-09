package com.company;

import java.sql.*;
import java.text.DecimalFormat;

public class ProductDB {
    private Connection conn;
    static final String local_format = "%-25s%-25s%-25s%-25s%-25s%-25s";
    static final String profit_format = "%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s";
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public ProductDB(Connection conn) { this.conn = conn; };

    public void save(Product product) {
        try {
            String query = "insert into products (productname, sellprice," +
                    "buyprice, stock, warehousename) values (?, ?, ?, ?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, product.getProductName());
            pStmt.setDouble(2, product.getSellPrice());
            pStmt.setDouble(3, product.getBuyPrice());
            pStmt.setInt(4, product.getStock());
            pStmt.setString(5, product.getWarehouse().getName());

            pStmt.executeUpdate();

            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Product getPOJO(int id) {
        int resultID;
        String productName;
        double sellPrice;
        double buyPrice;
        int stock;

        String warehousename;
        Warehouse warehouse;
        WarehouseDB warehouseDB = new WarehouseDB(conn);

        try {
            String query = "select * from products where productid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);
            ResultSet rs = pStmt.executeQuery();

            if(rs.next()) {
                resultID = rs.getInt("PRODUCTID");
                productName = rs.getString("PRODUCTNAME");
                sellPrice = rs.getDouble("SELLPRICE");
                buyPrice = rs.getDouble("BUYPRICE");
                stock = rs.getInt("STOCK");
                warehousename = rs.getString("WAREHOUSENAME");

                warehouse = warehouseDB.getPOJO(warehousename);

                return new Product(resultID, productName, sellPrice, buyPrice, stock, warehouse);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEmpty() {
        try {
            String query = "select * from products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
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

    public void update(Product product) {
        try {
            String query = "update products set stock = ? where productid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, product.getStock());
            pStmt.setInt(2, product.getProductID());

            pStmt.executeUpdate();

            pStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void printAll() {
        try {
            String query = "select * from products";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Products: ");
            System.out.printf(local_format, "Product ID", "Product Name", "Sell Price", "Buy Price", "Stock", "Warehouse Name");
            System.out.println();

            while(rs.next()) {
                int productid = rs.getInt("PRODUCTID");
                String productname = rs.getString("PRODUCTNAME");
                double sellprice = rs.getDouble("SELLPRICE");
                double buyprice = rs.getDouble("BUYPRICE");
                int stock = rs.getInt("STOCK");
                String warehousename = rs.getString("WAREHOUSENAME");

                System.out.printf(local_format, productid, productname, df.format(sellprice), df.format(buyprice), stock, warehousename);
                System.out.println();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printNeedRestock() {
        try {
            String query = "select * from products where stock <= 5 order by stock asc";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Products that need to be restocked: ");
            System.out.printf(local_format, "Product ID", "Product Name", "Sell Price", "Buy Price", "Stock", "Warehouse Name");
            System.out.println();

            while(rs.next()) {
                int productid = rs.getInt("PRODUCTID");
                String productname = rs.getString("PRODUCTNAME");
                double sellprice = rs.getDouble("SELLPRICE");
                double buyprice = rs.getDouble("BUYPRICE");
                int stock = rs.getInt("STOCK");
                String warehousename = rs.getString("WAREHOUSENAME");

                System.out.printf(local_format, productid, productname, df.format(sellprice), df.format(buyprice), stock, warehousename);
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printProfits() {
        try {
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

            while(rs.next()) {
                String productname = rs.getString("PRODUCTNAME");
                double sellprice =  rs.getDouble("SELLPRICE");
                double buyprice = rs.getDouble("BUYPRICE");
                int totalStock = rs.getInt("4");
                int totalOrdered = rs.getInt("5");
                double totalCost = rs.getDouble("6");
                double totalSales = rs.getDouble("7");
                double profitPercent = 100-((totalCost/totalSales)*100);

                System.out.printf(profit_format, productname, df.format(sellprice), df.format(buyprice), totalStock, totalOrdered,
                        df.format(totalSales), df.format(totalCost), df.format(profitPercent));
                System.out.println();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
