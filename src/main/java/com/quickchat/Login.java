package com.quickchat;

import java.util.regex.Pattern;

/**
 * Login.java
 *
 * Handles registration and login for the chat application.
 *

 */
public class Login {
    //declaration of variables
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    // Regular expression declarations
    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+\\d{2,3}\\d{1,10}$");
    private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL = Pattern.compile(".*[^a-zA-Z0-9].*");
    //default constructor
    public Login() {
    }
    //parameterised constructor
    public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    // Getters / Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    

    //Checking that the stored username contains an underscore and is no more than five characters long.
    public boolean checkUserName() {
        return checkUserName(this.username);
    }

    public boolean checkUserName(String usernameToCheck) {
        if (usernameToCheck == null) {
            return false;
        }
        return usernameToCheck.contains("_") && usernameToCheck.length() <= 5;
    }

    //Checking that the stored password meets the complexity rules:at least eight characters long, contains a capital letter,a number, and a special character.
    public boolean checkPasswordComplexity() {
        return checkPasswordComplexity(this.password);
    }

    public boolean checkPasswordComplexity(String passwordToCheck) {
        if (passwordToCheck == null) {
            return false;
        }
        if (passwordToCheck.length() < 8) {
            return false;
        }
        return UPPERCASE.matcher(passwordToCheck).matches()
                && DIGIT.matcher(passwordToCheck).matches()
                && SPECIAL.matcher(passwordToCheck).matches();
    }

    //Checking that the stored cell phone number contains the international country code followed by the subscriber number. 
    public boolean checkCellPhoneNumber() {
        return checkCellPhoneNumber(this.cellPhoneNumber);
    }

    public boolean checkCellPhoneNumber(String cellToCheck) {
        if (cellToCheck == null) {
            return false;
        }
        return CELL_PATTERN.matcher(cellToCheck).matches();
    }

  

    //Validating the username, password and cell phone number that have been captured on this Login object and returns the appropriate registration message. Registration only "succeeds" (all three messages returned) once every rule passes.
    public String registerUser() {
        StringBuilder message = new StringBuilder();

        if (!checkUserName()) {
            message.append("Username is not correctly formatted; please ensure that your username ")
                   .append("contains an underscore and is no more than five characters in length.");
            return message.toString();
        } else {
            message.append("Username successfully captured.");
        }

        if (!checkPasswordComplexity()) {
            message.append(" Password is not correctly formatted; please ensure that the password ")
                   .append("contains at least eight characters, a capital letter, a number, and a special character.");
            return message.toString();
        } else {
            message.append(" Password successfully captured.");
        }

        if (!checkCellPhoneNumber()) {
            message.append(" Cell phone number incorrectly formatted or does not contain international code.");
            return message.toString();
        } else {
            message.append(" Cell phone number successfully captured.");
        }

        return message.toString();
    }


    //Verifying that the supplied username and password match the details that were stored for this user at registration time.
    public boolean loginUser(String usernameToCheck, String passwordToCheck) {
        if (usernameToCheck == null || passwordToCheck == null) {
            return false;
        }
        return usernameToCheck.equals(this.username) && passwordToCheck.equals(this.password);
    }

    //Returning the appropriate login status message.@param loginSuccessful the result of loginUser()
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}
