package com.gradeapp.util;

import java.util.List;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * Generates various charts for visualizing grades and student performance.
 */
public class ChartGenerator {
    private final Calculator calculator;

    /**
     * Constructs a ChartGenerator instance.
     */
    public ChartGenerator() {
        this.calculator = new Calculator();
    }

    // ----------------------------- Grade Distribution Chart -----------------------------

    /**
     * Creates a bar chart representing the distribution of grades across students.
     *
     * @param grades The list of grades to visualize.
     * @return A BarChart displaying grade distribution.
     */
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

    /**
     * Creates a data series for grade distribution, suitable for adding to charts.
     *
     * @param grades The list of grades to include in the series.
     * @return An XYChart.Series object containing grade data.
     */
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

    // ----------------------------- Student Performance Chart -----------------------------

    /**
     * Creates a bar chart visualizing a student's performance across different assessments.
     *
     * @param student The student whose performance is to be visualized.
     * @return A BarChart displaying the student's performance.
     */
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
