package models;

import enumeration.MaritalStatus;
import enumeration.UserType;
import java.util.ArrayList;
import java.util.List;
import models.BTOProject;

public class HDBOfficer extends User {
    private List<BTOProject> handledProjects;

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password, UserType.HDB_OFFICER);
        this.handledProjects = new ArrayList<>();
    }

    public List<BTOProject> getHandledProjects() {
        return handledProjects;
    }

    public void addHandledProject(BTOProject project) {
        handledProjects.add(project);
    }

    public void removeHandledProject(BTOProject project) {
        handledProjects.remove(project);
    }
}


