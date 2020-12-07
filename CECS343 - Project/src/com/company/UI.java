package com.company;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.sql.*;

public class UI {
    // for testing purpose
    public static String DB_URL = "jdbc:derby:CECS343";
    static final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    static final String PRINT_FORMAT="%-25s%-25s%-25s%-25s\n";
    static final String PRINT_FORMAT2 = "%-25s%-25s%-45s%-25s%-35s%-25s%-25s" + PRINT_FORMAT;
    public static Connection conn;

    public static void main(String[] args) {
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            loginMenu();
            mainMenu();
        } catch (SQLNonTransientConnectionException e) {
            System.out.println("Connection to Database failed. (Database was not found)");
            System.out.println("Program will exit.");
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Closes");
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        }



    /**
     * mainMenu: Is the central hub where the user can direct a certain "action" that he/she wishes to do.
     */
    private static void mainMenu() {
        // temporary value TODO: 11/28/20 needs to have the "LOGIN CONTROLLER" to process the user click
        Scanner userInput = new Scanner(System.in);
        // --------------------------------------


        int sel = 0;
        while (sel != -1){
            System.out.format("---------------------------\n    Main Menu\nWhat would you like to do?\n 1.Warehouse menu\n 2.Salesman menu\n 3.Customer menu\n 4.Invoice menu\n 5.Product menu\n-1.Quit\n");
            sel = getUserOption(userInput);
            switch (sel){
                case 1:
                    warehouseMenu(userInput);
                    break;
                case 2:
                    salesmanMenu(userInput);
                    break;
                case 3:
                    customerMenu(userInput);
                    break;
                case 4:
                    invoiceMenu(userInput);
                    break;
                case 5:
                    productMenu(userInput);
                    break;
                case -1:
                    System.out.format("Thank you for using the system.");
                    break;
                default:
                    System.out.format("Incorrect option \n");
                    break;
            }
        }
    }

    /**
     * warehouseMenu: The central hub for the Warehouse.
     * @param userInput acts as the user "actions" as it determine where the UI should go to next tempary value
     */
    private static void warehouseMenu(Scanner userInput) {
        int sel = 0;

        while (sel != -1) {
            System.out.format("---------------------------\n    Warehouse Menu\nWhat would you like to do?\n 1.Add warehouse\n 2.Check stock\n 3.Update stock\n 4.Display all product in Warehouse\n-1.Return to main menu\n");
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add warehouse
                    break;
                case 2:
                    // TODO: 11/26/20 Redirected the user to update warehouse
                    System.out.format("---------------------------\n    update Warehouse\nWarehouse name: ");
                    break;
                case 3:
                    // TODO: 12/4/20 Redirected the user to select a warehouse and to update the product from within the warehouse
                    break;
                case 4:
                    // TODO: 12/4/20 display quantity for each product by warehouse. <- from rfp
                    break;
                case -1:
                    mainMenu();
                    break;
                default:
                    System.out.format("Incorrect option \n");
            }
        }
    }

    /**
     * salesmanMenu: The central hub for the Salesman.
     * @param userInput acts as the user "actions" as it determine where the UI should go to next
     */
    private static void salesmanMenu(Scanner userInput) {
        int sel = 0;

        while (sel != -1) {
            System.out.format("---------------------------\n    Salesman Menu\nWhat would you like to do?\n 1.Add salesman\n 2.Update salesman\n 3.Display all total sales and commissions\n-1.Return to main menu\n");
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add salesman
                    break;
                case 2:
                    // TODO: 11/26/20 Redirected the user to update warehouse
                    break;
                case 3:
                    // TODO: 12/4/20  Display the total amount of sales $$ and total commission earned each salesman
                    break;
                case -1:
                    mainMenu();
                    break;
                default:
                    System.out.format("Incorrect option \n");
            }
        }
    }

    /**
     * customerMenu: The central hub for the Customer.
     * @param userInput acts as the user "actions" as it determine where the UI should go to next
     */
    private static void customerMenu(Scanner userInput) {
        int sel = 0;

        while (sel != -1) {
            System.out.format("---------------------------\n    Customer Menu\nWhat would you like to do?\n 1.Add customer\n 2.Update customer\n-1.Return to main menu\n");
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add customer
                    CustomerDB customerDB = new CustomerDB(conn);
                    System.out.println("Enter customer fullname: ");
                    String fullname = userInput.nextLine();
                    System.out.println("Enter customer address: ");
                    String address = userInput.nextLine();
                    System.out.println("Enter customer phone: ");
                    String phone = userInput.nextLine();
                    CustomerStatus status;
                    String str;
                    while(true) {
                        System.out.println("Enter customer status: ");
                        str = userInput.nextLine();
                        if(str.equalsIgnoreCase("ACTIVE")) {
                            status = CustomerStatus.ACTIVE;
                            break;
                        } else if(str.equalsIgnoreCase("INACTIVE")) {
                            status = CustomerStatus.INACTIVE;
                            break;
                        } else {
                            System.out.println("Invalid. Please choose \"Active\" or \"Inactive\" status.");
                        }
                    }
                    double taxRate;
                    System.out.println("Enter customer tax rate: ");
                    while(!userInput.hasNextDouble()) {
                        System.out.println("Invalid input. Please enter a number.");
                        userInput.next();
                    }

                    taxRate = userInput.nextDouble();
                    customerDB.save(new Customer(fullname, address, phone, status, taxRate));

                    if(userInput.hasNextLine()) {
                        userInput.nextLine();
                    }

                    break;
                case 2:
                    // TODO: 11/26/20 Redirected the user to update customer
                    break;
                case -1:
                    mainMenu();
                    break;
                default:
                    System.out.format("Incorrect option \n");
            }
        }
    }

    /**
     * invoiceMenu: The central hub for the Invoice.
     * @param userInput acts as the user "actions" as it determine where the UI should go to next
     */
    private static void invoiceMenu(Scanner userInput) {
        int sel = 0;

        while (sel != -1) {
            System.out.format("---------------------------\n    Invoice Menu\nWhat would you like to do?\n 1.Add invoice\n 2.Update invoice\n 3.Display open invoices\n 4.Display closed invoices\n-1.Return to main menu\n");
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add invoice
                    break;
                case 2:
                    // TODO: 11/26/20 Redirected the user to update invoice
                    break;
                case 3:
                    // TODO: 12/4/20  Display invoices are open sorted in increasing order of invoice date. <- from rfp
                    break;
                case 4:
                    // TODO: 12/4/20 Display invoices are closed (paid) sorted in decreasing order of invoice amount <- from rfp
                    break;
                case -1:
                    mainMenu();
                    break;
                default:
                    System.out.format("Incorrect option \n");
            }
        }
    }

    /**
     * productMenu: The central hub for the product.
     * @param userInput acts as the user "actions" as it determine where the UI should go to next
     */
    private static void productMenu(Scanner userInput) {
        int sel = 0;

        while (sel != -1) {
            System.out.format("---------------------------\n    Product Menu\nWhat would you like to do?\n 1.Add product\n 2.Update product\n 3.Display all products\n 4.Display products that have 5 or fewer stock\n-1.Return to main menu\n");
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add product
                    break;
                case 2:
                    // TODO: 11/26/20 Redirected the user to update product
                    break;
                case 3:
                    // TODO: 12/4/20 Display each product (product name, Selling Price, Cost Price,
                    //  Total Quantity on Hand, Quantity Sold, Total Sales, Total Cost, Total Profit
                    //  and Total Profit Percent) need to be sorted in decreasing order of profit percent. <- from rfp
                    break;
                case 4:
                    // TODO: 12/4/20 Display products in inventory that have 5 or fewer in the warehouse
                    //  (sorted in increasing order by quantity) <- from rfp
                    break;
                case -1:
                    mainMenu();
                    break;
                default:
                    System.out.format("Incorrect option \n");
            }
        }
    }

    /**
     * getUserOption: Deteermine whether the user "actions" is qualify to be pass into the program
     * @param userInput acts as the user "actions" as it determine what the user is wishing to pass on program.
     * @return once the conditions is satisfy the user "actions" is saved
     */
    private static int getUserOption(Scanner userInput) {
        int sel;
        while (true){
            try {
                System.out.format("Enter: ");
                sel = Integer.parseInt(userInput.nextLine());
                return sel;
            }
            catch (Exception e){
                System.out.format("Incorrect format please try again. \n");
            }
        }
    }

    /**
     * loginMenu: Acts as the "the password protector" for the user, thus it is one of the first UI method called when
     * starting the program. Also, acts as the first requirement for the software once initialized also save the user
     * data and update them if he/she wishes.
     */
    private static void loginMenu() {
        // TODO: 11/26/20 Need to add a verification method to determine if the user information is store in the database
        // for testing purpose
        Scanner userInput = new Scanner(System.in);

        String userName = "admin", password = "";
        UserDB userdb = new UserDB(conn);
        User user = userdb.getPOJO("admin");

        if(user == null) {
            System.out.format("---------------------------\n    Login Menu\nUser information is not found.\nCreating user...\n");
            System.out.println("Username: admin");
            while (true) {
                System.out.format("Please enter a password: ");
                try {
                    password = userInput.nextLine();
                    break;
                }
                catch (Exception e) {
                    System.out.format("Incorrect format please try again.");
                }
            }
            user = new User(userName, password);
            userdb.save(user);
            System.out.format("Process complete...\nUsername: %s\nPassword: %s\n", user.getUsername(), user.getPassword());
        }
        else {
            System.out.format("---------------------------\n    Login Menu\nEnter User Information Below:\nUser: admin\n");
            while (true) {
                System.out.format("Password: ");
                try {
                    password = userInput.nextLine();
                    if(password.equalsIgnoreCase(user.getPassword())) {
                        break;
                    }
                }
                catch (Exception e) {
                    System.out.format("Incorrect format please try again.");
                }
            }
        }
    }

}
