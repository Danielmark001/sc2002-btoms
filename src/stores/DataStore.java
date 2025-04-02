package stores;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import models.Application;
import models.Enquiry;
import models.Project;
import models.Registration;
import models.User;
import models.enumeration.ApplicationStatus;
import models.enumeration.FlatType;
import models.enumeration.MaritalStatus;
import models.enumeration.RegistrationStatus;
import models.enumeration.UserType;
import util.DataLoader;

/**
 * Central data storage class responsible for all data persistence operations
 * Follows the Singleton pattern
 */
public class DataStore {
    private static DataStore instance;
    
    // Data collections
    private Map<String, User> users;                 // NRIC -> User
    private Map<String, Project> projects;           // ProjectID -> Project
    private Map<String, Application> applications;   // ApplicationID -> Application
    private Map<String, Enquiry> enquiries;          // EnquiryID -> Enquiry
    private Map<String, Registration> registrations; // RegistrationID -> Registration
    
    // File paths
    private final String DATA_DIR = "data";
    private final String USERS_FILE = DATA_DIR + "/users.txt";
    private final String PROJECTS_FILE = DATA_DIR + "/projects.txt";
    private final String APPLICATIONS_FILE = DATA_DIR + "/applications.txt";
    private final String ENQUIRIES_FILE = DATA_DIR + "/enquiries.txt";
    private final String REGISTRATIONS_FILE = DATA_DIR + "/registrations.txt";
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Private constructor for Singleton pattern
     */
    private DataStore() {
        users = new HashMap<>();
        projects = new HashMap<>();
        applications = new HashMap<>();
        enquiries = new HashMap<>();
        registrations = new HashMap<>();
        
        // Create data directory if it doesn't exist
        new File(DATA_DIR).mkdirs();
        
        // Initialize data
        loadAllData();
    }
    
    /**
     * Gets the singleton instance of DataStore
     * @return DataStore instance
     */
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }
    
    /**
     * Loads all data from files
     */
    private void loadAllData() {
        loadUsers();
        loadProjects();
        loadApplications();
        loadEnquiries();
        loadRegistrations();
    }
    
    /**
     * Saves all data to files
     */
    public void saveAllData() {
        saveUsers();
        saveProjects();
        saveApplications();
        saveEnquiries();
        saveRegistrations();
    }
    
    // ===== USER OPERATIONS =====
    
    /**
     * Loads users from file
     */
    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String nric = parts[0].trim();
                    String password = parts[1].trim();
                    int age = Integer.parseInt(parts[2].trim());
                    MaritalStatus maritalStatus = MaritalStatus.valueOf(parts[3].trim());
                    UserType userType = UserType.valueOf(parts[4].trim());
                    String appliedProjectId = parts.length > 5 ? parts[5].trim() : null;
                    String bookedProjectId = parts.length > 6 ? parts[6].trim() : null;
                    FlatType bookedFlatType = parts.length > 7 && !parts[7].trim().isEmpty() ? 
                            FlatType.valueOf(parts[7].trim()) : null;
                    
                    User user = new User(nric, password, age, maritalStatus, userType);
                    user.setAppliedProjectId(appliedProjectId);
                    user.setBookedProjectId(bookedProjectId);
                    user.setBookedFlatType(bookedFlatType);
                    
                    users.put(nric, user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            // Initialize with default data if file doesn't exist
            if (users.isEmpty()) {
                initializeDefaultUsers();
            }
        }
    }
    
    /**
     * Initializes default users if no data exists
     */
    private void initializeDefaultUsers() {
        // Default HDB Manager
        User manager = new User("S1234567A", "password", 40, MaritalStatus.MARRIED, UserType.HDB_MANAGER);
        users.put(manager.getNric(), manager);
        
        // Default HDB Officer
        User officer = new User("S2345678B", "password", 35, MaritalStatus.MARRIED, UserType.HDB_OFFICER);
        users.put(officer.getNric(), officer);
        
        // Default Married Applicant
        User marriedApp = new User("S3456789C", "password", 30, MaritalStatus.MARRIED, UserType.APPLICANT);
        users.put(marriedApp.getNric(), marriedApp);
        
        // Default Single Applicant (35+ years old)
        User singleApp = new User("S4567890D", "password", 36, MaritalStatus.SINGLE, UserType.APPLICANT);
        users.put(singleApp.getNric(), singleApp);
        
        saveUsers();
    }
    
    /**
     * Saves users to file
     */
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.write(String.format("%s,%s,%d,%s,%s,%s,%s,%s",
                        user.getNric(),
                        user.getPassword(),
                        user.getAge(),
                        user.getMaritalStatus(),
                        user.getUserType(),
                        user.getAppliedProjectId() != null ? user.getAppliedProjectId() : "",
                        user.getBookedProjectId() != null ? user.getBookedProjectId() : "",
                        user.getBookedFlatType() != null ? user.getBookedFlatType() : ""));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    /**
     * Gets a user by NRIC
     * @param nric User's NRIC
     * @return User object if found, null otherwise
     */
    public User getUserByNRIC(String nric) {
        return users.get(nric);
    }
    
    /**
     * Gets all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    /**
     * Gets all users of a specific type
     * @param userType User type to filter by
     * @return List of filtered users
     */
    public List<User> getUsersByType(UserType userType) {
        return users.values().stream()
                .filter(u -> u.getUserType() == userType)
                .collect(Collectors.toList());
    }
    
    /**
     * Adds a new user
     * @param user User to add
     * @return true if added successfully, false if user already exists
     */
    public boolean addUser(User user) {
        if (users.containsKey(user.getNric())) {
            return false;
        }
        users.put(user.getNric(), user);
        saveUsers();
        return true;
    }
    
    /**
     * Updates an existing user
     * @param user User to update
     * @return true if updated successfully, false if user doesn't exist
     */
    public boolean updateUser(User user) {
        if (!users.containsKey(user.getNric())) {
            return false;
        }
        users.put(user.getNric(), user);
        saveUsers();
        return true;
    }
    
    /**
     * Loads users from a CSV file
     * @param filepath Path to the CSV file
     * @return Number of users loaded
     */
    public int loadUsersFromCsv(String filepath) {
        List<User> loadedUsers = DataLoader.loadUsersFromCsv(filepath);
        int count = 0;
        
        for (User user : loadedUsers) {
            if (addUser(user)) {
                count++;
            }
        }
        
        return count;
    }
    
    // ===== PROJECT OPERATIONS =====
    
    /**
     * Loads projects from file
     */
    private void loadProjects() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 12) {
                    String projectId = parts[0].trim();
                    String projectName = parts[1].trim();
                    String neighborhood = parts[2].trim();
                    int twoRoomUnits = Integer.parseInt(parts[3].trim());
                    int threeRoomUnits = Integer.parseInt(parts[4].trim());
                    int twoRoomAvailable = Integer.parseInt(parts[5].trim());
                    int threeRoomAvailable = Integer.parseInt(parts[6].trim());
                    LocalDate openingDate = LocalDate.parse(parts[7].trim(), dateFormatter);
                    LocalDate closingDate = LocalDate.parse(parts[8].trim(), dateFormatter);
                    String managerInCharge = parts[9].trim();
                    int officerSlots = Integer.parseInt(parts[10].trim());
                    boolean visible = Boolean.parseBoolean(parts[11].trim());
                    
                    // Parse officer IDs
                    List<String> officerIds = new ArrayList<>();
                    if (parts.length > 12 && !parts[12].trim().isEmpty()) {
                        officerIds = Arrays.asList(parts[12].trim().split(";"));
                    }
                    
                    Project project = new Project(projectId, projectName, neighborhood, managerInCharge, 
                            openingDate, closingDate, officerSlots);
                    
                    // Set units and availability
                    project.setTotalUnits(FlatType.TWO_ROOM, twoRoomUnits);
                    project.setTotalUnits(FlatType.THREE_ROOM, threeRoomUnits);
                    project.setAvailableUnits(FlatType.TWO_ROOM, twoRoomAvailable);
                    project.setAvailableUnits(FlatType.THREE_ROOM, threeRoomAvailable);
                    project.setVisible(visible);
                    
                    // Add officers
                    for (String officerId : officerIds) {
                        project.addOfficer(officerId);
                    }
                    
                    projects.put(projectId, project);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading projects: " + e.getMessage());
        }
    }
    
    /**
     * Saves projects to file
     */
    private void saveProjects() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROJECTS_FILE))) {
            for (Project project : projects.values()) {
                // Join officer IDs with semicolon
                String officerIdsStr = String.join(";", project.getOfficerIds());
                
                writer.write(String.format("%s,%s,%s,%d,%d,%d,%d,%s,%s,%s,%d,%b,%s",
                        project.getProjectId(),
                        project.getProjectName(),
                        project.getNeighborhood(),
                        project.getTotalUnits(FlatType.TWO_ROOM),
                        project.getTotalUnits(FlatType.THREE_ROOM),
                        project.getAvailableUnits(FlatType.TWO_ROOM),
                        project.getAvailableUnits(FlatType.THREE_ROOM),
                        project.getOpeningDate().format(dateFormatter),
                        project.getClosingDate().format(dateFormatter),
                        project.getManagerInCharge(),
                        project.getOfficerSlots(),
                        project.isVisible(),
                        officerIdsStr));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving projects: " + e.getMessage());
        }
    }
    
    /**
     * Gets a project by ID
     * @param projectId Project ID
     * @return Project object if found, null otherwise
     */
    public Project getProjectById(String projectId) {
        return projects.get(projectId);
    }
    
    /**
     * Gets all projects
     * @return List of all projects
     */
    public List<Project> getAllProjects() {
        return new ArrayList<>(projects.values());
    }
    
    /**
     * Gets all visible projects
     * @return List of visible projects
     */
    public List<Project> getVisibleProjects() {
        return projects.values().stream()
                .filter(Project::isVisible)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets projects by manager
     * @param managerNric Manager's NRIC
     * @return List of projects managed by the manager
     */
    public List<Project> getProjectsByManager(String managerNric) {
        return projects.values().stream()
                .filter(p -> p.getManagerInCharge().equals(managerNric))
                .collect(Collectors.toList());
    }
    
    /**
     * Adds a new project
     * @param project Project to add
     * @return true if added successfully
     */
    public boolean addProject(Project project) {
        projects.put(project.getProjectId(), project);
        saveProjects();
        return true;
    }
    
    /**
     * Updates an existing project
     * @param project Project to update
     * @return true if updated successfully, false if project doesn't exist
     */
    public boolean updateProject(Project project) {
        if (!projects.containsKey(project.getProjectId())) {
            return false;
        }
        projects.put(project.getProjectId(), project);
        saveProjects();
        return true;
    }
    
    /**
     * Deletes a project
     * @param projectId ID of the project to delete
     * @return true if deleted successfully, false if project doesn't exist
     */
    public boolean deleteProject(String projectId) {
        if (!projects.containsKey(projectId)) {
            return false;
        }
        projects.remove(projectId);
        saveProjects();
        return true;
    }
    
    // ===== APPLICATION OPERATIONS =====
    
    /**
     * Loads applications from file
     */
    private void loadApplications() {
        try (BufferedReader reader = new BufferedReader(new FileReader(APPLICATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String applicationId = parts[0].trim();
                    String applicantNric = parts[1].trim();
                    String projectId = parts[2].trim();
                    FlatType flatType = FlatType.valueOf(parts[3].trim());
                    ApplicationStatus status = ApplicationStatus.valueOf(parts[4].trim());
                    LocalDate applicationDate = LocalDate.parse(parts[5].trim(), dateFormatter);
                    LocalDate statusUpdateDate = LocalDate.parse(parts[6].trim(), dateFormatter);
                    boolean withdrawalRequested = parts.length > 7 && Boolean.parseBoolean(parts[7].trim());
                    
                    Application application = new Application(applicationId, applicantNric, projectId, flatType);
                    application.setStatus(status);
                    application.setApplicationDate(applicationDate);
                    application.setStatusUpdateDate(statusUpdateDate);
                    application.setWithdrawalRequested(withdrawalRequested);
                    
                    applications.put(applicationId, application);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading applications: " + e.getMessage());
        }
    }
    
    /**
     * Saves applications to file
     */
    private void saveApplications() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPLICATIONS_FILE))) {
            for (Application application : applications.values()) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%b",
                        application.getApplicationId(),
                        application.getApplicantNric(),
                        application.getProjectId(),
                        application.getFlatType(),
                        application.getStatus(),
                        application.getApplicationDate().format(dateFormatter),
                        application.getStatusUpdateDate().format(dateFormatter),
                        application.isWithdrawalRequested()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
    }
    
    /**
     * Gets an application by ID
     * @param applicationId Application ID
     * @return Application object if found, null otherwise
     */
    public Application getApplicationById(String applicationId) {
        return applications.get(applicationId);
    }
    
    /**
     * Gets applications by applicant
     * @param applicantNric Applicant's NRIC
     * @return List of applications by the applicant
     */
    public List<Application> getApplicationsByApplicant(String applicantNric) {
        return applications.values().stream()
                .filter(a -> a.getApplicantNric().equals(applicantNric))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets applications by project
     * @param projectId Project ID
     * @return List of applications for the project
     */
    public List<Application> getApplicationsByProject(String projectId) {
        return applications.values().stream()
                .filter(a -> a.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets applications by project and status
     * @param projectId Project ID
     * @param status Status to filter by
     * @return List of filtered applications
     */
    public List<Application> getApplicationsByProjectAndStatus(String projectId, ApplicationStatus status) {
        return applications.values().stream()
                .filter(a -> a.getProjectId().equals(projectId) && a.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets applications with withdrawal requests
     * @return List of applications with withdrawal requests
     */
    public List<Application> getWithdrawalRequests() {
        return applications.values().stream()
                .filter(Application::isWithdrawalRequested)
                .collect(Collectors.toList());
    }
    
    /**
     * Adds a new application
     * @param application Application to add
     * @return true if added successfully
     */
    public boolean addApplication(Application application) {
        applications.put(application.getApplicationId(), application);
        saveApplications();
        return true;
    }
    
    /**
     * Updates an existing application
     * @param application Application to update
     * @return true if updated successfully, false if application doesn't exist
     */
    public boolean updateApplication(Application application) {
        if (!applications.containsKey(application.getApplicationId())) {
            return false;
        }
        applications.put(application.getApplicationId(), application);
        saveApplications();
        return true;
    }
    
    /**
     * Deletes an application
     * @param applicationId ID of the application to delete
     * @return true if deleted successfully, false if application doesn't exist
     */
    public boolean deleteApplication(String applicationId) {
        if (!applications.containsKey(applicationId)) {
            return false;
        }
        applications.remove(applicationId);
        saveApplications();
        return true;
    }
    
    // ===== ENQUIRY OPERATIONS =====
    
    /**
     * Loads enquiries from file
     */
    private void loadEnquiries() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ENQUIRIES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                // This is more complex due to possible commas in content
                // Using a simple format: ID|UserID|ProjectID|Timestamp|Content|Responses
                String[] parts = line.split("\\|", 6);
                if (parts.length >= 5) {
                    String enquiryId = parts[0].trim();
                    String userNric = parts[1].trim();
                    String projectId = parts[2].trim();
                    String timestamp = parts[3].trim();
                    String content = parts[4].trim();
                    
                    Enquiry enquiry = new Enquiry(enquiryId, userNric, projectId, content);
                    
                    // Parse responses if they exist
                    if (parts.length > 5 && !parts[5].trim().isEmpty()) {
                        String[] responseStrings = parts[5].split(";");
                        for (String responseStr : responseStrings) {
                            String[] responseParts = responseStr.split("\\~", 3);
                            if (responseParts.length == 3) {
                                String responderNric = responseParts[0].trim();
                                String responseTimestamp = responseParts[1].trim();
                                String responseContent = responseParts[2].trim();
                                
                                enquiry.addResponse(responderNric, responseContent);
                            }
                        }
                    }
                    
                    enquiries.put(enquiryId, enquiry);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading enquiries: " + e.getMessage());
        }
    }
    
    /**
     * Saves enquiries to file
     */
    private void saveEnquiries() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENQUIRIES_FILE))) {
            for (Enquiry enquiry : enquiries.values()) {
                StringBuilder responsesStr = new StringBuilder();
                List<Enquiry.Response> responses = enquiry.getResponses();
                
                for (int i = 0; i < responses.size(); i++) {
                    Enquiry.Response response = responses.get(i);
                    responsesStr.append(response.getResponderNric())
                               .append("~")
                               .append(response.getTimestamp())
                               .append("~")
                               .append(response.getContent());
                    
                    if (i < responses.size() - 1) {
                        responsesStr.append(";");
                    }
                }
                
                writer.write(String.format("%s|%s|%s|%s|%s|%s",
                        enquiry.getEnquiryId(),
                        enquiry.getUserNric(),
                        enquiry.getProjectId(),
                        enquiry.getTimestamp(),
                        enquiry.getContent(),
                        responsesStr.toString()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving enquiries: " + e.getMessage());
        }
    }
    
    /**
     * Gets an enquiry by ID
     * @param enquiryId Enquiry ID
     * @return Enquiry object if found, null otherwise
     */
    public Enquiry getEnquiryById(String enquiryId) {
        return enquiries.get(enquiryId);
    }
    
    /**
     * Gets enquiries by user
     * @param userNric User's NRIC
     * @return List of enquiries by the user
     */
    public List<Enquiry> getEnquiriesByUser(String userNric) {
        return enquiries.values().stream()
                .filter(e -> e.getUserNric().equals(userNric))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets enquiries by project
     * @param projectId Project ID
     * @return List of enquiries for the project
     */
    public List<Enquiry> getEnquiriesByProject(String projectId) {
        return enquiries.values().stream()
                .filter(e -> e.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all enquiries
     * @return List of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        return new ArrayList<>(enquiries.values());
    }
    
    /**
     * Adds a new enquiry
     * @param enquiry Enquiry to add
     * @return true if added successfully
     */
    public boolean addEnquiry(Enquiry enquiry) {
        enquiries.put(enquiry.getEnquiryId(), enquiry);
        saveEnquiries();
        return true;
    }
    
    /**
     * Updates an existing enquiry
     * @param enquiry Enquiry to update
     * @return true if updated successfully, false if enquiry doesn't exist
     */
    public boolean updateEnquiry(Enquiry enquiry) {
        if (!enquiries.containsKey(enquiry.getEnquiryId())) {
            return false;
        }
        enquiries.put(enquiry.getEnquiryId(), enquiry);
        saveEnquiries();
        return true;
    }
    
    /**
     * Deletes an enquiry
     * @param enquiryId ID of the enquiry to delete
     * @return true if deleted successfully, false if enquiry doesn't exist
     */
    public boolean deleteEnquiry(String enquiryId) {
        if (!enquiries.containsKey(enquiryId)) {
            return false;
        }
        enquiries.remove(enquiryId);
        saveEnquiries();
        return true;
    }
    
    // ===== REGISTRATION OPERATIONS =====
    
    /**
     * Loads registrations from file
     */
    private void loadRegistrations() {
        try (BufferedReader reader = new BufferedReader(new FileReader(REGISTRATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String registrationId = parts[0].trim();
                    String officerNric = parts[1].trim();
                    String projectId = parts[2].trim();
                    RegistrationStatus status = RegistrationStatus.valueOf(parts[3].trim());
                    LocalDate registrationDate = LocalDate.parse(parts[4].trim(), dateFormatter);
                    
                    Registration registration = new Registration(registrationId, officerNric, projectId);
                    registration.setStatus(status);
                    registration.setRegistrationDate(registrationDate);
                    
                    registrations.put(registrationId, registration);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading registrations: " + e.getMessage());
        }
    }
    
    /**
     * Saves registrations to file
     */
    private void saveRegistrations() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REGISTRATIONS_FILE))) {
            for (Registration registration : registrations.values()) {
                writer.write(String.format("%s,%s,%s,%s,%s",
                        registration.getRegistrationId(),
                        registration.getOfficerNric(),
                        registration.getProjectId(),
                        registration.getStatus(),
                        registration.getRegistrationDate().format(dateFormatter)));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving registrations: " + e.getMessage());
        }
    }
    
    /**
     * Gets a registration by ID
     * @param registrationId Registration ID
     * @return Registration object if found, null otherwise
     */
    public Registration getRegistrationById(String registrationId) {
        return registrations.get(registrationId);
    }
    
    /**
     * Gets registrations by officer
     * @param officerNric Officer's NRIC
     * @return List of registrations by the officer
     */
    public List<Registration> getRegistrationsByOfficer(String officerNric) {
        return registrations.values().stream()
                .filter(r -> r.getOfficerNric().equals(officerNric))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets registrations by project
     * @param projectId Project ID
     * @return List of registrations for the project
     */
    public List<Registration> getRegistrationsByProject(String projectId) {
        return registrations.values().stream()
                .filter(r -> r.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets registrations by status
     * @param status Status to filter by
     * @return List of registrations with the specified status
     */
    public List<Registration> getRegistrationsByStatus(RegistrationStatus status) {
        return registrations.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets registrations by project and status
     * @param projectId Project ID
     * @param status Status to filter by
     * @return List of filtered registrations
     */
    public List<Registration> getRegistrationsByProjectAndStatus(String projectId, RegistrationStatus status) {
        return registrations.values().stream()
                .filter(r -> r.getProjectId().equals(projectId) && r.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Adds a new registration
     * @param registration Registration to add
     * @return true if added successfully
     */
    public boolean addRegistration(Registration registration) {
        registrations.put(registration.getRegistrationId(), registration);
        saveRegistrations();
        return true;
    }
    
    /**
     * Updates an existing registration
     * @param registration Registration to update
     * @return true if updated successfully, false if registration doesn't exist
     */
    public boolean updateRegistration(Registration registration) {
        if (!registrations.containsKey(registration.getRegistrationId())) {
            return false;
        }
        registrations.put(registration.getRegistrationId(), registration);
        saveRegistrations();
        return true;
    }
    
    /**
     * Deletes a registration
     * @param registrationId ID of the registration to delete
     * @return true if deleted successfully, false if registration doesn't exist
     */
    public boolean deleteRegistration(String registrationId) {
        if (!registrations.containsKey(registrationId)) {
            return false;
        }
        registrations.remove(registrationId);
        saveRegistrations();
        return true;
    }
    
    // ===== BOOKING OPERATIONS =====
    
    /**
     * Creates a flat booking
     * @param application The application to be booked
     * @return true if booking successful, false otherwise
     */
    public boolean createBooking(Application application) {
        if (application == null || application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return false;
        }
        
        Project project = getProjectById(application.getProjectId());
        User applicant = getUserByNRIC(application.getApplicantNric());
        
        if (project == null || applicant == null) {
            return false;
        }
        
        // Check if there are available units
        FlatType flatType = application.getFlatType();
        if (project.getAvailableUnits(flatType) <= 0) {
            return false;
        }
        
        // Update project
        project.decrementAvailableUnits(flatType);
        updateProject(project);
        
        // Update application
        application.setStatus(ApplicationStatus.BOOKED);
        updateApplication(application);
        
        // Update user
        applicant.setBookedProjectId(project.getProjectId());
        applicant.setBookedFlatType(flatType);
        updateUser(applicant);
        
        return true;
    }
    
    /**
     * Generates a booking receipt
     * @param nric User's NRIC
     * @return Receipt text
     */
    public String generateBookingReceipt(String nric) {
        User user = getUserByNRIC(nric);
        if (user == null || !user.hasBooked()) {
            return "No booking found for this user.";
        }
        
        Project project = getProjectById(user.getBookedProjectId());
        if (project == null) {
            return "Project information not found.";
        }
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== BOOKING RECEIPT =====\n\n");
        receipt.append("Applicant Information:\n");
        receipt.append("Name: ").append(user.getNric()).append("\n");
        receipt.append("NRIC: ").append(user.getNric()).append("\n");
        receipt.append("Age: ").append(user.getAge()).append("\n");
        receipt.append("Marital Status: ").append(user.getMaritalStatus()).append("\n\n");
        
        receipt.append("Booking Details:\n");
        receipt.append("Project Name: ").append(project.getProjectName()).append("\n");
        receipt.append("Neighborhood: ").append(project.getNeighborhood()).append("\n");
        receipt.append("Flat Type: ").append(user.getBookedFlatType().getDisplayName()).append("\n\n");
        
        receipt.append("===== END OF RECEIPT =====");
        
        return receipt.toString();
    }
}