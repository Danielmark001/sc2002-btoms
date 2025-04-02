package stores;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User;
import services.CsvDataService;
import util.FilePathsUtils;

/**
 * Central data store for the application. Manages loading and saving data.
 * Follows the Singleton pattern.
 */
public class DataStore {
    private static DataStore instance;
    private Map<String, List<String[]>> allData;
    private CsvDataService csvDataService;
    private Map<String, String> filePaths;
    private boolean initialized = false;

    // Private constructor for Singleton pattern
    private DataStore() {
        this.csvDataService = new CsvDataService();
        this.allData = new HashMap<>();
        this.filePaths = FilePathsUtils.csvFilePaths();
    }

    /**
     * Gets the singleton instance of DataStore
     * 
     * @return DataStore instance
     */
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Initializes the data store with service and file paths
     * 
     * @param csvService CSV data service
     * @param filePaths Map of file paths
     */
    public static void initDataStore(CsvDataService csvService, Map<String, String> filePaths) {
        DataStore store = getInstance();
        store.csvDataService = csvService;
        store.filePaths = filePaths;
        store.loadData();
        store.initialized = true;
    }

    /**
     * Loads data from all CSV files
     */
    private void loadData() {
        try {
            allData = csvDataService.readAllData(filePaths);
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            // Initialize with empty data if files don't exist
            for (String fileType : filePaths.keySet()) {
                allData.put(fileType, List.of());
            }
        }
    }

    /**
     * Saves all data to CSV files
     */
    public static void saveData() {
        DataStore store = getInstance();
        if (!store.initialized) {
            return;
        }
        
        try {
            store.csvDataService.writeAllData(store.filePaths, store.allData);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    /**
     * Gets a user by NRIC
     * 
     * @param nric User's NRIC
     * @return User object if found, null otherwise
     */
    public User getUserByNRIC(String nric) {
        List<String[]> users = allData.getOrDefault("users", List.of());
        for (String[] userData : users) {
            if (userData.length > 0 && userData[0].equals(nric)) {
                // Convert user data to User object
                return createUserFromData(userData);
            }
        }
        return null;
    }

    /**
     * Updates an existing user
     * 
     * @param user User to update
     * @return true if updated successfully, false if user doesn't exist
     */
    public boolean updateUser(User user) {
        if (user == null) {
            return false;
        }

        List<String[]> users = allData.getOrDefault("users", List.of());
        for (int i = 0; i < users.size(); i++) {
            String[] userData = users.get(i);
            if (userData.length > 0 && userData[0].equals(user.getNric())) {
                // Convert User object to string array
                users.set(i, convertUserToData(user));
                saveData();
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all users
     * 
     * @return List of all User objects
     */
    public static List<User> getUsers() {
        DataStore store = getInstance();
        List<String[]> usersData = store.allData.getOrDefault("users", List.of());
        return usersData.stream()
                .map(store::createUserFromData)
                .filter(user -> user != null)
                .toList();
    }

    /**
     * Adds a new user
     * 
     * @param user User to add
     * @return true if added successfully
     */
    public static boolean addUser(User user) {
        if (user == null) {
            return false;
        }

        DataStore store = getInstance();
        List<String[]> users = store.allData.getOrDefault("users", List.of());
        
        // Check if user already exists
        for (String[] userData : users) {
            if (userData.length > 0 && userData[0].equals(user.getNric())) {
                return false; // User already exists
            }
        }
        
        // Convert User object to string array and add
        users.add(store.convertUserToData(user));
        store.allData.put("users", users);
        saveData();
        return true;
    }

    /**
     * Helper method to create a User object from string array data
     * 
     * @param userData String array of user data
     * @return User object
     */
    private User createUserFromData(String[] userData) {
        // Implementation would depend on your User class structure
        // This is a placeholder
        return null; 
    }

    /**
     * Helper method to convert a User object to string array data
     * 
     * @param user User object
     * @return String array of user data
     */
    private String[] convertUserToData(User user) {
        // Implementation would depend on your User class structure
        // This is a placeholder
        return null;
    }
}