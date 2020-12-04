package com.company;

public class User {
    // instance variable
    private String username;
    private String password;

    // constructor
    public User(String username, String password){
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


    // accessor
    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
