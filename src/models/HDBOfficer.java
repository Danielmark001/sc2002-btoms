package models;

import enumeration.MaritalStatus;
import enumeration.UserType;

public class HDBOfficer extends User {
    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password, UserType.HDB_OFFICER);
    }
}


