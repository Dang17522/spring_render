package com.zalo.Spring_Zalo.request;

public class UserRequestLogin {

   
    private String username;
    private String password;

    // Default constructor (required for deserialization)
    public UserRequestLogin() {
    }

    // Parameterized constructor
    public UserRequestLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter and Setter methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Optional: Override toString() for debugging purposes
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}


