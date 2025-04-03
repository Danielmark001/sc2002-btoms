package services;

import models.BTOApplication;
import models.BTOProject;
import models.User;
import enumeration.ApplicationStatus;
import enumeration.FlatType;
import enumeration.MaritalStatus;
import stores.DataStore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {
    private DataStore dataStore;
    private BTOApplicationService applicationService;
    private ProjectService projectService;
    private UserService userService;

    public ReportService() {
        this.dataStore = DataStore.getInstance();
        this.applicationService = BTOApplicationService.getInstance();
        this.projectService = ProjectService.getInstance();
        this.userService = UserService.getInstance();
    }

    /**
     * Generates a report based on project name
     * 
     * @param projectName Name of the project
     * @return Report as a string
     */
    public String generateProjectReport(String projectName) {
        BTOProject project = projectService.getProjectByName(projectName);
        if (project == null) {
            return "Project not found";
        }
        
        List<BTOApplication> applications = applicationService.getApplicationsByProject(project);
        
        StringBuilder report = new StringBuilder();
        report.append(String.format("Report for Project: %s\n", project.getProjectName()));
        report.append(String.format("Neighborhood: %s\n", project.getNeighborhood()));
        report.append(String.format("Application Period: %s to %s\n\n", 
                                   project.getApplicationOpeningDate(), 
                                   project.getApplicationClosingDate()));
        
        report.append(String.format("Total Applications: %d\n", applications.size()));
        
        // Count by status
        Map<ApplicationStatus, Long> statusCounts = applications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getStatus, Collectors.counting()));
        
        report.append("\nApplication Status:\n");
        for (ApplicationStatus status : ApplicationStatus.values()) {
            report.append(String.format("  %s: %d\n", 
                                       status.toString(), 
                                       statusCounts.getOrDefault(status, 0L)));
        }
        
        // Count by flat type
        Map<FlatType, Long> flatTypeCounts = applications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getFlatType, Collectors.counting()));
        
        report.append("\nFlat Type Distribution:\n");
        for (FlatType flatType : FlatType.values()) {
            report.append(String.format("  %s: %d\n", 
                                       flatType.toString(), 
                                       flatTypeCounts.getOrDefault(flatType, 0L)));
        }
        
        // Count by marital status
        Map<MaritalStatus, Long> maritalStatusCounts = applications.stream()
            .collect(Collectors.groupingBy(app -> app.getApplicant().getMaritalStatus(), 
                                          Collectors.counting()));
        
        report.append("\nMarital Status Distribution:\n");
        for (MaritalStatus status : MaritalStatus.values()) {
            report.append(String.format("  %s: %d\n", 
                                       status.toString(), 
                                       maritalStatusCounts.getOrDefault(status, 0L)));
        }
        
        // Count bookings
        long bookings = applications.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.BOOKED)
            .count();
        
        report.append(String.format("\nTotal Bookings: %d\n", bookings));
        
        // Booking rate
        double bookingRate = applications.isEmpty() ? 0 : (double) bookings / applications.size() * 100;
        report.append(String.format("Booking Rate: %.2f%%\n", bookingRate));
        
        return report.toString();
    }

    /**
     * Generates a report based on flat type
     * 
     * @param flatTypeStr String representation of flat type
     * @return Report as a string
     */
    public String generateFlatTypeReport(String flatTypeStr) {
        FlatType flatType;
        try {
            flatType = FlatType.valueOf(flatTypeStr.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return "Invalid flat type. Please use TWO_ROOM or THREE_ROOM.";
        }
        
        List<BTOApplication> allApplications = applicationService.getAllApplications();
        List<BTOApplication> filteredApplications = allApplications.stream()
            .filter(app -> app.getFlatType() == flatType)
            .collect(Collectors.toList());
        
        StringBuilder report = new StringBuilder();
        report.append(String.format("Report for Flat Type: %s\n\n", flatType.toString()));
        
        report.append(String.format("Total Applications: %d\n", filteredApplications.size()));
        
        // Group by project
        Map<BTOProject, List<BTOApplication>> projectGroups = filteredApplications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getProject));
        
        report.append("\nDistribution by Project:\n");
        for (Map.Entry<BTOProject, List<BTOApplication>> entry : projectGroups.entrySet()) {
            report.append(String.format("  %s: %d applications\n", 
                                       entry.getKey().getProjectName(), 
                                       entry.getValue().size()));
        }
        
        // Count by status
        Map<ApplicationStatus, Long> statusCounts = filteredApplications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getStatus, Collectors.counting()));
        
        report.append("\nApplication Status:\n");
        for (ApplicationStatus status : ApplicationStatus.values()) {
            report.append(String.format("  %s: %d\n", 
                                       status.toString(), 
                                       statusCounts.getOrDefault(status, 0L)));
        }
        
        // Count by marital status
        Map<MaritalStatus, Long> maritalStatusCounts = filteredApplications.stream()
            .collect(Collectors.groupingBy(app -> app.getApplicant().getMaritalStatus(), 
                                          Collectors.counting()));
        
        report.append("\nMarital Status Distribution:\n");
        for (MaritalStatus status : MaritalStatus.values()) {
            report.append(String.format("  %s: %d\n", 
                                       status.toString(), 
                                       maritalStatusCounts.getOrDefault(status, 0L)));
        }
        
        // Count bookings
        long bookings = filteredApplications.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.BOOKED)
            .count();
        
        report.append(String.format("\nTotal Bookings: %d\n", bookings));
        
        // Booking rate
        double bookingRate = filteredApplications.isEmpty() ? 0 : 
                            (double) bookings / filteredApplications.size() * 100;
        report.append(String.format("Booking Rate: %.2f%%\n", bookingRate));
        
        return report.toString();
    }

    /**
     * Generates a report based on marital status
     * 
     * @param maritalStatusStr String representation of marital status
     * @return Report as a string
     */
    public String generateMaritalStatusReport(String maritalStatusStr) {
        MaritalStatus maritalStatus;
        try {
            maritalStatus = MaritalStatus.valueOf(maritalStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Invalid marital status. Please use SINGLE or MARRIED.";
        }
        
        List<BTOApplication> allApplications = applicationService.getAllApplications();
        List<BTOApplication> filteredApplications = allApplications.stream()
            .filter(app -> app.getApplicant().getMaritalStatus() == maritalStatus)
            .collect(Collectors.toList());
        
        StringBuilder report = new StringBuilder();
        report.append(String.format("Report for Marital Status: %s\n\n", maritalStatus.toString()));
        
        report.append(String.format("Total Applications: %d\n", filteredApplications.size()));
        
        // Count by flat type
        Map<FlatType, Long> flatTypeCounts = filteredApplications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getFlatType, Collectors.counting()));
        
        report.append("\nFlat Type Distribution:\n");
        for (FlatType flatType : FlatType.values()) {
            report.append(String.format("  %s: %d\n", 
                                       flatType.toString(), 
                                       flatTypeCounts.getOrDefault(flatType, 0L)));
        }
        
        // Count by status
        Map<ApplicationStatus, Long> statusCounts = filteredApplications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getStatus, Collectors.counting()));
        
        report.append("\nApplication Status:\n");
        for (ApplicationStatus status : ApplicationStatus.values()) {
            report.append(String.format("  %s: %d\n", 
                                       status.toString(), 
                                       statusCounts.getOrDefault(status, 0L)));
        }
        
        // Group by project
        Map<BTOProject, List<BTOApplication>> projectGroups = filteredApplications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getProject));
        
        report.append("\nDistribution by Project:\n");
        for (Map.Entry<BTOProject, List<BTOApplication>> entry : projectGroups.entrySet()) {
            report.append(String.format("  %s: %d applications\n", 
                                       entry.getKey().getProjectName(), 
                                       entry.getValue().size()));
        }
        
        // Count bookings
        long bookings = filteredApplications.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.BOOKED)
            .count();
        
        report.append(String.format("\nTotal Bookings: %d\n", bookings));
        
        // Booking rate
        double bookingRate = filteredApplications.isEmpty() ? 0 : 
                            (double) bookings / filteredApplications.size() * 100;
        report.append(String.format("Booking Rate: %.2f%%\n", bookingRate));
        
        // Age distribution (binned)
        Map<String, Long> ageBins = filteredApplications.stream()
            .collect(Collectors.groupingBy(app -> {
                int age = app.getApplicant().calculateAge();
                if (age < 30) return "< 30";
                if (age < 40) return "30-39";
                if (age < 50) return "40-49";
                return "50+";
            }, Collectors.counting()));
        
        report.append("\nAge Distribution:\n");
        report.append(String.format("  < 30: %d\n", ageBins.getOrDefault("< 30", 0L)));
        report.append(String.format("  30-39: %d\n", ageBins.getOrDefault("30-39", 0L)));
        report.append(String.format("  40-49: %d\n", ageBins.getOrDefault("40-49", 0L)));
        report.append(String.format("  50+: %d\n", ageBins.getOrDefault("50+", 0L)));
        
        return report.toString();
    }

    /**
     * Generates a report on application status across all projects
     * 
     * @return Report as a string
     */
    public String generateApplicationStatusReport() {
        List<BTOApplication> allApplications = applicationService.getAllApplications();
        
        StringBuilder report = new StringBuilder();
        report.append("Application Status Report\n\n");
        
        report.append(String.format("Total Applications: %d\n", allApplications.size()));
        
        // Count by status
        Map<ApplicationStatus, List<BTOApplication>> statusGroups = allApplications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getStatus));
        
        report.append("\nApplication Status Breakdown:\n");
        for (ApplicationStatus status : ApplicationStatus.values()) {
            List<BTOApplication> apps = statusGroups.getOrDefault(status, List.of());
            report.append(String.format("  %s: %d (%.2f%%)\n", 
                                       status.toString(), 
                                       apps.size(),
                                       allApplications.isEmpty() ? 0 : 
                                           (double) apps.size() / allApplications.size() * 100));
        }
        
        // Analyze by project
        Map<BTOProject, Map<ApplicationStatus, Long>> projectStatusCounts = allApplications.stream()
            .collect(Collectors.groupingBy(
                BTOApplication::getProject,
                Collectors.groupingBy(BTOApplication::getStatus, Collectors.counting())
            ));
        
        report.append("\nStatus by Project:\n");
        for (Map.Entry<BTOProject, Map<ApplicationStatus, Long>> entry : projectStatusCounts.entrySet()) {
            BTOProject project = entry.getKey();
            Map<ApplicationStatus, Long> counts = entry.getValue();
            
            report.append(String.format("  %s:\n", project.getProjectName()));
            for (ApplicationStatus status : ApplicationStatus.values()) {
                report.append(String.format("    %s: %d\n", 
                                           status.toString(), 
                                           counts.getOrDefault(status, 0L)));
            }
        }
        
        // Withdrawal analysis
        long withdrawalRequests = allApplications.stream()
            .filter(BTOApplication::isWithdrawalRequested)
            .count();
        
        report.append(String.format("\nTotal Withdrawal Requests: %d (%.2f%%)\n", 
                                   withdrawalRequests,
                                   allApplications.isEmpty() ? 0 : 
                                       (double) withdrawalRequests / allApplications.size() * 100));
        
        // Success rate
        long successfulApps = statusGroups.getOrDefault(ApplicationStatus.SUCCESSFUL, List.of()).size();
        long pendingApps = statusGroups.getOrDefault(ApplicationStatus.PENDING, List.of()).size();
        
        double successRate = (pendingApps + successfulApps) == 0 ? 0 : 
                            (double) successfulApps / (pendingApps + successfulApps) * 100;
        
        report.append(String.format("Application Success Rate: %.2f%%\n", successRate));
        
        // Booking rate
        long bookedApps = statusGroups.getOrDefault(ApplicationStatus.BOOKED, List.of()).size();
        
        double bookingRate = successfulApps == 0 ? 0 : (double) bookedApps / successfulApps * 100;
        
        report.append(String.format("Booking Rate: %.2f%%\n", bookingRate));
        
        return report.toString();
    }

    /**
     * Generates a comprehensive report on project applications
     * 
     * @return Report as a string
     */
    public String generateProjectApplicationReport() {
        List<BTOProject> allProjects = projectService.getAllProjects();
        List<BTOApplication> allApplications = applicationService.getAllApplications();
        
        StringBuilder report = new StringBuilder();
        report.append("Project Application Report\n\n");
        
        report.append(String.format("Total Projects: %d\n", allProjects.size()));
        report.append(String.format("Total Applications: %d\n", allApplications.size()));
        
        // Group applications by project
        Map<BTOProject, List<BTOApplication>> projectApplications = allApplications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getProject));
        
        report.append("\nProjects by Application Count:\n");
        projectApplications.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
            .forEach(entry -> {
                BTOProject project = entry.getKey();
                List<BTOApplication> apps = entry.getValue();
                
                report.append(String.format("  %s: %d applications\n", 
                                           project.getProjectName(), 
                                           apps.size()));
            });
        
        // Flat type demand across all projects
        Map<FlatType, Long> flatTypeCounts = allApplications.stream()
            .collect(Collectors.groupingBy(BTOApplication::getFlatType, Collectors.counting()));
        
        report.append("\nOverall Flat Type Demand:\n");
        for (FlatType flatType : FlatType.values()) {
            report.append(String.format("  %s: %d (%.2f%%)\n", 
                                       flatType.toString(), 
                                       flatTypeCounts.getOrDefault(flatType, 0L),
                                       allApplications.isEmpty() ? 0 : 
                                           (double) flatTypeCounts.getOrDefault(flatType, 0L) / 
                                           allApplications.size() * 100));
        }
        
        // Projects with highest booking rates
        report.append("\nProjects by Booking Rate:\n");
        projectApplications.entrySet().stream()
            .filter(entry -> !entry.getValue().isEmpty())
            .map(entry -> {
                BTOProject project = entry.getKey();
                List<BTOApplication> apps = entry.getValue();
                
                long bookedApps = apps.stream()
                    .filter(app -> app.getStatus() == ApplicationStatus.BOOKED)
                    .count();
                
                double bookingRate = (double) bookedApps / apps.size() * 100;
                
                return Map.entry(project, bookingRate);
            })
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> {
                report.append(String.format("  %s: %.2f%%\n", 
                                           entry.getKey().getProjectName(), 
                                           entry.getValue()));
            });
        
        // Application status summary by project
        report.append("\nApplication Status by Project:\n");
        for (BTOProject project : allProjects) {
            List<BTOApplication> apps = projectApplications.getOrDefault(project, List.of());
            
            if (apps.isEmpty()) {
                report.append(String.format("  %s: No applications\n", project.getProjectName()));
                continue;
            }
            
            report.append(String.format("  %s:\n", project.getProjectName()));
            
            Map<ApplicationStatus, Long> statusCounts = apps.stream()
                .collect(Collectors.groupingBy(BTOApplication::getStatus, Collectors.counting()));
            
            for (ApplicationStatus status : ApplicationStatus.values()) {
                long count = statusCounts.getOrDefault(status, 0L);
                double percentage = (double) count / apps.size() * 100;
                
                report.append(String.format("    %s: %d (%.2f%%)\n", 
                                           status.toString(), 
                                           count,
                                           percentage));
            }
        }
        
        // Project popularity by neighborhood
        Map<String, List<BTOApplication>> neighborhoodApplications = allApplications.stream()
            .collect(Collectors.groupingBy(app -> app.getProject().getNeighborhood()));
        
        report.append("\nNeighborhood Popularity:\n");
        neighborhoodApplications.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
            .forEach(entry -> {
                String neighborhood = entry.getKey();
                List<BTOApplication> apps = entry.getValue();
                
                report.append(String.format("  %s: %d applications (%.2f%%)\n", 
                                           neighborhood, 
                                           apps.size(),
                                           allApplications.isEmpty() ? 0 : 
                                               (double) apps.size() / allApplications.size() * 100));
            });
        
        return report.toString();
    }

    /**
     * Generates a booking report based on filter and value
     * @param filter Filter category
     * @param value Filter value
     * @return Formatted report
     */
    public String generateBookingReport(String filter, String value) {
        switch (filter.toLowerCase()) {
            case "project":
                return generateProjectReport(value);
            case "flat-type":
                return generateFlatTypeReport(value);
            case "marital-status":
                return generateMaritalStatusReport(value);
            default:
                return "Invalid filter. Please use project, flat-type, or marital-status.";
        }
    }
}