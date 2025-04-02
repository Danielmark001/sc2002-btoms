package services;

import models.entity.Application;
import models.entity.Project;
import stores.DataStore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService implements IReportService {
    private DataStore dataStore;

    public ReportService() {
        this.dataStore = DataStore.getInstance();
    }

    public String generateBookingReport(String filter, String value) {
        List<Application> applications;
        
        // Filter applications based on criteria
        switch (filter.toLowerCase()) {
            case "project":
                applications = dataStore.getApplicationsByProject(value)
                        .stream()
                        .filter(app -> app.getStatus() == ApplicationStatus.BOOKED)
                        .toList();
                break;
            case "flat-type":
                applications = dataStore.getAllApplications()  
                        .stream()
                        .filter(app -> app.getStatus() == ApplicationStatus.BOOKED && 
                                       app.getBookedFlatType().name().equalsIgnoreCase(value))
                        .toList();
                break;
            case "marital-status": 
                applications = dataStore.getAllApplications()
                        .stream() 
                        .filter(app -> app.getStatus() == ApplicationStatus.BOOKED &&
                                       app.getApplicant().getMaritalStatus().name().equalsIgnoreCase(value)) 
                        .toList();
                break;
            default:
                throw new IllegalArgumentException("Invalid filter criteria: " + filter);
        }

        // Generate report header
        StringBuilder report = new StringBuilder();
        report.append(String.format("%-20s | %-15s | %-10s | %-10s | %-15s\n", 
                                    "Applicant", "Project", "Flat Type", "Age", "Marital Status"));
        report.append("-".repeat(80)).append("\n");

        // Populate report with booking data
        for (Application app : applications) {
            report.append(String.format("%-20s | %-15s | %-10s | %-10d | %-15s\n",
                                        app.getApplicant().getName(),  
                                        app.getProject().getProjectName(),
                                        app.getBookedFlatType(),
                                        app.getApplicant().calculateAge(),
                                        app.getApplicant().getMaritalStatus())); 
        }

        return report.toString();
    }

    public Map<String, Long> generateApplicationStatusReport() {
        return dataStore.getAllApplications()
                .stream()
                .collect(Collectors.groupingBy(app -> app.getStatus().name(), Collectors.counting()));
    }

    public Map<String, Long> generateProjectApplicationReport() {
        return dataStore.getAllApplications() 
                .stream()
                .collect(Collectors.groupingBy(app -> app.getProject().getProjectName(), Collectors.counting()));
    }
}