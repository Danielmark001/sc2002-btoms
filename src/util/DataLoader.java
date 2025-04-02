package bto_management_system.util;

import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.User;
import bto_management_system.model.enumeration.MaritalStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for loading data from data files
 */
public class DataLoader {
    private static final String DATA_DIRECTORY = "data";
    private static final String USERS_FILE = DATA_DIRECTORY + "/users.ser";
    private static final String PROJECTS_FILE = DATA_DIRECTORY + "/projects.ser";
    private static final String USER_TXT_FILE = DATA_DIRECTORY + "/users.txt";
    
    /**
     * Loads users from data file
     * 
     * @return List of users
     */
    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() {
        // Create data directory if it doesn't exist
        createDataDirectory();
        
        // First try to load serialized users
        File userFile = new File(USERS_FILE);
        if (userFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
                return (List<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading users from serialized file: " + e.getMessage());
            }
        }
        
        // If serialized file doesn't exist or couldn't be loaded, try text file
        File userTxtFile = new File(USER_TXT_FILE);
        if (userTxtFile.exists()) {
            try {
                List<User> users = new ArrayList<>();
                List<String> lines = Files.readAllLines(Paths.get(USER_TXT_FILE));
                
                // Skip header line
                for (int i = 1; i < lines.size(); i++) {
                    User user = parseUserLine(lines.get(i));
                    if (user != null) {
                        users.add(user);
                    }
                }
                
                return users;
            } catch (IOException e) {
                System.err.println("Error loading users from text file: " + e.getMessage());
            }
        }
        
        // Return empty list if no files could be loaded
        return new ArrayList<>();
    }
    
    /**
     * Parses a line from the users text file
     * 
     * @param line Line to parse
     * @return User object if parsed successfully, null otherwise
     */
    private static User parseUserLine(String line) {
        // Expected format: type,nric,password,age,maritalStatus
        String[] parts = line.split(",");
        if (parts.length < 5) {
            return null;
        }
        
        String type = parts[0].trim();
        String nric = parts[1].trim();
        String password = parts[2].trim();
        int age;
        try {
            age = Integer.parseInt(parts[3].trim());
        } catch (NumberFormatException e) {
            return null;
        }
        
        MaritalStatus maritalStatus;
        try {
            maritalStatus = MaritalStatus.valueOf(parts[4].trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
        
        // Create appropriate user type
        switch (type.toUpperCase()) {
            case "APPLICANT":
                return new bto_management_system.model.entity.Applicant(nric, password, age, maritalStatus);
            case "OFFICER":
                return new bto_management_system.model.entity.HDBOfficer(nric, password, age, maritalStatus);
            case "MANAGER":
                return new bto_management_system.model.entity.HDBManager(nric, password, age, maritalStatus);
            default:
                return null;
        }
    }
    
    /**
     * Loads projects from data file
     * 
     * @return List of projects
     */
    @SuppressWarnings("unchecked")
    public static List<BTOProject> loadProjects() {
        // Create data directory if it doesn't exist
        createDataDirectory();
        
        File projectFile = new File(PROJECTS_FILE);
        if (projectFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(projectFile))) {
                return (List<BTOProject>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading projects: " + e.getMessage());
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Creates the data directory if it doesn't exist
     */
    private static void createDataDirectory() {
        File dir = new File(DATA_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}

