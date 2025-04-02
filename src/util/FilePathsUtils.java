package utils;

import java.util.HashMap;
import java.util.Map;

public class FilePathsUtils {
    // File paths for different data types
    private static final String BASE_PATH = "data/";
    private static final String USER_FILE = BASE_PATH + "users.csv";
    private static final String PROJECT_FILE = BASE_PATH + "projects.csv";
    private static final String APPLICATION_FILE = BASE_PATH + "applications.csv";
    private static final String REGISTRATION_FILE = BASE_PATH + "registrations.csv";
    private static final String ENQUIRY_FILE = BASE_PATH + "enquiries.csv";

    /**
     * Provides a map of CSV file paths for different data types.
     * 
     * @return Map of data type to file path
     */
    public static Map<String, String> csvFilePaths() {
        Map<String, String> paths = new HashMap<>();
        paths.put("users", USER_FILE);
        paths.put("projects", PROJECT_FILE);
        paths.put("applications", APPLICATION_FILE);
        paths.put("registrations", REGISTRATION_FILE);
        paths.put("enquiries", ENQUIRY_FILE);
        return paths;
    }

    /**
     * Get file path for a specific data type.
     * 
     * @param dataType Type of data (e.g., "users", "projects")
     * @return File path for the specified data type
     */
    public static String getFilePath(String dataType) {
        Map<String, String> paths = csvFilePaths();
        return paths.getOrDefault(dataType, "");
    }
}