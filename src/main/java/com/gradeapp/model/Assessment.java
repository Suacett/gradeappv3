package com.gradeapp.model;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Represents an assessment within the grading application.
 * An assessment can have multiple parts and is associated with various outcomes.
 */
public class Assessment {
    private int id;
    private StringProperty nameProperty;
    private String description;
    private double weight;
    private double maxScore;
    private ObservableList<AssessmentPart> parts;
    private ObservableMap<Outcome, Double> outcomeWeights;
    private ObservableList<Assessment> childAssessments;
    private Assessment parentAssessment;

    /**
     * Constructs an Assessment with the specified details.
     *
     * @param id          The unique identifier of the assessment.
     * @param name        The name of the assessment.
     * @param description A brief description of the assessment.
     * @param weight      The weight of the assessment in the overall grade.
     * @param maxScore    The maximum possible score for the assessment.
     */
    public Assessment(int id, String name, String description, double weight, double maxScore) {
        this.id = id;
        this.nameProperty = new SimpleStringProperty(name);
        this.description = description;
        setWeight(weight);
        this.maxScore = maxScore;
        this.parts = FXCollections.observableArrayList();
        this.outcomeWeights = FXCollections.observableHashMap();
        this.childAssessments = FXCollections.observableArrayList();
    }

    /**
     * Constructs an Assessment without specifying an ID.
     * Useful for creating new assessments before assigning an ID.
     *
     * @param name        The name of the assessment.
     * @param description A brief description of the assessment.
     * @param weight      The weight of the assessment in the overall grade.
     * @param maxScore    The maximum possible score for the assessment.
     */
    public Assessment(String name, String description, double weight, double maxScore) {
        this(-1, name, description, weight, maxScore); // Use -1 as a temporary id
        this.outcomeWeights = FXCollections.observableHashMap();
    }

    // ----------------------------- Getters and Setters -----------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return nameProperty.get();
    }

    public void setName(String name) {
        this.nameProperty.set(name);
    }

    public StringProperty nameProperty() {
        return nameProperty;
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

    public Map<Outcome, Double> getOutcomes() {
        return new HashMap<>(outcomeWeights);
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

    // ----------------------------- Calculations -----------------------------

    /**
     * Calculates the total weight of the assessment, including its child assessments.
     *
     * @return The total weight.
     */
    public double calculateTotalWeight() {
        return weight + childAssessments.stream()
                .mapToDouble(Assessment::calculateTotalWeight)
                .sum();
    }

    /**
     * Calculates the grade based on the provided part scores.
     *
     * @param partScores A map of AssessmentPart to the student's score for that part.
     * @return The calculated grade.
     */
    public double calculateGrade(Map<AssessmentPart, Double> partScores) {
        return parts.stream()
                .mapToDouble(part -> {
                    double score = partScores.getOrDefault(part, 0.0);
                    return (score / part.getMaxScore()) * part.getWeight();
                })
                .sum();
    }

    /**
     * Calculates the total score of the assessment based on its parts.
     *
     * @return The total score.
     */
    public double calculateTotalScore() {
        return parts.stream().mapToDouble(part -> part.getWeight() * part.getMaxScore()).sum();
    }

    /**
     * Calculates the grade based on the provided part scores.
     *
     * @param partScores An observable map of AssessmentPart to the student's score for that part.
     * @return The calculated grade.
     */
    public double calculateGrade(ObservableMap<AssessmentPart, Double> partScores) {
        return parts.stream()
                .mapToDouble(part -> (partScores.getOrDefault(part, 0.0) / part.getMaxScore()) * part.getWeight())
                .sum();
    }

    /**
     * Calculates the outcome-specific grade based on part scores.
     *
     * @param outcome    The Outcome to calculate the grade for.
     * @param partScores A map of AssessmentPart to the student's score for that part.
     * @return The outcome-specific grade as a percentage.
     */
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

    // ----------------------------- Task Management -----------------------------

    /**
     * Adds a new task (assessment part) to the assessment.
     *
     * @param task The AssessmentPart to add.
     */
    public void addTask(AssessmentPart task) {
        this.parts.add(task);
    }

    /**
     * Removes a task (assessment part) from the assessment.
     *
     * @param task The AssessmentPart to remove.
     */
    public void removeTask(AssessmentPart task) {
        this.parts.remove(task);
    }

    // ----------------------------- toString Override -----------------------------

    /**
     * Returns the string representation of the assessment, which is its name.
     *
     * @return The name of the assessment.
     */
    @Override
    public String toString() {
        return getName();
    }
}
