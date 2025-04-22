package models;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

import enumeration.FlatType;

/**
 * Represents a Build-To-Order (BTO) housing project.
 * 
 * This class contains information about a BTO project including its name,
 * location, application dates, available flat types, and assigned staff.
 * It manages the core data structure for housing projects in the system.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class BTOProject {

    private String projectName;
    private String neighborhood;

    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;

    private Map<FlatType, FlatTypeDetails> flatTypes;

    private HDBManager hdbManager;

    private int hdbOfficerSlots;
    private List<HDBOfficer> hdbOfficers;

    private boolean visible;


    public BTOProject(String projectName, String neighborhood, LocalDate applicationOpeningDate, LocalDate applicationClosingDate, Map<FlatType, FlatTypeDetails> flatTypes, HDBManager hdbManager, int hdbOfficerSlots, List<HDBOfficer> hdbOfficers, boolean visible) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.flatTypes = flatTypes;
        this.hdbManager = hdbManager;
        this.hdbOfficerSlots = hdbOfficerSlots;
        this.hdbOfficers = hdbOfficers;
        this.visible = visible;
    }

    //Getters
    public String getProjectName() {
        return projectName;
    }
    
    public String getNeighborhood() {
        return neighborhood;
    }

    public LocalDate getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    public LocalDate getApplicationClosingDate() {
        return applicationClosingDate;
    }

    public Map<FlatType, FlatTypeDetails> getFlatTypes() {
        return flatTypes;
    }

    public HDBManager getHDBManager() {
        return hdbManager;
    }

    public int getHDBOfficerSlots() {
        return hdbOfficerSlots;
    }

    public List<HDBOfficer> getHDBOfficers() {
        return hdbOfficers;
    }

    public boolean isVisible() {
        return visible;
    }

    //Setters
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setApplicationOpeningDate(LocalDate applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }

    public void setApplicationClosingDate(LocalDate applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }

    public void setFlatTypes(Map<FlatType, FlatTypeDetails> flatTypes) {
        this.flatTypes = flatTypes;
    }

    public void setHDBManager(HDBManager hdbManager) {
        this.hdbManager = hdbManager;
    }

    public void setHDBOfficerSlots(int hdbOfficerSlots) {
        this.hdbOfficerSlots = hdbOfficerSlots;
    }

    public void setHDBOfficers(List<HDBOfficer> hdbOfficers) {
        this.hdbOfficers = hdbOfficers;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    //Other methods
    public void addHDBOfficer(HDBOfficer hdbOfficer) {
        hdbOfficers.add(hdbOfficer);
    }

    public void removeHDBOfficer(HDBOfficer hdbOfficer) {
        hdbOfficers.remove(hdbOfficer);
    }
    
    
}