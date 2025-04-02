package models;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import enumeration.*;

public class User {
    // Potential Bug Fix: Make fields final where possible
    private final String nric;
    private String name;
    private LocalDate dateOfBirth;
    private String contactNumber;
    private String email;
    private String password;
    private UserType userType;
    private MaritalStatus maritalStatus;
    
    // Bug Fix: Use defensive copying for collections
    private List<Application> applications;
    private List<Registration> registrations;

    // Regex patterns for validation
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Z]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+?6?0?)[1-9]\\d{7,9}$");

    // Constructors with comprehensive validation
    public User(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus) {
        validateNRIC(nric);
        validateName(name);
        validateDateOfBirth(dateOfBirth);
        
        this.nric = nric;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.maritalStatus = maritalStatus;
        
        // Bug Fix: Initialize collections safely
        this.applications = new ArrayList<>();
        this.registrations = new ArrayList<>();
        
        // Default initialization
        this.userType = UserType.APPLICANT;
        this.password = generateDefaultPassword();
    }

    // Comprehensive validation methods
    private void validateNRIC(String nric) {
        if (nric == null || !NRIC_PATTERN.matcher(nric).matches()) {
            throw new IllegalArgumentException("Invalid NRIC format");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            throw new IllegalArgumentException("Invalid name");
        }
    }

    private void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid date of birth");
        }
    }

    // Bug Fix: Generate a more secure default password
    private String generateDefaultPassword() {
        return "Temp" + System.currentTimeMillis() + "!";
    }

    // Enhanced age calculation with robust error handling
    public int calculateAge() {
        try {
            return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
        } catch (Exception e) {
            // Log error or handle exceptional cases
            return -1;
        }
    }

    // Secure password change method
    public boolean setPassword(String oldPassword, String newPassword) {
        // Bug Fix: Add password complexity checks
        if (!authenticate(oldPassword)) {
            return false;
        }
        
        if (!isPasswordValid(newPassword)) {
            throw new IllegalArgumentException("Password does not meet complexity requirements");
        }
        
        this.password = newPassword;
        return true;
    }

    // Enhanced password validation
    private boolean isPasswordValid(String password) {
        return password != null && 
               password.length() >= 8 && 
               password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    // Enhanced authentication
    public boolean authenticate(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    // Defensive copy methods
    public List<Application> getApplications() {
        return new ArrayList<>(applications);
    }

    public List<Registration> getRegistrations() {
        return new ArrayList<>(registrations);
    }

    // Bug Fix: Safe method for adding applications
    public void addApplication(Application application) {
        if (application != null && !applications.contains(application)) {
            applications.add(application);
        }
    }

    // Bug Fix: Safe method for adding registrations
    public void addRegistration(Registration registration) {
        if (registration != null && !registrations.contains(registration)) {
            registrations.add(registration);
        }
    }

    // Enhanced email setter with validation
    public void setEmail(String email) {
        if (email == null || EMAIL_PATTERN.matcher(email).matches()) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    // Enhanced contact number setter with validation
    public void setContactNumber(String contactNumber) {
        if (contactNumber == null || PHONE_PATTERN.matcher(contactNumber).matches()) {
            this.contactNumber = contactNumber;
        } else {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    // Comprehensive equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(nric, user.nric);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nric);
    }

    // Comprehensive toString
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

    public String getNric() {
        return nric;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public UserType getStatus() {
        return userType;
    }

    public void setStatus(UserType userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (isPasswordValid(password)) {
            this.password = password;
        } else {
            throw new IllegalArgumentException("Invalid password format");
        }
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        validateDateOfBirth(dateOfBirth);
        this.dateOfBirth = dateOfBirth;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setApplications(List<Application> applications) {
        this.applications = new ArrayList<>(applications);
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = new ArrayList<>(registrations);
    }

    




}