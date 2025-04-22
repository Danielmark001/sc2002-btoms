package stores;

import java.util.HashMap;
import java.util.Map;

import interfaces.IFileDataService;
import models.Applicant;
import models.BTOProject;
import models.HDBManager;
import models.HDBOfficer;
import models.BTOApplication;
import models.HDBOfficerRegistration;
import models.Enquiry;
import models.WithdrawalRequest;

/**
 * The {@link DataStore} class provides utility methods for managing data
 * storage within the application. It offers methods to initialize the data
 * store, import and export data to and from the file system, and interact with
 * data maps for various data types.
 */
public class DataStore {
	/**
	 * The {@link IFileDataService} instance used for data operations.
	 */
	private static IFileDataService fileDataService;

	/**
	 * A {@link Map} containing file paths for various data types.
	 */
	private static Map<String, String> filePathsMap;

	/**
	 * A {@link Map} containing applicant ID as the key and {@link Applicant}
	 * objects as the value.
	 */
	private static Map<String, Applicant> applicantsData = new HashMap<String, Applicant>();

	/**
	 * A {@link Map} containing HDB manager ID as the key and {@link HDBManager}
	 * objects as the value.
	 */
	private static Map<String, HDBManager> hdbManagersData = new HashMap<String, HDBManager>();

	/**
	 * A {@link Map} containing HDB officer ID as the key and
	 * {@link HDBOfficer} objects as the value.
	 */
	private static Map<String, HDBOfficer> hdbOfficersData = new HashMap<String, HDBOfficer>();

	/**
	 * A {@link Map} containing BTO project ID as the key and {@link BTOProject}
	 * objects as the value.
	 */
	private static Map<String, BTOProject> btoProjectsData = new HashMap<String, BTOProject>();

	/**
	 * A {@link Map} containing BTO application ID as the key and {@link BTOApplication}
	 * objects as the value.
	 */
	private static Map<String, BTOApplication> btoApplicationsData = new HashMap<String, BTOApplication>();

	/**
	 * A {@link Map} containing HDB officer registration ID as the key and {@link HDBOfficerRegistration}
	 * objects as the value.
	 */
	private static Map<String, HDBOfficerRegistration> hdbOfficerRegistrationsData = new HashMap<String, HDBOfficerRegistration>();

	/**
	 * A {@link Map} containing enquiry ID as the key and {@link Enquiry}
	 * objects as the value.
	 */
	private static Map<String, Enquiry> enquiriesData = new HashMap<String, Enquiry>();

	/**
	 * A {@link Map} containing withdrawal request ID as the key and {@link WithdrawalRequest}
	 * objects as the value.
	 */
	private static Map<String, WithdrawalRequest> withdrawalRequestsData = new HashMap<>();

	/**	 * Private constructor to prevent instantiation of the class.
	 */
	private DataStore() {
	}

	/**
	 * Initializes the DataStore by setting up the file data service, file paths
	 * map, and importing data from the file system.
	 *
	 * @param fileDataService the {@link IFileDataService} instance to use for data
	 *                        operations
	 * @param filePathsMap    the {@link Map} containing file paths for various data
	 *                        types
	 * @return {@code true} if the initialization is successful, {@code false}
	 *         otherwise
	 */
	public static boolean initDataStore(IFileDataService fileDataService, Map<String, String> filePathsMap) {
		// Initialize fileDataService and filePathsMap
		DataStore.filePathsMap = filePathsMap;
		DataStore.fileDataService = fileDataService;

		// Import data
		DataStore.applicantsData = fileDataService.importApplicantData(filePathsMap.get("applicant"));
		DataStore.hdbManagersData = fileDataService.importHDBManagerData(filePathsMap.get("hdbManager"));
		DataStore.hdbOfficersData = fileDataService.importHDBOfficerData(filePathsMap.get("hdbOfficer"));
		DataStore.btoProjectsData = fileDataService.importBTOProjectData(filePathsMap.get("btoProject"));
		DataStore.btoApplicationsData = fileDataService.importBTOApplicationData(filePathsMap.get("btoApplication"));
		DataStore.hdbOfficerRegistrationsData = fileDataService.importHDBOfficerRegistrationData(filePathsMap.get("hdbOfficerRegistrations"));
		DataStore.enquiriesData = fileDataService.importEnquiryData(filePathsMap.get("enquiry"));
		DataStore.withdrawalRequestsData = fileDataService.importWithdrawalRequestData(filePathsMap.get("withdrawalRequest"));

		// Set up handled projects for HDB officers
		for (BTOProject project : btoProjectsData.values()) {
			for (HDBOfficer officer : project.getHDBOfficers()) {
				officer.addHandledProject(project);
			}
		}

		return true;
	}

	/**
	 * Saves the data from the DataStore to the file system.
	 *
	 * @return {@code true} if the data is saved successfully, {@code false}
	 *         otherwise
	 */
	public static boolean saveData() {
		DataStore.setApplicantsData(applicantsData);
		DataStore.setHDBManagersData(hdbManagersData);
		DataStore.setHDBOfficersData(hdbOfficersData);
		DataStore.setBTOProjectsData(btoProjectsData);
		DataStore.setBTOApplicationsData(btoApplicationsData);
		DataStore.setHDBOfficerRegistrationsData(hdbOfficerRegistrationsData);
		DataStore.setEnquiriesData(enquiriesData);
		DataStore.setWithdrawalRequestsData(withdrawalRequestsData);

		return true;
	}

	// ---------- Student ---------- //
	/**
	 * Gets the students data map.
	 *
	 * @return a {@link Map} containing student ID as the key and {@link Student}
	 *         objects as the value
	 */
	public static Map<String, Applicant> getApplicantsData() {
		return DataStore.applicantsData;
	}

	/**
	 * Sets the students data map and saves the data to the file system.
	 *
	 * @param studentsData a {@link Map} containing student ID as the key and
	 *                     {@link Student} objects as the value
	 */
	public static void setApplicantsData(Map<String, Applicant> applicantsData) {
		DataStore.applicantsData = applicantsData;
		fileDataService.exportApplicantData(filePathsMap.get("applicant"), applicantsData);
	}

	// ---------- Supervisor ---------- //
	/**
	 * Gets the supervisors data map.
	 *
	 * @return a {@link Map} containing supervisor ID as the key and
	 *         {@link Supervisor} objects as the value
	 */
	public static Map<String, HDBManager> getHDBManagersData() {
		return DataStore.hdbManagersData;
	}

	/**
	 * Sets the supervisors data map and saves the data to the file system.
	 *
	 * @param supervisorsData a {@link Map} containing supervisor ID as the key and
	 *                        {@link Supervisor} objects as the value
	 */
	public static void setHDBManagersData(Map<String, HDBManager> hdbManagersData) {
		DataStore.hdbManagersData = hdbManagersData;
		fileDataService.exportHDBManagerData(filePathsMap.get("hdbManager"), hdbManagersData);
	}

	// ---------- FYP Coordinator ---------- //
	/**
	 * Gets the FYP coordinators data map.
	 *
	 * @return a {@link Map} containing FYP coordinator ID as the key and
	 *         {@link FYPCoordinator} objects as the value
	 */
	public static Map<String, HDBOfficer> getHDBOfficersData() {
		return DataStore.hdbOfficersData;
	}

	/**
	 * Sets the FYP coordinators data map and saves the data to the file system.
	 *
	 * @param fypcoordinatorsData a {@link Map} containing FYP coordinator ID as the
	 *                            key and {@link FYPCoordinator} objects as the
	 *                            value
	 */
	public static void setHDBOfficersData(Map<String, HDBOfficer> hdbOfficersData) {
		DataStore.hdbOfficersData = hdbOfficersData;
		fileDataService.exportHDBOfficerData(filePathsMap.get("hdbOfficer"), hdbOfficersData);
	}

	// ---------- BTO Project ---------- //
	/**
	 * Gets the BTO projects data map.
	 *
	 * @return a {@link Map} containing BTO project ID as the key and
	 *         {@link BTOProject} objects as the value
	 */
	public static Map<String, BTOProject> getBTOProjectsData() {
		return DataStore.btoProjectsData;
	}

	/**
	 * Sets the BTO projects data map and saves the data to the file system.
	 *
	 * @param btoProjectsData a {@link Map} containing BTO project ID as the key and
	 *                        {@link BTOProject} objects as the value
	 */
	public static void setBTOProjectsData(Map<String, BTOProject> btoProjectsData) {
		DataStore.btoProjectsData = btoProjectsData;
		fileDataService.exportBTOProjectData(filePathsMap.get("btoProject"), btoProjectsData);
	}
	
	// ---------- BTO Application ---------- //
	/**
	 * Gets the BTO applications data map.
	 *
	 * @return a {@link Map} containing BTO application ID as the key and
	 *         {@link BTOApplication} objects as the value
	 */
	public static Map<String, BTOApplication> getBTOApplicationsData() {
		return DataStore.btoApplicationsData;
	}

	/**
	 * Sets the BTO applications data map and saves the data to the file system.
	 *
	 * @param btoApplicationsData a {@link Map} containing BTO application ID as the key and
	 *                           {@link BTOApplication} objects as the value
	 */
	public static void setBTOApplicationsData(Map<String, BTOApplication> btoApplicationsData) {
		DataStore.btoApplicationsData = btoApplicationsData;
		fileDataService.exportBTOApplicationData(filePathsMap.get("btoApplication"), btoApplicationsData);
	}

	/**
	 * Gets the map of HDB officer registrations data
	 * 
	 * @return a {@link Map} containing registration ID as the key and
	 *         {@link HDBOfficerRegistration} objects as the value
	 */
	public static Map<String, HDBOfficerRegistration> getHDBOfficerRegistrationsData() {
		return hdbOfficerRegistrationsData;
	}

	/**
	 * Sets the map of HDB officer registrations data and saves to the file system
	 * 
	 * @param hdbOfficerRegistrationsData a {@link Map} containing registration ID as the key and
	 *                                  {@link HDBOfficerRegistration} objects as the value
	 */
	public static void setHDBOfficerRegistrationsData(Map<String, HDBOfficerRegistration> hdbOfficerRegistrationsData) {
		DataStore.hdbOfficerRegistrationsData = hdbOfficerRegistrationsData;
		fileDataService.exportHDBOfficerRegistrationData(filePathsMap.get("hdbOfficerRegistrations"), hdbOfficerRegistrationsData);
	}

	/**
	 * Gets the map of enquiries data
	 * 
	 * @return a {@link Map} containing enquiry ID as the key and
	 *         {@link Enquiry} objects as the value
	 */
	public static Map<String, Enquiry> getEnquiriesData() {
		return enquiriesData;
	}

	/**
	 * Sets the map of enquiries data and saves to the file system
	 * 
	 * @param enquiriesData a {@link Map} containing enquiry ID as the key and
	 *                    {@link Enquiry} objects as the value
	 */
	public static void setEnquiriesData(Map<String, Enquiry> enquiriesData) {
		DataStore.enquiriesData = enquiriesData;
		fileDataService.exportEnquiryData(filePathsMap.get("enquiry"), enquiriesData);
	}

	/**
	 * Gets the map of withdrawal requests data
	 * 
	 * @return a {@link Map} containing withdrawal request ID as the key and
	 *         {@link WithdrawalRequest} objects as the value
	 */
	public static Map<String, WithdrawalRequest> getWithdrawalRequestsData() {
		return withdrawalRequestsData;
	}

	/**
	 * Sets the map of withdrawal requests data and saves to the file system
	 * 
	 * @param withdrawalRequestsData a {@link Map} containing withdrawal request ID as the key and
	 *                             {@link WithdrawalRequest} objects as the value
	 */
	public static void setWithdrawalRequestsData(Map<String, WithdrawalRequest> withdrawalRequestsData) {
		DataStore.withdrawalRequestsData = withdrawalRequestsData;
		fileDataService.exportWithdrawalRequestData(filePathsMap.get("withdrawalRequest"), withdrawalRequestsData);
	}
}