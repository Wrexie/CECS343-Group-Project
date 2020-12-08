package com.company;
import java.sql.*;

public class WarehouseDB {
    private Connection conn;

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
}
