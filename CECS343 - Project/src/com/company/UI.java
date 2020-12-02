package com.company;

import java.util.Scanner;

public class UI {
    // for testing purpose
    public static void main(String[] args) {
        loginMenu();
        mainMenu();
    }

    /**
     * mainMenu: Is the central hub where the user can direct a certain "action" that he/she wishes to do.
     */
    private static void mainMenu() {
        // temporary value TODO: 11/28/20 needs to have the "LOGIN CONTROLLER" to process the user click
        Scanner userInput = new Scanner(System.in);
        // --------------------------------------
        System.out.format("---------------------------\n    Main Menu\nWhat would you like to do?\n 1.Warehouse menu\n 2.Salesman menu\n 3.Customer menu\n 4.Invoice menu\n 5.Product menu\n-1.Quit\n");

        int sel = 0;
        while (sel != -1){
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
        System.out.format("---------------------------\n    Warehouse Menu\nWhat would you like to do?\n 1.Add warehouse\n 2.Check stock\n-1.Return to main menu\n");
        while (sel != -1) {
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add warehouse
                    break;
                case 2:
                    // TODO: 11/26/20 Redirected the user to update warehouse
                    System.out.format("---------------------------\n    update Warehouse\nWarehouse name: ");
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
        System.out.format("---------------------------\n    Salesman Menu\nWhat would you like to do?\n 1.Add salesman\n 2.Update salesman\n-1.Return to main menu\n");
        while (sel != -1) {
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add salesman
                    break;
                case 2:
                    // TODO: 11/26/20 Redirected the user to update warehouse
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
        System.out.format("---------------------------\n    Customer Menu\nWhat would you like to do?\n 1.Add customer\n 2.Update customer\n-1.Return to main menu\n");
        while (sel != -1) {
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add customer
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
        System.out.format("---------------------------\n    Invoice Menu\nWhat would you like to do?\n 1.Add invoice\n 2.Update invoice\n-1.Return to main menu\n");
        while (sel != -1) {
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add invoice
                    break;
                case 2:
                    // TODO: 11/26/20 Redirected the user to update invoice
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
     * productMenu: The central hub for the Invoice.
     * @param userInput acts as the user "actions" as it determine where the UI should go to next
     */
    private static void productMenu(Scanner userInput) {
        int sel = 0;
        System.out.format("---------------------------\n    Product Menu\nWhat would you like to do?\n 1.Add product\n-1.Return to main menu\n");
        while (sel != -1) {
            sel = getUserOption(userInput);
            switch (sel) {
                case 1:
                    // TODO: 11/26/20 Redirected the user to add product
                    break;
                case 2:
                    // TODO: 11/26/20 Redirected the user to update product
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
        LoginController controller = new LoginController();
        controller.checkAcount();

        String userName = "", password = "";
        System.out.format("---------------------------\n    Login Menu\nUser information is not found.\nCreating user...\n");
        while (true) {
            System.out.format("Please enter a Username: ");
            try {
                userName = userInput.nextLine();
                break;
            }
            catch (Exception e) {
                System.out.format("Incorrect format please try again.");
            }
        }
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
        User user = new User(userName, password);
        System.out.format("Process complete...\nUsername: %s\nPassword: %s\n", user.getUsername(), user.getPassword());
    }

}
