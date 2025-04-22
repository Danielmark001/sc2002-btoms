package view;

import models.HDBOfficerRegistration;
import java.util.List;
/**
 * Class for viewing HDB Officer Registrations
 */
public class HDBOfficerRegistrationView {
    
    public void displayRegistrationsHeader() {
        System.out.println("\n===== HDB Officer Registrations =====");
    }

    public void displayRegistrationList(List<HDBOfficerRegistration> registrations) {
        if (registrations.isEmpty()) {
            System.out.println("No HDB Officer registrations found for your projects.");
            return;
        }
        
        for (int i = 0; i < registrations.size(); i++) {
            HDBOfficerRegistration registration = registrations.get(i);
            System.out.println("\nRegistration " + (i + 1) + ":");
            System.out.println("Registration ID: " + registration.getRegistrationId());
            System.out.println("HDB Officer: " + registration.getHDBOfficer().getName() + " (" + registration.getHDBOfficer().getNric() + ")");
            System.out.println("Project: " + registration.getProject().getProjectName());
            System.out.println("Status: " + registration.getStatus().getDisplayName());
        }
    }

    public void displayPendingRegistrationsHeader() {
        System.out.println("\n===== Approve/Reject HDB Officer Registration =====");
    }

    public void displayNoPendingRegistrations() {
        System.out.println("No pending HDB Officer registrations found for your projects.");
    }

    public void displayNoProjectsMessage() {
        System.out.println("You have not created any projects.");
    }

    public void displayNoSlotsAvailable() {
        System.out.println("Project has no available HDB Officer slots.");
    }

    public void displayRegistrationApprovalSuccess() {
        System.out.println("Registration approved successfully!");
    }

    public void displayRegistrationRejection() {
        System.out.println("Registration rejected.");
    }
} 