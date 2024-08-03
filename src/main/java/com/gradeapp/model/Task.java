package com.gradeapp.model;

public class Task {
    private String name;
    private String description;
    private double weight;
    private double maxScore;
    private Assessment assessment;
    private boolean completed;
    private double score;

    public Task(String name, String description, double weight, double maxScore) {
        this.name = name;
        this.description = description;
        setWeight(weight);
        this.maxScore = maxScore;
        this.completed = false;
    }

    // Methods for managing the task
    public double getWeightedScore() {
        return (score / maxScore) * weight;
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

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        if (score < 0 || score > maxScore) {
            throw new IllegalArgumentException("Score must be between 0 and max score");
        }
        this.score = score;
        this.completed = true;
    }
}
