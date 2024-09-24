package com.gradeapp.util;

import java.util.List;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class ChartGenerator {
    private final Calculator calculator;

    public ChartGenerator() {
        this.calculator = new Calculator();
    }

    public BarChart<String, Number> createGradeDistributionChart(List<Grade> grades) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Grade Distribution");
        xAxis.setLabel("Student ID");
        yAxis.setLabel("Grade");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Grades");

        for (Grade grade : grades) {
            String studentId = grade.getStudent().getStudentId();
            double score = grade.getScore();
            series.getData().add(new XYChart.Data<>(studentId, score));
        }

        barChart.getData().add(series);
        return barChart;
    }

    public XYChart.Series<String, Number> createGradeDistributionSeries(List<Grade> grades) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Scores");

        for (Grade grade : grades) {
            String studentId = grade.getStudent().getStudentId();
            double score = grade.getScore();
            series.getData().add(new XYChart.Data<>(studentId, score));
        }

        return series;
    }

    public BarChart<String, Number> createStudentPerformanceChart(Student student) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Student Performance");
        xAxis.setLabel("Assessment");
        yAxis.setLabel("Score");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Scores");

        for (Grade grade : student.getGrades()) {
            series.getData().add(new XYChart.Data<>(grade.getAssessment().getName(), grade.getScore()));
        }

        barChart.getData().add(series);
        return barChart;
    }
}
