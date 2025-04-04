package interfaces;

import java.util.Map;

import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;

/**
 * The {@link IFileDataService} interface defines a contract for importing and
 * exporting data to and from files.
 */
public interface IFileDataService {
	// ---------- Student ---------- //
	/**
	 * Imports applicant data from the specified file paths.
	 *
	 * @param applicantsFilePath the file path of the applicants file
	 * @return a {@link Map} of {@link Applicant} objects with their IDs as keys
	 */
	Map<String, Applicant> importApplicantData(String applicantsFilePath);
	
	/**
	 * Exports applicant data to the specified file paths.
	 *
	 * @param applicantsFilePath the file path of the applicants file
	 * @param applicantMap       a {@link Map} of {@link Applicant} objects with their
	 *                         IDs as keys
	 * @return true if the data was exported successfully, false otherwise
	 */
	boolean exportApplicantData(String applicantsFilePath, Map<String, Applicant> applicantMap);

	// ---------- HDBManager ---------- //
	/**
	 * Imports HDB Manager data from the specified file paths.
	 *
	 * @param hdbManagerFilePath the file path of the HDB Managers file
	 * @return a {@link Map} of {@link HDBManager} objects with their IDs as keys
	 */
	Map<String, HDBManager> importHDBManagerData(String hdbManagerFilePath);
	
	/**
	 * Exports HDB Manager data to the specified file paths.
	 *
	 * @param hdbManagerFilePath the file path of the HDB Managers file
	 * @param hdbManagerMap       a {@link Map} of {@link HDBManager} objects with
	 *                            their IDs as keys
	 * @return true if the data was exported successfully, false otherwise
	 */
	boolean exportHDBManagerData(String hdbManagerFilePath, Map<String, HDBManager> hdbManagerMap);

	// ---------- HDBOfficer ---------- //
	/**
	 * Imports HDB Officer data from the specified file paths.
	 *
	 * @param hdbOfficerFilePath the file path of the HDB Officers file
	 * @return a {@link Map} of {@link HDBOfficer} objects with their IDs as
	 *         keys
	 */
	Map<String, HDBOfficer> importHDBOfficerData(String hdbOfficerFilePath);
	
	/**
	 * Exports HDB Officer data to the specified file paths.
	 *
	 * @param hdbOfficerFilePath the file path of the HDB Officers file
	 * @param hdbOfficerMap       a {@link Map} of {@link HDBOfficer}
	 *                                objects with their IDs as keys
	 * @return true if the data was exported successfully, false otherwise
	 */
	boolean exportHDBOfficerData(String hdbOfficerFilePath, Map<String, HDBOfficer> hdbOfficerMap);
}