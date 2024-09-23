package com.gradeapp.controller;

import java.io.IOException;
import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.util.List;

public class ExportController {

    // FXML elements
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
    private TableColumn<Grade, Double> scoreColumn;
    @FXML
    private TableColumn<Grade, Double> percentageColumn;
    @FXML
    private TableColumn<Grade, String> feedbackColumn;

    private Database db = new Database();
    private Course selectedCourse;

    @FXML
    private void initialize() {
        setupCourseSelector();
        setupClassSelector();
        setupAssessmentSelector();
        setupGradeTable();
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateClassSelector(newSelection);
                updateClassSelector(newSelection);
                updateAssessmentSelector();
                if (!classSelector.getItems().isEmpty()) {
                    updateGradeTable(classSelector.getItems().get(0));
                }
            }
        });
    
        classSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateGradeTable(newSelection);
            }
        });
    
        assessmentSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateGradeTable(classSelector.getSelectionModel().getSelectedItem());
            }
        });
    }

    // Method to select course from dropdown
    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCourse = newSelection; // Store selected course
                updateClassSelector(newSelection);
                updateAssessmentSelector(); // Update assessments based on selected course
            }
        });
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
        // Update classSelector when course is selected
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateClassSelector(newSelection);
            }
        });
        if (!courses.isEmpty()) { // Select first course in list
            courseSelector.getSelectionModel().selectFirst();
        }
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
        classSelector.getSelectionModel().selectFirst();
        // if (!classes.isEmpty()) {
        //     updateStudentList(classes.get(0));
        // }
    }

    // Method to update assessmentSelector based on selected class
    private void updateAssessmentSelector() {
        Course selectedCourse = courseSelector.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            ObservableList<Assessment> assessments = FXCollections
                    .observableArrayList(db.getAssessmentsForCourse(selectedCourse.getId()));
            assessmentSelector.setItems(assessments);
            assessmentSelector.getSelectionModel().selectFirst();
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
        assessmentColumn.setCellValueFactory(cellData -> cellData.getValue().getAssessment().nameProperty());
        partColumn.setCellValueFactory(cellData -> cellData.getValue().getAssessmentPart() != null ? cellData.getValue().getAssessmentPart().nameProperty() : null);
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        percentageColumn.setCellValueFactory(cellData -> cellData.getValue().percentageProperty().asObject());
        feedbackColumn.setCellValueFactory(new PropertyValueFactory<>("feedback"));
    }

    private void updateGradeTable(Classes selectedClass) {
        ObservableList<Grade> grades = FXCollections.observableArrayList();
        List<Student> students = db.getStudentsInClass(selectedClass.getClassId());
        Assessment selectedAssessment = assessmentSelector.getSelectionModel().getSelectedItem();

        for (Student student : students) {
            grades.addAll(db.getGradesForStudentAndAssessment(student.getStudentId(), selectedAssessment.getId()));
        }

        gradeTable.setItems(grades);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}