package stores;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import models.Applicant;
import models.BTOApplication;
import models.BTOProject;
import models.Enquiry;
import models.HDBManager;
import models.HDBOfficer;
import models.Registration;
import models.User;
import enumeration.FlatType;
import enumeration.MaritalStatus;
import enumeration.UserStatus;
import enumeration.UserType;
import enumeration.RegistrationStatus;
import enumeration.ApplicationStatus;
import services.CsvDataService;
import util.FilePathsUtils;
import util.NRICValidator;

/**
 * Central data store for the application. Manages loading and saving data.
 * Follows the Singleton pattern.
 */
public class DataStore {
    private static DataStore instance;
    private Map<String, List<String[]>> allData;
    private CsvDataService csvDataService;
    private Map<String, String> filePaths;
    private boolean initialized = false;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // Maps to cache objects and avoid repeated creation
    private final Map<String, User> userCache = new ConcurrentHashMap<>();
    private final Map<String, BTOProject> projectCache = new ConcurrentHashMap<>();
    private final Map<String, BTOApplication> applicationCache = new ConcurrentHashMap<>();
    
    // Formatter for date handling
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Private constructor for Singleton pattern
    private DataStore() {
        this.csvDataService = new CsvDataService();
        this.allData = new ConcurrentHashMap<>();
        this.filePaths = FilePathsUtils.csvFilePaths();
    }

    /**
     * Gets the singleton instance of DataStore
     * 
     * @return DataStore instance
     */
    public static DataStore getInstance() {
        if (instance == null) {
            synchronized (DataStore.class) {
                if (instance == null) {
                    instance = new DataStore();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes the data store with service and file paths
     * 
     * @param csvService CSV data service
     * @param filePaths Map of file paths
     */
    public static void initDataStore(CsvDataService csvService, Map<String, String> filePaths) {
        DataStore store = getInstance();
        store.lock.writeLock().lock();
        try {
            store.csvDataService = csvService;
            store.filePaths = filePaths;
            store.loadData();
            store.initialized = true;
        } finally {
            store.lock.writeLock().unlock();
        }
    }

    /**
     * Loads data from all CSV files
     */
    private void loadData() {
        lock.writeLock().lock();
        try {
            allData.clear();
            try {
                allData.putAll(csvDataService.readAllData(filePaths));
            } catch (IOException e) {
                System.err.println("Error loading data: " + e.getMessage());
                // Initialize with empty data if files don't exist
                for (String fileType : filePaths.keySet()) {
                    allData.put(fileType, Collections.synchronizedList(new ArrayList<>()));
                }
            }
            
            // Clear caches
            userCache.clear();
            projectCache.clear();
            applicationCache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Saves all data to CSV files
     */
    public static void saveData() {
        DataStore store = getInstance();
        if (!store.initialized) {
            return;
        }
        
        store.lock.readLock().lock();
        try {
            store.csvDataService.writeAllData(store.filePaths, store.allData);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        } finally {
            store.lock.readLock().unlock();
        }
    }

    /**
     * Gets a user by NRIC
     * 
     * @param nric User's NRIC
     * @return User object if found, null otherwise
     */
    public User getUserByNRIC(String nric) {
        if (nric == null || !NRICValidator.isValid(nric)) {
            return null;
        }
        
        // Check cache first
        if (userCache.containsKey(nric)) {
            return userCache.get(nric);
        }
        
        lock.readLock().lock();
        try {
            List<String[]> users = allData.getOrDefault("users", Collections.emptyList());
            for (String[] userData : users) {
                if (userData.length > 1 && userData[1].equals(nric)) {
                    // Convert user data to User object
                    User user = createUserFromData(userData);
                    if (user != null) {
                        userCache.put(nric, user);
                    }
                    return user;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets all users
     * 
     * @return List of all User objects
     */
    public static List<User> getUsers() {
        DataStore store = getInstance();
        store.lock.readLock().lock();
        try {
            List<String[]> usersData = store.allData.getOrDefault("users", Collections.emptyList());
            return usersData.stream()
                    .map(store::createUserFromData)
                    .filter(user -> user != null)
                    .collect(Collectors.toList());
        } finally {
            store.lock.readLock().unlock();
        }
    }

    /**
     * Adds a new user
     * 
     * @param user User to add
     * @return true if added successfully
     */
    public static boolean addUser(User user) {
        if (user == null) {
            return false;
        }

        DataStore store = getInstance();
        store.lock.writeLock().lock();
        try {
            List<String[]> users = new ArrayList<>(store.allData.getOrDefault("users", new ArrayList<>()));
            
            // Check if user already exists
            for (String[] userData : users) {
                if (userData.length > 1 && userData[1].equals(user.getNric())) {
                    return false; // User already exists
                }
            }
            
            // Convert User object to string array and add
            String[] userData = store.convertUserToData(user);
            users.add(userData);
            store.allData.put("users", users);
            
            // Update cache
            store.userCache.put(user.getNric(), user);
            
            saveData();
            return true;
        } finally {
            store.lock.writeLock().unlock();
        }
    }

    /**
     * Updates an existing user
     * 
     * @param user User to update
     * @return true if updated successfully, false if user doesn't exist
     */
    public boolean updateUser(User user) {
        if (user == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            List<String[]> users = new ArrayList<>(allData.getOrDefault("users", new ArrayList<>()));

            boolean updated = false;
            for (int i = 0; i < users.size(); i++) {
                String[] userData = users.get(i);
                if (userData.length > 1 && userData[1].equals(user.getNric())) {
                    users.set(i, convertUserToData(user));
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                allData.put("users", users);
                
                // Update cache
                userCache.put(user.getNric(), user);
                
                saveData();
                return true;
            }
            
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Deletes a user by NRIC
     * 
     * @param nric NRIC of the user to delete
     * @return true if deleted successfully
     */
    public boolean deleteUser(String nric) {
        if (nric == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            List<String[]> users = new ArrayList<>(allData.getOrDefault("users", new ArrayList<>()));

            boolean removed = users.removeIf(userData -> userData.length > 1 && userData[1].equals(nric));

            if (removed) {
                allData.put("users", users);
                
                // Remove from cache
                userCache.remove(nric);
                
                saveData();
            }

            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Gets users by type
     * 
     * @param userType User type to filter by
     * @return List of users with the specified type
     */
    public List<User> getUsersByType(UserType userType) {
        if (userType == null) {
            return Collections.emptyList();
        }
        
        lock.readLock().lock();
        try {
            List<String[]> users = allData.getOrDefault("users", Collections.emptyList());
            List<User> filteredUsers = new ArrayList<>();

            for (String[] userData : users) {
                if (userData.length > 3 && getUserTypeFromString(userData[3]) == userType) {
                    User user = createUserFromData(userData);
                    if (user != null) {
                        filteredUsers.add(user);
                    }
                }
            }

            return filteredUsers;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets a project by ID
     * 
     * @param projectId Project ID
     * @return Project if found, null otherwise
     */
    public BTOProject getProjectById(String projectId) {
        if (projectId == null) {
            return null;
        }
        
        // Check cache first
        if (projectCache.containsKey(projectId)) {
            return projectCache.get(projectId);
        }
        
        lock.readLock().lock();
        try {
            List<String[]> projects = allData.getOrDefault("projects", Collections.emptyList());
            
            for (String[] projectData : projects) {
                if (projectData.length > 0 && projectData[0].equals(projectId)) {
                    BTOProject project = createProjectFromData(projectData);
                    if (project != null) {
                        projectCache.put(projectId, project);
                    }
                    return project;
                }
            }
            
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets all projects
     * 
     * @return List of all projects
     */
    public List<BTOProject> getAllProjects() {
        lock.readLock().lock();
        try {
            List<String[]> projectsData = allData.getOrDefault("projects", Collections.emptyList());
            List<BTOProject> projects = new ArrayList<>();
            
            for (String[] projectData : projectsData) {
                BTOProject project = createProjectFromData(projectData);
                if (project != null) {
                    projects.add(project);
                    
                    // Update cache
                    projectCache.put(project.getProjectId(), project);
                }
            }
            
            return projects;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets applications by project ID
     * 
     * @param projectId ID of the project
     * @return List of applications for the project
     */
    public List<BTOApplication> getApplicationsByProject(String projectId) {
        if (projectId == null) {
            return Collections.emptyList();
        }
        
        lock.readLock().lock();
        try {
            List<String[]> applications = allData.getOrDefault("applications", Collections.emptyList());
            List<BTOApplication> filteredApplications = new ArrayList<>();

            for (String[] applicationData : applications) {
                if (applicationData.length > 2 && applicationData[2].equals(projectId)) {
                    BTOApplication application = createApplicationFromData(applicationData);
                    if (application != null) {
                        filteredApplications.add(application);
                    }
                }
            }

            return filteredApplications;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets all applications
     * 
     * @return List of all applications
     */
    public List<BTOApplication> getAllApplications() {
        lock.readLock().lock();
        try {
            List<String[]> applicationsData = allData.getOrDefault("applications", Collections.emptyList());
            List<BTOApplication> applications = new ArrayList<>();
            
            for (String[] applicationData : applicationsData) {
                BTOApplication application = createApplicationFromData(applicationData);
                if (application != null) {
                    applications.add(application);
                    
                    // Update cache
                    applicationCache.put(application.getApplicationId(), application);
                }
            }
            
            return applications;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets enquiry data
     * 
     * @return List of enquiry data rows
     */
    public List<String[]> getEnquiryData() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(allData.getOrDefault("enquiries", new ArrayList<>()));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Helper method to create a User object from CSV data
     * 
     * @param userData CSV data row for a user
     * @return User object, or null if data is invalid
     */
    private User createUserFromData(String[] userData) {
        if (userData == null || userData.length < 5) {
            return null;
        }
        
        try {
            String name = userData[0];
            String nric = userData[1];
            int age = Integer.parseInt(userData[2]);
            MaritalStatus maritalStatus = MaritalStatus.valueOf(userData[3]);
            String password = userData[4];
            
            // Calculate date of birth from age
            LocalDate dateOfBirth = LocalDate.now().minusYears(age);
            
            // Determine user type (check column 5 if available)
            UserType userType = UserType.APPLICANT; // Default
            if (userData.length > 5 && userData[5] != null && !userData[5].isEmpty()) {
                userType = getUserTypeFromString(userData[5]);
            }
            
            // Create appropriate user type
            User user;
            switch (userType) {
                case APPLICANT:
                    user = new Applicant(nric, name, dateOfBirth, maritalStatus);
                    break;
                case OFFICER:
                    user = new HDBOfficer(nric, name, dateOfBirth, maritalStatus);
                    break;
                case MANAGER:
                    user = new HDBManager(nric, name, dateOfBirth);
                    break;
                default:
                    user = new User(nric, name, dateOfBirth, maritalStatus);
            }
            
            // Set password
            user.setPassword(password);
            
            // Set contact number and email if available
            if (userData.length > 6 && userData[6] != null && !userData[6].isEmpty()) {
                user.setContactNumber(userData[6]);
            }
            
            if (userData.length > 7 && userData[7] != null && !userData[7].isEmpty()) {
                user.setEmail(userData[7]);
            }
            
            return user;
        } catch (Exception e) {
            System.err.println("Error creating user from data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to convert a User object to CSV data
     * 
     * @param user User object
     * @return CSV data row for the user
     */
    private String[] convertUserToData(User user) {
        if (user == null) {
            return null;
        }
        
        String[] userData = new String[8];
        userData[0] = user.getName();
        userData[1] = user.getNric();
        userData[2] = String.valueOf(user.calculateAge());
        userData[3] = user.getMaritalStatus().toString();
        userData[4] = user.getPassword();
        
        // Determine user type string
        String userTypeStr;
        if (user instanceof Applicant) {
            userTypeStr = "APPLICANT";
        } else if (user instanceof HDBOfficer) {
            userTypeStr = "OFFICER";
        } else if (user instanceof HDBManager) {
            userTypeStr = "MANAGER";
        } else {
            userTypeStr = "APPLICANT"; // Default
        }
        userData[5] = userTypeStr;
        
        userData[6] = user.getContactNumber() != null ? user.getContactNumber() : "";
        userData[7] = user.getEmail() != null ? user.getEmail() : "";
        
        return userData;
    }

    /**
     * Helper method to create a Project object from CSV data
     * 
     * @param projectData CSV data row for a project
     * @return Project object, or null if data is invalid
     */
    private BTOProject createProjectFromData(String[] projectData) {
        if (projectData == null || projectData.length < 10) {
            return null;
        }
        
        try {
            String projectId = projectData[0];
            String projectName = projectData[1];
            String neighborhood = projectData[2];
            
            // Parse flat type counts
            Map<FlatType, Integer> flatTypes = new HashMap<>();
            flatTypes.put(FlatType.TWO_ROOM, Integer.parseInt(projectData[3]));
            flatTypes.put(FlatType.THREE_ROOM, Integer.parseInt(projectData[4]));
            
            // Parse dates
            LocalDate openingDate = LocalDate.parse(projectData[5], dateFormatter);
            LocalDate closingDate = LocalDate.parse(projectData[6], dateFormatter);
            
            // Create project
            BTOProject project = new BTOProject(projectName, neighborhood, openingDate, closingDate);
            
            // Set project ID (normally generated but using provided ID here)
            java.lang.reflect.Field field = BTOProject.class.getDeclaredField("projectId");
            field.setAccessible(true);
            field.set(project, projectId);
            
            // Set flat types
            project.setFlatTypes(flatTypes);
            
            // Set manager
            String managerNric = projectData[7];
            User manager = getUserByNRIC(managerNric);
            if (manager instanceof HDBManager) {
                project.setHdbManager((HDBManager) manager);
            }
            
            // Set officer slots
            project.setAvailableHDBOfficerSlots(Integer.parseInt(projectData[8]));
            
            // Set visibility
            project.setVisibility(Boolean.parseBoolean(projectData[9]));
            
            return project;
        } catch (Exception e) {
            System.err.println("Error creating project from data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to create an Application object from CSV data
     * 
     * @param applicationData CSV data row for an application
     * @return Application object, or null if data is invalid
     */
    private BTOApplication createApplicationFromData(String[] applicationData) {
        if (applicationData == null || applicationData.length < 6) {
            return null;
        }
        
        try {
            String applicationId = applicationData[0];
            String applicantNric = applicationData[1];
            String projectId = applicationData[2];
            String flatTypeStr = applicationData[3];
            String applicationDateStr = applicationData[4];
            String statusStr = applicationData[5];
            
            // Get applicant and project
            User applicantUser = getUserByNRIC(applicantNric);
            BTOProject project = getProjectById(projectId);
            
            if (applicantUser instanceof Applicant && project != null) {
                Applicant applicant = (Applicant) applicantUser;
                
                // Create application
                BTOApplication application = new BTOApplication(applicationId, applicant, project);
                
                // Set flat type
                FlatType flatType = FlatType.valueOf(flatTypeStr);
                application.setFlatType(flatType);
                
                // Set application date
                LocalDate applicationDate = LocalDate.parse(applicationDateStr, dateFormatter);
                application.setApplicationDate(java.time.LocalDateTime.of(
                    applicationDate, java.time.LocalTime.MIDNIGHT));
                
                // Set status
                ApplicationStatus status = ApplicationStatus.valueOf(statusStr);
                application.setStatus(status);
                
                // Set booked unit if available
                if (applicationData.length > 6 && applicationData[6] != null && !applicationData[6].isEmpty()) {
                    application.setBookingReceipt(applicationData[6]);
                }
                
                return application;
            }
            // If either applicant or project is null, or conversion failed
            return null;
        } catch (Exception e) {
            System.err.println("Error creating application from data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to get UserType from a string representation
     * 
     * @param userTypeStr String representation of UserType
     * @return UserType enum value
     */
    private UserType getUserTypeFromString(String userTypeStr) {
        if (userTypeStr == null || userTypeStr.isEmpty()) {
            return UserType.APPLICANT; // Default
        }
        
        try {
            return UserType.valueOf(userTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle legacy or invalid values
            if ("MANAGER".equalsIgnoreCase(userTypeStr)) {
                return UserType.MANAGER;
            } else if ("OFFICER".equalsIgnoreCase(userTypeStr)) {
                return UserType.OFFICER;
            } else {
                return UserType.APPLICANT; // Default
            }
        }
    }

    /**
     * Gets registrations by project ID
     * 
     * @param projectId ID of the project
     * @return List of registrations for the project
     */
    public List<Registration> getRegistrationsByProject(String projectId) {
        if (projectId == null) {
            return Collections.emptyList();
        }
        
        lock.readLock().lock();
        try {
            List<String[]> registrationsData = allData.getOrDefault("registrations", Collections.emptyList());
            List<Registration> registrations = new ArrayList<>();
            
            for (String[] registrationData : registrationsData) {
                if (registrationData.length > 2 && registrationData[2].equals(projectId)) {
                    Registration registration = createRegistrationFromData(registrationData);
                    if (registration != null) {
                        registrations.add(registration);
                    }
                }
            }
            
            return registrations;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets registrations by officer NRIC
     * 
     * @param officerNric NRIC of the officer
     * @return List of registrations for the officer
     */
    public List<Registration> getRegistrationsByOfficer(String officerNric) {
        if (officerNric == null) {
            return Collections.emptyList();
        }
        
        lock.readLock().lock();
        try {
            List<String[]> registrationsData = allData.getOrDefault("registrations", Collections.emptyList());
            List<Registration> registrations = new ArrayList<>();
            
            for (String[] registrationData : registrationsData) {
                if (registrationData.length > 1 && registrationData[1].equals(officerNric)) {
                    Registration registration = createRegistrationFromData(registrationData);
                    if (registration != null) {
                        registrations.add(registration);
                    }
                }
            }
            
            return registrations;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Helper method to create a Registration object from CSV data
     * 
     * @param registrationData CSV data row for a registration
     * @return Registration object, or null if data is invalid
     */
    private Registration createRegistrationFromData(String[] registrationData) {
        if (registrationData == null || registrationData.length < 5) {
            return null;
        }
        
        try {
            String registrationId = registrationData[0];
            String officerNric = registrationData[1];
            String projectId = registrationData[2];
            String registrationDateStr = registrationData[3];
            String statusStr = registrationData[4];
            
            // Get officer and project
            User officerUser = getUserByNRIC(officerNric);
            BTOProject project = getProjectById(projectId);
            
            if (officerUser instanceof HDBOfficer && project != null) {
                HDBOfficer officer = (HDBOfficer) officerUser;
                
                // Create registration
                Registration registration = new Registration(officer, project);
                
                // Set registration ID
                java.lang.reflect.Field field = Registration.class.getDeclaredField("registrationId");
                field.setAccessible(true);
                field.set(registration, registrationId);
                
                // Set registration date
                LocalDate registrationDate = LocalDate.parse(registrationDateStr, dateFormatter);
                registration.setRegistrationDate(registrationDate);
                
                // Set status
                RegistrationStatus status = RegistrationStatus.valueOf(statusStr);
                registration.setStatus(status);
                
                return registration;
            }
            
            // If either officer or project is null, or conversion failed
            return null;
        } catch (Exception e) {
            System.err.println("Error creating registration from data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds a new project
     * 
     * @param project Project to add
     * @return true if added successfully
     */
    public boolean addProject(BTOProject project) {
        if (project == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            List<String[]> projects = new ArrayList<>(allData.getOrDefault("projects", new ArrayList<>()));
            
            // Check if project already exists
            for (String[] projectData : projects) {
                if (projectData.length > 0 && 
                    (projectData[0].equals(project.getProjectId()) || 
                     (projectData.length > 1 && projectData[1].equals(project.getProjectName())))) {
                    return false; // Project already exists
                }
            }
            
            // Convert Project object to string array and add
            String[] projectData = convertProjectToData(project);
            projects.add(projectData);
            allData.put("projects", projects);
            
            // Update cache
            projectCache.put(project.getProjectId(), project);
            
            saveData();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Updates an existing project
     * 
     * @param project Project to update
     * @return true if updated successfully, false if project doesn't exist
     */
    public boolean updateProject(BTOProject project) {
        if (project == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            List<String[]> projects = new ArrayList<>(allData.getOrDefault("projects", new ArrayList<>()));

            boolean updated = false;
            for (int i = 0; i < projects.size(); i++) {
                String[] projectData = projects.get(i);
                if (projectData.length > 0 && projectData[0].equals(project.getProjectId())) {
                    projects.set(i, convertProjectToData(project));
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                allData.put("projects", projects);
                
                // Update cache
                projectCache.put(project.getProjectId(), project);
                
                saveData();
                return true;
            }
            
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Deletes a project by ID
     * 
     * @param projectId ID of the project to delete
     * @return true if deleted successfully
     */
    public boolean deleteProject(String projectId) {
        if (projectId == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            List<String[]> projects = new ArrayList<>(allData.getOrDefault("projects", new ArrayList<>()));

            boolean removed = projects.removeIf(projectData -> 
                projectData.length > 0 && projectData[0].equals(projectId));

            if (removed) {
                allData.put("projects", projects);
                
                // Remove from cache
                projectCache.remove(projectId);
                
                // Also remove all applications and registrations for this project
                removeApplicationsForProject(projectId);
                removeRegistrationsForProject(projectId);
                
                saveData();
            }

            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Helper method to remove all applications for a project
     * 
     * @param projectId ID of the project
     */
    private void removeApplicationsForProject(String projectId) {
        List<String[]> applications = new ArrayList<>(allData.getOrDefault("applications", new ArrayList<>()));
        
        boolean removed = applications.removeIf(applicationData -> 
            applicationData.length > 2 && applicationData[2].equals(projectId));
        
        if (removed) {
            allData.put("applications", applications);
            
            // Clear application cache as it's harder to know which to remove
            applicationCache.clear();
        }
    }

    /**
     * Helper method to remove all registrations for a project
     * 
     * @param projectId ID of the project
     */
    private void removeRegistrationsForProject(String projectId) {
        List<String[]> registrations = new ArrayList<>(allData.getOrDefault("registrations", new ArrayList<>()));
        
        boolean removed = registrations.removeIf(registrationData -> 
            registrationData.length > 2 && registrationData[2].equals(projectId));
        
        if (removed) {
            allData.put("registrations", registrations);
        }
    }

    /**
     * Helper method to convert a Project object to CSV data
     * 
     * @param project Project object
     * @return CSV data row for the project
     */
    private String[] convertProjectToData(BTOProject project) {
        if (project == null) {
            return null;
        }
        
        String[] projectData = new String[11];
        projectData[0] = project.getProjectId();
        projectData[1] = project.getProjectName();
        projectData[2] = project.getNeighborhood();
        
        // Flat type counts
        Map<FlatType, Integer> flatTypes = project.getFlatTypes();
        projectData[3] = String.valueOf(flatTypes.getOrDefault(FlatType.TWO_ROOM, 0));
        projectData[4] = String.valueOf(flatTypes.getOrDefault(FlatType.THREE_ROOM, 0));
        
        // Dates
        projectData[5] = project.getApplicationOpeningDate().format(dateFormatter);
        projectData[6] = project.getApplicationClosingDate().format(dateFormatter);
        
        // Manager
        HDBManager manager = project.getHdbManager();
        projectData[7] = manager != null ? manager.getNric() : "";
        
        // Officer slots and visibility
        projectData[8] = String.valueOf(project.getAvailableHDBOfficerSlots());
        projectData[9] = String.valueOf(project.isVisibility());
        
        // Additional data - officer IDs (comma-separated)
        List<String> officerIds = project.getOfficerIds();
        projectData[10] = String.join(",", officerIds);
        
        return projectData;
    }

    /**
     * Adds a new application
     * 
     * @param application Application to add
     * @return true if added successfully
     */
    public boolean addApplication(BTOApplication application) {
        if (application == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            List<String[]> applications = new ArrayList<>(allData.getOrDefault("applications", new ArrayList<>()));
            
            // Check if application already exists
            for (String[] applicationData : applications) {
                if (applicationData.length > 0 && applicationData[0].equals(application.getApplicationId())) {
                    return false; // Application already exists
                }
            }
            
            // Convert Application object to string array and add
            String[] applicationData = convertApplicationToData(application);
            applications.add(applicationData);
            allData.put("applications", applications);
            
            // Update cache
            applicationCache.put(application.getApplicationId(), application);
            
            saveData();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Updates an existing application
     * 
     * @param application Application to update
     * @return true if updated successfully, false if application doesn't exist
     */
    public boolean updateApplication(BTOApplication application) {
        if (application == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            List<String[]> applications = new ArrayList<>(allData.getOrDefault("applications", new ArrayList<>()));

            boolean updated = false;
            for (int i = 0; i < applications.size(); i++) {
                String[] applicationData = applications.get(i);
                if (applicationData.length > 0 && applicationData[0].equals(application.getApplicationId())) {
                    applications.set(i, convertApplicationToData(application));
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                allData.put("applications", applications);
                
                // Update cache
                applicationCache.put(application.getApplicationId(), application);
                
                saveData();
                return true;
            }
            
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Helper method to convert an Application object to CSV data
     * 
     * @param application Application object
     * @return CSV data row for the application
     */
    private String[] convertApplicationToData(BTOApplication application) {
        if (application == null) {
            return null;
        }
        
        String[] applicationData = new String[7];
        applicationData[0] = application.getApplicationId();
        applicationData[1] = application.getApplicant().getNric();
        applicationData[2] = application.getProject().getProjectId();
        applicationData[3] = application.getFlatType().toString();
        applicationData[4] = application.getApplicationDate().toLocalDate().format(dateFormatter);
        applicationData[5] = application.getStatus().toString();
        applicationData[6] = application.getBookingReceipt() != null ? application.getBookingReceipt() : "";
        
        return applicationData;
    }

    /**
     * Adds a new registration
     * 
     * @param registration Registration to add
     * @return true if added successfully
     */
    public boolean addRegistration(Registration registration) {
        if (registration == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            List<String[]> registrations = new ArrayList<>(allData.getOrDefault("registrations", new ArrayList<>()));
            
            // Check if registration already exists
            for (String[] registrationData : registrations) {
                if (registrationData.length > 0 && registrationData[0].equals(registration.getRegistrationId())) {
                    return false; // Registration already exists
                }
            }
            
            // Convert Registration object to string array and add
            String[] registrationData = convertRegistrationToData(registration);
            registrations.add(registrationData);
            allData.put("registrations", registrations);
            
            saveData();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Updates an existing registration
     * 
     * @param registration Registration to update
     * @return true if updated successfully, false if registration doesn't exist
     */
    public boolean updateRegistration(Registration registration) {
        if (registration == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            List<String[]> registrations = new ArrayList<>(allData.getOrDefault("registrations", new ArrayList<>()));

            boolean updated = false;
            for (int i = 0; i < registrations.size(); i++) {
                String[] registrationData = registrations.get(i);
                if (registrationData.length > 0 && registrationData[0].equals(registration.getRegistrationId())) {
                    registrations.set(i, convertRegistrationToData(registration));
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                allData.put("registrations", registrations);
                saveData();
                return true;
            }
            
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Helper method to convert a Registration object to CSV data
     * 
     * @param registration Registration object
     * @return CSV data row for the registration
     */
    private String[] convertRegistrationToData(Registration registration) {
        if (registration == null) {
            return null;
        }
        
        String[] registrationData = new String[5];
        registrationData[0] = registration.getRegistrationId();
        registrationData[1] = registration.getOfficer().getNric();
        registrationData[2] = registration.getProject().getProjectId();
        registrationData[3] = registration.getRegistrationDate().format(dateFormatter);
        registrationData[4] = registration.getStatus().toString();
        
        return registrationData;
    }
}