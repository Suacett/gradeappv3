package com.gradeapp.model;


import java.util.ArrayList;
import java.util.List;

public class Assessment {
    private String name;
    private double weight;
    private double maxScore;
    private List<Outcomes> outcomes;
    private List<Task> tasks;

    public Assessment(String name, double weight, double maxScore) {
        this.name = name;
        this.weight = weight;
        this.maxScore = maxScore;
        this.tasks = new ArrayList<>();
        this.outcomes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
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

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Outcomes> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<Outcomes> outcomes) {
        this.outcomes = outcomes;
    }
}
