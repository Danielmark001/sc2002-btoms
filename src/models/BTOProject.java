package models;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

import enumeration.FlatType;

public class BTOProject {

    private String projectName;
    private String neighborhood;

    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;

    private Map<FlatType, FlatTypeDetails> flatTypes;

    private HDBManager hdbManager;

    private int hdbOfficerSlots;
    private List<HDBOfficer> hdbOfficers;

    private boolean visible;

}