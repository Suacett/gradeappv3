package com.gradeapp.controller;

import java.util.HashMap;
import java.util.List;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.GradeBook;
import com.gradeapp.model.Student;
import com.gradeapp.util.Calculator;
import com.gradeapp.util.ChartGenerator;
import com.gradeapp.util.ReportExporter;

import javafx.scene.chart.BarChart;

public class ReportController {
    private Calculator calculator;
    private ChartGenerator chartGenerator;
    private ReportExporter reportExporter;

    public ReportController() {
        this.calculator = new Calculator();
        this.chartGenerator = new ChartGenerator();
        this.reportExporter = new ReportExporter();
    }

    // Methods for generating reports
    public HashMap<String, Object> generateClassReport(Course course) {
        HashMap<String, Object> report = new HashMap<>();
        List<Grade> allGrades = course.getAllGrades();

        report.put("courseName", course.getName());
        report.put("studentCount", course.getStudents().size());
        report.put("statistics", calculator.calculateStatistics(allGrades));
        report.put("gradeDistribution", calculator.calculateGradeDistribution(allGrades));
        report.put("gradeDistributionChart",
                (BarChart<String, Number>) chartGenerator.createGradeDistributionChart(allGrades));

        return report;
    }

    public HashMap<String, Object> generateStudentReport(Student student) {
        HashMap<String, Object> report = new HashMap<>();
        report.put("studentName", student.getName());
        report.put("studentId", student.getStudentId());
        report.put("overallGrade", calculator.calculateOverallGrade(student));
        report.put("gradesByAssessment", calculator.calculateGradesByAssessment(student));
        report.put("outcomeAchievements", calculator.calculateOutcomeAchievement(student));
        report.put("performanceChart",
                (BarChart<String, Number>) chartGenerator.createStudentPerformanceChart(student));

        return report;
    }

    public HashMap<String, Object> generateAssessmentReport(Assessment assessment, GradeBook gradeBook) {
        HashMap<String, Object> report = new HashMap<>();
        List<Grade> assessmentGrades = gradeBook.getGradesForAssessment(assessment);

        report.put("assessmentName", assessment.getName());
        report.put("statistics", calculator.calculateStatistics(assessmentGrades));
        // report.put("taskCompletionRates",
        // calculator.calculateTaskCompletionRates(assessment));
        report.put("gradeDistribution", calculator.calculateGradeDistribution(assessmentGrades));
        report.put("gradeDistributionChart",
                (BarChart<String, Number>) chartGenerator.createGradeDistributionChart(assessmentGrades));

        return report;
    }

    // Method for exporting reports
    public void exportReport(HashMap<String, Object> report, String filePath, ReportFormat format) {
        reportExporter.exportReport(report, filePath, format);
    }

    // Enum for report formats
    public enum ReportFormat {
        PDF, XLS, CSV
    }
}
