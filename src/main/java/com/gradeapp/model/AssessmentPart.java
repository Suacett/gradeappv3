package com.gradeapp.model;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a part (task) of an assessment within the grading application.
 * Each assessment part has its own weight, maximum score, and can be linked to multiple outcomes.
 */
public class AssessmentPart {
    private final IntegerProperty id;
    private final StringProperty name;
    private final DoubleProperty weight;
    private final DoubleProperty maxScore;
    private final DoubleProperty score;
    private Map<Outcome, Double> linkedOutcomes = new HashMap<>();

    /**
     * Constructs an AssessmentPart with the specified details.
     *
     * @param id        The unique identifier of the assessment part.
     * @param name      The name of the assessment part.
     * @param weight    The weight of the assessment part in the overall assessment.
     * @param maxScore  The maximum possible score for the assessment part.
     */
    public AssessmentPart(int id, String name, double weight, double maxScore) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.weight = new SimpleDoubleProperty(weight);
        this.maxScore = new SimpleDoubleProperty(maxScore);
        this.score = new SimpleDoubleProperty(0);
    }

    // ----------------------------- Getters and Setters -----------------------------

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public double getWeight() {
        return weight.get();
    }

    public void setWeight(double weight) {
        this.weight.set(weight);
    }

    public DoubleProperty weightProperty() {
        return weight;
    }

    public double getMaxScore() {
        return maxScore.get();
    }

    public void setMaxScore(double maxScore) {
        this.maxScore.set(maxScore);
    }

    public DoubleProperty maxScoreProperty() {
        return maxScore;
    }

    public double getScore() {
        return score.get();
    }

    public void setScore(double score) {
        this.score.set(score);
    }

    public DoubleProperty scoreProperty() {
        return score;
    }

    // ----------------------------- Utility Methods -----------------------------

    /**
     * Checks if the assessment part has been completed based on the score.
     *
     * @return `true` if the score is greater than 0, indicating completion; `false` otherwise.
     */
    public boolean isCompleted() {
        return this.getScore() > 0;
    }

    /**
     * Returns the string representation of the assessment part, which is its name.
     *
     * @return The name of the assessment part.
     */
    @Override
    public String toString() {
        return getName();
    }

    // ----------------------------- Outcome Management -----------------------------

    /**
     * Links an outcome to this assessment part with a specified weight.
     *
     * @param outcome The Outcome to link.
     * @param weight  The weight to assign to the linked outcome.
     */
    public void addLinkedOutcome(Outcome outcome, double weight) {
        linkedOutcomes.put(outcome, weight);
    }

    /**
     * Retrieves all outcomes linked to this assessment part along with their weights.
     *
     * @return A map of Outcome objects to their respective weights.
     */
    public Map<Outcome, Double> getLinkedOutcomes() {
        return linkedOutcomes;
    }

    /**
     * Retrieves the weight of a specific linked outcome.
     *
     * @param outcome The Outcome whose weight is to be retrieved.
     * @return The weight of the outcome, or 0.0 if the outcome is not linked.
     */
    public double getOutcomeWeight(Outcome outcome) {
        return linkedOutcomes.getOrDefault(outcome, 0.0);
    }
}
