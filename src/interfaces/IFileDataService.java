package interfaces;

import java.util.Map;

import models.Applicant;
import models.BTOProject;
import models.HDBManager;
import models.HDBOfficer;
import models.BTOApplication;
import models.HDBOfficerRegistration;
import models.Enquiry;
import models.WithdrawalRequest;

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

	// ---------- BTOProject ---------- //
	/**
	 * Imports BTO Project data from the specified file paths.
	 *
	 * @param btoProjectFilePath the file path of the BTO Projects file
	 * @param btoProjectMap       a {@link Map} of {@link BTOProject} objects with
	 *                            their IDs as keys
	 * 
	 * @return a {@link Map} of {@link BTOProject} objects with their IDs as keys
	 */
	Map<String, BTOProject> importBTOProjectData(String btoProjectFilePath);
	
	/**
	 * Exports BTO Project data to the specified file paths.
	 *
	 * @param btoProjectFilePath the file path of the BTO Projects file
	 * @param btoProjectMap       a {@link Map} of {@link BTOProject} objects with
	 *                            their IDs as keys
	 * @return true if the data was exported successfully, false otherwise
	 */
	boolean exportBTOProjectData(String btoProjectFilePath, Map<String, BTOProject> btoProjectMap);

	// ---------- BTOApplication ---------- //
	/**
	 * Imports BTO Application data from the specified file paths.
	 *
	 * @param btoApplicationFilePath the file path of the BTO Applications file
	 * @param btoApplicationMap       a {@link Map} of {@link BTOApplication} objects with
	 *                                their IDs as keys
	 * @return a {@link Map} of {@link BTOApplication} objects with their IDs as keys
	 */
	Map<String, BTOApplication> importBTOApplicationData(String btoApplicationFilePath);

	/**
	 * Exports BTO Application data to the specified file paths.
	 *
	 * @param btoApplicationFilePath the file path of the BTO Applications file
	 * @param btoApplicationMap       a {@link Map} of {@link BTOApplication} objects with
	 *                                their IDs as keys
	 * @return true if the data was exported successfully, false otherwise
	 */
	boolean exportBTOApplicationData(String btoApplicationFilePath, Map<String, BTOApplication> btoApplicationMap);

	/**
	 * Imports HDB officer registration data from a CSV file.
	 * @param hdbOfficerRegistrationsFilePath The file path of the CSV file
	 * @return A map of HDB officer registration data
	 */
	Map<String, HDBOfficerRegistration> importHDBOfficerRegistrationData(String hdbOfficerRegistrationsFilePath);

	/**
	 * Exports HDB officer registration data to a CSV file.
	 * @param hdbOfficerRegistrationsFilePath The file path of the CSV file
	 * @param hdbOfficerRegistrationMap The map of HDB officer registration data
	 * @return true if the export was successful, false otherwise
	 */
	boolean exportHDBOfficerRegistrationData(String hdbOfficerRegistrationsFilePath, Map<String, HDBOfficerRegistration> hdbOfficerRegistrationMap);

	// ---------- Enquiry ---------- //
	/**
	 * Imports enquiry data from a CSV file.
	 * @param enquiryFilePath The file path of the CSV file
	 * @return A map of enquiry data
	 */
	Map<String, Enquiry> importEnquiryData(String enquiryFilePath);

	/**
	 * Exports enquiry data to a CSV file.
	 * @param enquiryFilePath The file path of the CSV file
	 * @param enquiryMap The map of enquiry data
	 * @return true if the export was successful, false otherwise
	 */
	boolean exportEnquiryData(String enquiryFilePath, Map<String, Enquiry> enquiryMap);

	// ---------- Withdrawal Request ---------- //
	/**
	 * Imports withdrawal request data from a CSV file.
	 * @param withdrawalRequestFilePath The file path of the CSV file
	 * @return A map of withdrawal request data
	 */
	Map<String, WithdrawalRequest> importWithdrawalRequestData(String withdrawalRequestFilePath);

	/**
	 * Exports withdrawal request data to a CSV file.
	 * @param withdrawalRequestFilePath The file path of the CSV file
	 * @param withdrawalRequestMap The map of withdrawal request data
	 * @return true if the export was successful, false otherwise
	 */
	boolean exportWithdrawalRequestData(String withdrawalRequestFilePath, Map<String, WithdrawalRequest> withdrawalRequestMap);
}