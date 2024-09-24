
package com.gradeapp.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

public class ExportController {

    @FXML
    private ComboBox<Course> courseSelector;
    @FXML
    private ComboBox<Classes> classSelector;
    @FXML
    private ComboBox<Assessment> assessmentSelector;
    @FXML
    private TableView<Grade> gradeTable;
    @FXML
    private TableColumn<Grade, String> studentColumn;
    @FXML
    private TableColumn<Grade, String> assessmentColumn;
    @FXML
    private TableColumn<Grade, String> partColumn;
    @FXML
    private TableColumn<Grade, String> scoreColumn;
    @FXML
    private TableColumn<Grade, Double> percentageColumn;
    @FXML
    private TableColumn<Grade, String> feedbackColumn;
    @FXML
    private Button exportButton;
    @FXML
    private ComboBox<String> exportTypeSelector;
    @FXML
    private TableColumn<Grade, String> classColumn;

    private Database db = new Database();
    private Course selectedCourse;
    private ExportType currentExportType;

    private enum ExportType {
        COURSE, CLASS, ASSESSMENT
    }

    @FXML
    private void initialize() {
        setupExportTypeSelector();
        setupCourseSelector();
        setupClassSelector();
        setupAssessmentSelector();
        setupGradeTable();
        exportTypeSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateExportType(newVal);
        });

        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCourse = newSelection;
                updateClassSelector(newSelection);
                updateAssessmentSelector();
                updateGradeTable();
            }
        });

        classSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateGradeTable();
            }
        });

        assessmentSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateGradeTable();
            }
        });
    }

    // Method to select course from dropdown
    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);
        courseSelector.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course == null ? "" : course.getName();
            }

            @Override
            public Course fromString(String string) {
                return null;
            }
        });
        if (!courses.isEmpty()) { // Select first course in list
            courseSelector.getSelectionModel().selectFirst();
        }
    }

    private void setupExportTypeSelector() {
        exportTypeSelector.setItems(FXCollections.observableArrayList("Course", "Class", "Assessment"));
        exportTypeSelector.getSelectionModel().selectFirst();
        updateExportType("Course");
    }

    private void updateExportType(String type) {
        switch (type) {
            case "Course":
                currentExportType = ExportType.COURSE;
                classSelector.setDisable(true);
                assessmentSelector.setDisable(true);
                break;
            case "Class":
                currentExportType = ExportType.CLASS;
                classSelector.setDisable(false);
                assessmentSelector.setDisable(true);
                break;
            case "Assessment":
                currentExportType = ExportType.ASSESSMENT;
                classSelector.setDisable(false);
                assessmentSelector.setDisable(false);
                break;
        }
        updateGradeTable();
    }

    private void updateGradeTable() {
        ObservableList<Grade> grades = FXCollections.observableArrayList();
        Course selectedCourse = courseSelector.getSelectionModel().getSelectedItem();
        Classes selectedClass = classSelector.getSelectionModel().getSelectedItem();
        Assessment selectedAssessment = assessmentSelector.getSelectionModel().getSelectedItem();

        if (selectedCourse != null) {
            switch (currentExportType) {
                case COURSE:
                    grades.addAll(db.getAllGradesForCourse(selectedCourse.getId()));
                    break;
                case CLASS:
                    if (selectedClass != null) {
                        grades.addAll(db.getAllGradesForClass(selectedClass.getClassId()));
                    }
                    break;
                case ASSESSMENT:
                    if (selectedClass != null && selectedAssessment != null) {
                        List<Student> students = db.getStudentsInClass(selectedClass.getClassId());
                        for (Student student : students) {
                            grades.addAll(db.getGradesForStudentAndAssessment(student.getStudentId(),
                                    selectedAssessment.getId()));
                        }
                    }
                    break;
            }
        }

        gradeTable.setItems(grades);
    }

    private void setupAssessmentSelector() {
        assessmentSelector.setConverter(new StringConverter<Assessment>() {
            @Override
            public String toString(Assessment assessment) {
                return assessment == null ? "" : assessment.getName();
            }

            @Override
            public Assessment fromString(String string) {
                return null;
            }
        });
    }

    // Method to update classSelector based on selected course
    private void updateClassSelector(Course selectedCourse) {
        ObservableList<Classes> classes = FXCollections
                .observableArrayList(db.getClassesForCourse(selectedCourse.getId()));
        classSelector.setItems(classes);
        if (!classes.isEmpty()) {
            classSelector.getSelectionModel().selectFirst();
            updateGradeTable(classes.get(0));
        } else {
            gradeTable.setItems(FXCollections.observableArrayList());
        }
    }

    // Method to update assessmentSelector based on selected course
    private void updateAssessmentSelector() {
        Course selectedCourse = courseSelector.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            ObservableList<Assessment> assessments = FXCollections
                    .observableArrayList(db.getAssessmentsForCourse(selectedCourse.getId()));
            assessmentSelector.setItems(assessments);
            if (!assessments.isEmpty()) {
                assessmentSelector.getSelectionModel().selectFirst();
            }
        }
    }

    private void setupClassSelector() {
        classSelector.setConverter(new StringConverter<Classes>() {
            @Override
            public String toString(Classes classObj) {
                return classObj == null ? "" : classObj.getName();
            }

            @Override
            public Classes fromString(String string) {
                return null;
            }
        });
    }

    private void setupGradeTable() {
        studentColumn.setCellValueFactory(cellData -> cellData.getValue().getStudent().nameProperty());
        TableColumn<Grade, String> classColumn = new TableColumn<>("Class");
        classColumn.setCellValueFactory(cellData -> {
            Student student = cellData.getValue().getStudent();
            Classes studentClass = db.getClassForStudent(student.getStudentId());
            return new SimpleStringProperty(studentClass != null ? studentClass.getName() : "N/A");
        });
        gradeTable.getColumns().add(1, classColumn);
        assessmentColumn.setCellValueFactory(cellData -> cellData.getValue().getAssessment().nameProperty());
        partColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getAssessmentPart() != null) {
                return cellData.getValue().getAssessmentPart().nameProperty();
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });
        scoreColumn.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getScore();
            double maxScore = cellData.getValue().getAssessmentPart() != null
                    ? cellData.getValue().getAssessmentPart().getMaxScore()
                    : cellData.getValue().getAssessment().getMaxScore();
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f/%.2f", score, maxScore));
        });
        percentageColumn.setCellValueFactory(cellData -> cellData.getValue().percentageProperty().asObject());
        feedbackColumn.setCellValueFactory(new PropertyValueFactory<>("feedback"));

        scoreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        scoreColumn.setOnEditCommit(event -> {
            Grade grade = event.getRowValue();
            try {
                String newValue = event.getNewValue();
                double newScore;
                if (newValue.contains("/")) {
                    newScore = Double.parseDouble(newValue.split("/")[0]);
                } else {
                    newScore = Double.parseDouble(newValue);
                }
                grade.setScore(newScore);
                gradeTable.refresh();
                calculatePercentages();
            } catch (NumberFormatException e) {
                // Handle invalid input
                gradeTable.refresh();
            }
        });

        gradeTable.setEditable(true);
    }

    private void updateGradeTable(Classes selectedClass) {
        ObservableList<Grade> grades = FXCollections.observableArrayList();
        List<Student> students = db.getStudentsInClass(selectedClass.getClassId());
        Assessment selectedAssessment = assessmentSelector.getSelectionModel().getSelectedItem();

        if (selectedAssessment != null) {
            for (Student student : students) {
                List<Grade> studentGrades = db.getGradesForStudentAndAssessment(student.getStudentId(),
                        selectedAssessment.getId());
                grades.addAll(studentGrades);
            }
        }

        gradeTable.setItems(grades);
    }

    @FXML
    private void handleExportAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Grades Export");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Window stage = exportButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Student,Class,Assessment,Part,Score,Percentage,Feedback");
                writer.newLine();
                for (Grade grade : gradeTable.getItems()) {
                    String studentName = grade.getStudent().getName();
                    String className = db.getClassForStudent(grade.getStudent().getStudentId()).getName();
                    String assessmentName = grade.getAssessment().getName();
                    String partName = (grade.getAssessmentPart() != null) ? grade.getAssessmentPart().getName() : "N/A";
                    String scoreStr = String.format("%.2f/%.2f", grade.getScore(),
                            (grade.getAssessmentPart() != null) ? grade.getAssessmentPart().getMaxScore()
                                    : grade.getAssessment().getMaxScore());
                    double percentage = grade.getPercentage();
                    String feedback = grade.getFeedback().replace(",", " ");
                    String line = String.join(",",
                            escapeCSV(studentName),
                            escapeCSV(className),
                            escapeCSV(assessmentName),
                            escapeCSV(partName),
                            escapeCSV(scoreStr),
                            String.format("%.2f%%", percentage),
                            escapeCSV(feedback));
                    writer.write(line);
                    writer.newLine();
                }
                showAlert("Export successful.");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error exporting grades: " + e.getMessage());
            }
        }
    }

    private String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void calculatePercentages() {
        double totalScore = 0;
        double totalMaxScore = 0;
        for (Grade grade : gradeTable.getItems()) {
            totalScore += grade.getScore();
            totalMaxScore += (grade.getAssessmentPart() != null) ? grade.getAssessmentPart().getMaxScore()
                    : grade.getAssessment().getMaxScore();
        }
        double percentage = totalMaxScore != 0 ? (totalScore / totalMaxScore) * 100 : 0;
    }
}
