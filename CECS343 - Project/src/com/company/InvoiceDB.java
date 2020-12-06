package com.company;
import java.sql.*;

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
}
