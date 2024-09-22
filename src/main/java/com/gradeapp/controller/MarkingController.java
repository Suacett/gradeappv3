package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;

public class MarkingController {

    // FXML elements
    @FXML
    private ComboBox<Course> courseSelector;
    @FXML
    private ComboBox<Classes> classSelector;
    @FXML
    private ComboBox<Assessment> assessmentSelector;
    @FXML
    private VBox studentsInClass;
    @FXML
    private Label studentName;
    @FXML
    private Label studentId;

    private Database db = new Database();
    private Course selectedCourse;

    @FXML
    private void initialize() {
        setupCourseSelector();
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateClassSelector(newSelection);
                updateAssessmentSelector();
            }
        });
        classSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateStudentList(newSelection);
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

    // Method to update classSelector based on selected course
    private void updateClassSelector(Course selectedCourse) {
        ObservableList<Classes> classes = FXCollections.observableArrayList(db.getClassesForCourse(selectedCourse.getId()));
        classSelector.setItems(classes);
        classSelector.getSelectionModel().selectFirst();
    }

    // Method to update assessmentSelector based on selected class
    private void updateAssessmentSelector() {
        Course selectedCourse = courseSelector.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            ObservableList<Assessment> assessments = FXCollections.observableArrayList(db.getAssessmentsForCourse(selectedCourse.getId()));
            assessmentSelector.setItems(assessments);
            assessmentSelector.getSelectionModel().selectFirst();
        }
    }

    // Method to update the student list based on the selected class
    private void updateStudentList(Classes selectedClass) {
        ObservableList<Student> students = FXCollections.observableArrayList(db.getStudentsInClass(selectedClass.getClassId()));
        studentsInClass.getChildren().clear();
        for (Student student : students) {
            HBox studentCard = createStudentCard(student);
            studentsInClass.getChildren().add(studentCard);
        }
    }

    // Method to create a student card
    private HBox createStudentCard(Student student) {
        HBox studentCard = new HBox();
        studentCard.getStyleClass().add("card");
        studentCard.setSpacing(10);
        Label nameLabel = new Label("Name: " + student.getName());
        Label idLabel = new Label("ID: " + student.getStudentId());
        HBox buttonContainer = new HBox();
        Button markBookButton = new Button("Mark Book");
        markBookButton.setOnAction(event -> openMarkBook(student)); // Set event handler
        buttonContainer.getChildren().add(markBookButton);
        Region spacer = new Region(); // Space between items
        HBox.setHgrow(spacer, Priority.ALWAYS);
        studentCard.getChildren().addAll(nameLabel, idLabel, spacer, buttonContainer);
        return studentCard;
    }

    // Method to open the Mark Book view
    private void openMarkBook(Student student) {
        try {
            Assessment selectedAssessment = assessmentSelector.getSelectionModel().getSelectedItem();
            if (selectedAssessment == null) {
                showAlert("Please select an assessment.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo3/student-markbook.fxml"));
            VBox studentMarkBook = loader.load();

            StudentMarkbookController controller = loader.getController();
            controller.setStudent(student);
            controller.setAssessment(selectedAssessment);

            Stage stage = new Stage();
            stage.setTitle("Mark Book for " + student.getName());
            stage.setScene(new Scene(studentMarkBook));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading student's MarkBook: " + e.getMessage());
        }
    }

    
    // Method to set details in student-markbook.fxml
    private void setStudent(Student student) {
        studentName.setText(student.getName());
        studentId.setText(student.getStudentId());
    }

    // Method to show an alert
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}