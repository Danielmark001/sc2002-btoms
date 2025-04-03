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
    private List<BTOApplication> applications;
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
        this.password = "password"; // Default password
    }
    
    public User(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus, String password) {
        this(nric, name, dateOfBirth, maritalStatus);
        this.password = password;
    }

    public User(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus, String password,
            UserType userType) {
        this(nric, name, dateOfBirth, maritalStatus);
        this.password = password;
        this.userType = userType;
    }
    public User(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus, String password,
            UserType userType, String contactNumber, String email) {
        this(nric, name, dateOfBirth, maritalStatus);
        this.password = password;
        this.userType = userType;
        this.contactNumber = contactNumber;
        this.email = email;
    }
    public User(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus, String password,
            UserType userType, String contactNumber, String email, List<BTOApplication> applications,
            List<Registration> registrations) {
        this(nric, name, dateOfBirth, maritalStatus);
        this.password = password;
        this.userType = userType;
        this.contactNumber = contactNumber;
        this.email = email;
        this.applications = applications;
        this.registrations = registrations;
    }
    public User(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus, String password,
            UserType userType, String contactNumber, String email, List<BTOApplication> applications,
            List<Registration> registrations, String status) {
        this(nric, name, dateOfBirth, maritalStatus);
        this.password = password;
        this.userType = userType;
        this.contactNumber = contactNumber;
        this.email = email;
        this.applications = applications;
        this.registrations = registrations;
    }
    public User() {
        this.nric = null;
        this.name = null;
        this.dateOfBirth = null;
        this.maritalStatus = null;
        this.password = null;
        this.userType = UserType.APPLICANT; // Default user type
        this.applications = new ArrayList<>();
        this.registrations = new ArrayList<>();
    }

    // Additional constructor for complete initialization
    public User(String nric, String password, int age, MaritalStatus maritalStatus, UserType userType) {
        this.nric = nric;
        this.password = password;
        // Calculate date of birth from age
        this.dateOfBirth = LocalDate.now().minusYears(age);
        this.maritalStatus = maritalStatus;
        this.userType = userType;
        this.applications = new ArrayList<>();
        this.registrations = new ArrayList<>();
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

    // Enhanced age calculation with robust error handling
/**
 * Enhanced age calculation with robust error handling
 * @return Age in years, or -1 if date of birth is invalid
 */
    public int calculateAge() {
        try {
            if (this.dateOfBirth == null) {
                return -1;
            }
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

    
    // Enhanced authentication
    public boolean authenticate(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    // Defensive copy methods
    public List<BTOApplication> getApplications() {
        return new ArrayList<>(applications);
    }

    public List<Registration> getRegistrations() {
        return new ArrayList<>(registrations);
    }

    // Bug Fix: Safe method for adding applications
    public void addApplication(BTOApplication application) {
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
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

    public void setApplications(List<BTOApplication> applications) {
        this.applications = new ArrayList<>(applications);
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = new ArrayList<>(registrations);
    }
    
    // Bug Fix: Added method to check if user has applied
    public boolean hasApplied() {
        return !applications.isEmpty();
    }
    
    // Bug Fix: Added method to get applied project ID
    public String getAppliedProjectId() {
        if (!applications.isEmpty()) {
            return applications.get(0).getProject().getProjectId();
        }
        return null;
    }
    
    // Bug Fix: Method to check if user is HDB manager
    public boolean isHdbManager() {
        return userType == UserType.MANAGER;
    }
    
    // Utility method that can be used to check role
    public Role getRole() {
        if (userType == UserType.APPLICANT) {
            return Role.APPLICANT;
        } else if (userType == UserType.OFFICER) {
            return Role.OFFICER;
        } else if (userType == UserType.MANAGER) {
            return Role.MANAGER;
        }
        return Role.APPLICANT; // Default
    }
    
    // Added enum for roles to make switching easier
    public enum Role {
        APPLICANT,
        OFFICER,
        MANAGER
    }
    public Applicant toApplicant() {
    if (this instanceof Applicant) {
        return (Applicant) this;
    }
    
    Applicant applicant = new Applicant(getNric(), getName(), getDateOfBirth(), getMaritalStatus());
    applicant.setContactNumber(getContactNumber());
    applicant.setEmail(getEmail());
    applicant.setPassword(getPassword());
    return applicant;
}

/**
 * Converts this user to an HDBOfficer
 * @return HDBOfficer version of this user
 */
public HDBOfficer toHDBOfficer() {
    if (this instanceof HDBOfficer) {
        return (HDBOfficer) this;
    }
    
    HDBOfficer officer = new HDBOfficer(getNric(), getName(), getDateOfBirth(), getMaritalStatus());
    officer.setContactNumber(getContactNumber());
    officer.setEmail(getEmail());
    officer.setPassword(getPassword());
    return officer;
}

/**
 * Converts this user to an HDBManager
 * @return HDBManager version of this user
 */
public HDBManager toHDBManager() {
    if (this instanceof HDBManager) {
        return (HDBManager) this;
    }

    HDBManager manager = new HDBManager(getNric(), getName(), getDateOfBirth());
    manager.setContactNumber(getContactNumber());
    manager.setEmail(getEmail());
    manager.setPassword(getPassword());
    return manager;
}
private boolean isPasswordValid(String password) {
    // Password must:
    // 1. Be at least 8 characters long
    // 2. Contain at least one uppercase letter
    // 3. Contain at least one digit
    if (password == null || password.length() < 8) {
        return false;
    }
    
    boolean hasUppercase = false;
    boolean hasDigit = false;
    
    for (char c : password.toCharArray()) {
        if (Character.isUpperCase(c)) {
            hasUppercase = true;
        } else if (Character.isDigit(c)) {
            hasDigit = true;
        }
        
        if (hasUppercase && hasDigit) {
            return true;
        }
    }
    
    return false;
}



}