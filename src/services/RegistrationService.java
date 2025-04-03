package services;

import interfaces.IRegistrationService;
import models.*;
import enumeration.*;
import stores.DataStore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing officer registrations
 */
public class RegistrationService implements IRegistrationService {
    private static RegistrationService instance;
    private DataStore dataStore;
    
    private RegistrationService() {
        this.dataStore = DataStore.getInstance();
    }
    
    /**
     * Gets the singleton instance of RegistrationService
     * @return RegistrationService instance
     */
    public static synchronized RegistrationService getInstance() {
        if (instance == null) {
            instance = new RegistrationService();
        }
        return instance;
    }
    
    @Override
    public Registration registerOfficer(HDBOfficer officer, BTOProject project) {
        // Validate inputs
        if (officer == null || project == null) {
            throw new IllegalArgumentException("Officer and project cannot be null");
        }
        
        // Create registration
        Registration registration = new Registration(officer, project);
        
        // Check eligibility
        if (!registration.isEligible()) {
            throw new IllegalStateException("Officer is not eligible for registration");
        }
        
        // Set initial status
        registration.setStatus(Registration.RegistrationStatus.PENDING);
        registration.setRegistrationDate(LocalDate.now());
        
        // Add to officer's registrations
        officer.addRegistration(registration);
        
        // Add to project's registrations
        project.addRegistration(registration);
        
        // Save to data store
        dataStore.addRegistration(registration);
        
        return registration;
    }
    
    @Override
    public boolean approveRegistration(Registration registration, HDBManager manager) {
        // Validate inputs
        if (registration == null || manager == null) {
            return false;
        }
        
        // Verify the manager is managing this project
        if (!registration.getProject().getHdbManager().equals(manager)) {
            return false;
        }
        
        // Check registration status
        if (registration.getStatus() != Registration.RegistrationStatus.PENDING) {
            return false;
        }
        
        // Check if project has available slots
        if (registration.getProject().getAvailableHDBOfficerSlots() <= 0) {
            return false;
        }
        
        // Update registration status
        registration.setStatus(Registration.RegistrationStatus.APPROVED);
        
        // Update officer - set handling project
        if (registration.getOfficer() instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) registration.getOfficer();
            officer.setHandlingProject(registration.getProject());
        }
        
        // Update project - decrement available slots
        BTOProject project = registration.getProject();
        project.setAvailableHDBOfficerSlots(project.getAvailableHDBOfficerSlots() - 1);
        
        // Save to data store
        dataStore.updateRegistration(registration);
        
        return true;
    }
    
    @Override
    public boolean rejectRegistration(Registration registration, HDBManager manager) {
        // Validate inputs
        if (registration == null || manager == null) {
            return false;
        }
        
        // Verify the manager is managing this project
        if (!registration.getProject().getHdbManager().equals(manager)) {
            return false;
        }
        
        // Check registration status
        if (registration.getStatus() != Registration.RegistrationStatus.PENDING) {
            return false;
        }
        
        // Update registration status
        registration.setStatus(Registration.RegistrationStatus.REJECTED);
        
        // Save to data store
        dataStore.updateRegistration(registration);
        
        return true;
    }
    
    @Override
    public List<Registration> getRegistrationsByProject(BTOProject project) {
        if (project == null) {
            return new ArrayList<>();
        }
        
        return dataStore.getRegistrationsByProject(project.getProjectId());
    }
    
    @Override
    public List<Registration> getRegistrationsByStatus(Registration.RegistrationStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }
        
        // Get all registrations from all projects
        List<Registration> allRegistrations = new ArrayList<>();
        for (BTOProject project : dataStore.getAllProjects()) {
            allRegistrations.addAll(project.getRegistrations());
        }
        
        // Filter by status
        return allRegistrations.stream()
            .filter(reg -> reg.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Registration> getRegistrationsByOfficer(HDBOfficer officer) {
        if (officer == null) {
            return new ArrayList<>();
        }
        
        return dataStore.getRegistrationsByOfficer(officer.getNric());
    }
    
    /**
     * Gets registrations by status for a specific project
     * @param project Project to filter by
     * @param status Status to filter by
     * @return List of registrations with the specified project and status
     */
    public List<Registration> getRegistrationsByProjectAndStatus(BTOProject project, Registration.RegistrationStatus status) {
        if (project == null || status == null) {
            return new ArrayList<>();
        }
        
        return getRegistrationsByProject(project).stream()
            .filter(reg -> reg.getStatus() == status)
            .collect(Collectors.toList());
    }
}