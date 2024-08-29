package com.gradeapp.model;

import javafx.beans.property.*;

public class AssessmentPart {
    private final IntegerProperty id;
    private final StringProperty name;
    private final DoubleProperty weight;
    private final DoubleProperty maxScore;

    public AssessmentPart(int id, String name, double weight, double maxScore) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.weight = new SimpleDoubleProperty(weight);
        this.maxScore = new SimpleDoubleProperty(maxScore);
    }

    // Getters and setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public double getWeight() { return weight.get(); }
    public void setWeight(double weight) { this.weight.set(weight); }
    public DoubleProperty weightProperty() { return weight; }

    public double getMaxScore() { return maxScore.get(); }
    public void setMaxScore(double maxScore) { this.maxScore.set(maxScore); }
    public DoubleProperty maxScoreProperty() { return maxScore; }

    public boolean isCompleted() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCompleted'");
    }
}