package com.gradeapp.model;

/**
 * Represents a specific task within an assessment, including its name, weight, and maximum score.
 */
public class Task {
    private String name;
    private double weight;
    private double maxScore;

    public Task(String name, double weight, double maxScore) {
        this.name = name;
        setWeight(weight);
        this.maxScore = maxScore;
    }




    public void setWeight(double weight) {
        if (weight < 0 || weight > 100) {
            throw new IllegalArgumentException("Weight must be between 0 and 100");
        }
        this.weight = weight;
    }
//getters and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }
}