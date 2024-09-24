package com.gradeapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Outcome;
import com.gradeapp.model.Student;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

public class StudentMarkbookController {

    @FXML
    private Label studentName;
    @FXML
    private Label studentId;
    @FXML
    private TableView<Grade> gradeTable;
    @FXML
    private TableColumn<Grade, String> assessmentColumn;
    @FXML
    private TableColumn<Grade, String> partColumn;
    @FXML
    private TableColumn<Grade, String> outcomeColumn;
    @FXML
    private TableColumn<Grade, String> scoreColumn;
    @FXML
    private TableColumn<Grade, String> percentageColumn;
    @FXML
    private Label percentageLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Student student;
    private Assessment assessment;
    private Database db = new Database();

    @FXML
    private void initialize() {
        setupGradeTable();
    }

    public void setupGradeTable() {
        assessmentColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getAssessment().getName()));
        partColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAssessmentPart() != null ? cellData.getValue().getAssessmentPart().getName()
                        : "N/A"));
        outcomeColumn.setCellValueFactory(cellData -> {
            List<Outcome> outcomes = cellData.getValue().getAssessmentPart() != null
                    ? db.getOutcomesForAssessmentPart(cellData.getValue().getAssessmentPart().getId())
                    : db.getOutcomesForAssessment(cellData.getValue().getAssessment().getId());
            return new SimpleStringProperty(outcomes.stream().map(Outcome::getName).collect(Collectors.joining(", ")));
        });

        scoreColumn.setCellValueFactory(cellData -> {
            Grade grade = cellData.getValue();
            double score = grade.getScore();
            double maxScore = grade.getAssessmentPart() != null
                    ? grade.getAssessmentPart().getMaxScore()
                    : grade.getAssessment().getMaxScore();
            return new SimpleStringProperty(String.format("%.2f / %.2f", score, maxScore));
        });

        percentageColumn.setCellValueFactory(cellData -> {
            Grade grade = cellData.getValue();
            double score = grade.getScore();
            double maxScore = grade.getAssessmentPart() != null
                    ? grade.getAssessmentPart().getMaxScore()
                    : grade.getAssessment().getMaxScore();
            double percentage = maxScore > 0 ? (score / maxScore) * 100 : 0;
            return new SimpleStringProperty(String.format("%.2f%%", percentage));
        });

        scoreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        scoreColumn.setOnEditCommit(event -> {
            Grade grade = event.getRowValue();
            try {
                String[] parts = event.getNewValue().split("/");
                double newScore = Double.parseDouble(parts[0].trim());
                grade.setScore(newScore);
                gradeTable.refresh();
                calculatePercentages();
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                gradeTable.refresh();
            }
        });

        gradeTable.setEditable(true);
    }

    public void initializeData(Student student, Assessment assessment) {
        this.student = student;
        this.assessment = assessment;
        studentName.setText(student.getName());
        studentId.setText(student.getStudentId());
        loadGrades();
    }

    private void calculateOverallGrade() {
        double totalWeightedScore = 0;
        double totalWeight = 0;
        for (Grade grade : gradeTable.getItems()) {
            double maxScore = grade.getAssessmentPart() != null
                    ? grade.getAssessmentPart().getMaxScore()
                    : assessment.getMaxScore();
            double weight = grade.getAssessmentPart() != null
                    ? grade.getAssessmentPart().getWeight()
                    : 1;
            totalWeightedScore += (grade.getScore() / maxScore) * weight;
            totalWeight += weight;
        }
        double overallPercentage = (totalWeightedScore / totalWeight) * 100;
        percentageLabel.setText(String.format("Overall Grade: %.2f%%", overallPercentage));
    }

    private void loadGrades() {
        if (student == null || assessment == null) {
            System.err.println("Cannot load grades. Student or Assessment is not set.");
            return;
        }
        ObservableList<Grade> gradesList = FXCollections.observableArrayList();
        if (assessment.getParts() != null && !assessment.getParts().isEmpty()) {
            for (AssessmentPart part : assessment.getParts()) {
                Grade grade = db.getGrade(student.getStudentId(), assessment.getId(), part.getId());
                if (grade == null) {
                    grade = new Grade(student, assessment, part, 0.0, "");
                }
                gradesList.add(grade);
            }
        } else {
            Grade grade = db.getGrade(student.getStudentId(), assessment.getId(), null);
            if (grade == null) {
                grade = new Grade(student, assessment, null, 0.0, "");
            }
            gradesList.add(grade);
        }
        gradeTable.setItems(gradesList);
        calculatePercentages();
    }

    private void calculatePercentages() {
        double totalScore = 0;
        double totalMaxScore = 0;
        for (Grade grade : gradeTable.getItems()) {
            totalScore += grade.getScore();
            totalMaxScore += (grade.getAssessmentPart() != null) ? grade.getAssessmentPart().getMaxScore()
                    : assessment.getMaxScore();
        }
        double percentage = totalMaxScore != 0 ? (totalScore / totalMaxScore) * 100 : 0;
        percentageLabel.setText(String.format("Total Percentage: %.2f%%", percentage));
        calculateOverallGrade();
    }

    @FXML
    private void saveGrades() {
        try {
            for (Grade grade : gradeTable.getItems()) {
                db.saveGrade(grade);

                System.out.println("Saving grade: " + grade.getStudent().getStudentId() +
                        ", Assessment: " + grade.getAssessment().getId() +
                        ", Part: " + (grade.getAssessmentPart() != null ? grade.getAssessmentPart().getId() : "null") +
                        ", Score: " + grade.getScore());
            }

            showAlert("Grades saved successfully.");
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error saving grades: " + e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}