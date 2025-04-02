package services;

import interfaces.IUserService;
import models.User;
import stores.AuthStore;
import stores.DataStore;
import enumeration.UserType;
import enumeration.MaritalStatus;

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

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!isValidPassword(newPassword)) {
            return false;
        }
        return authStore.changePassword(oldPassword, newPassword);
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH && PASSWORD_PATTERN.matcher(password).matches();
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

        User newUser = new User(nric, password, age, maritalStatus, userType);
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

    public boolean updateUser(String nric, String name, String contactNumber, String email) {
        User user = getUserByNRIC(nric);
        if (user == null) {
            return false;
        }

        user.setName(name);
        user.setContactNumber(contactNumber);
        user.setEmail(email);

        return updateUser(user);
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
}