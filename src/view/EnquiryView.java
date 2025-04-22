package view;

import models.Enquiry;
import java.util.List;

/**
 * View class for Enquiries
 */
public class EnquiryView {
    
    public void displayAllEnquiriesHeader() {
        System.out.println("\n===== All Enquiries =====");
    }

    public void displayProjectEnquiriesHeader() {
        System.out.println("\n===== Project Enquiries =====");
    }
    public void displayCannotModifyRepliedMessage() {
    System.out.println("\nCannot modify or delete an enquiry that has already been replied to.");
}

public void displayEditSuccess() {
    System.out.println("\nEnquiry edited successfully!");
}

public void displayDeleteSuccess() {
    System.out.println("\nEnquiry deleted successfully!");
}

public void displayDeleteConfirmation() {
    System.out.print("Are you sure you want to delete this enquiry? (yes/no): ");
}

    public void displayEnquiryList(List<Enquiry> enquiries) {
        if (enquiries.isEmpty()) {
            System.out.println("There are no enquiries in the system.");
            return;
        }
        
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println("\n" + (i + 1) + ". Enquiry ID: " + enquiry.getEnquiryId());
            System.out.println("   Project: " + enquiry.getProject().getProjectName());
            System.out.println("   Applicant: " + enquiry.getApplicant().getName() + " (" + enquiry.getApplicant().getNric() + ")");
            System.out.println("   Message: " + enquiry.getMessage());
            System.out.println("   Submitted: " + enquiry.getCreatedAt());
            if (enquiry.hasReply()) {
                System.out.println("   Reply: " + enquiry.getReply());
                System.out.println("   Replied: " + enquiry.getRepliedAt());
            } else {
                System.out.println("   Status: Pending reply");
            }
            System.out.println("----------------------------------------");
        }
    }

    public void displayNoEnquiriesMessage() {
        System.out.println("There are no enquiries for this project.");
    }

    public void displayAlreadyRepliedMessage() {
        System.out.println("\nThis enquiry has already been replied to.");
    }

    public void displayEmptyReplyWarning() {
        System.out.println("Reply cannot be empty.");
    }

    public void displayReplySuccess() {
        System.out.println("\nReply submitted successfully!");
    }

    public void displayNoProjectsMessage() {
        System.out.println("\nThere are no BTO projects available.");
    }
} 