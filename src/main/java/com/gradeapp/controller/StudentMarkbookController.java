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

/**
 * Controller class for managing the student's markbook.
 * Handles displaying and editing grades for a specific student and assessment.
 */
public class StudentMarkbookController {

    // ----------------------------- FXML UI Components
    // -----------------------------

    @FXML
    private Label studentName; // Label to display the student's name

    @FXML
    private Label studentId; // Label to display the student's ID

    @FXML
    private TableView<Grade> gradeTable; // TableView to display grades

    @FXML
    private TableColumn<Grade, String> assessmentColumn; // Column for assessment names

    @FXML
    private TableColumn<Grade, String> partColumn; // Column for assessment parts

    @FXML
    private TableColumn<Grade, String> outcomeColumn; // Column for outcomes

    @FXML
    private TableColumn<Grade, String> scoreColumn; // Column for scores

    @FXML
    private TableColumn<Grade, String> percentageColumn; // Column for percentages

    @FXML
    private Label percentageLabel; // Label to display overall percentage

    @FXML
    private Button saveButton; // Button to save grades

    @FXML
    private Button cancelButton; // Button to cancel and close the window

    // ----------------------------- Non-UI Fields -----------------------------

    private Student student; // The current student
    private Assessment assessment; // The current assessment
    private Database db = new Database(); // Database instance for data operations

    // ----------------------------- Initialization -----------------------------

    /**
     * Initializes the controller after its root element has been completely
     * processed.
     * Sets up the grade table with appropriate columns and editing capabilities.
     */
    @FXML
    private void initialize() {
        setupGradeTable();
    }

    /**
     * Sets up the grade table columns and their respective cell value factories.
     * Enables editing for the score column.
     */
    public void setupGradeTable() {
        // Define how each column retrieves data from the Grade object
        assessmentColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getAssessment().getName()));
        partColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAssessmentPart() != null ? cellData.getValue().getAssessmentPart().getName()
                        : "N/A"));
        outcomeColumn.setCellValueFactory(cellData -> {
            List<Outcome> outcomes = cellData.getValue().getAssessmentPart() != null
                    ? db.getOutcomesForAssessmentPart(cellData.getValue().getAssessmentPart().getId())
                    : db.getOutcomesForAssessment(cellData.getValue().getAssessment().getId());
            String outcomesStr = outcomes.stream().map(Outcome::getName).collect(Collectors.joining(", "));
            return new SimpleStringProperty(outcomesStr);
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

        // Enable editing for the score column
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
                // Handle invalid input by refreshing the table to revert changes
                gradeTable.refresh();
            }
        });

        // Make the table editable
        gradeTable.setEditable(true);
    }

    // ----------------------------- Data Initialization
    // -----------------------------

    /**
     * Initializes the controller with the selected student's data and assessment.
     *
     * @param student    The student whose markbook is to be displayed.
     * @param assessment The assessment for which grades are managed.
     */
    public void initializeData(Student student, Assessment assessment) {
        this.student = student;
        this.assessment = assessment;
        studentName.setText(student.getName());
        studentId.setText(student.getStudentId());
        loadGrades();
    }

    // ----------------------------- Grade Management -----------------------------

    /**
     * Loads grades for the current student and assessment from the database.
     * Populates the grade table with existing grades or initializes them if absent.
     */
    private void loadGrades() {
        if (student == null || assessment == null) {
            System.err.println("Cannot load grades. Student or Assessment is not set.");
            return;
        }
        ObservableList<Grade> gradesList = FXCollections.observableArrayList();
        if (assessment.getParts() != null && !assessment.getParts().isEmpty()) {
            // If the assessment has parts, load grades for each part
            for (AssessmentPart part : assessment.getParts()) {
                Grade grade = db.getGrade(student.getStudentId(), assessment.getId(), part.getId());
                if (grade == null) {
                    // Initialize grade with default values if not present
                    grade = new Grade(student, assessment, part, 0.0, "");
                }
                gradesList.add(grade);
            }
        } else {
            // If the assessment does not have parts, load the overall grade
            Grade grade = db.getGrade(student.getStudentId(), assessment.getId(), null);
            if (grade == null) {
                grade = new Grade(student, assessment, null, 0.0, "");
            }
            gradesList.add(grade);
        }
        gradeTable.setItems(gradesList);
        calculatePercentages();
    }

    /**
     * Calculates the overall grade percentage based on all grades in the table.
     * Updates the percentage label accordingly.
     */
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

    /**
     * Calculates the overall grade considering weights of different assessment
     * parts.
     * Updates the overall percentage label.
     */
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

    // ----------------------------- Event Handlers -----------------------------

    /**
     * Handles the action of saving all grades.
     * Saves each grade to the database and closes the markbook window upon success.
     */
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

    /**
     * Handles the action of cancelling and closing the markbook window without
     * saving.
     */
    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // ----------------------------- Helper Methods -----------------------------

    /**
     * Displays an informational alert to the user.
     *
     * @param message The message to display in the alert.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
