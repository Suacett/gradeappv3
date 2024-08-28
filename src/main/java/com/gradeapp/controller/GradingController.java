package com.gradeapp.controller;

import com.gradeapp.model.*;
import com.gradeapp.util.WeightedAverageGradeCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradingController {
    private WeightedAverageGradeCalculator calculator;

    public GradingController() {
        this.calculator = new WeightedAverageGradeCalculator();
    }

    // Methods for calculating grades and achievements
    public double calculateOverallGrade(Student student, Assessment assessment) {
        return calculator.calculateWeightedAverage(student.getGrades(), assessment);
    }

    public Map<Outcome, Double> calculateOutcomeAchievement(Student student) {
        Map<Outcome, Double> achievements = new HashMap<>();
        for (Outcome outcome : student.getCourse().getOutcomes()) {
            double achievement = calculator.calculateOutcomeAchievement(student, outcome);
            achievements.put(outcome, achievement);
        }
        return achievements;
    }

    public double calculateWeightedAverage(List<Grade> grades, Assessment assessment) {
        return calculator.calculateWeightedAverage(grades, assessment);
    }

    // Methods for managing grades
    public void addGrade(Student student, Assessment assessment, double score, String feedback) {
        student.addGrade(assessment, score, feedback);
    }

    public void removeGrade(Student student, Grade grade) {
        student.removeGrade(grade);
    }

    public List<Grade> getStudentGrades(Student student) {
        return student.getGrades();
    }

    public Grade getGrade(Student student, Assessment assessment) {
        return student.getGrades().stream()
                .filter(grade -> grade.getAssessment().equals(assessment))
                .findFirst()
                .orElse(null);
    }

    public double getAverageGradeForStudent(Student student) {
        return student.calculateOverallPerformance();
    }

    public Map<Student, Double> getAllStudentAverages(Course course) {
        Map<Student, Double> averages = new HashMap<>();
        for (Student student : course.getStudents()) {
            averages.put(student, student.calculateOverallPerformance());
        }
        return averages;
    }
}