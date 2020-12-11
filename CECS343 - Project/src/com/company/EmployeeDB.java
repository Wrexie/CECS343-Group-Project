package com.company;
import java.sql.*;
import java.text.DecimalFormat;

//EmployeeDB class:
//Handles all of the required interactions with the database for employee data
public class EmployeeDB {
    //local atabase connection variable
    private Connection conn;

    //Print format for printing employee data retrieved from DB
    static final String local_format = "%-25s%-25s%-25s%-25s";
    //Print format for printing total sales and commission data retrieved from DB
    static final String sales_comm_format = "%-25s%-25s%-25s%-25s%-25s";
    //Format for decimal values
    private static final DecimalFormat df = new DecimalFormat("0.00");

    //Constructor. Takes in DB connection
    public EmployeeDB(Connection conn) { this.conn = conn; }

    //Save method which saves an entity object created by user to the database
    public void save(Employee employee) {
        try{
            //SQL statement used to insert entity object data into the database
            String query = "insert into employees (fullname, commissionrate, phone)" +
                    "values (?, ?, ?)";

            //Creating and setting the prepared statement to the appropriate sql statement using
            //the data from the employee entity object input by the user
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, employee.getEmployeeName());
            pStmt.setDouble(2, employee.getCommRate());
            pStmt.setString(3, employee.getPhone());

            //SQL statement is executed.. employee object has been saved to the database
            pStmt.executeUpdate();
            //Clost statement
            pStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Method that returns the requested entity object using data retrieved from database
    public Employee getPOJO(int id) {
        //Variables to store attribute data
        int resultID;
        String fullname;
        double commRate;
        String phone;

        try{
            //Select statement retrieving the requested object data using the ID entered by user
            String query = "select * from employees where employeeid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);

            //Query is executed and data retrieved is saved to a resultset
            ResultSet rs = pStmt.executeQuery();

            //Iterate through the columns returned in the result set if there was columns returned
            if (rs.next()) {
                //Setting attribute variables to the data retrieved
                resultID = rs.getInt("EMPLOYEEID");
                fullname = rs.getString("FULLNAME");
                commRate = rs.getDouble("COMMISSIONRATE");
                phone = rs.getString("PHONE");

                //Create and return the new entity object
                return new Employee(resultID, fullname, phone, commRate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Return null by default if data requested is not found in database
        return null;
    }

    //Update method to update data for specified entry in the database
    public void update(Employee employee) {
        try {
            //Statement that updates the commission rate for the employee selected
            String query = "update employees set commissionrate = ? where employeeid = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setDouble(1, employee.getCommRate());
            pStmt.setInt(2, employee.getEmployeeID());

            //Update is executed
            pStmt.executeUpdate();
            pStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Prints all data from table
    public void printAll() {
        try {
            //Select all data from table
            String query = "select * from employees";
            PreparedStatement pStmt = conn.prepareStatement(query);

            //Execute statement and save returned data to result set
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Employees:");
            System.out.printf(local_format, "EmployeeID", "Fullname", "Commission Rate", "Phone");
            System.out.println();

            //Iterate through all rows returned
            while(rs.next()) {
                //Set attribtue variables to data returned
                int employeeid = rs.getInt("EMPLOYEEID");
                String fullname = rs.getString("FULLNAME");
                double commRate = rs.getDouble("COMMISSIONRATE");
                String phone = rs.getString("PHONE");

                //Print each entry
                System.out.printf(local_format, employeeid, fullname, commRate, phone);
                System.out.println();

            }
            System.out.println("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Checks if table is empty
    public boolean isEmpty() {
        try {
            //Select all data from table
            String query = "select * from employees";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            //If no data is returned then table is empty, else it is not
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

    //Prints total sales and commissions for all employees
    public void printTotalSalesAndCommissions() {
        try {
            //Selects necessary data from table and performs calculation for total sales and total commission amount
            String query = "select employees.employeeid, fullname, commissionrate," +
                    " sum(total), sum(commissionamount) from employees inner join invoices" +
                    " on employees.employeeid = invoices.employeeid group by employees.employeeid," +
                    " fullname, commissionrate order by sum(total) desc";
            PreparedStatement pStmt = conn.prepareStatement(query);

            //Query is executed and data returned is saved to result set
            ResultSet rs = pStmt.executeQuery();

            System.out.println("Employees and their Sales and Commission Totals: ");
            System.out.printf(sales_comm_format, "Employee ID", "Employee Name", "Commission Rate",
                    "Total Sales Amount", "Total Commissions Amount");
            System.out.println();

            //Iterate through all rows returned by database
            while(rs.next()) {
                int employeeid = rs.getInt("EMPLOYEEID");
                String employeename = rs.getString("FULLNAME");
                double commRate = rs.getDouble("COMMISSIONRATE");
                double salesTotal = rs.getDouble("4");
                double commsTotal = rs.getDouble("5");

                //Print data
                System.out.printf(sales_comm_format, employeeid, employeename, commRate, df.format(salesTotal), df.format(commsTotal));
                System.out.println();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
