package models;

import enumeration.MaritalStatus;
import enumeration.UserType;

public class Applicant extends User {
    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password, UserType.APPLICANT);
    }
}
