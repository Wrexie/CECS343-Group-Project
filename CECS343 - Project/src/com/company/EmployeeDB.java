package com.company;
import java.sql.*;

public class EmployeeDB {
    private Connection conn;

    public EmployeeDB(Connection conn) { this.conn = conn; }

    public void save(Employee employee) {
        try{
            String query = "insert into employees (fullname, commissionrate, phone)" +
                    "values (?, ?, ?)";

            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, employee.getEmployeeName());
            pStmt.setDouble(2, employee.getCommRate());
            pStmt.setString(3, employee.getPhone());

            pStmt.executeUpdate();
            pStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Employee getPOJO(int id) {
        int resultID;
        String fullname;
        double commRate;
        String phone;

        try{
            String query = "select * from employees where employeeid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);
            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                resultID = rs.getInt("EMPLOYEEID");
                fullname = rs.getString("FULLNAME");
                commRate = rs.getDouble("COMMISSIONRATE");
                phone = rs.getString("PHONE");
                return new Employee(resultID, fullname, phone, commRate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
