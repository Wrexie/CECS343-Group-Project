package com.company;/*
 * CECS323 - JDBC Project
 * By: Rithrangsey Suon, Andrew Baltazar & Nicholas Bautista
 * Due: 10/27/2020
 */
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;


//Main JBDC Class
public class Main {
    public static String DB_URL = "jdbc:mysql://localhost:3306/test";
    public static String DBNAME;
    public static String user;
    public static String pass;
    static Scanner input = new Scanner(System.in);
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String PRINT_FORMAT="%-25s%-25s%-25s%-25s\n";
    static final String PRINT_FORMAT2 = "%-25s%-25s%-45s%-25s%-35s%-25s%-25s" + PRINT_FORMAT;

    //Main
    public static void main(String[] args) {
        String query;
        ResultSet rs = null;
        System.out.println("Enter name of the database: ");
        DBNAME = input.nextLine();
        System.out.println("Enter username: ");
        user = input.nextLine();
        System.out.println("Enter password: ");
        pass = input.nextLine();
        if(user.length() == 0 && pass.length() == 0)
        {
            DB_URL = DB_URL + DBNAME;
        }
        else
        {
            DB_URL = DB_URL + DBNAME + ";user=" + user + ";password=" + pass;
        }
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement preparedStatement2 = null;
        try {
            //need to establish driver
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "1234");
            System.out.println("Connection Successful!");
            stmt = conn.createStatement();

            //Main loop
            while(true){
                //Print menu
                System.out.println("\nWhat would you like to do?\n1.List all writing groups\n2.List all publishers\n"
                        + "3.List all book titles\n"+ "4.List all data for a writing group\n"+"5.Insert a new book\n"
                        + "6.List all data from a publisher\n"+
                        "7.List all data from a book\n"+"8.Insert a new publisher and replace old publisher\n"+"9.Delete a specific book\n"+"E.Exit");
                String choice = input.nextLine();

                //Evaluate user choice
                //Menu choice 1: List all writing groups
                if(choice.equals("1")){
                    query = "select GROUPNAME from WRITINGGROUPS";
                    rs = stmt.executeQuery(query);
                    System.out.println("\nWriting Groups: ");
                    System.out.printf("GROUPNAME\n");
                    while (rs.next()){
                        String groupName = rs.getString("GROUPNAME");
                        System.out.printf(dispNull(groupName) + "\n");
                    }
                    System.out.println("\n");
                }

                //Menu choice 2: List all publishers
                else if(choice.equals("2")){
                    query = "select PUBLISHERNAME from PUBLISHERS";
                    rs = stmt.executeQuery(query);
                    System.out.println("\nPublishers:");
                    System.out.printf("PUBLISHERNAME\n");
                    while(rs.next()){
                        String publisherName = rs.getString("PUBLISHERNAME");
                        System.out.printf(dispNull(publisherName) + "\n");
                    }
                    System.out.println("\n");
                }

                //Menu choice 3: List all booktitles
                else if(choice.equals("3")){
                    query = "select BOOKTITLE from BOOKS";
                    rs = stmt.executeQuery(query);
                    System.out.println("\nBooks:");
                    System.out.print("BOOKTITLE\n");
                    while(rs.next()){
                        String bookTitle = rs.getString("BOOKTITLE");
                        System.out.println(dispNull(bookTitle));
                    }
                    System.out.println("\n");
                }

                //Menu choice 4: List all data from a group specified by user
                else if(choice.equals("4")){
                    String LOCAL_FORMAT = "%-25s%-35s%-50s%-25s%-35s%-25s%-25s" + PRINT_FORMAT;
                    query = "select * from WRITINGGROUPS natural join books natural join publishers where groupname = ?";
                    preparedStatement2 = conn.prepareStatement(query);
                    System.out.println("What group would you like to get all data from?");
                    choice = input.nextLine();
                    preparedStatement2.setString(1, choice);
                    rs = preparedStatement2.executeQuery();
                    System.out.printf(LOCAL_FORMAT,"GROUPNAME","PUBLISHERNAME","PUBLISHERADDRESS","PUBLISHERPHONE","PUBLISHEREMAIL","HEADWRITER","YEARFORMED","SUBJECT","BOOKTITLE","YEARPUBLISHED","NUMBERPAGES");
                    while(rs.next()){
                        String groupName = rs.getString("GROUPNAME");
                        String publisherName = rs.getString("PUBLISHERNAME");
                        String address = rs.getString("PUBLISHERADDRESS");
                        String phone = rs.getString("PUBLISHERPHONE");
                        String email = rs.getString("PUBLISHEREMAIL");
                        String headWriter = rs.getString("HEADWRITER");
                        String yearFormed = rs.getString("YEARFORMED");
                        String subject = rs.getString("SUBJECT");
                        String bookTitle = rs.getString("BOOKTITLE");
                        String yearPublished = rs.getString("YEARPUBLISHED");
                        String numberPages = rs.getString("NUMBERPAGES");
                        System.out.printf(LOCAL_FORMAT, dispNull(groupName), dispNull(publisherName),dispNull(address),dispNull(phone),
                                dispNull(email),dispNull(headWriter),dispNull(yearFormed),dispNull(subject),dispNull(bookTitle),dispNull(yearPublished),dispNull(numberPages));
                    }
                    System.out.println("\n");
                }

                //Menu choice 5: Insert a new book
                else if(choice.equals("5")) {
                    try {
                        //inserting a new book || publisher exists, writinggroup exists, bookname doesn't already exist
                        query = "insert into books(groupname, publishername, booktitle, yearpublished, numberpages) values (?, ?, ?, ?, ?)";
                        preparedStatement2 = conn.prepareStatement(query);
                        System.out.println("Enter writing group name: ");
                        String group = input.nextLine();
                        System.out.println("Enter publisher name: ");
                        String pub = input.nextLine();
                        System.out.println("Enter book title: ");
                        String title = input.nextLine();
                        System.out.println("Enter year published: ");
                        int yearPub = input.nextInt();
                        System.out.println("Enter number of pages: ");
                        int numPages = input.nextInt();
                        preparedStatement2.setString(1, group);
                        preparedStatement2.setString(2, pub);
                        preparedStatement2.setString(3, title);
                        preparedStatement2.setInt(4, yearPub);
                        preparedStatement2.setInt(5, numPages);

                        int rows = preparedStatement2.executeUpdate();
                        System.out.println("Executed. " + rows + " row(s) affected.\n");


                    } catch (SQLIntegrityConstraintViolationException e) {

                        System.out.println("Execution failed! Invalid input.");
                        System.out.println(e.getMessage());
                    } catch (InputMismatchException e) {
                        System.out.println("Execution failed! Invalid input type (int type required).");
                    }   catch (SQLDataException e) {
                        System.out.println("Execution failed! Invalid input length.");
                        System.out.println(e.getMessage());
                    } finally {
                        //clear input scanner for menu
                        if(input.hasNextLine()) {
                            input.nextLine();
                        }
                    }
                }

                //Menu choice 6: List all data for a publisher specified by user
                else if(choice.equals("6")){
                    query = "select * from PUBLISHERS natural join BOOKS natural join WRITINGGROUPS where publishername = ?";
                    preparedStatement2 = conn.prepareStatement(query);
                    System.out.println("What publisher would you like to get all data from?");
                    choice = input.nextLine();
                    preparedStatement2.setString(1, choice);
                    rs = preparedStatement2.executeQuery();
                    System.out.printf(PRINT_FORMAT2,"GROUPNAME","PUBLISHERNAME","PUBLISHERADDRESS","PUBLISHERPHONE","PUBLISHEREMAIL","HEADWRITER","YEARFORMED","SUBJECT","BOOKTITLE","YEARPUBLISHED","NUMBERPAGES");

                    while(rs.next()){
                        String groupName = rs.getString("GROUPNAME");
                        String publisherName = rs.getString("PUBLISHERNAME");
                        String address = rs.getString("PUBLISHERADDRESS");
                        String phone = rs.getString("PUBLISHERPHONE");
                        String email = rs.getString("PUBLISHEREMAIL");
                        String headWriter = rs.getString("HEADWRITER");
                        String yearFormed = rs.getString("YEARFORMED");
                        String subject = rs.getString("SUBJECT");
                        String bookTitle = rs.getString("BOOKTITLE");
                        String yearPublished = rs.getString("YEARPUBLISHED");
                        String numberPages = rs.getString("NUMBERPAGES");
                        System.out.printf(PRINT_FORMAT2, dispNull(groupName), dispNull(publisherName),dispNull(address),dispNull(phone),
                                dispNull(email),dispNull(headWriter),dispNull(yearFormed)
                                ,dispNull(subject),dispNull(bookTitle),dispNull(yearPublished),dispNull(numberPages));
                    }
                    System.out.println("\n");
                }

                //Menu choice 7: List all data from a book specified by user
                else if(choice.equals("7")){
                    query = "select * from books natural join publishers natural join WRITINGGROUPS where booktitle = ? and groupname = ?";
                    preparedStatement2 = conn.prepareStatement(query);
                    System.out.println("What book would you like to get all data from?");
                    choice = input.nextLine();
                    System.out.println("Please specify a writing group: ");
                    String group = input.nextLine();
                    preparedStatement2.setString(1, choice);
                    preparedStatement2.setString(2, group);
                    rs = preparedStatement2.executeQuery();
                    System.out.printf(PRINT_FORMAT2,"GROUPNAME","PUBLISHERNAME","PUBLISHERADDRESS","PUBLISHERPHONE","PUBLISHEREMAIL","HEADWRITER","YEARFORMED","SUBJECT","BOOKTITLE","YEARPUBLISHED","NUMBERPAGES");

                    while(rs.next()){
                        String groupName = rs.getString("GROUPNAME");
                        String publisherName = rs.getString("PUBLISHERNAME");
                        String address = rs.getString("PUBLISHERADDRESS");
                        String phone = rs.getString("PUBLISHERPHONE");
                        String email = rs.getString("PUBLISHEREMAIL");
                        String headWriter = rs.getString("HEADWRITER");
                        String yearFormed = rs.getString("YEARFORMED");
                        String subject = rs.getString("SUBJECT");
                        String bookTitle = rs.getString("BOOKTITLE");
                        String yearPublished = rs.getString("YEARPUBLISHED");
                        String numberPages = rs.getString("NUMBERPAGES");
                        System.out.printf(PRINT_FORMAT2, dispNull(groupName), dispNull(publisherName),dispNull(address),dispNull(phone),
                                dispNull(email),dispNull(headWriter),dispNull(yearFormed)
                                ,dispNull(subject),dispNull(bookTitle),dispNull(yearPublished),dispNull(numberPages));
                    }
                    System.out.println("\n");
                }

                //Menu choice 8: Insert a new publisher
                else if(choice.equals("8")) {
                    try{
                        //Prepared statement to insert new publisher
                        query = "insert into publishers(publishername, publisheraddress, publisherphone, publisheremail) values"
                                + "(?, ?, ?, ?)";
                        preparedStatement2 = conn.prepareStatement(query);

                        //User inputs all info for the new publisher
                        System.out.println("Please input the name of the new publisher:");
                        String newPubName = input.nextLine();
                        System.out.println("Please input the address of the new publisher:");
                        String newPubAddress = input.nextLine();
                        System.out.println("Please input the phone number of the new publisher:");
                        String newPubPhone = input.nextLine();
                        System.out.println("Please input the email address of the new publisher:");
                        String newPubEmail = input.nextLine();

                        //First prepared statement is set and ran with new and old publisher name
                        preparedStatement2.setString(1, newPubName);
                        preparedStatement2.setString(2, newPubAddress);
                        preparedStatement2.setString(3, newPubPhone);
                        preparedStatement2.setString(4, newPubEmail);

                        int rows = preparedStatement2.executeUpdate();

                        //Remove foreign key constraint from books so that we can edit publisher name for books
                        query = "alter table books drop constraint books_publisher_fk";
                        stmt.executeUpdate(query);

                        //User inputs what publisher they would like to replace
                        System.out.println("What publisher would you like to be replaced (Old publisher)?");
                        String oldPubName = input.nextLine();

                        //Second prepared statement to edit publisher name from BOOKS
                        query = "update books set publishername = ? where publishername = ?";
                        preparedStatement2 = conn.prepareStatement(query);
                        preparedStatement2.setString(1, newPubName);
                        preparedStatement2.setString(2, oldPubName);
                        int rows2 = preparedStatement2.executeUpdate();

                        //Foreign key constraint is added back
                        query = "alter table books "
                                + "add constraint books_publisher_fk foreign key (publishername) references publishers(publishername)";
                        stmt.executeUpdate(query);

                        //check to see if old publisher was found
                        if(rows2 == 0) {
                            System.out.println("\"Old\" Publisher not found. Insert Failed.");
                            query = "delete from publishers where publishername = ?";
                            preparedStatement2 = conn.prepareStatement(query);
                            preparedStatement2.setString(1, newPubName);
                            preparedStatement2.executeUpdate();
                        } else {
                            rows += rows2;
                            System.out.println("Executed. " + rows + " row(s) affected.\n");
                        }


                    } catch (SQLIntegrityConstraintViolationException e) {

                        System.out.println("Execution failed! Invalid input.");
                        System.out.println(e.getMessage());
                    } catch (InputMismatchException e) {
                        System.out.println("Execution failed! Invalid input type (String type required).");
                    }  catch (SQLDataException e) {
                        System.out.println("Execution failed! Invalid input length.");
                        System.out.println(e.getMessage());
                    }
                }

                //Menu choice 9: Delete book specified by user
                else if(choice.equals("9")) {
                    //Delete query
                    query = "delete from BOOKS where groupname = ? and booktitle = ?";
                    preparedStatement2 = conn.prepareStatement(query);
                    //User inputs writing group
                    System.out.println("Enter the writing group the book belongs to: ");
                    String groupName = input.nextLine();
                    //User inputs book title
                    System.out.println("Enter the title of the book: ");
                    String bookTitle = input.nextLine();

                    //Prepared statement is set and ran
                    preparedStatement2.setString(1, groupName);
                    preparedStatement2.setString(2, bookTitle);
                    int rows = preparedStatement2.executeUpdate();
                    System.out.println("Executed. " + rows + " row(s) affected.\n");
                }

                else if(choice.equals("E") || choice.equals("e")) {
                    break;
                }

                else {
                    System.out.println("Invalid menu input.");
                }
            }

        } catch (SQLNonTransientConnectionException e) {
            System.out.println("Connection to Database failed. (Database was not found)");
            System.out.println("Program will exit.");
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (rs != null) {
                    rs.close();
                }

                input.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try


    }

    public static String dispNull (String input) {
        //because of short circuiting, if it's null, it never checks the length.
        if (input == null || input.length() == 0)
            return "N/A";
        else
            return input;
    }

}
