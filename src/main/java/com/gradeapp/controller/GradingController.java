package com.gradeapp.controller;

import com.gradeapp.model.Assessment;
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

    // Methods for calculating grades and achievements
    public double calculateOverallGrade(Student student, Assessment assessment) {
        return calculator.calculateWeightedAverage(student.getGrades(), assessment);
    }

    public Map<Outcomes, Double> calculateOutcomeAchievement(Student student) {
        Map<Outcomes, Double> achievements = new HashMap<>();
        for (Outcomes outcome : student.getCourse().getOutcomes()) {
            double achievement = calculator.calculateOutcomeAchievement(student, outcome);
            achievements.put(outcome, achievement);
        }
        return achievements;
    }

    public double calculateWeightedAverage(List<? extends Grade> grades, Assessment assessment) {
        return calculator.calculateWeightedAverage(grades, assessment);
    }

    // Methods for managing grades
    public StudentGrade addGrade(Student student, Assessment assessment, double score, String feedback) {
        StudentGrade grade = new StudentGrade(student.getName(), assessment.getName(), score);
        grade.setFeedback(feedback);
        student.addGrade(assessment, score, feedback);
        student.getCourse().getGradeBook().addGrade(grade);
        return grade;
    }

    public List<StudentGrade> getStudentGrades(Student student) {
        return student.getGrades();
    }

    public void saveGrades(List<StudentGrade> grades) {
        // Implementation for saving grades
        // This method should update the grades in the database or persistent storage(?)
    }
}
