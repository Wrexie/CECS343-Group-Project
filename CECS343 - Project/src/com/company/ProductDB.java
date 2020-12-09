package com.company;
import org.apache.derby.iapi.types.SqlXmlUtil;

import java.sql.*;

public class ProductDB {
    private Connection conn;

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

                /*
                query = "select * from warehouses where warehousename = ?";
                pStmt = conn.prepareStatement(query);
                pStmt.setString(1, rs.getString("WAREHOUSENAME"));
                rs = pStmt.executeQuery();
                if(rs.next()) {
                    warehousename = rs.getString("WAREHOUSENAME");
                    address = rs.getString("ADDRESS");
                    phone = rs.getString("PHONE");

                    warehouse = new Warehouse(warehousename, address, phone);
                    return new Product(resultID, productName, sellPrice, buyPrice, stock, warehouse);
                }*/
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



}
