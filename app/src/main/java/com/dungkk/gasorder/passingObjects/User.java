package com.dungkk.gasorder.passingObjects;

public class User {
    private static String firstName;
    private String lastName;
    private static String username = null;
    private String password;
    private String phoneNumber;
    private String email;

    public User(String name){
        username = name;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        User.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }
}
