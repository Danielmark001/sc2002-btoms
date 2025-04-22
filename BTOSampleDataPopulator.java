import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to populate CSV files with the provided data for the BTO Management System
 */
public class BTOSampleDataPopulator {
    
    // File paths
    private static final String APPLICANTS_FILE = "data/ApplicantList.csv";
    private static final String HDB_MANAGERS_FILE = "data/HDBManagerList.csv";
    private static final String HDB_OFFICERS_FILE = "data/HDBOfficerList.csv";
    private static final String BTO_PROJECTS_FILE = "data/BTOProjectList.csv";
    private static final String BTO_APPLICATIONS_FILE = "data/BTOApplicationList.csv";
    private static final String HDB_OFFICER_REGISTRATIONS_FILE = "data/HDBOfficerRegistrationList.csv";
    private static final String ENQUIRY_FILE = "data/EnquiryList.csv";
    private static final String WITHDRAWAL_REQUESTS_FILE = "data/WithdrawalRequestList.csv";
    private static final String FAILED_LOGIN_FILE = "data/FailedLoginAttempts.csv";
    
    public static void main(String[] args) {
        // Populate all CSV files
        populateApplicants();
        populateHDBManagers();
        populateHDBOfficers();
        populateBTOProjects();
        populateBTOApplications();
        clearHDBOfficerRegistrations();
        clearEnquiries();
        clearWithdrawalRequests();
        clearFailedLoginAttempts();
        
        System.out.println("Sample CSV population completed successfully!");
    }
    
    /**
     * Populate applicants data
     */
    private static void populateApplicants() {
        List<String> applicants = new ArrayList<>();
        
        // CSV header
        applicants.add("Name,NRIC,Age,MaritalStatus,Password");
        
        // Data provided
        applicants.add("Lucas,T7890123W,36,Married,password");
        applicants.add("Charlotte,S6789012J,30,Single,password");
        applicants.add("Michael,S0123456P,39,Single,password");
        applicants.add("Michelle,T8901234G,32,Married,password");
        applicants.add("Mason,T9012345Y,44,Married,password");
        applicants.add("Olivia,T5678901K,36,Single,password");
        applicants.add("Charlotte,S5678901H,31,Married,password");
        applicants.add("Isabella,S6789012V,31,Married,password");
        applicants.add("Emma,T7890123M,41,Married,password");
        applicants.add("Noah,S8901234N,24,Single,password");
        applicants.add("Liam,T5678901U,28,Single,password");
        applicants.add("John,S1234567A,35,Single,password");
        applicants.add("Amelia,S8901234X,23,Single,password");
        applicants.add("William,S6789012L,29,Married,password");
        applicants.add("Ava,T9012345O,33,Married,password");
        applicants.add("Zoe,S4567890T,37,Single,password");
        applicants.add("Sarah,T7654321B,40,Married,password");
        applicants.add("Mia,T3456789S,42,Married,password");
        applicants.add("James,T2345678D,30,Married,password");
        applicants.add("Benjamin,S2345678H,38,Married,password");
        applicants.add("Ethan,S4567890J,45,Married,password");
        applicants.add("Rachel,S3456789E,25,Single,password");
        applicants.add("Alex,S7890123F,27,Single,password");
        applicants.add("Chloe,T1234567Q,26,Single,password");
        applicants.add("Daniel,S2345678R,34,Married,password");
        applicants.add("Grace,S9876543C,37,Married,password");
        applicants.add("Sophia,T3456789I,37,Single,password");
        
        // Write to file
        writeToFile(APPLICANTS_FILE, applicants);
    }
    
    /**
     * Populate HDB managers data
     */
    private static void populateHDBManagers() {
        List<String> managers = new ArrayList<>();
        
        // CSV header
        managers.add("Name,NRIC,Age,MaritalStatus,Password");
        
        // Data provided
        managers.add("Michael,T8765432F,36,Single,password");
        managers.add("Mark,T2345678H,30,Single,password");
        managers.add("Daniel,S1234567W,25,Married,mark");
        managers.add("Jessica,S5678901G,26,Married,password");
        
        // Write to file
        writeToFile(HDB_MANAGERS_FILE, managers);
    }
    
    /**
     * Populate HDB officers data
     */
    private static void populateHDBOfficers() {
        List<String> officers = new ArrayList<>();
        
        // CSV header
        officers.add("Name,NRIC,Age,MaritalStatus,Password");
        
        // Data provided
        officers.add("Jennifer,S3210987K,32,Married,password");
        officers.add("Jason,T4321098L,35,Single,password");
        officers.add("Matthew,T0987654R,38,Married,password");
        officers.add("David,T1234567J,29,Married,password");
        officers.add("Samantha,S7654321O,31,Married,password");
        officers.add("Elizabeth,S1098765S,26,Single,password");
        officers.add("Daniel,T2109876H,36,Single,password");
        officers.add("Victoria,S3210987U,39,Single,password");
        officers.add("Christopher,T6543210N,40,Married,password");
        officers.add("Emily,S6543210I,28,Single,password");
        officers.add("Rebecca,S9876543Q,30,Married,password");
        officers.add("Jonathan,T4321098V,29,Single,password");
        officers.add("Amanda,S5432109M,27,Single,password");
        officers.add("Andrew,T8765432P,34,Single,password");
        officers.add("Ryan,T2109876T,33,Married,password");
        
        // Write to file
        writeToFile(HDB_OFFICERS_FILE, officers);
    }
    
    /**
     * Populate BTO projects data
     */
    private static void populateBTOProjects() {
        List<String> projects = new ArrayList<>();
        
        // CSV header
        projects.add("ProjectName,Neighborhood,Type1,NumberOfUnitsType1,SellingPriceType1,Type2,NumberOfUnitsType2,SellingPriceType2,ApplicationOpeningDate,ApplicationClosingDate,Manager,OfficerSlot,Officers");
        
        // Data provided
        projects.add("Pasir Ris Sands,Pasir Ris,2-Room,38,330000.0,3-Room,42,430000.0,2023-01-01,2026-12-31,Mark,5,Ryan");
        projects.add("Eastwood Residences,Bedok,2-Room,35,350000.0,3-Room,40,450000.0,2026-01-01,2028-12-31,Jessica,5,");
        projects.add("Meadow View,Boon Lay,2-Room,25,320000.0,3-Room,19,420000.0,2023-01-01,2026-12-31,Michael,3,David");
        projects.add("Rumah Daniel baru,Marina Bay Sands,2-Room,2,1000000.0,3-Room,5,5.0E7,2023-01-01,2026-12-31,Daniel,5,");
        projects.add("Binjai Hall,North Hill,2-Room,10,250000.0,3-Room,15,150000.0,2023-01-01,2026-12-31,Mark,9,Emily");
        projects.add("Woodlands Sanctuary,Woodlands,2-Room,40,280000.0,3-Room,45,380000.0,2023-01-01,2026-12-31,Mark,6,");
        projects.add("Acacia Breeze,Yishun,2-Room,20,350000.0,3-Room,15,450000.0,2023-01-01,2026-12-31,Jessica,3,Daniel");
        projects.add("Sunrise Gardens,Tampines,2-Room,18,340000.0,3-Room,11,460000.0,2023-01-01,2026-12-31,Jessica,2,Daniel,David");
        projects.add("Hillview Haven,Bukit Batok,2-Room,30,320000.0,3-Room,35,410000.0,2023-01-01,2026-12-31,Mark,5,");
        
        // Write to file
        writeToFile(BTO_PROJECTS_FILE, projects);
    }
    
    /**
     * Populate BTO applications data
     */
    private static void populateBTOApplications() {
        List<String> applications = new ArrayList<>();
        
        // CSV header
        applications.add("ApplicationId,ApplicantNRIC,ProjectName,FlatType,Status");
        
        // Data provided
        applications.add("20250416-m5n6o7p8,S6789012V,Acacia Breeze,3-Room,Unsuccessful");
        applications.add("20250416-eb2a9f89,T2345678D,Binjai Hall,2-Room,Unsuccessful");
        applications.add("20250419-c56ba963,T3456789I,Rumah Daniel baru,null,Unsuccessful");
        applications.add("20250416-52c30d75,S3456789E,Sunrise Gardens,3-Room,Unsuccessful");
        applications.add("20250416-i9j0k1l2,S2345678H,Eastwood Residences,3-Room,Pending");
        applications.add("20250416-y7z8a9b0,T9012345Y,Eastwood Residences,3-Room,Pending");
        applications.add("20250416-i1j2k3l4,T5678901U,Meadow View,2-Room,Pending");
        applications.add("20250416-o1p2q3r4,S0123456P,Sunrise Gardens,3-Room,Unsuccessful");
        applications.add("20250416-u1v2w3x4,T5678901K,Woodlands Sanctuary,3-Room,Unsuccessful");
        applications.add("20250416-k7l8m9n0,T9012345O,Hillview Haven,2-Room,Pending");
        applications.add("20250416-a3b4c5d6,T3456789S,Pasir Ris Sands,2-Room,Booked");
        
        // Write to file
        writeToFile(BTO_APPLICATIONS_FILE, applications);
    }
    
    /**
     * Clear HDB officer registrations (empty file with header)
     */
    private static void clearHDBOfficerRegistrations() {
        List<String> registrations = new ArrayList<>();
        
        // CSV header
        registrations.add("RegistrationID,OfficerNRIC,ProjectName,RegistrationDate,Status");
        
        // Write to file
        writeToFile(HDB_OFFICER_REGISTRATIONS_FILE, registrations);
    }
    
    /**
     * Clear enquiries data (empty file with header)
     */
    private static void clearEnquiries() {
        List<String> enquiries = new ArrayList<>();
        
        // CSV header
        enquiries.add("EnquiryID,ApplicantNRIC,ProjectName,Message,Reply,CreatedAt,RepliedAt");
        
        // Write to file
        writeToFile(ENQUIRY_FILE, enquiries);
    }
    
    /**
     * Clear withdrawal requests data (empty file with header)
     */
    private static void clearWithdrawalRequests() {
        List<String> withdrawals = new ArrayList<>();
        
        // CSV header
        withdrawals.add("RequestId,ApplicationId,RequestedAt,IsApproved,ProcessedAt,ProcessedBy");
        
        // Write to file
        writeToFile(WITHDRAWAL_REQUESTS_FILE, withdrawals);
    }
    
    /**
     * Clear failed login attempts (empty file with header)
     */
    private static void clearFailedLoginAttempts() {
        List<String> failedLogins = new ArrayList<>();
        
        // CSV header
        failedLogins.add("Timestamp,NRIC,RoleAttempted,IPAddress");
        
        // Write to file
        writeToFile(FAILED_LOGIN_FILE, failedLogins);
    }
    
    /**
     * Write data to CSV file
     * @param filePath Path to the file
     * @param lines List of lines to write
     */
    private static void writeToFile(String filePath, List<String> lines) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
            System.out.println("Successfully wrote to " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}