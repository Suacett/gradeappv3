package com.gradeapp.controller;

import com.gradeapp.model.Course;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

// CoursesController manages the Courses category dynamic content.
public class CoursesController {

    @FXML
    private VBox coursesContainer;

    @FXML
    private Button addCourseButton;

    @FXML
    private VBox newCourseInputContainer;

    @FXML
    private VBox currentCoursesContainer;

    private List<Course> coursesList = new ArrayList<>();

    // Initialize the CoursesController
    @FXML
    private void initialize() {
        displayCurrentCourses();
    }

    // Method to handle 'Add Course' button click
    @FXML
    private void handleAddCourseButtonAction() {
        VBox courseInputBox = createCourseInputBox();
        newCourseInputContainer.getChildren().add(courseInputBox);
    }

    // Create a new course input box
    private VBox createCourseInputBox() {
        VBox courseInputBox = new VBox();
        courseInputBox.setSpacing(10);

        Label courseNameLabel = new Label("Course Name:");
        TextField courseNameField = new TextField();
        courseNameField.setPromptText("Course name");

        Label courseDescriptionLabel = new Label("Course Description:");
        TextField courseDescriptionField = new TextField();
        courseDescriptionField.setPromptText("Course description");

        Button submitButton = new Button("+ Add Course");
        submitButton.setOnAction(event -> handleSubmitButtonAction(courseNameField, courseDescriptionField));

        courseInputBox.getChildren().addAll(courseNameLabel, courseNameField, courseDescriptionLabel, courseDescriptionField, submitButton);
        return courseInputBox;
    }

    // Method to handle 'Submit' new course button click
    private void handleSubmitButtonAction(TextField courseNameField, TextField courseDescriptionField) {
        String courseName = courseNameField.getText();
        String courseDescription = courseDescriptionField.getText();

        if (!courseName.isEmpty() && !courseDescription.isEmpty()) {
            Course newCourse = new Course(courseName, courseDescription); // Create a new Course object
            coursesList.add(newCourse); // Add the new course to the list
            courseNameField.clear(); // Clear the input fields
            courseDescriptionField.clear();
            displayCurrentCourses(); // Update the display of current courses
        } else {
            // Handle empty input fields, maybe show an alert to the user
            System.out.println("Course name or description cannot be empty.");
        }
    }

    // Display current courses
    private void displayCurrentCourses() {
        currentCoursesContainer.getChildren().clear();
        for (Course course : coursesList) {
            Label courseLabel = new Label(course.getName() + ": " + course.getDescription());
            currentCoursesContainer.getChildren().add(courseLabel);
        }
    }
}
