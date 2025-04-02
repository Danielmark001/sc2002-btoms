package models;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import enumeration.FlatType;
import enumeration.MaritalStatus;
import enumeration.UserType;

public class User {
    private String nric;
    private String name;
    private LocalDate dateOfBirth;
    private String contactNumber;
    private String email;
    private String password;
    private UserType userType;
    private MaritalStatus maritalStatus;
    private List<Application> applications;
    private List<Registration> registrations;
    private String appliedProjectId;
    private String bookedProjectId;
    private FlatType bookedFlatType;

    // Constructors
    public User() {
        this.applications = new ArrayList<>();
        this.registrations = new ArrayList<>();
    }

    public User(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus) {
        validateNRIC(nric);
        this.nric = nric;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.maritalStatus = maritalStatus;
        this.password = "password"; // default password as per requirement
        this.userType = UserType.APPLICANT; // default user type
        this.applications = new ArrayList<>();
        this.registrations = new ArrayList<>();
    }

    // Full constructor with all parameters
    public User(String nric, String password, int age, MaritalStatus maritalStatus, UserType userType) {
        validateNRIC(nric);
        this.nric = nric;
        this.password = password;
        this.dateOfBirth = LocalDate.now().minusYears(age);
        this.maritalStatus = maritalStatus;
        this.userType = userType;
        this.applications = new ArrayList<>();
        this.registrations = new ArrayList<>();
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

    // Password Change and Authentication Methods
    /**
     * Authenticates the user with the provided password
     * 
     * @param inputPassword Password to verify
     * @return true if password matches, false otherwise
     */
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    /**
     * Changes the user's password
     * 
     * @param oldPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password changed successfully, false otherwise
     */
    public boolean setPassword(String oldPassword, String newPassword) {
        if (authenticate(oldPassword)) {
            this.password = newPassword;
            return true;
        }
        return false;
    }
    
    /**
     * Sets a new password directly (used during initial setup or password reset)
     * 
     * @param newPassword New password to set
     */
    public void setPassword(String newPassword) {
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

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public List<Application> getApplications() {
        return new ArrayList<>(applications);
    }

    public void setApplications(List<Application> applications) {
        this.applications = new ArrayList<>(applications);
    }
    
    public void addApplication(Application application) {
        if (!applications.contains(application)) {
            applications.add(application);
        }
    }

    public List<Registration> getRegistrations() {
        return new ArrayList<>(registrations);
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = new ArrayList<>(registrations);
    }
    
    public void addRegistration(Registration registration) {
        if (!registrations.contains(registration)) {
            registrations.add(registration);
        }
    }
    
    public String getAppliedProjectId() {
        return appliedProjectId;
    }

    public void setAppliedProjectId(String appliedProjectId) {
        this.appliedProjectId = appliedProjectId;
    }

    public String getBookedProjectId() {
        return bookedProjectId;
    }

    public void setBookedProjectId(String bookedProjectId) {
        this.bookedProjectId = bookedProjectId;
    }

    public FlatType getBookedFlatType() {
        return bookedFlatType;
    }

    public void setBookedFlatType(FlatType bookedFlatType) {
        this.bookedFlatType = bookedFlatType;
    }

    // Utility methods
    /**
     * Checks if the user has applied for any project
     * 
     * @return true if the user has applied, false otherwise
     */
    public boolean hasApplied() {
        return appliedProjectId != null;
    }
    
    /**
     * Checks if the user has booked a flat
     * 
     * @return true if the user has booked, false otherwise
     */
    public boolean hasBooked() {
        return bookedProjectId != null && bookedFlatType != null;
    }
    
    /**
     * Gets the age of the user
     * 
     * @return User's age
     */
    public int getAge() {
        return calculateAge();
    }

    @Override
    public String toString() {
        return "User{" +
                "nric='" + nric + '\'' +
                ", name='" + name + '\'' +
                ", age=" + calculateAge() +
                ", userType=" + userType +
                ", maritalStatus=" + maritalStatus +
                '}';
    }
}