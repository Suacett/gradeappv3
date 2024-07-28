package com.gradeapp.controller;

import com.gradeapp.model.*;
import com.gradeapp.util.WeightedAverageGradeCalculator;
import java.util.*;

public class GradingController {
    private WeightedAverageGradeCalculator calculator;

    public GradingController() {
        this.calculator = new WeightedAverageGradeCalculator();
    }

    public double calculateOverallGrade(Student student, Assessment assessment) {
        return calculateWeightedAverage(student, assessment);
    }

    public Map<String, Double> calculateOutcomeAchievement(Student student) {
        Map<String, Double> achievements = new HashMap<>();
        for (Outcomes outcome : student.getCourse().getOutcomes()) {
            double achievement = calculator.calculateOutcomeAchievement(student, outcome);
            achievements.put(outcome.getName(), achievement);
        }
        return achievements;
    }

    private double calculateWeightedAverage(Student student, Assessment assessment) {
        return calculator.calculateWeightedAverage(student.getGrades(), assessment);
    }

    public void saveGrades(List<StudentGrade> grades) {
        // Implement the logic to save grades to the database or file
    }
}
