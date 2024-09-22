package com.gradeapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Outcome;
import com.gradeapp.model.Student;

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
    private TableColumn<Grade, Double> scoreColumn;
    @FXML
    private TableColumn<Grade, String> percentageColumn;
    @FXML
    private Label percentageLabel;
    @FXML
    private Button saveStudent;
    @FXML
    private Button cancel;

    private Student student;
    private Assessment assessment;
    private Database db = new Database();

    @FXML
    private void initialize() {
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
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());
        percentageColumn.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getScore();
            double maxScore = cellData.getValue().getAssessmentPart() != null
                    ? cellData.getValue().getAssessmentPart().getMaxScore()
                    : assessment.getMaxScore();
            double percentage = maxScore > 0 ? (score / maxScore) * 100 : 0;
            return new SimpleStringProperty(String.format("%.2f%%", percentage));
        });

        scoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        scoreColumn.setOnEditCommit(event -> {
            Grade grade = event.getRowValue();
            grade.setScore(event.getNewValue());
            gradeTable.refresh();
            calculatePercentages();
        });

        gradeTable.setEditable(true);
    }

    public void setStudent(Student student) {
        this.student = student;
        studentName.setText(student.getName());
        studentId.setText(student.getStudentId());

    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public void initializeData(Student student, Assessment assessment) {
        this.student = student;
        this.assessment = assessment;
        studentName.setText(student.getName());
        studentId.setText(student.getStudentId());
        loadGrades();
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
    }

    @FXML
    private void saveStudent() {
        for (Grade grade : gradeTable.getItems()) {
            db.saveGrade(grade);
        }
        showAlert("Grades saved successfully.");
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) cancel.getScene().getWindow();
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
