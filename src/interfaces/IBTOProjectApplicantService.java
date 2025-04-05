package interfaces;

import java.util.List;
import models.BTOProject;
import models.Applicant;

public interface IBTOProjectApplicantService {
    public List<BTOProject> getAvailableProjects(Applicant applicant);
}
