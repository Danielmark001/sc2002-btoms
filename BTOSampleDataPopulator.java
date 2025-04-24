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
        managers.add("Sophia,T3456789K,33,Married,password");
        managers.add("Natalie,T6543210P,31,Single,password");
        managers.add("Kevin,S9876543N,42,Married,password");
        managers.add("Christopher,S2109876Q,34,Married,password");
        managers.add("Raymond,S7890123L,38,Married,password");
        managers.add("Mark,T2345678H,30,Single,password");
        managers.add("Daniel,S1234567W,25,Married,mark");
        managers.add("Jessica,S5678901G,26,Married,password");
        managers.add("Elizabeth,T1234567M,29,Single,password");
        
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
        
        // Data from BTOProjectList.csv
        projects.add("Harmony Gardens,Geylang,2-Room,45,320000.0,3-Room,50,420000.0,2025-06-15,2025-08-15,Kevin,7,");
        projects.add("Parkview Residence,Bukit Batok West,2-Room,35,330000.0,3-Room,40,430000.0,2025-08-15,2025-10-15,Christopher,6,");
        projects.add("Heritage View,Chinatown,2-Room,20,390000.0,3-Room,25,490000.0,2025-01-15,2025-03-15,Kevin,4,");
        projects.add("Binjai Hall,North Hill,2-Room,10,250000.0,3-Room,15,150000.0,2025-03-01,2025-04-30,Mark,9,Emily,Andrew");
        projects.add("Emerald Heights,Sengkang,2-Room,45,310000.0,3-Room,50,410000.0,2025-01-15,2025-03-15,Sophia,4,");
        projects.add("Garden Vista,Bishan,2-Room,40,330000.0,3-Room,45,430000.0,2025-04-15,2025-06-15,Raymond,7,");
        projects.add("Urban Oasis,Kallang,2-Room,35,350000.0,3-Room,40,450000.0,2025-08-15,2025-10-15,Elizabeth,6,");
        projects.add("Lakeside View,Jurong,2-Room,50,300000.0,3-Room,55,400000.0,2025-09-15,2025-11-15,Raymond,8,");
        projects.add("Green Haven,Upper Thomson,2-Room,30,340000.0,3-Room,35,440000.0,2025-05-15,2025-07-15,Michael,5,");
        projects.add("Pasir Ris Sands,Pasir Ris,2-Room,38,330000.0,3-Room,42,430000.0,2025-01-01,2025-02-28,Mark,5,Ryan");
        projects.add("Forest Hill,Bukit Panjang,2-Room,40,320000.0,3-Room,45,420000.0,2025-01-01,2025-02-28,Christopher,7,");
        projects.add("Highland Towers,Bukit Timah,2-Room,25,400000.0,3-Room,30,520000.0,2025-02-01,2025-03-31,Raymond,5,");
        projects.add("Sunshine Court,Simei,2-Room,40,320000.0,3-Room,45,420000.0,2025-08-01,2025-09-30,Michael,7,");
        projects.add("Rumah Daniel baru,Marina Bay Sands,2-Room,2,1000000.0,3-Room,5,5.0E7,2025-03-15,2025-04-30,Daniel,5,");
        projects.add("Waterfront Bay,Pasir Panjang,2-Room,25,380000.0,3-Room,30,480000.0,2025-09-15,2025-11-15,Natalie,4,");
        projects.add("Acacia Breeze,Yishun,2-Room,20,350000.0,3-Room,15,450000.0,2025-06-01,2025-07-31,Jessica,3,Daniel");
        projects.add("Serenity Heights,Ang Mo Kio,2-Room,40,330000.0,3-Room,45,430000.0,2025-09-01,2025-10-31,Kevin,5,");
        projects.add("Skyline Towers,Toa Payoh,2-Room,40,340000.0,3-Room,45,440000.0,2025-06-01,2025-07-31,Elizabeth,7,");
        projects.add("Meadow Springs,Sembawang,2-Room,50,290000.0,3-Room,55,390000.0,2025-02-01,2025-03-31,Natalie,8,");
        projects.add("Hillview Haven,Bukit Batok,2-Room,30,320000.0,3-Room,35,410000.0,2025-07-01,2025-08-31,Mark,5,");
        projects.add("Tranquil Gardens,Thomson,2-Room,30,360000.0,3-Room,35,460000.0,2025-01-01,2025-02-28,Elizabeth,6,");
        projects.add("Coastal Breeze,East Coast,2-Room,25,370000.0,3-Room,30,470000.0,2025-10-15,2025-12-15,Michael,5,");
        projects.add("River Valley Heights,River Valley,2-Room,20,440000.0,3-Room,25,540000.0,2025-06-01,2025-07-31,Christopher,4,");
        projects.add("Golden Heights,Hougang,2-Room,35,310000.0,3-Room,40,410000.0,2025-04-15,2025-06-15,Natalie,6,");
        projects.add("Woodlands Sanctuary,Woodlands,2-Room,40,280000.0,3-Room,45,380000.0,2025-05-01,2025-06-30,Mark,6,");
        projects.add("Boulevard Heights,Novena,2-Room,35,370000.0,3-Room,40,470000.0,2025-04-01,2025-05-31,Kevin,6,");
        projects.add("Sunset Heights,Clementi,2-Room,35,320000.0,3-Room,40,420000.0,2025-07-01,2025-08-31,Raymond,4,");
        projects.add("City Square,Bugis,2-Room,25,380000.0,3-Room,30,480000.0,2025-03-15,2025-05-15,Elizabeth,5,");
        projects.add("Eastwood Residences,Bedok,2-Room,35,350000.0,3-Room,40,450000.0,2025-04-01,2025-05-31,Jessica,5,");
        projects.add("Meadow View,Boon Lay,2-Room,25,320000.0,3-Room,19,420000.0,2025-04-01,2025-05-31,Michael,3,David");
        projects.add("Central Park,Serangoon,2-Room,30,350000.0,3-Room,35,450000.0,2025-07-01,2025-08-31,Natalie,5,");
        projects.add("Orchard Residences,Orchard,2-Room,10,500000.0,3-Room,15,650000.0,2025-09-01,2025-10-31,Sophia,2,");
        projects.add("Pearl Gardens,Kembangan,2-Room,35,340000.0,3-Room,40,440000.0,2025-08-01,2025-09-30,Daniel,7,");
        projects.add("Riverview Terrace,Punggol,2-Room,30,370000.0,3-Room,35,470000.0,2025-04-01,2025-05-31,Sophia,6,");
        projects.add("Marina Gateway,Marina South,2-Room,15,450000.0,3-Room,20,550000.0,2025-06-15,2025-08-15,Sophia,3,");
        projects.add("Sunrise Gardens,Tampines,2-Room,18,340000.0,3-Room,11,460000.0,2025-02-01,2025-03-31,Jessica,2,Daniel,David");
        projects.add("Evergreen Residence,Choa Chu Kang,2-Room,45,300000.0,3-Room,50,400000.0,2025-03-15,2025-05-15,Christopher,8,");
        projects.add("Mountain View,Upper Bukit Timah,2-Room,30,360000.0,3-Room,35,460000.0,2025-05-15,2025-07-15,Daniel,6,");
        
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
        
        // Use the BTOApplication sample data
        applications.add("20250305-e5f6g7h8,S6789012J,Parkview Residence,2-Room,Successful");
        applications.add("20250310-i9j0k1l2,S0123456P,Heritage View,null,Pending");
        applications.add("20250312-m3n4o5p6,T8901234G,Binjai Hall,3-Room,Booked");
        applications.add("20250315-q7r8s9t0,T9012345Y,Emerald Heights,null,Unsuccessful");
        applications.add("20250320-u1v2w3x4,T5678901K,Garden Vista,2-Room,Successful");
        applications.add("20250325-y5z6a7b8,S5678901H,Urban Oasis,3-Room,Booked");
        applications.add("20250328-c9d0e1f2,S6789012V,Lakeside View,null,Pending");
        applications.add("20250401-g3h4i5j6,T7890123M,Green Haven,3-Room,Successful");
        applications.add("20250405-k7l8m9n0,S8901234N,Pasir Ris Sands,2-Room,Booked");
        applications.add("20250408-o1p2q3r4,T5678901U,Forest Hill,null,Pending");
        applications.add("20250412-s5t6u7v8,S1234567A,Highland Towers,2-Room,Successful");
        applications.add("20250415-w9x0y1z2,S8901234X,Sunshine Court,null,Unsuccessful");
        applications.add("20250418-a3b4c5d6,S6789012L,Waterfront Bay,3-Room,Successful");
        applications.add("20250422-e7f8g9h0,T9012345O,Acacia Breeze,3-Room,Booked");
        
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