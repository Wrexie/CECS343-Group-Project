package com.company;
import java.sql.*;

//UserDB class
//Handles all required interactions with database for user data
public class UserDB {
    //Private connection variable
    private Connection conn;

    //Constructor. Takes in DB connection
    public UserDB(Connection conn) { this.conn = conn; }

    //Saves user object to database
    public void save(User user) {
        try {
            //Insert user object to database
            String query = "insert into users (username, password) values (?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, user.getUsername());
            pStmt.setString(2, user.getPassword());

            //Execute statement
            pStmt.executeUpdate();

            pStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Returns specified user from table
    public User getPOJO(String username) {
        //Attribute variables
        String resultUsername;
        String password;

        try {
            //Select specified user from table
            String query = "select * from users where username = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, username);
            ResultSet rs = pStmt.executeQuery();

            //Iterate through row returned if any
            if(rs.next()) {
                resultUsername = rs.getString("USERNAME");
                password = rs.getString("PASSWORD");

                //Create and return requested user object
                return new User(resultUsername, password);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Return null by default if requested data is not found
        return null;
    }
}
