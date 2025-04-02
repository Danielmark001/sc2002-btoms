package stores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User;
import services.CsvDataService;
import util.FilePathsUtils;
import enumeration.UserType;
import enumeration.MaritalStatus;
import enumeration.UserStatus;

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

    public boolean deleteUser(String nric) {
        List<String[]> users = allData.getOrDefault("users", new ArrayList<>());

        boolean removed = users.removeIf(userData -> userData[1].equals(nric));

        if (removed) {
            allData.put("users", users);
            saveData();
        }

        return removed;
    }

    public boolean updateUser(User user) {
        List<String[]> users = allData.getOrDefault("users", new ArrayList<>());

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i)[1].equals(user.getNric())) {
                users.set(i, new String[] {
                        user.getName(),
                        user.getNric(),
                        String.valueOf(user.calculateAge()),
                        user.getMaritalStatus().toString(),
                        user.getPassword()
                });
                allData.put("users", users);
                saveData();
                return true;
            }
        }

        return false;
    }
    
    public List<User> getUsersByType(UserType userType) {
        List<String[]> users = allData.getOrDefault("users", new ArrayList<>());
        List<User> filteredUsers = new ArrayList<>();

        for (String[] userData : users) {
            if (userData[3].equals(userType)) {
                filteredUsers.add(createUserFromData(userData));
            }
        }

        return filteredUsers;
    }
    
    public List<String[]> getApplicationsByProject(String projectId) {
        List<String[]> applications = allData.getOrDefault("applications", new ArrayList<>());
        List<String[]> filteredApplications = new ArrayList<>();

        for (String[] applicationData : applications) {
            if (applicationData[1].equals(projectId)) {
                filteredApplications.add(applicationData);
            }
        }

        return filteredApplications;
    }

    
    public List<String[]> getApplications() {
        return allData.getOrDefault("applications", new ArrayList<>());
    }
    public List<String[]> getProjects() {
        return allData.getOrDefault("projects", new ArrayList<>());
    }
    
    public List<String[]> getHDBOfficers() {
        return allData.getOrDefault("hdb_officers", new ArrayList<>());
    }

    public List<String[]> getHDBManagers() {
        return allData.getOrDefault("hdb_managers", new ArrayList<>());
    }

    public static List<String[]> getAllApplications() {
        DataStore store = getInstance();
        return store.allData.getOrDefault("applications", new ArrayList<>());
    }
    public List<String[]> getAllProjects() {
        return allData.getOrDefault("projects", new ArrayList<>());
    }


}