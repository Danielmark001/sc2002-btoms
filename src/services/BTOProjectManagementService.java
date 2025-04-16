package services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

import enumeration.FlatType;
import models.BTOProject;
import models.FlatTypeDetails;
import models.HDBManager;
import stores.DataStore;

public class BTOProjectManagementService {
    
    /**
     * Create a new BTO project
     * @param projectName Project name
     * @param neighborhood Neighborhood
     * @param openingDate Application opening date
     * @param closingDate Application closing date
     * @param flatTypes Map of flat types and their details
     * @param hdbManager HDB Manager creating the project
     * @param hdbOfficerSlots Number of HDB Officer slots
     * @return The created project
     */
    public BTOProject createProject(
        String projectName,
        String neighborhood,
        LocalDate openingDate,
        LocalDate closingDate,
        Map<FlatType, FlatTypeDetails> flatTypes,
        HDBManager hdbManager,
        int hdbOfficerSlots
    ) {
        BTOProject project = new BTOProject(
            projectName,
            neighborhood,
            openingDate,
            closingDate,
            flatTypes,
            hdbManager,
            hdbOfficerSlots,
            new ArrayList<>(),
            false
        );
        
        DataStore.getBTOProjectsData().put(projectName, project);
        DataStore.saveData();
        
        return project;
    }
    
    /**
     * Update a project's name
     * @param project Project to update
     * @param newName New project name
     */
    public void updateProjectName(BTOProject project, String newName) {
        DataStore.getBTOProjectsData().remove(project.getProjectName());
        project.setProjectName(newName);
        DataStore.getBTOProjectsData().put(newName, project);
        DataStore.saveData();
    }
    
    /**
     * Update a project's neighborhood
     * @param project Project to update
     * @param newNeighborhood New neighborhood
     */
    public void updateNeighborhood(BTOProject project, String newNeighborhood) {
        project.setNeighborhood(newNeighborhood);
        DataStore.saveData();
    }
    
    /**
     * Update a project's application dates
     * @param project Project to update
     * @param newOpeningDate New opening date
     * @param newClosingDate New closing date
     */
    public void updateApplicationDates(BTOProject project, LocalDate newOpeningDate, LocalDate newClosingDate) {
        project.setApplicationOpeningDate(newOpeningDate);
        project.setApplicationClosingDate(newClosingDate);
        DataStore.saveData();
    }
    
    /**
     * Update a project's flat types
     * @param project Project to update
     * @param newFlatTypes New flat types
     */
    public void updateFlatTypes(BTOProject project, Map<FlatType, FlatTypeDetails> newFlatTypes) {
        project.setFlatTypes(newFlatTypes);
        DataStore.saveData();
    }
    
    /**
     * Update a project's HDB Officer slots
     * @param project Project to update
     * @param newSlots New number of slots
     */
    public void updateHDBOfficerSlots(BTOProject project, int newSlots) {
        project.setHDBOfficerSlots(newSlots);
        DataStore.saveData();
    }
    
    /**
     * Update a project's visibility
     * @param project Project to update
     * @param newVisibility New visibility status
     */
    public void updateVisibility(BTOProject project, boolean newVisibility) {
        project.setVisible(newVisibility);
        DataStore.saveData();
    }
    
    /**
     * Delete a project
     * @param project Project to delete
     */
    public void deleteProject(BTOProject project) {
        DataStore.getBTOProjectsData().remove(project.getProjectName());
        DataStore.saveData();
    }
    
    /**
     * Check if a manager is handling an active project
     * @param hdbManager Manager to check
     * @return true if handling an active project, false otherwise
     */
    public boolean isHandlingActiveProject(HDBManager hdbManager) {
        LocalDate today = LocalDate.now();
        
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .anyMatch(project -> 
                !today.isBefore(project.getApplicationOpeningDate()) && 
                !today.isAfter(project.getApplicationClosingDate())
            );
    }
    
    /**
     * Get projects managed by a specific HDB Manager
     * @param hdbManager Manager to get projects for
     * @return List of managed projects
     */
    public List<BTOProject> getManagedProjects(HDBManager hdbManager) {
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .collect(Collectors.toList());
    }
} 