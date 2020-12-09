package com.company;
import java.sql.*;

public class WarehouseDB {
    private Connection conn;
    static final String local_format = "%-25s%-25s%-25s";
    static final String product_format = "%-25s%-25s%-25s%-25s%-25s%-25s";

    public WarehouseDB(Connection conn) { this.conn = conn; }

    public void save(Warehouse warehouse) {
        try {
            String query = "insert into warehouses (warehousename, address, phone)" +
                    "values (?, ?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, warehouse.getName());
            pStmt.setString(2, warehouse.getAddress());
            pStmt.setString(3, warehouse.getPhone());

            pStmt.executeUpdate();

            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Warehouse getPOJO(String warehousename) {
        String resultName;
        String address;
        String phone;

        try {
            String query = "select * from warehouses where warehousename = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, warehousename);
            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                resultName = rs.getString("WAREHOUSENAME");
                address = rs.getString("ADDRESS");
                phone = rs.getString("PHONE");
                return new Warehouse(resultName, address, phone);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEmpty() {
        try {
            String query = "select * from warehouses";
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

    public void update(Warehouse warehouse) {
        try {
            String query = "update warehouses set address = ?, phone = ? where warehousename = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, warehouse.getAddress());
            pStmt.setString(2, warehouse.getPhone());
            pStmt.setString(3, warehouse.getName());

            pStmt.executeUpdate();

            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printAll() {
        try {
            String query = "select * from warehouses";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Warehouses: ");
            System.out.printf(local_format, "Warehouse Name", "Address", "Phone");
            System.out.println();

            while(rs.next()) {
                String warehousename = rs.getString("WAREHOUSENAME");
                String address = rs.getString("ADDRESS");
                String phone = rs.getString("PHONE");

                System.out.printf(local_format, warehousename, address, phone);
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printProducts(String warehousename) {
        try {
            String query = "select * from products where warehousename = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, warehousename);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Products in " + warehousename + " :");
            System.out.printf(product_format, "Product ID", "Product Name", "Sell Price", "Buy Price", "Stock", "Warehouse Name");
            System.out.println();

            while(rs.next()) {
                int productid = rs.getInt("PRODUCTID");
                String productname = rs.getString("PRODUCTNAME");
                double sellprice = rs.getDouble("SELLPRICE");
                double buyprice = rs.getDouble("BUYPRICE");
                int stock = rs.getInt("STOCK");

                System.out.printf(product_format, productid, productname, sellprice, buyprice, stock, warehousename);
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
