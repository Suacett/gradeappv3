package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Course;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Assessment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

public class MarkingController {

    // FXML elements
    @FXML
    private ComboBox<Course> courseSelector;
    @FXML
    private ComboBox<Classes> classSelector; // Corrected type to Classes
    @FXML
    private ComboBox<Assessment> assessmentSelector; // Corrected type to Assessment
    @FXML
    private Button openRubric;

    private Database db = new Database();

    @FXML
    private void initialize() {
        setupCourseSelector();
        // load students
    }

    
    // Method to select course from dropdown
    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);
        courseSelector.setCellFactory(lv -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getName() + " (" + course.getId() + ")");
                }
            }
        });
        courseSelector.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course == null ? "" : course.getName() + " (" + course.getId() + ")";
            }
            @Override
            public Course fromString(String string) {
                return null; // No need to implement
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

    // Method to update classSelector based on selected course
    private void updateClassSelector(Course selectedCourse) {
        ObservableList<Classes> classes = FXCollections.observableArrayList(db.getClassesForCourse(selectedCourse.getId()));
        classSelector.setItems(classes);
        classSelector.setCellFactory(lv -> new ListCell<Classes>() {
            @Override
            protected void updateItem(Classes cls, boolean empty) {
                super.updateItem(cls, empty);
                if (empty || cls == null) {
                    setText(null);
                } else {
                    setText(cls.getName() + " (" + cls.getClassId() + ")");
                }
            }
        });
        classSelector.setConverter(new StringConverter<Classes>() {
            @Override
            public String toString(Classes cls) {
                return cls == null ? "" : cls.getName() + " (" + cls.getClassId() + ")";
            }

            @Override
            public Classes fromString(String string) {
                return null;
            }
        });

        // Add listener to update assessmentSelector when a class is selected
        classSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateAssessmentSelector(newSelection);
            }
        });

        // Automatically select the first class if available
        if (!classes.isEmpty()) {
            classSelector.getSelectionModel().selectFirst();
        }
    }

    // Method to update assessmentSelector based on selected class
    private void updateAssessmentSelector(Classes selectedClass) {
        ObservableList<Assessment> assessments = FXCollections.observableArrayList(db.getAssessmentsForClass(selectedClass.getClassId()));
        assessmentSelector.setItems(assessments);
        assessmentSelector.setCellFactory(lv -> new ListCell<Assessment>() {
            @Override
            protected void updateItem(Assessment assessment, boolean empty) {
                super.updateItem(assessment, empty);
                if (empty || assessment == null) {
                    setText(null);
                } else {
                    setText(assessment.getName() + " (" + assessment.getId() + ")");
                }
            }
        });
        assessmentSelector.setConverter(new StringConverter<Assessment>() {
            @Override
            public String toString(Assessment assessment) {
                return assessment == null ? "" : assessment.getName() + " (" + assessment.getId() + ")";
            }

            @Override
            public Assessment fromString(String string) {
                return null;
            }
        });

        // Automatically select the first assessment if available
        if (!assessments.isEmpty()) {
            assessmentSelector.getSelectionModel().selectFirst();
        }
    }
}