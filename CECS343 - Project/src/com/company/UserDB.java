package com.company;
import java.sql.*;

public class UserDB {
    private Connection conn;

    public UserDB(Connection conn) { this.conn = conn; }

    public User getPOJO(String username) {
        String resultUsername;
        String password;

        try {
            String query = "select * from users where username = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, username);
            ResultSet rs = pStmt.executeQuery();

            if(rs.next()) {
                resultUsername = rs.getString("USERNAME");
                password = rs.getString("PASSWORD");

                return new User(resultUsername, password);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
