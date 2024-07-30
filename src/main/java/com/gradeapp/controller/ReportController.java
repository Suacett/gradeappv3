package com.gradeapp.controller;

import com.gradeapp.model.*;
import com.gradeapp.util.*;
import javafx.scene.chart.BarChart;
import java.util.*;

public class ReportController {
    private Calculator calculator;
    private ChartGenerator chartGenerator;
    private ReportExporter reportExporter;

    public ReportController() {
        this.calculator = Calculator.getInstance();
        this.chartGenerator = new ChartGenerator();
        this.reportExporter = new ReportExporter();
    }

    public Map<String, Object> generateClassReport(Course course) {
        Map<String, Object> report = new HashMap<>();
        List<Grade> allGrades = course.getAllGrades();

        report.put("courseName", course.getName());
        report.put("studentCount", course.getStudents().size());
        report.put("statistics", calculator.calculateStatistics(allGrades));
        report.put("gradeDistribution", calculator.calculateGradeDistribution(allGrades));
        report.put("gradeDistributionChart", (BarChart<String, Number>) chartGenerator.createGradeDistributionChart(allGrades));

        return report;
    }

    public Map<String, Object> generateStudentReport(Student student) {
        Map<String, Object> report = new HashMap<>();
        report.put("studentName", student.getName());
        report.put("studentId", student.getStudentId());
        report.put("overallGrade", calculator.calculateOverallGrade(student));
        report.put("gradesByAssessment", calculator.calculateGradesByAssessment(student));
        report.put("outcomeAchievements", calculator.calculateOutcomeAchievement(student));
        report.put("performanceChart", (BarChart<String, Number>) chartGenerator.createStudentPerformanceChart(student));

        return report;
    }

    public Map<String, Object> generateAssessmentReport(Assessment assessment) {
        Map<String, Object> report = new HashMap<>();
        List<Grade> assessmentGrades = assessment.getGradeBook().getGradesForAssessment(assessment);

        report.put("assessmentName", assessment.getName());
        report.put("statistics", calculator.calculateStatistics(assessmentGrades));
        report.put("taskCompletionRates", calculator.calculateTaskCompletionRates(assessment));
        report.put("gradeDistribution", calculator.calculateGradeDistribution(assessmentGrades));
        report.put("gradeDistributionChart", (BarChart<String, Number>) chartGenerator.createGradeDistributionChart(assessmentGrades));

        return report;
    }

    public void exportReport(Map<String, Object> report, String filePath, ReportFormat format) {
        reportExporter.exportReport(report, filePath, format);
    }

    public enum ReportFormat {
        PDF, XLS, CSV
    }
}
