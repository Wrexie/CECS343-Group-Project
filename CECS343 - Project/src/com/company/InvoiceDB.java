package com.company;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InvoiceDB {
    private Connection conn;
    public InvoiceDB(Connection conn) {this.conn = conn;}
    //static final String local_format = "%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s";
    static final String local_format = "%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s";
    static final String product_format = "%-25s%-25s%-25s";
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public Invoice getPOJO(int id) {
        int resultID = 0;
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
        HashMap<Integer, Integer> prodList = new HashMap<Integer, Integer>();
        int productID;
        int quantityOrdered;
        Customer customer;
        Employee employee;
        CustomerDB customerdb = new CustomerDB(conn);
        EmployeeDB employeedb = new EmployeeDB(conn);

        try {
            String query = "select * from invoices where invoiceid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);
            ResultSet rs = pStmt.executeQuery();

            if(rs.next()) {
                resultID = rs.getInt("INVOICEID");
                total = rs.getDouble("TOTAL");
                remainingBalance = rs.getDouble("REMAININGBALANCE");
                customerID = rs.getInt("CUSTOMERID");
                employeeID = rs.getInt("EMPLOYEEID");
                deliveryFee = rs.getDouble("DELIVERYFEE");
                isDeliverable = rs.getBoolean("ISDELIVERABLE");
                openedDate = rs.getDate("OPENEDDATE").toLocalDate();
                thirtyDayCount = rs.getInt("THIRTYDAYCOUNT");
                taxAmt = rs.getDouble("TAXAMOUNT");
                commAmount = rs.getDouble("COMMISSIONAMOUNT");
                status = InvoiceStatus.valueOf(rs.getString("STATUS"));

                customer = customerdb.getPOJO(customerID);
                employee = employeedb.getPOJO(employeeID);

                query = "select productid, quantityordered from orderdetails where invoiceid = ?";
                pStmt = conn.prepareStatement(query);
                pStmt.setInt(1, resultID);
                rs = pStmt.executeQuery();
                while(rs.next()) {
                    productID = rs.getInt("PRODUCTID");
                    quantityOrdered = rs.getInt("QUANTITYORDERED");
                    prodList.put(productID, quantityOrdered);
                }
                return new Invoice(resultID, total, remainingBalance, isDeliverable, deliveryFee, thirtyDayCount, openedDate, customer, employee, prodList, status);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Invoice invoice) {
        Iterator it = invoice.getProdList().entrySet().iterator();
        InvoiceDB invoicedb = new InvoiceDB(conn);
        int resultID = 0;
        try {
            String query = "insert into invoices (total, remainingbalance, customerid, employeeid," +
                    "deliveryfee, isdeliverable, openeddate, thirtydaycount, taxamount, commissionamount, status)" +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setDouble(1, invoice.getTotal());
            pStmt.setDouble(2, invoice.getOwed());
            pStmt.setInt(3, invoice.getCustomer().getId());
            pStmt.setInt(4, invoice.getEmployee().getEmployeeID());
            pStmt.setDouble(5, invoice.getDeliveryFee());
            pStmt.setBoolean(6, invoice.getIsDeliverable());
            pStmt.setDate(7, java.sql.Date.valueOf(invoice.getOpenedDate()));
            pStmt.setInt(8, invoice.getThirtyDayCount());
            pStmt.setDouble(9, invoice.getTaxAmt());
            pStmt.setDouble(10, invoice.getCommAmount());
            pStmt.setString(11, invoice.getStatus().toString());

            pStmt.executeUpdate();

            query = "select invoiceid from invoices where total = ? and remainingbalance = ? and customerid = ? and employeeid = ? " +
                    "and deliveryfee = ? and isdeliverable = ? and openeddate = ? and thirtydaycount = ? and taxamount = ? " +
                    "and commissionamount = ? and status = ?";
            pStmt = conn.prepareStatement(query);
            pStmt.setDouble(1, invoice.getTotal());
            pStmt.setDouble(2, invoice.getOwed());
            pStmt.setInt(3, invoice.getCustomer().getId());
            pStmt.setInt(4, invoice.getEmployee().getEmployeeID());
            pStmt.setDouble(5, invoice.getDeliveryFee());
            pStmt.setBoolean(6, invoice.getIsDeliverable());
            pStmt.setDate(7, java.sql.Date.valueOf(invoice.getOpenedDate()));
            pStmt.setInt(8, invoice.getThirtyDayCount());
            pStmt.setDouble(9, invoice.getTaxAmt());
            pStmt.setDouble(10, invoice.getCommAmount());
            pStmt.setString(11, invoice.getStatus().toString());

            ResultSet rs = pStmt.executeQuery();

            while(rs.next()) {
                resultID = rs.getInt("INVOICEID");
            }

            query = "insert into orderdetails (invoiceid, productid, quantityordered)" +
                    "values(?, ?, ?)";
            pStmt = conn.prepareStatement(query);

            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                pStmt.setInt(1, resultID);
                pStmt.setInt(2, (int)pair.getKey());
                pStmt.setInt(3, (int)pair.getValue());

                pStmt.executeUpdate();
            }

            pStmt.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void printAll() {
        try {
            String query = "select * from invoices";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Invoices: ");

            while(rs.next()) {
                int invoiceid = rs.getInt("INVOICEID");
                double total = rs.getDouble("TOTAL");
                double remaining = rs.getDouble("REMAININGBALANCE");
                int customerid = rs.getInt("CUSTOMERID");
                int employeeid = rs.getInt("EMPLOYEEID");
                double deliveryFee = rs.getDouble("DELIVERYFEE");
                boolean isDeliverable = rs.getBoolean("ISDELIVERABLE");
                LocalDate date = rs.getDate("OPENEDDATE").toLocalDate();
                int thirtyDayCount = rs.getInt("THIRTYDAYCOUNT");
                double taxAmount = rs.getDouble("TAXAMOUNT");
                double commissionAmount = rs.getDouble("COMMISSIONAMOUNT");
                String status = rs.getString("STATUS");

                System.out.println("--------------------------------------------------------------------------------------");
                System.out.printf(local_format, "InvoiceID", "Total", "Remaining Balance", "CustomerID", "EmployeeID",
                        "Delivery Fee", "Is Deliverable?", "Opened Date", "Tax Amount", "Commission Amount", "Status");
                System.out.println();
                System.out.printf(local_format, invoiceid, df.format(total), df.format(remaining), customerid, employeeid, df.format(deliveryFee), isDeliverable, date,
                         df.format(taxAmount), df.format(commissionAmount), status);

                System.out.println();
                printProducts(invoiceid);
                System.out.println("--------------------------------------------------------------------------------------");
                System.out.println();
            }
            System.out.println("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Invoice invoice) {
        try {
            String query = "update invoices set remainingbalance = ?, total = ?, deliveryfee = ?, taxamount = ?," +
                    "thirtydaycount = ?, commissionamount = ?, status = ?, openeddate = ?, isdeliverable = ?," +
                    "customerid = ?, employeeid = ? where invoiceid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setDouble(1, invoice.getOwed());
            pStmt.setDouble(2, invoice.getTotal());
            pStmt.setDouble(3, invoice.getDeliveryFee());
            pStmt.setDouble(4, invoice.getTaxAmt());
            pStmt.setInt(5, invoice.getThirtyDayCount());
            pStmt.setDouble(6, invoice.getCommAmount());
            pStmt.setString(7, invoice.getStatus().toString());
            pStmt.setDate(8, java.sql.Date.valueOf(invoice.getOpenedDate()));
            pStmt.setBoolean(9, invoice.getIsDeliverable());
            pStmt.setInt(10, invoice.getCustomer().getId());
            pStmt.setInt(11, invoice.getEmployee().getEmployeeID());
            pStmt.setInt(12, invoice.getInvoiceID());

            pStmt.executeUpdate();

            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public boolean isEmpty() {
        try {
            String query = "select * from invoices";
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

    public void printPaid() {
        try {
            String query = "select * from invoices where status = 'PAID' order by total desc";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            System.out.println("Paid Invoices: ");

            while(rs.next()) {
                int invoiceid = rs.getInt("INVOICEID");
                double total = rs.getDouble("TOTAL");
                double remaining = rs.getDouble("REMAININGBALANCE");
                int customerid = rs.getInt("CUSTOMERID");
                int employeeid = rs.getInt("EMPLOYEEID");
                double deliveryFee = rs.getDouble("DELIVERYFEE");
                boolean isDeliverable = rs.getBoolean("ISDELIVERABLE");
                LocalDate date = rs.getDate("OPENEDDATE").toLocalDate();
                int thirtyDayCount = rs.getInt("THIRTYDAYCOUNT");
                double taxAmount = rs.getDouble("TAXAMOUNT");
                double commissionAmount = rs.getDouble("COMMISSIONAMOUNT");
                String status = rs.getString("STATUS");

                System.out.println("--------------------------------------------------------------------------------------");
                System.out.printf(local_format, "InvoiceID", "Total", "Remaining Balance", "CustomerID", "EmployeeID",
                        "Delivery Fee", "Is Deliverable?", "Opened Date",  "Tax Amount", "Commission Amount", "Status");
                System.out.println();
                System.out.printf(local_format, invoiceid, total, remaining, customerid, employeeid, deliveryFee, isDeliverable, date,
                         taxAmount, commissionAmount, status);

                System.out.println();
                printProducts(invoiceid);
                System.out.println("--------------------------------------------------------------------------------------");
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void printUnpaid() {
        try {
            String query = "select * from invoices where status = 'UNPAID' order by openeddate asc";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            System.out.println("Unpaid Invoices: ");

            while(rs.next()) {
                int invoiceid = rs.getInt("INVOICEID");
                double total = rs.getDouble("TOTAL");
                double remaining = rs.getDouble("REMAININGBALANCE");
                int customerid = rs.getInt("CUSTOMERID");
                int employeeid = rs.getInt("EMPLOYEEID");
                double deliveryFee = rs.getDouble("DELIVERYFEE");
                boolean isDeliverable = rs.getBoolean("ISDELIVERABLE");
                LocalDate date = rs.getDate("OPENEDDATE").toLocalDate();
                int thirtyDayCount = rs.getInt("THIRTYDAYCOUNT");
                double taxAmount = rs.getDouble("TAXAMOUNT");
                double commissionAmount = rs.getDouble("COMMISSIONAMOUNT");
                String status = rs.getString("STATUS");

                System.out.println("--------------------------------------------------------------------------------------");
                System.out.printf(local_format, "InvoiceID", "Total", "Remaining Balance", "CustomerID", "EmployeeID",
                        "Delivery Fee", "Is Deliverable?", "Opened Date", "Tax Amount", "Commission Amount", "Status");
                System.out.println();
                System.out.printf(local_format, invoiceid, total, remaining, customerid, employeeid, deliveryFee, isDeliverable, date,
                         taxAmount, commissionAmount, status);

                System.out.println();
                printProducts(invoiceid);
                System.out.println("--------------------------------------------------------------------------------------");
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printProducts(int invoiceid) {
        try {
            String query = "select orderdetails.productid, productname, quantityordered" +
                    " from orderdetails inner join products on products.productid = orderdetails.productid where invoiceid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, invoiceid);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Products from invoice " + invoiceid + " :");
            System.out.printf(product_format, "Product ID", "Product Name", "Quantity Ordered");
            System.out.println();

            while(rs.next()) {
                int productid = rs.getInt("PRODUCTID");
                String productname = rs.getString("PRODUCTNAME");
                int quantityordered = rs.getInt("QUANTITYORDERED");

                System.out.printf(product_format, productid, productname, quantityordered);
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePenalty() {
        CustomerDB customerdb = new CustomerDB(conn);
        EmployeeDB employeedb = new EmployeeDB(conn);
        try {
            String query = "select * from invoices where status = 'UNPAID'";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            ResultSet rs2;
            while(rs.next()) {
                int invoiceid = rs.getInt("INVOICEID");
                double total = rs.getDouble("TOTAL");
                double remaining = rs.getDouble("REMAININGBALANCE");
                int customerid = rs.getInt("CUSTOMERID");
                int employeeid = rs.getInt("EMPLOYEEID");
                double deliveryFee = rs.getDouble("DELIVERYFEE");
                boolean isDeliverable = rs.getBoolean("ISDELIVERABLE");
                LocalDate date = rs.getDate("OPENEDDATE").toLocalDate();
                int thirtyDayCount = rs.getInt("THIRTYDAYCOUNT");
                double taxAmount = rs.getDouble("TAXAMOUNT");
                double commissionAmount = rs.getDouble("COMMISSIONAMOUNT");
                InvoiceStatus resultStatus = InvoiceStatus.valueOf(rs.getString("STATUS"));

                HashMap<Integer, Integer> prodList = new HashMap<Integer, Integer>();
                int productID;
                int quantityOrdered;
                query = "select productid, quantityordered from orderdetails where invoiceid = ?";
                pStmt = conn.prepareStatement(query);
                pStmt.setInt(1, invoiceid);
                rs2 = pStmt.executeQuery();
                while(rs2.next()) {
                    productID = rs2.getInt("PRODUCTID");
                    quantityOrdered = rs2.getInt("QUANTITYORDERED");
                    prodList.put(productID, quantityOrdered);
                }

                Customer customer = customerdb.getPOJO(customerid);
                Employee employee = employeedb.getPOJO(employeeid);

                Invoice invoice = new Invoice(invoiceid, total, remaining, isDeliverable, deliveryFee, thirtyDayCount,
                        date, customer, employee, prodList, resultStatus);

                invoice.checkPenalty();
                customerdb.update(invoice.getCustomer());
                update(invoice);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
