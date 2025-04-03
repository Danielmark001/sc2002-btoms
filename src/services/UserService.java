package services;

import interfaces.IUserService;
import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.User;
import stores.AuthStore;
import stores.DataStore;
import enumeration.UserType;
import enumeration.MaritalStatus;
import enumeration.UserStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class UserService implements IUserService {
    private static UserService instance;
    private DataStore dataStore;
    private AuthStore authStore;

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]+$");
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Z]$");

    private UserService() {
        this.dataStore = DataStore.getInstance();
        this.authStore = AuthStore.getInstance();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    @Override
    public boolean login(String nric, String password) {
        if (!validateNRIC(nric)) {
            return false;
        }
        return authStore.login(nric, password);
    }

    @Override
    public void logout() {
        authStore.logout();
    }

    @Override
    public User getCurrentUser() {
        return AuthStore.getCurrentUser();
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH && PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!isValidPassword(newPassword)) {
            return false;
        }
        return authStore.changePassword(oldPassword, newPassword);
    }

    @Override
    public boolean isApplicant() {
        return AuthStore.isApplicant();
    }

    @Override
    public boolean isHdbOfficer() {
        return AuthStore.isHdbOfficer();
    }

    @Override
    public boolean isHdbManager() {
        return AuthStore.isHdbManager();
    }

    @Override
    public User getUserByNRIC(String nric) {
        return dataStore.getUserByNRIC(nric);
    }

    @Override
    public List<User> getAllUsers() {
        return DataStore.getUsers();
    }

    @Override
    public List<User> getUsersByType(UserType userType) {
        return dataStore.getUsersByType(userType);
    }

    @Override
    public boolean createUser(String nric, String password, int age, MaritalStatus maritalStatus, UserType userType) {
        // Comprehensive validation
        if (!validateNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC format");
        }

        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        if (age < 21) {
            throw new IllegalArgumentException("User must be at least 21 years old");
        }

        User existingUser = getUserByNRIC(nric);
        if (existingUser != null) {
            throw new IllegalArgumentException("User with this NRIC already exists");
        }

        // Calculate date of birth from age
        LocalDate dateOfBirth = LocalDate.now().minusYears(age);
        
        // Create appropriate user type
        User newUser;
        switch (userType) {
            case APPLICANT:
                newUser = new Applicant(nric, null, dateOfBirth, maritalStatus);
                break;
            case OFFICER:
                newUser = new HDBOfficer(nric, null, dateOfBirth, maritalStatus);
                break;
            case MANAGER:
                newUser = new HDBManager(nric, null, dateOfBirth);
                break;
            default:
                throw new IllegalArgumentException("Invalid user type");
        }
    
    // Set password and default name (can be updated later)
    newUser.setPassword(password);
    newUser.setName("User " + nric); // Set a default name
    
    return DataStore.addUser(newUser);
}

    @Override
    public boolean updateUser(User user) {
        if (user == null) {
            return false;
        }
        return dataStore.updateUser(user);
    }

    @Override
    public boolean validateNRIC(String nric) {
        return nric != null && NRIC_PATTERN.matcher(nric).matches();
    }

    

    public boolean registerApplicant(String nric, String password, int age, MaritalStatus maritalStatus) {
        return createUser(nric, password, age, maritalStatus, UserType.APPLICANT);
    }

    public boolean authenticateUser(String nric, String password) {
        return login(nric, password);
    }

    /**
 * Creates a new user with full details including date of birth
 * 
 * @param nric User's NRIC
 * @param name User's name
 * @param dateOfBirth User's date of birth
 * @param maritalStatus User's marital status
 * @param userStatus User's status
 * @return Created user
 */
public User createUser(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus, UserStatus userStatus) {
    // Validate inputs
    if (!validateNRIC(nric)) {
        throw new IllegalArgumentException("Invalid NRIC format");
    }
    
    User existingUser = getUserByNRIC(nric);
    if (existingUser != null) {
        throw new IllegalArgumentException("User with this NRIC already exists");
    }
    
    // Create appropriate user type based on status
    User newUser;
    switch (userStatus) {
        case APPLICANT:
            newUser = new Applicant(nric, name, dateOfBirth, maritalStatus);
            break;
        case OFFICER:
            newUser = new HDBOfficer(nric, name, dateOfBirth, maritalStatus);
            break;
        case MANAGER:
            newUser = new HDBManager(nric, name, dateOfBirth);
            break;
        default:
            throw new IllegalArgumentException("Invalid user status");
    }
    
    // Set default password
    newUser.setPassword("password");
    
    // Add to data store
    DataStore.addUser(newUser);
    
    return newUser;
}

/**
 * Updates a user with new profile information
 * 
 * @param nric User's NRIC
 * @param name User's name
 * @param contactNumber User's contact number
 * @param email User's email
 * @return Updated user
 */
 public User updateUser(String nric, String name, String contactNumber, String email) {
    User user = getUserByNRIC(nric);
    if (user == null) {
        throw new IllegalArgumentException("User not found");
    }
    
    // Update fields if provided
    if (name != null && !name.isEmpty()) {
        user.setName(name);
    }
    
    if (contactNumber != null) {
        user.setContactNumber(contactNumber);
    }
    
    if (email != null) {
        user.setEmail(email);
    }
    
    // Update in data store
    dataStore.updateUser(user);
    
    return user;
}

    // Additional utility methods
    public boolean deleteUser(String nric) {
        return dataStore.deleteUser(nric);
    }

    public List<User> searchUsers(String searchTerm) {
        return getAllUsers().stream()
                .filter(user -> user.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        user.getNric().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    
    public void setCurrentUser(User user) {
        AuthStore.setCurrentUser(user);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            // Convert bytes to hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to plain text if hashing is not available
            System.err.println("Warning: Secure password hashing not available");
            return password;
        }
    }
public boolean authenticate(User user, String password) {
    if (user == null || password == null) {
        return false;
    }
    
    // For existing accounts with unhashed passwords, check direct match first
    if (user.getPassword().equals(password)) {
        // Update to hashed password for future authentications
        user.setPassword(hashPassword(password));
        return true;
    }
    
    // Otherwise, check against hashed password
    return user.getPassword().equals(hashPassword(password));
}
}