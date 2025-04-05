package interfaces;

import java.util.List;
import models.BTOProject;
import models.User;

public interface IBTOProjectApplicantService {
    public List<BTOProject> getAvailableProjects(User applicant);
}
