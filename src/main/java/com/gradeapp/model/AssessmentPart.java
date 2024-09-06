package com.gradeapp.model;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AssessmentPart {
    private final IntegerProperty id;
    private final StringProperty name;
    private final DoubleProperty weight;
    private final DoubleProperty maxScore;
    private final DoubleProperty score;
    private Map<Outcome, Double> linkedOutcomes = new HashMap<>();

    public AssessmentPart(int id, String name, double weight, double maxScore) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.weight = new SimpleDoubleProperty(weight);
        this.maxScore = new SimpleDoubleProperty(maxScore);
        this.score = new SimpleDoubleProperty(0);

    }

    // Getters and setters
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

    public boolean isCompleted() {
        return this.getScore() > 0;
    }

    @Override
    public String toString() {
        return getName();

    }

    public void addLinkedOutcome(Outcome outcome, double weight) {
        linkedOutcomes.put(outcome, weight);
    }

    public Map<Outcome, Double> getLinkedOutcomes() {
        return linkedOutcomes;
    }

    public double getOutcomeWeight(Outcome outcome) {
        return linkedOutcomes.getOrDefault(outcome, 0.0);
    }

}