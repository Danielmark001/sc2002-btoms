package services;

import interfaces.IUserService;
import models.User;
import stores.AuthStore;
import stores.DataStore;
import enumeration.UserType;
import enumeration.MaritalStatus;
import java.util.List;
import java.util.stream.Collectors;

public class UserService implements IUserService {

    private static UserService instance;

    // Singleton pattern to ensure a single instance of UserService
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private UserService() {
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        User user = AuthStore.getCurrentUser();
        if (user == null)
            return false;
        if (!user.setPassword(oldPassword, newPassword))
            return false;

        DataStore.saveData(); // Save new password to database
        return true;
    }

    @Override
    public boolean isApplicant() {
        User user = AuthStore.getCurrentUser();
        return user != null && user.getUserType() == UserType.APPLICANT;
    }

    @Override
    public void logout() {
        AuthStore.setCurrentUser(null);
    }

    @Override
    public User getUserByNRIC(String nric) {
        return DataStore.getUsers().stream()
                .filter(user -> user.getNric().equals(nric))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean updateUser(User user) {
        List<User> users = DataStore.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getNric().equals(user.getNric())) {
                users.set(i, user);
                DataStore.saveData();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validateNRIC(String nric) {
        return nric != null && nric.matches("^[ST]\\d{7}[A-Z]$");
    }

    @Override
    public User createUser(String nric, String password, int age, MaritalStatus maritalStatus, UserType userType) {
        if (!validateNRIC(nric))
            return null;

        User newUser = new User(nric, password, age, maritalStatus, userType);
        DataStore.addUser(newUser);
        DataStore.saveData();
        return newUser;
    }

    @Override
    public User getCurrentUser() {
        return AuthStore.getCurrentUser();
    }

    @Override
    public List<User> getUsersByType(UserType userType) {
        return DataStore.getUsers().stream()
                .filter(user -> user.getUserType() == userType)
                .collect(Collectors.toList());
    }

    @Override
    public boolean registerApplicant(String nric, String password, int age, MaritalStatus maritalStatus) {
        if (!validateNRIC(nric))
            return false;

        User newUser = new User(nric, password, age, maritalStatus, UserType.APPLICANT);
        DataStore.addUser(newUser);
        DataStore.saveData();
        return true;
    }

    @Override
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*");
    }

    @Override
    public boolean authenticateUser(String nric, String password) {
        User user = getUserByNRIC(nric);
        if (user != null && user.getPassword().equals(password)) {
            AuthStore.setCurrentUser(user);
            return true;
        }
        return false;

    }
    @Override
    public boolean updateUser(String nric, String name, String contactNumber, String email) {
        User user = getUserByNRIC(nric);
        if (user == null)
            return false;

        user.setName(name);
        user.setContactNumber(contactNumber);
        user.setEmail(email);
        return updateUser(user);
    }
}
