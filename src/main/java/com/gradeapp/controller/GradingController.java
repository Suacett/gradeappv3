package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.*;
import com.gradeapp.util.WeightedAverageGradeCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradingController {
    private WeightedAverageGradeCalculator calculator;
    private Database db;

    public GradingController() {
        this.calculator = new WeightedAverageGradeCalculator();
        this.db = new Database();
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
    public void removeGrade(Student student, Grade grade) {
        student.removeGrade(grade);
    }

    public Grade getGrade(Student student, Assessment assessment) {
        return student.getGrades().stream()
                .filter(grade -> grade.getAssessment().equals(assessment))
                .findFirst()
                .orElse(null);
    }

    public List<Grade> getStudentGrades(Student student) {
        return db.getGradesForStudent(student.getStudentId());
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

    public Grade addGrade(Student student, Assessment assessment, AssessmentPart part, double score, String feedback) {
        Grade grade = new Grade(student, assessment, part, score, feedback);
        student.addGrade(grade);
        db.saveGrade(grade);
        System.out.println("Grade added: " + grade);
        return grade;
    }

    public Grade addGrade(Student student, Assessment assessment, double score, String feedback) {
        return addGrade(student, assessment, null, score, feedback);
    }


    
    public double calculateAssessmentGrade(Student student, Assessment assessment) {
        Map<AssessmentPart, Double> partScores = new HashMap<>();
        for (AssessmentPart part : assessment.getParts()) {
            Grade partGrade = getGrade(student, part);
            if (partGrade != null) {
                partScores.put(part, partGrade.getScore());
            }
        }
        return assessment.calculateGrade(partScores);
    }

    public Map<Outcome, Double> calculateOutcomeGrades(Student student, Assessment assessment) {
        Map<Outcome, Double> outcomeGrades = new HashMap<>();
        Map<AssessmentPart, Double> partScores = new HashMap<>();
        
        for (AssessmentPart part : assessment.getParts()) {
            Grade partGrade = getGrade(student, part);
            if (partGrade != null) {
                partScores.put(part, partGrade.getScore());
            }
        }

        for (Outcome outcome : assessment.getOutcomeWeights().keySet()) {
            double outcomeGrade = assessment.calculateOutcomeGrade(outcome, partScores);
            outcomeGrades.put(outcome, outcomeGrade);
        }

        return outcomeGrades;
    }

private Grade getGrade(Student student, AssessmentPart part) {
    return student.getGrades().stream()
            .filter(grade -> grade.getAssessment().getParts().contains(part))
            .findFirst()
            .orElse(null);
}
}
