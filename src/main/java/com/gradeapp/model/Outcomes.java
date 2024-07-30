package com.gradeapp.model;

import java.util.*;

public class Outcomes {
    private String id;
    private String name;
    private String description;
    private List<String> learningOutcomes;
    private Map<Assessment, Double> linkedAssessments;
    private Map<Student, Double> studentAchievements;

    public Outcomes(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.learningOutcomes = new ArrayList<>();
        this.linkedAssessments = new HashMap<>();
        this.studentAchievements = new HashMap<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getLearningOutcomes() { return new ArrayList<>(learningOutcomes); }

    public void addLearningOutcome(String outcome) {
        learningOutcomes.add(outcome);
    }

    public void linkAssessment(Assessment assessment, double weight) {
        linkedAssessments.put(assessment, weight);
    }

    public void updateStudentAchievement(Student student, double achievement) {
        studentAchievements.put(student, achievement);
    }

    public double getStudentAchievement(Student student) {
        return studentAchievements.getOrDefault(student, 0.0);
    }

    public Map<Assessment, Double> getLinkedAssessments() {
        return new HashMap<>(linkedAssessments);
    }

    public double aggregateAchievement() {
        return studentAchievements.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}

