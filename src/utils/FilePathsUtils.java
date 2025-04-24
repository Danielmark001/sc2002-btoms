package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link FilePathsUtils} class provides utility methods for managing file
 * paths within the application. It contains a method to return a mapping of CSV
 * file paths for various data types.
 */
public class FilePathsUtils {
	/**
	 * A {@link Map} object that contains the file paths for various data types used
	 */
	private static Map<String, String> filePathsMap = new HashMap<String, String>();

	/**
     * Private constructor to prevent instantiation of the class.
     */
    private FilePathsUtils() {
    };

	/**
	 * Returns a mapping of CSV file paths for various data types used in the
	 * application. The returned map contains keys such as "user", "student",
	 * "HDB Manager", "coordinator", "project", "request",
	 * "transferStudentRequest", and "changeProjectTitleRequest",
	 * each associated with their respective file paths.
	 *
	 * @return a {@link Map} containing the CSV file paths for various data types
	 */
	public static Map<String, String> csvFilePaths() {
		filePathsMap.clear();

		// Initialize filePathsMap
		filePathsMap.put("applicant", "data/ApplicantList.csv");
		filePathsMap.put("hdbManager", "data/HDBManagerList.csv");
		filePathsMap.put("hdbOfficer", "data/HDBOfficerList.csv");
		filePathsMap.put("hdbOfficers", "data/HDBOfficer.csv");
		filePathsMap.put("btoProject", "data/BTOProjectList.csv");
		filePathsMap.put("btoApplication", "data/BTOApplicationList.csv");
		filePathsMap.put("hdbOfficerRegistrations", "data/HDBOfficerRegistrationList.csv");
		filePathsMap.put("enquiry", "data/EnquiryList.csv");
		filePathsMap.put("withdrawalRequest", "data/WithdrawalRequestList.csv");

		return filePathsMap;
	}
}