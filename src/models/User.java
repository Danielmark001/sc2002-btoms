package models;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

public class User {
    private String nric;
    private String name;
    private LocalDate dateOfBirth;
    private String contactNumber;
    private String email;
    private String password;
    private UserStatus status;
    private MaritalStatus maritalStatus;
    private List<Application> applications;
    private List<Registration> registrations;

    // Enums for user-related statuses
    public enum UserStatus {
        OFFICER,
        APPLICANT,
        MANAGER,
        INACTIVE
    }

    // Enum for marital status
    public enum MaritalStatus {
        SINGLE,
        MARRIED
    }

    // Constructors
    public User() {}

    public User(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus) {
        validateNRIC(nric);
        this.nric = nric;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.maritalStatus = maritalStatus;
        this.password = "password"; // default password as per requirement
        this.status = UserStatus.APPLICANT; // default status
    }

    // NRIC Validation Method
    private void validateNRIC(String nric) {
        if (nric == null || !Pattern.matches("^[ST]\\d{7}[A-Z]$", nric)) {
            throw new IllegalArgumentException("Invalid NRIC format. Must start with S or T, followed by 7 digits, and end with a letter.");
        }
    }

    // Calculate Age Method
    public int calculateAge() {
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    // Password Change Method
    public void changePassword(String newPassword) {
        // Add password complexity checks if needed
        this.password = newPassword;
    }

    // Getters and Setters
    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        validateNRIC(nric);
        this.nric = nric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

    // Additional method to check officer status
    public boolean isOfficer() {
        return this.status == UserStatus.OFFICER;
    }

    public boolean isManager() {
        return this.status == UserStatus.MANAGER;
    }

    @Override
    public String toString() {
        return "User{" +
                "nric='" + nric + '\'' +
                ", name='" + name + '\'' +
                ", age=" + calculateAge() +
                ", status=" + status +
                ", maritalStatus=" + maritalStatus +
                '}';
    }

    // Authentication method
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}