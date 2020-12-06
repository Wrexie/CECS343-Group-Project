package com.company;
import java.sql.*;
import java.time.LocalDate;

public class InvoiceDB {
    private Connection conn;
    public InvoiceDB(Connection conn) {this.conn = conn;}

    public void save(Invoice invoice) {
        try {
            String query = "insert into invoices(invoiceid, total, remainingbalance, customerid, employeeid," +
                    "deliveryfee, isdeliverable, opendate, thirtydaycount, taxamount, comissionamount, status)" +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setDouble(2, invoice.getTotal());
            pStmt.setDouble(3, invoice.getOwed());
            pStmt.setInt(4, invoice.getCustomer().getId());
            pStmt.setInt(5, invoice.getEmployee().getEmployeeID());
            pStmt.setDouble(6, invoice.getDeliveryFee());
            pStmt.setBoolean(7, invoice.getIsDeliverable());
            pStmt.setDate(8, java.sql.Date.valueOf(invoice.getOpenedDate()));
            pStmt.setInt(9, invoice.getThirtyDayCount());
            pStmt.setDouble(10, invoice.getTaxAmt());
            pStmt.setDouble(11, invoice.getCommAmount());
            pStmt.setString(12, invoice.getStatus().toString());

            pStmt.executeUpdate();
            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //TODO: finish method
    public Invoice getPOJO(int id){
        int resultID;
        double total;
        double remainingBalance;
        int customerID;
        int employeeID;
        double deliveryFee;
        boolean isDeliverable;
        LocalDate openedDate;
        int thirtyDayCount;
        double taxAmt;
        double commAmount;
        InvoiceStatus status;

        try {
            String query = "select * from invoices where invoiceid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);
            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                resultID = rs.getInt("INVOICEID");
                total = rs.getDouble("TOTAL");
                remainingBalance = rs.getDouble("REMAININGBALANCE");
                customerID = rs.getInt("CUSTOMERID");
                employeeID = rs.getInt("EMPLOYEEID");
                deliveryFee = rs.getDouble("DELIVERYFEE");
                isDeliverable = rs.getBoolean("ISDELIVERABLE");
                openedDate = rs.getDate("OPENDATE").toLocalDate();
                thirtyDayCount = rs.getInt("THIRTYDAYCOUNT");
                taxAmt = rs.getDouble("TAXAMOUNT");
                commAmount = rs.getDouble("COMISSIONAMOUNT");
                status = InvoiceStatus.valueOf(rs.getString("STATUS"));

                //TODO: return invoice object... make invoice constructor work
                //return new Invoice(resultID, total, remainingBalance, customerID, employeeID,
                  //      deliveryFee, isDeliverable, openedDate, thirtyDayCount, taxAmt, commAmount, status);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
