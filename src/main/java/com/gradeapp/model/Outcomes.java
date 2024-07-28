package com.gradeapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about the course syllabus, including learning outcomes.
 */
public class Outcomes {
    private String name;
    private List<String> learningOutcomes;

    public Outcomes(String name) {
        this.name = name;
        this.learningOutcomes = new ArrayList<>();
    }

    public void addOutcome(String outcome) {
        learningOutcomes.add(outcome);
    }

    public String getName() {
        return name;
    }

    public List<String> getLearningOutcomes() {
        return learningOutcomes;
    }

    public void setLearningOutcomes(List<String> learningOutcomes) {
        this.learningOutcomes = learningOutcomes;
    }
}
