package com.gradeapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an assessment structure, including its name, weight, max score, and associated tasks and outcomes.
 */
public class Assessment {
    private String name;
    private String description;
    private double weight;
    private double maxScore;
    private List<Outcomes> outcomes;
    private List<Task> tasks;
    private List<Assessment> childAssessments;
    private Assessment parentAssessment;
    private GradeBook gradeBook;

    public Assessment(String name, String description, double weight, double maxScore) {
        this.name = name;
        this.description = description;
        setWeight(weight);
        this.maxScore = maxScore;
        this.outcomes = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.childAssessments = new ArrayList<>();
    }

    // Methods for managing nested assessments
    public static void createNestedAssessment(Assessment parentAssessment, Assessment childAssessment) {
        parentAssessment.addChildAssessment(childAssessment);
    }

    public void addChildAssessment(Assessment child) {
        childAssessments.add(child);
        child.parentAssessment = this;
    }

    // Methods for managing the grade book
    public GradeBook getGradeBook() {
        return gradeBook;
    }

    public void setGradeBook(GradeBook gradeBook) {
        this.gradeBook = gradeBook;
    }

    // Methods for generating reports
    public Map<String, Object> generateDetailedReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("name", name);
        report.put("description", description);
        report.put("weight", weight);
        report.put("maxScore", maxScore);
        report.put("taskCount", tasks.size());
        report.put("tasks", tasks.stream().map(Task::getName).toList());
        report.put("outcomeCount", outcomes.size());
        report.put("outcomes", outcomes.stream().map(Outcomes::getName).toList());
        report.put("childAssessmentCount", childAssessments.size());
        report.put("childAssessments", childAssessments.stream().map(Assessment::getName).toList());

        return report;
    }

    // Methods for calculating values
    public double calculateTotalWeight() {
        return weight + childAssessments.stream()
                .mapToDouble(Assessment::calculateTotalWeight)
                .sum();
    }

    public double calculateGrade(List<Grade> grades) {
        return grades.stream()
                .filter(grade -> grade.getAssessment().equals(this))
                .mapToDouble(Grade::getScore)
                .sum();
    }

    // Methods for managing tasks
    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    // Methods for managing outcomes
    public void addOutcome(Outcomes outcome) {
        outcomes.add(outcome);
    }

    public void removeOutcome(Outcomes outcome) {
        outcomes.remove(outcome);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (weight < 0 || weight > 100) {
            throw new IllegalArgumentException("Weight must be between 0 and 100");
        }
        this.weight = weight;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Outcomes> getOutcomes() {
        return outcomes;
    }

    public List<Assessment> getChildAssessments() {
        return childAssessments;
    }

    public Assessment getParentAssessment() {
        return parentAssessment;
    }
}
