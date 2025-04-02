package services;

import models.Application;
import models.BTOProject;
import stores.DataStore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import enumeration.ApplicationStatus;
import interfaces.IReportService;

public class ReportService implements IReportService {
    private DataStore dataStore;

    public ReportService() {
        this.dataStore = DataStore.getInstance();
    }

    public String generateBookingReport(String filter, String value) {
        List<Application> applications;

        // Filter applications based on criteria

        // Generate report header
        StringBuilder report = new StringBuilder();
        report.append(String.format("%-20s | %-15s | %-10s | %-10s | %-15s\n",
                "Applicant", "Project", "Flat Type", "Age", "Marital Status"));
        report.append("-".repeat(80)).append("\n");

        // Populate report with booking data
        
        return report.toString();
    }
    

    
}