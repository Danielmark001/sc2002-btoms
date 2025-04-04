package models;

import enumeration.UserType;
import enumeration.MaritalStatus;

public class User {
    private String name;
    private String nric;
    private int age;
    private MaritalStatus maritalStatus;
    private String password;
    private UserType userType;

    public User(String name, String nric, int age, MaritalStatus maritalStatus, String password, UserType userType) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = password;
        this.userType = userType;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getNric() {
        return nric;
    }

    public int getAge() {
        return age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public boolean setPassword(String oldPassword, String newPassword) {
        if (oldPassword.equals(password)) {
            password = newPassword;
            return true;
        }
        return false;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", nric='" + nric + '\'' +
                ", age=" + age +
                ", maritalStatus=" + maritalStatus +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                '}';
    }
}   
