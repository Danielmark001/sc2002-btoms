package services;

import enumeration.FlatType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import interfaces.IFileDataService;
import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import enumeration.MaritalStatus;
import models.BTOProject;
import models.FlatTypeDetails;
import utils.FilePathsUtils;
import stores.DataStore;
import view.CommonView;

/**
 * The {@link CsvDataService} class implements the {@link IFileDataService}
 * interface and provides
 * methods for reading and writing data from/to CSV files.
 */
public class CsvDataService implements IFileDataService {

	/**
	 * The list of headers for the CSV file that stores applicant data.
	 */
	private static List<String> applicantCsvHeaders = new ArrayList<String>();

	/**
	 * The list of headers for the CSV file that stores HDB manager data.
	 */
	private static List<String> hdbManagerCsvHeaders = new ArrayList<String>();

	/**
	 * The list of headers for the CSV file that stores HDB officer data.
	 */
	private static List<String> hdbOfficerCsvHeaders = new ArrayList<String>();

	/**
	 * The list of headers for the CSV file that stores BTO project data.
	 */
	private static List<String> btoProjectCsvHeaders = new ArrayList<String>();

	/**
	 * Constructs an instance of the {@link CsvDataService} class.
	 */
    public CsvDataService() {
    };

	// ---------- Helper Function ---------- //
	/**
	 * Reads data from the CSV file located at the given file path and returns it as
	 * a list of string arrays.
	 *
	 * @param filePath the file path of the CSV file to read
	 * @param headers  the list of headers for the CSV file
	 * @return a list of string arrays containing the CSV data
	 */
	public List<String[]> readCsvFile(String filePath, List<String> headers) {
		List<String[]> dataList = new ArrayList<String[]>();
		headers.clear();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			// Headers
			String[] headerRow = br.readLine().split(",");
			for (String header : headerRow) {
				headers.add(header);
			}

			// Content
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				dataList.add(values);
			}

		} catch (IOException e) {
			System.out.println("Cannot import data!" + filePath);
		}

		return dataList;
	}

	/**
	 * Writes the given data to a CSV file located at the given file path.
	 *
	 * @param filePath the file path of the CSV file to write
	 * @param headers  the list of headers for the CSV file
	 * @param lines    the list of lines to write to the CSV file
	 * @return true if the data is written successfully, false otherwise
	 */
	public boolean writeCsvFile(String filePath, List<String> headers, List<String> lines) {
		try (FileWriter writer = new FileWriter(filePath)) {
			// Write Headers
			String headerLine = String.join(",", headers);
			writer.write(headerLine + "\n");

			// Write Content
			for (String line : lines) {
				writer.write(line + "\n");
			}
		} catch (IOException e) {
			System.out.println("Cannot export data!");
			return false;
		}
		return true;
	}

	/**
	 * Parses a string array containing user data and returns a map of user
	 * information.
	 *
	 * @param userRow the string array containing the user data
	 * @return a map of user information, where the keys are "userID", "password",
	 *         "email", "role", and "name" and the values are the corresponding
	 *         values in the userRow array
	 */
	private Map<String, String> parseUserRow(String[] userRow) {
		String name = userRow[0];
		String nric = userRow[1];
		String age = userRow[2];
		MaritalStatus maritalStatus = parseMaritalStatus(userRow[3]);
		String password = userRow[4];

		// Return
		Map<String, String> userInfoMap = new HashMap<String, String>();
		userInfoMap.put("name", name);
		userInfoMap.put("nric", nric);
		userInfoMap.put("age", age);
		userInfoMap.put("maritalStatus", maritalStatus.toString());
		userInfoMap.put("password", password);

		return userInfoMap;
	}

	private MaritalStatus parseMaritalStatus(String status) {
		// Convert to uppercase to match enum values
		String upperStatus = status.toUpperCase();
		try {
			return MaritalStatus.valueOf(upperStatus);
		} catch (IllegalArgumentException e) {
			// If exact match fails, try to match by display name
			for (MaritalStatus ms : MaritalStatus.values()) {
				if (ms.getDisplayName().equalsIgnoreCase(status)) {
					return ms;
				}
			}
			throw new IllegalArgumentException("Invalid marital status: " + status);
		}
	}

	private FlatType parseFlatType(String displayName) {
		for (FlatType type : FlatType.values()) {
			if (type.getDisplayName().equals(displayName)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid flat type: " + displayName);
	}

	// ---------- Interface method implementation ---------- //
	// Applicant
	@Override
	public Map<String, Applicant> importApplicantData(String applicantsFilePath) {
		Map<String, Applicant> applicantsMap = new HashMap<String, Applicant>();

		List<String[]> applicantsRows = this.readCsvFile(applicantsFilePath, applicantCsvHeaders);

		for (String[] applicantRow : applicantsRows) {
			Map<String, String> applicantInfoMap = parseUserRow(applicantRow);

			String name = applicantInfoMap.get("name");
			String nric = applicantInfoMap.get("nric");
			int age = Integer.parseInt(applicantInfoMap.get("age"));
			MaritalStatus maritalStatus = parseMaritalStatus(applicantInfoMap.get("maritalStatus"));
			String password = applicantInfoMap.get("password");

			Applicant applicant = new Applicant(name, nric, age, maritalStatus, password);

			applicantsMap.put(nric, applicant);
		}

		return applicantsMap;
	}

	@Override
	public boolean exportApplicantData(String applicantsFilePath, Map<String, Applicant> applicantMap) {
		List<String> applicantLines = new ArrayList<String>();


		List<String[]> applicantsRows = this.readCsvFile(applicantsFilePath, applicantCsvHeaders);
		for (String[] applicantRow : applicantsRows) {
			Map<String, String> applicantInfoMap = parseUserRow(applicantRow);
			String applicantLine = String.format("%s,%s,%s,%s,%s",
					applicantInfoMap.get("name"),
					applicantInfoMap.get("nric"),
					applicantInfoMap.get("age"),
					applicantInfoMap.get("maritalStatus"),
					applicantInfoMap.get("password"));

			applicantLines.add(applicantLine);
		}


		// Write to CSV
		boolean success = this.writeCsvFile(applicantsFilePath, applicantCsvHeaders, applicantLines);
		return success;
	}

	@Override
	public Map<String, HDBManager> importHDBManagerData(String hdbManagersFilePath) {
		Map<String, HDBManager> hdbManagersMap = new HashMap<String, HDBManager>();

		List<String[]> hdbManagersRows = this.readCsvFile(hdbManagersFilePath, hdbManagerCsvHeaders);

		for (String[] hdbManagerRow : hdbManagersRows) {
			Map<String, String> hdbManagerInfoMap = parseUserRow(hdbManagerRow);

			String name = hdbManagerInfoMap.get("name");
			String nric = hdbManagerInfoMap.get("nric");
			int age = Integer.parseInt(hdbManagerInfoMap.get("age"));
			MaritalStatus maritalStatus = parseMaritalStatus(hdbManagerInfoMap.get("maritalStatus"));
			String password = hdbManagerInfoMap.get("password");

			HDBManager hdbManager = new HDBManager(name, nric, age, maritalStatus, password);

			hdbManagersMap.put(nric, hdbManager);
		}

		return hdbManagersMap;
	}

	@Override
	public boolean exportHDBManagerData(String hdbManagersFilePath, Map<String, HDBManager> hdbManagerMap) {
		List<String> hdbManagerLines = new ArrayList<String>();

		List<String[]> hdbManagersRows = this.readCsvFile(hdbManagersFilePath, hdbManagerCsvHeaders);
		for (String[] hdbManagerRow : hdbManagersRows) {
			Map<String, String> hdbManagerInfoMap = parseUserRow(hdbManagerRow);
			String hdbManagerLine = String.format("%s,%s,%s,%s,%s",
					hdbManagerInfoMap.get("name"),
					hdbManagerInfoMap.get("nric"),
					hdbManagerInfoMap.get("age"),
					hdbManagerInfoMap.get("maritalStatus"),
					hdbManagerInfoMap.get("password"));

			hdbManagerLines.add(hdbManagerLine);
		}

		// Write to CSV
		boolean success = this.writeCsvFile(hdbManagersFilePath, hdbManagerCsvHeaders, hdbManagerLines);
		return success;
	}

	@Override
	public Map<String, HDBOfficer> importHDBOfficerData(String hdbOfficersFilePath) {
		Map<String, HDBOfficer> hdbOfficersMap = new HashMap<String, HDBOfficer>();

		List<String[]> hdbOfficersRows = this.readCsvFile(hdbOfficersFilePath, hdbOfficerCsvHeaders);

		for (String[] hdbOfficerRow : hdbOfficersRows) {
			Map<String, String> hdbOfficerInfoMap = parseUserRow(hdbOfficerRow);

			String name = hdbOfficerInfoMap.get("name");
			String nric = hdbOfficerInfoMap.get("nric");
			int age = Integer.parseInt(hdbOfficerInfoMap.get("age"));
			MaritalStatus maritalStatus = parseMaritalStatus(hdbOfficerInfoMap.get("maritalStatus"));
			String password = hdbOfficerInfoMap.get("password");

			HDBOfficer hdbOfficer = new HDBOfficer(name, nric, age, maritalStatus, password);

			hdbOfficersMap.put(nric, hdbOfficer);
		}

		return hdbOfficersMap;
	}

	@Override
	public boolean exportHDBOfficerData(String hdbOfficersFilePath, Map<String, HDBOfficer> hdbOfficerMap) {
		List<String> hdbOfficerLines = new ArrayList<String>();

		List<String[]> hdbOfficersRows = this.readCsvFile(hdbOfficersFilePath, hdbOfficerCsvHeaders);
		for (String[] hdbOfficerRow : hdbOfficersRows) {
			Map<String, String> hdbOfficerInfoMap = parseUserRow(hdbOfficerRow);
			String hdbOfficerLine = String.format("%s,%s,%s,%s,%s",
					hdbOfficerInfoMap.get("name"),
					hdbOfficerInfoMap.get("nric"),
					hdbOfficerInfoMap.get("age"),
					hdbOfficerInfoMap.get("maritalStatus"),
					hdbOfficerInfoMap.get("password"));

			hdbOfficerLines.add(hdbOfficerLine);
		}


		// Write to CSV
		boolean success = this.writeCsvFile(hdbOfficersFilePath, hdbOfficerCsvHeaders, hdbOfficerLines);
		return success;
	}

	private BTOProject parseBTOProjectRow(String[] btoProjectRow) {
		// ProjectName,Neighborhood,Type1,NumberOfUnitsType1,SellingPriceType1,Type2,NumberOfUnitsType2,SellingPriceType2,ApplicationOpeningDate,ApplicationClosingDate,Manager,OfficerSlot,Officers
		String projectName = btoProjectRow[0];
		String neighborhood = btoProjectRow[1];

		Map<FlatType, FlatTypeDetails> flatTypes = new HashMap<FlatType, FlatTypeDetails>();

		for (int i = 2; i < 8; i += 3) {
			FlatType flatType = parseFlatType(btoProjectRow[i]);
			int numberOfUnits = Integer.parseInt(btoProjectRow[i + 1]);
			double sellingPrice = Double.parseDouble(btoProjectRow[i + 2]);

			FlatTypeDetails flatTypeDetails = new FlatTypeDetails(numberOfUnits, sellingPrice);
			flatTypes.put(flatType, flatTypeDetails);
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate applicationOpeningDate = LocalDate.parse(btoProjectRow[8], formatter);
		LocalDate applicationClosingDate = LocalDate.parse(btoProjectRow[9], formatter);

		String managerName = btoProjectRow[10];
		HDBManager manager = null;
		// Find manager by name
		for (HDBManager m : DataStore.getHDBManagersData().values()) {
			if (m.getName().equals(managerName)) {
				manager = m;
				break;
			}
		}
		
		int officerSlots = Integer.parseInt(btoProjectRow[11]);
		
		// Get all officers from remaining columns
		List<HDBOfficer> hdbOfficers = new ArrayList<HDBOfficer>();
		for (int i = 12; i < btoProjectRow.length; i++) {
			String officerName = btoProjectRow[i];
			// Find officer by name
			for (HDBOfficer o : DataStore.getHDBOfficersData().values()) {
				if (o.getName().equals(officerName)) {
					hdbOfficers.add(o);
					break;
				}
			}
		}

		return new BTOProject(projectName, neighborhood, applicationOpeningDate, applicationClosingDate, 
							flatTypes, manager, officerSlots, hdbOfficers, false);
	}

	@Override
	public Map<String, BTOProject> importBTOProjectData(String btoProjectFilePath) {
		Map<String, BTOProject> btoProjectsMap = new HashMap<String, BTOProject>();

		List<String[]> btoProjectsRows = this.readCsvFile(btoProjectFilePath, btoProjectCsvHeaders);

		for (String[] btoProjectRow : btoProjectsRows) {
			BTOProject btoProject = parseBTOProjectRow(btoProjectRow);
			btoProjectsMap.put(btoProject.getProjectName(), btoProject);
		}

		return btoProjectsMap;
	}

	@Override
	public boolean exportBTOProjectData(String btoProjectFilePath, Map<String, BTOProject> btoProjectMap) {
		List<String> btoProjectLines = new ArrayList<String>();
		
		for (BTOProject project : btoProjectMap.values()) {
			StringBuilder line = new StringBuilder();
			line.append(project.getProjectName()).append(",");
			line.append(project.getNeighborhood()).append(",");
			
			Map<FlatType, FlatTypeDetails> flatTypes = project.getFlatTypes();
			for (Map.Entry<FlatType, FlatTypeDetails> entry : flatTypes.entrySet()) {
				line.append(entry.getKey().getDisplayName()).append(",");
				line.append(entry.getValue().getUnits()).append(",");
				line.append(entry.getValue().getPrice()).append(",");
			}
			
			line.append(project.getApplicationOpeningDate()).append(",");
			line.append(project.getApplicationClosingDate()).append(",");
			line.append(project.getHDBManager().getName()).append(",");
			line.append(project.getHDBOfficerSlots()).append(",");
			
			List<HDBOfficer> officers = project.getHDBOfficers();
			String[] officerNames = officers.stream()
				.map(HDBOfficer::getName)
				.toArray(String[]::new);
			line.append(String.join(",", officerNames));
			
			btoProjectLines.add(line.toString());
		}

		return this.writeCsvFile(btoProjectFilePath, btoProjectCsvHeaders, btoProjectLines);
	}
	
	
}
