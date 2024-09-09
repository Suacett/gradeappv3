package com.gradeapp.model;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Assessment {
    private int id;
    private String name;
    private String description;
    private double weight;
    private double maxScore;
    private ObservableList<AssessmentPart> parts;
    private ObservableMap<Outcome, Double> outcomeWeights;
    private ObservableList<Assessment> childAssessments;
    private Assessment parentAssessment;

    // Constructor with id
    public Assessment(int id, String name, String description, double weight, double maxScore) {
        this.id = id;
        this.name = name;
        this.description = description;
        setWeight(weight);
        this.maxScore = maxScore;
        this.parts = FXCollections.observableArrayList();
        this.outcomeWeights = FXCollections.observableHashMap();
        this.childAssessments = FXCollections.observableArrayList();
    }

    // Constructor without id
    public Assessment(String name, String description, double weight, double maxScore) {
        this(-1, name, description, weight, maxScore); // Use -1 as a temporary id
        this.outcomeWeights = FXCollections.observableHashMap();
    }

    // Getters and setters for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Methods for managing nested assessments
    public static void createNestedAssessment(Assessment parentAssessment, Assessment childAssessment) {
        parentAssessment.addChildAssessment(childAssessment);
    }

    public void addChildAssessment(Assessment child) {
        childAssessments.add(child);
        child.parentAssessment = this;
    }

    // Methods for generating reports
    public Map<String, Object> generateDetailedReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("id", id);
        report.put("name", name);
        report.put("description", description);
        report.put("weight", weight);
        report.put("maxScore", maxScore);
        report.put("partsCount", parts.size());
        report.put("parts", parts.stream().map(AssessmentPart::getName).toList());
        report.put("outcomeCount", outcomeWeights.size());
        report.put("outcomes", outcomeWeights.keySet().stream().map(Outcome::getName).toList());
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

    public double calculateGrade(Map<AssessmentPart, Double> partScores) {
        return parts.stream()
                .mapToDouble(part -> {
                    double score = partScores.getOrDefault(part, 0.0);
                    return (score / part.getMaxScore()) * part.getWeight();
                })
                .sum();
    }

    // Methods for managing tasks
    public void addTask(AssessmentPart task) {
        this.parts.add(task);
    }

    public void removeTask(AssessmentPart task) {
        this.parts.remove(task);
    }

    public Map<Outcome, Double> getOutcomes() {
        return new HashMap<>(outcomeWeights);
    }

    // Getters and Setters for other fields
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

    public ObservableList<AssessmentPart> getTasks() {
        return parts;
    }

    public ObservableList<AssessmentPart> getParts() {
        return parts;
    }

    public void addPart(AssessmentPart part) {
        if (this.parts == null) {
            this.parts = FXCollections.observableArrayList();
        }
        this.parts.add(part);
    }

    public void removePart(AssessmentPart part) {
        this.parts.remove(part);
    }

    public void setOutcomeWeight(Outcome outcome, double weight) {
        this.outcomeWeights.put(outcome, weight);
    }

    public ObservableList<Assessment> getChildAssessments() {
        return childAssessments;
    }

    public void removeChildAssessment(Assessment child) {
        this.childAssessments.remove(child);
        child.setParentAssessment(null);
    }

    public Assessment getParentAssessment() {
        return parentAssessment;
    }

    public void setParentAssessment(Assessment parentAssessment) {
        this.parentAssessment = parentAssessment;
    }

    public void addOutcome(Outcome outcome, double weight) {
        this.outcomeWeights.put(outcome, weight);
    }

    public void removeOutcome(Outcome outcome) {
        this.outcomeWeights.remove(outcome);
    }

    public void updateOutcomeWeight(Outcome outcome, double weight) {
        this.outcomeWeights.put(outcome, weight);
    }

    public double getOutcomeWeight(Outcome outcome) {
        return this.outcomeWeights.getOrDefault(outcome, 0.0);
    }

    public Map<Outcome, Double> getOutcomeWeights() {
        return new HashMap<>(outcomeWeights);
    }

    // Method to calculate total score
    public double calculateTotalScore() {
        return parts.stream().mapToDouble(part -> part.getWeight() * part.getMaxScore()).sum();
    }

    // Method to calculate grade based on part scores
    public double calculateGrade(ObservableMap<AssessmentPart, Double> partScores) {
        return parts.stream()
                .mapToDouble(part -> (partScores.getOrDefault(part, 0.0) / part.getMaxScore()) * part.getWeight())
                .sum();
    }

    public double calculateOutcomeGrade(Outcome outcome, Map<AssessmentPart, Double> partScores) {
        double totalScore = 0.0;
        double maxPossibleScore = 0.0;

        for (AssessmentPart part : parts) {
            double partScore = partScores.getOrDefault(part, 0.0);
            double partWeight = part.getWeight() / 100.0;
            double outcomeWeight = outcomeWeights.getOrDefault(outcome, 0.0) / 100.0; 

            totalScore += partScore * partWeight * outcomeWeight;
            maxPossibleScore += part.getMaxScore() * partWeight * outcomeWeight;
        }

        return maxPossibleScore > 0 ? (totalScore / maxPossibleScore) * 100 : 0;
    }

    @Override
    public String toString() {
        return getName();
    }
}
