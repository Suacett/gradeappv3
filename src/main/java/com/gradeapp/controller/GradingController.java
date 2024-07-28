package com.gradeapp.controller;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Outcomes;
import com.gradeapp.model.Student;
import com.gradeapp.model.StudentGrade;
import com.gradeapp.util.WeightedAverageGradeCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
