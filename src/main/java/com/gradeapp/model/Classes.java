package com.gradeapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Classes {
    private String name;
    private String classId;
    private List<Classes> classes;
    private List<Assessment> assessments;
    private Set<Outcome> outcomes;

    public Classes(String name, String classId) {
        this.name = name;
        this.classId = classId;
        this.classes = new ArrayList<>();
        this.assessments = new ArrayList<>();
        this.outcomes = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getClassId() {
        return classId;
    }

    public List<Classes> getClasses() {
        return classes;
    }

    public List<Assessment> getAssessments() {
        return Collections.unmodifiableList(assessments);
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    public Set<Outcome> getOutcomes() {
        return Collections.unmodifiableSet(outcomes);
    }

    public void addOutcome(Outcome outcome) {
        outcomes.add(outcome);
    }

}
