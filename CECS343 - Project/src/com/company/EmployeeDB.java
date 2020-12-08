package com.company;
import java.sql.*;

public class EmployeeDB {
    private Connection conn;
    static final String local_format = "%-25s%-25s%-25s%-25s";

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

    public void update(Employee employee) {
        try {
            String query = "update employees set commissionrate = ? where employeeid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setDouble(1, employee.getCommRate());
            pStmt.setInt(2, employee.getEmployeeID());

            pStmt.executeUpdate();
            pStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printAll() {
        try {
            String query = "select * from employees";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Employees:");
            System.out.printf(local_format, "EmployeeID", "Fullname", "Commission Rate", "Phone");
            System.out.println();

            while(rs.next()) {
                int employeeid = rs.getInt("EMPLOYEEID");
                String fullname = rs.getString("FULLNAME");
                double commRate = rs.getDouble("COMMISSIONRATE");
                String phone = rs.getString("PHONE");
                System.out.printf(local_format, employeeid, fullname, commRate, phone);
                System.out.println();

            }
            System.out.println("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
