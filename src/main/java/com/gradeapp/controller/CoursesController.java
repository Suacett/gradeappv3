package com.gradeapp.controller;

import java.util.ArrayList;
import java.util.List;
import com.gradeapp.database.Database;
import com.gradeapp.model.Course;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


// ClassController manages the Class category content.
public class CoursesController {
    
// FXML ids
    @FXML
    private VBox courseContainer;
    @FXML
    private Button addCourseButton;
    @FXML
    private VBox newCourseInputContainer;
    @FXML
    private VBox currentCourseContainer;

    private List<Course> courseList = new ArrayList<>();

    private Database db = new Database();

    // Initialise ClassController
    @FXML
    private void initialize() {
        db.initialiseDatabase();
        displayCurrentCourse();
    }

    // Add Course onClick event
    @FXML
    private void handleAddCourseButtonAction() {
        VBox courseInputBox = createCourseInputBox();
        newCourseInputContainer.getChildren().add(courseInputBox);
    }

    // Create new Course inputs, displayed when Add Course button clicked
    private VBox createCourseInputBox() {
        VBox courseInputBox = new VBox();
        courseInputBox.setPadding(new Insets(20, 20, 20, 20));
        courseInputBox.setSpacing(10);
        Label courseNameLabel = new Label("Course Name:");
        TextField courseNameField = new TextField();
        courseNameField.setPromptText("Course name");
        Label courseIdLabel = new Label("Course Id:");
        TextField courseIdField = new TextField();
        courseIdField.setPromptText("Course Id");
        Button submitButton = new Button("+ Add Course");
        submitButton.setOnAction(event -> handleSubmitButtonAction(courseNameField, courseIdField));
        courseInputBox.getChildren().addAll(courseNameLabel, courseNameField, courseIdLabel, courseIdField, submitButton);
        return courseInputBox;
    }

    // Submit New Course
    private void handleSubmitButtonAction(TextField courseNameField, TextField courseIdField) {
        String courseName = courseNameField.getText();
        String courseId = courseIdField.getText();
        if (!courseName.isEmpty() && !courseId.isEmpty()) {
            Course newCourse = new Course(courseName, courseId); // Create a new course object
            courseList.add(newCourse); // Add to courseList
            db.addCourse(courseName, courseId); // Add courseList to database
            courseNameField.clear(); // Clear name input field
            courseIdField.clear(); // Clear id input field
            displayCurrentCourse(); // Update current course field
        } else { // Handle empty fields
            System.out.println("The form is incomplete");
        }
    }

    // Course card, displays current courses
    private VBox createCourseCard(Course course) {
        VBox courseCard = new VBox();
        courseCard.setPadding(new Insets(10));
        courseCard.setSpacing(10);
        courseCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 5px;");
        Label courseNameLabel = new Label(course.getName());
        Label courseIdLabel = new Label(course.getDescription());
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> { // Delete from db
            db.delete("courses", "name", course.getName()); 
            displayCurrentCourse(); // Display course list
        });
        courseCard.getChildren().addAll(courseNameLabel, courseIdLabel, deleteButton);
        return courseCard;
    }
    
    // Display current courses
    private void displayCurrentCourse() {
        currentCourseContainer.getChildren().clear();
        List<Course> courseFromDb = db.getAllCourses(); // Get courses from db
        if (courseFromDb.isEmpty()) { // Display message when empty
            Label emptyLabel = new Label("You have no current courses.");
            currentCourseContainer.getChildren().add(emptyLabel);
        } else {
            for (Course course : courseFromDb) {
                VBox courseCard = createCourseCard(course);
                currentCourseContainer.getChildren().add(courseCard);
            }
        }
    }

}