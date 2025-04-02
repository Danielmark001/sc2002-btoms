package util;

import models.entity.BTOProject;
import models.entity.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Utility class for writing data to data files
 */
public class DataWriter {
    private static final String DATA_DIRECTORY = "data";
    private static final String USERS_FILE = DATA_DIRECTORY + "/users.ser";
    private static final String PROJECTS_FILE = DATA_DIRECTORY + "/projects.ser";
    
    /**
     * Saves users to data file
     * 
     * @param users List of users to save
     */
    public static void saveUsers(List<User> users) {
        // Create data directory if it doesn't exist
        createDataDirectory();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    /**
     * Saves projects to data file
     * 
     * @param projects List of projects to save
     */
    public static void saveProjects(List<BTOProject> projects) {
        // Create data directory if it doesn't exist
        createDataDirectory();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROJECTS_FILE))) {
            oos.writeObject(projects);
        } catch (IOException e) {
            System.err.println("Error saving projects: " + e.getMessage());
        }
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