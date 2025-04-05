package controllers;

import interfaces.IBTOProjectApplicantService;
import models.Applicant;
import models.BTOProject;
import models.FlatTypeDetails;
import stores.DataStore;
import enumeration.FlatType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

public class BTOProjectApplicantService implements IBTOProjectApplicantService {

    @Override
    public List<BTOProject> getAvailableProjects(Applicant applicant) {
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getApplicationOpeningDate().isBefore(LocalDate.now()) && 
                    project.getApplicationClosingDate().isAfter(LocalDate.now()) &&
                    project.isVisible())
            .collect(Collectors.toList());
    }

    /**
     * Gets a filtered map of flat types that the applicant is eligible for
     * @param project The BTO project
     * @param applicant The applicant
     * @return Map of eligible flat types and their details
     */
    public Map<FlatType, FlatTypeDetails> getEligibleFlatTypes(BTOProject project, Applicant applicant) {
        Map<FlatType, FlatTypeDetails> allFlatTypes = project.getFlatTypes();
        Map<FlatType, FlatTypeDetails> eligibleFlatTypes = new HashMap<>();
        
        int age = applicant.getAge();
        boolean isMarried = applicant.getMaritalStatus().toString().equals("MARRIED");
        
        // Married applicants 21 and above can apply for any flat type
        if (isMarried && age >= 21) {
            return allFlatTypes;
        }
        
        // Single applicants 35 and above can only apply for 2-room
        if (!isMarried && age >= 35) {
            if (allFlatTypes.containsKey(FlatType.TWO_ROOM)) {
                eligibleFlatTypes.put(FlatType.TWO_ROOM, allFlatTypes.get(FlatType.TWO_ROOM));
            }
        }
        
        return eligibleFlatTypes;
    }
}
