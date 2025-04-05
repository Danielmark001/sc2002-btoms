package models;

import enumeration.HDBOfficerRegistrationStatus;

public class HDBOfficerRegistration {
    private String registrationId;
    private final HDBOfficer hdbOfficer;
    private final BTOProject project;
    private HDBOfficerRegistrationStatus status;

    public HDBOfficerRegistration(String registrationId, HDBOfficer hdbOfficer, BTOProject project, HDBOfficerRegistrationStatus status) {
        this.registrationId = registrationId;
        this.hdbOfficer = hdbOfficer;
        this.project = project;
        this.status = status;
    }

    public HDBOfficerRegistration(HDBOfficer hdbOfficer, BTOProject project) {
        this(generateRegistrationId(), hdbOfficer, project, HDBOfficerRegistrationStatus.PENDING);
    }

    private static String generateRegistrationId() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "REG-" + date + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public HDBOfficer getHDBOfficer() {
        return hdbOfficer;
    }

    public BTOProject getProject() {
        return project;
    }

    public HDBOfficerRegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(HDBOfficerRegistrationStatus status) {
        this.status = status;
    }
} 