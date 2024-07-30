package com.gradeapp.view;

import com.gradeapp.controller.ReportController;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Presents an interface for generating and viewing various reports with interactive charts.
 */
public class ReportView {
    private VBox root;
    private ComboBox<String> reportTypeComboBox;
    private Button generateReportButton;
    private TextArea reportTextArea;
    private ReportController reportController;
    private VBox chartContainer;

    public ReportView(ReportController reportController) {
        root = new VBox(10);
        this.reportController = reportController;
        initializeUI();
    }

    private void initializeUI() {
        root.setPadding(new Insets(10));

        Label titleLabel = new Label("Generate Reports");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        reportTypeComboBox = new ComboBox<>();
        reportTypeComboBox.getItems().addAll("Class Report", "Student Report");
        reportTypeComboBox.setValue("Class Report");

        generateReportButton = new Button("Generate Report");
       // generateReportButton.setOnAction(e -> generateReport(reportTypeComboBox.getValue()));

        reportTextArea = new TextArea();
        reportTextArea.setEditable(false);
        
        chartContainer = new VBox(10);
        chartContainer.setPadding(new Insets(10));

        root.getChildren().addAll(titleLabel, reportTypeComboBox, generateReportButton, reportTextArea, chartContainer);
    }
/**
    private void generateReport(String reportType) {
        Map<String, Object> reportData;
        if ("Class Report".equals(reportType)) {
            reportData = reportController.generateClassReport(getCurrentCourse());
        } else {
            reportData = reportController.generateStudentReport(getCurrentStudent());
        }

        // Update the report text area
        StringBuilder reportText = new StringBuilder();
        for (Map.Entry<String, Object> entry : reportData.entrySet()) {
            reportText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        reportTextArea.setText(reportText.toString());

        // Generate and display charts
        generateCharts(reportData);
    }
*/
    private void generateCharts(Map<String, Object> reportData) {
        chartContainer.getChildren().clear();

        // Example: Create a bar chart for grade distribution
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Grade Distribution");

        // ... Populate the chart with data from reportData ...

        chartContainer.getChildren().add(barChart);

        // Add more charts as needed
    }

    public VBox getRoot() {
        return root;
    }
}
