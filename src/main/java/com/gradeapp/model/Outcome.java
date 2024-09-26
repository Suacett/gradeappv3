package com.gradeapp.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents an outcome associated with assessments within the grading application.
 * Each outcome has a unique ID, name, description, and weight.
 */
public class Outcome {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final DoubleProperty weight;

    /**
     * Constructs an Outcome with the specified details.
     *
     * @param id          The unique identifier of the outcome.
     * @param name        The name of the outcome.
     * @param description A brief description of the outcome.
     * @param weight      The weight of the outcome in the overall assessment.
     */
    public Outcome(String id, String name, String description, double weight) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.weight = new SimpleDoubleProperty(weight);
    }

    // ----------------------------- Getters and Setters -----------------------------

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
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

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
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

    // ----------------------------- Utility Methods -----------------------------

    /**
     * Returns a string representation of the Outcome.
     *
     * @return A formatted string containing the outcome's details.
     */
    @Override
    public String toString() {
        return String.format("Outcome{id='%s', name='%s', description='%s', weight=%.2f}",
                getId(), getName(), getDescription(), getWeight());
    }
}
