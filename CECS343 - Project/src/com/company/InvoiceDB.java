package com.company;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//InvoiceDB class
//Handles all required interactions with the database for invoice data
public class InvoiceDB {
    //Private DB connection variable
    private Connection conn;
    //Print format for printing invoice data retrieved from database
    static final String local_format = "%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s";
    //Print format for printing product data retrieved from database
    static final String product_format = "%-25s%-25s%-25s";
    //Formatting for decimal values
    private static final DecimalFormat df = new DecimalFormat("0.00");

    //Constructor. Takes in DB connection
    public InvoiceDB(Connection conn) {this.conn = conn;}

    //Returns requested entity object retrieved from DB
    public Invoice getPOJO(int id) {
        //Attribute variables
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
        //Map to store products for creating invoice object
        HashMap<Integer, Integer> prodList = new HashMap<Integer, Integer>();
        int productID;
        int quantityOrdered;
        //Customer object for creating invoice object
        Customer customer;
        //Employee object for creating invoice object
        Employee employee;
        //Customer and Employee DB objects to retrieve appropriate customer and employee data
        CustomerDB customerdb = new CustomerDB(conn);
        EmployeeDB employeedb = new EmployeeDB(conn);

        try {
            //Select all data for the requested invoice
            String query = "select * from invoices where invoiceid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);
            //Execute query
            ResultSet rs = pStmt.executeQuery();

            //Iterate through returned row from DB
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

                //Get appropriate customer object
                customer = customerdb.getPOJO(customerID);
                //Get appropriate employee object
                employee = employeedb.getPOJO(employeeID);

                //Select products pertaining to that invoice
                query = "select productid, quantityordered from orderdetails where invoiceid = ?";
                pStmt = conn.prepareStatement(query);
                pStmt.setInt(1, resultID);
                rs = pStmt.executeQuery();

                //Iterate through all rows returned
                while(rs.next()) {
                    productID = rs.getInt("PRODUCTID");
                    quantityOrdered = rs.getInt("QUANTITYORDERED");
                    //Put each product retrieved from DB into map
                    prodList.put(productID, quantityOrdered);
                }
                //Create and return new invoice object
                return new Invoice(resultID, total, remainingBalance, isDeliverable, deliveryFee, thirtyDayCount, openedDate, customer, employee, prodList, status);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Return null by default if requested data is not found
        return null;
    }

    //Saves invoice object created by user to the DB
    public void save(Invoice invoice) {
        //Iterator to iterate through product map containing all products for specific invoice
        Iterator it = invoice.getProdList().entrySet().iterator();

        int resultID = 0;
        try {
            //Insert invoice object data to database
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

            //Execute statement
            pStmt.executeUpdate();

            //Since invoiceIDs are auto generated, we need to query DB for the latest invoice to get the invoiceID to insert products into
            //orderdetails table that associates a list of products with a specific invoice
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

            //Statement to insert product data into orderdetails for the invoice created
            query = "insert into orderdetails (invoiceid, productid, quantityordered)" +
                    "values(?, ?, ?)";
            pStmt = conn.prepareStatement(query);

            //Iterate through the product map given by the invoice object and insert each product into
            //orderdetails table
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

    //Print all data from invoice table
    public void printAll() {
        try {
            String query = "select * from invoices";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Invoices: ");

            //Iterate through all rows returned from DB
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

                //Print all data
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

    //Updates an invoice specified by user
    public void update(Invoice invoice) {
        try {
            //Update statement
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

    //Checks if table is empty
    public boolean isEmpty() {
        try {
            String query = "select * from invoices";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            //If no data is returned then table is empty. If not then table is not empty
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

    //Print all paid invoices
    public void printPaid() {
        try {
            //Select all data for paid invoices
            String query = "select * from invoices where status = 'PAID' order by total desc";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            System.out.println("Paid Invoices: ");

            //Iterate through all rows returned from DB
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

                //Print all data
                System.out.println("--------------------------------------------------------------------------------------");
                System.out.printf(local_format, "InvoiceID", "Total", "Remaining Balance", "CustomerID", "EmployeeID",
                        "Delivery Fee", "Is Deliverable?", "Opened Date",  "Tax Amount", "Commission Amount", "Status");
                System.out.println();
                System.out.printf(local_format, invoiceid, df.format(total), df.format(remaining), customerid, employeeid, df.format(deliveryFee), isDeliverable, date,
                        df.format(taxAmount), df.format(commissionAmount), status);

                System.out.println();
                printProducts(invoiceid);
                System.out.println("--------------------------------------------------------------------------------------");
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Print all unpaid invoices
    public void printUnpaid() {
        try {
            //Select all data for unpaid invoices
            String query = "select * from invoices where status = 'UNPAID' order by openeddate asc";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            System.out.println("Unpaid Invoices: ");

            //Iterate through all rows returned from DB
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

                //Print all data
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Print all products attached to an invoice
    public void printProducts(int invoiceid) {
        try {
            //Select all products attached to invoice specified
            String query = "select orderdetails.productid, productname, quantityordered" +
                    " from orderdetails inner join products on products.productid = orderdetails.productid where invoiceid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, invoiceid);
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Products from invoice " + invoiceid + " :");
            System.out.printf(product_format, "Product ID", "Product Name", "Quantity Ordered");
            System.out.println();

            //Iterate through all rows returned by DB
            while(rs.next()) {
                int productid = rs.getInt("PRODUCTID");
                String productname = rs.getString("PRODUCTNAME");
                int quantityordered = rs.getInt("QUANTITYORDERED");

                //Print data
                System.out.printf(product_format, productid, productname, quantityordered);
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Calls check penalty method on each unpaid invoice in database and updates those invoices in the database
    public void updatePenalty() {
        CustomerDB customerdb = new CustomerDB(conn);
        EmployeeDB employeedb = new EmployeeDB(conn);
        try {
            //Select all unpaid invoices
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
