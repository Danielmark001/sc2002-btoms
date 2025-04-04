package util;

import java.util.HashMap;
import java.util.Map;

public class FilePathsUtils {
    // File paths for different data types
    private static final String BASE_PATH = "data/";
    private static final String APPLICANT_FILE = BASE_PATH + "ApplicantList.csv";
    private static final String MANAGER_FILE = BASE_PATH + "ManagerList.csv";
    private static final String OFFICER_FILE = BASE_PATH + "OfficerList.csv";
    private static final String PROJECT_FILE = BASE_PATH + "ProjectList.csv";
    private static final String APPLICATION_FILE = BASE_PATH + "ApplicationList.csv";
    private static final String REGISTRATION_FILE = BASE_PATH + "OfficerRegistration.csv";
    private static final String ENQUIRY_FILE = BASE_PATH + "EnquiryList.csv";

    /**
     * Provides a map of CSV file paths for different data types.
     * 
     * @return Map of data type to file path
     */
    public static Map<String, String> csvFilePaths() {
        Map<String, String> paths = new HashMap<>();
        paths.put("users", APPLICANT_FILE);
        paths.put("managers", MANAGER_FILE);
        paths.put("officers", OFFICER_FILE);
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

    /**
     * Provides a method to update file paths if needed
     * 
     * @param dataType Type of data
     * @param newPath New file path
     */
    public static void updateFilePath(String dataType, String newPath) {
        Map<String, String> paths = csvFilePaths();
        if (paths.containsKey(dataType)) {
            paths.put(dataType, newPath);
        }
    }
}