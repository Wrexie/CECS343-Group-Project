package com.company;

public class User {
    // instance variable
    private String username;
    private String password;
    private int userID;

    // constructor
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    // load db once created
    public User(int id, String username, String password){
        this.userID = id;
        this.username = username;
        this.password = password;
    }

    // getters
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public int getUserID() {
        return userID;
    }

    // accessor
    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
