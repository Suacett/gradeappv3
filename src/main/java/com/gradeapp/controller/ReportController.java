package com.gradeapp.controller;

import com.gradeapp.model.*;
import com.gradeapp.util.Calculator;

import java.util.*;

/**
 * Handles the generation and formatting of different types of reports.
 */

public class ReportController {
    public Map<String, Object> generateClassReport() {
        Map<String, Object> report = new HashMap<>();
        List<Grade> allGrades = new ArrayList<>();

        for (Student student : getCurrentCourse().getStudents()) {
            allGrades.addAll(student.getGrades());
        }

        report.put("courseName", getCurrentCourse().getName());
        report.put("studentCount", getCurrentCourse().getStudents().size());
        report.put("meanGrade", String.format("%.2f", Calculator.calculateMean(allGrades)));
        report.put("standardDeviation", String.format("%.2f", Calculator.calculateStandardDeviation(allGrades)));
        report.put("highestGrade", getHighestGradeDetails(allGrades));
        report.put("lowestGrade", getLowestGradeDetails(allGrades));

        return report;
    }

    private String getLowestGradeDetails(List<Grade> allGrades) {
        Grade lowestGrade = Calculator.getLowestGrade(allGrades);
        if (lowestGrade != null) {
            return String.format("Student: %s, Grade: %.2f", lowestGrade.getStudent().getName(), lowestGrade.getScore());
        }
        return "N/A";
    }

    private String getHighestGradeDetails(List<Grade> allGrades) {
        Grade highestGrade = Calculator.getHighestGrade(allGrades);
        if (highestGrade != null) {
            return String.format("Student: %s, Grade: %.2f", highestGrade.getStudent().getName(), highestGrade.getScore());
        }
        return "N/A";
    }

    public Map<String, Object> generateStudentReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("studentName", getCurrentStudent().getName());
        report.put("studentId", getCurrentStudent().getId() != null ? getCurrentStudent().getId() : "N/A");

        GradingController gradingController = new GradingController();
        Map<String, Double> outcomeAchievements = gradingController.calculateOutcomeAchievement(getCurrentStudent());
        report.put("outcomeAchievements", outcomeAchievements);

        List<Grade> grades = getCurrentStudent().getGrades();
        
        // Calculate overall grade using the last assessment
        Assessment lastAssessment = grades.isEmpty() ? null : grades.get(grades.size() - 1).getAssessment();
        if (lastAssessment != null) {
            double overallGrade = gradingController.calculateOverallGrade(getCurrentStudent(), lastAssessment);
            report.put("overallGrade", String.format("%.2f", overallGrade));
        } else {
            report.put("overallGrade", "N/A");
        }

        return report;
    }

    private Course getCurrentCourse() {
        return getCurrentStudent().getCourse();
    }

    private Student getCurrentStudent() {
        // Implement logic to get the current student
        return null;
    }
}
