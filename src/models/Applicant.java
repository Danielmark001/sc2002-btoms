package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import enumeration.MaritalStatus;
import enumeration.UserStatus;
import enumeration.FlatType;
import enumeration.ApplicationStatus;
import enumeration.UserType;
public class Applicant extends User {
    private BTOProject bookedProject;
    private FlatType bookedFlatType;
    

    // Constructor
    public Applicant(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus) {
        super(nric, name, dateOfBirth, maritalStatus);
    }

    // Getter and setter for booked project
    public BTOProject getBookedProject() {
        return bookedProject;
    }

    public void setBookedProject(BTOProject bookedProject) {
        this.bookedProject = bookedProject;
    }

    // Getter and setter for booked flat type
    public FlatType getBookedFlatType() {
        return bookedFlatType;
    }

    public void setBookedFlatType(FlatType bookedFlatType) {
        this.bookedFlatType = bookedFlatType;
    }

    // Method to check if applicant is eligible for a specific flat type
    public boolean isEligibleForFlatType(FlatType flatType) {
        int age = calculateAge();
        
        switch (flatType) {
            case TWO_ROOM:
                return (this.getMaritalStatus() == MaritalStatus.SINGLE && age >= 35) ||
                       (this.getMaritalStatus() == MaritalStatus.MARRIED && age >= 21);
            case THREE_ROOM:
                return this.getMaritalStatus() == MaritalStatus.MARRIED && age >= 21;
            default:
                return false;
        }
    }

    // Method to check if applicant has an active application
    public boolean hasActiveApplication() {
        return this.getApplications().stream()
            .anyMatch(app -> app.getStatus() != ApplicationStatus.BOOKED && 
                              app.getStatus() != ApplicationStatus.UNSUCCESSFUL);
    }

    // Override toString to include applicant-specific information
    @Override
    public String toString() {
        return "Applicant{" +
                "name='" + getName() + '\'' +
                ", nric='" + getNric() + '\'' +
                ", age=" + calculateAge() +
                ", maritalStatus=" + getMaritalStatus() +
                ", bookedProject=" + (bookedProject != null ? bookedProject.getProjectName() : "None") +
                ", bookedFlatType=" + bookedFlatType +
                '}';
    }

    // Method to apply for a project
   

 
    
    
    


}