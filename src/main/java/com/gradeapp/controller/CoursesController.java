package com.gradeapp.controller;

import com.gradeapp.model.Course;
import com.gradeapp.database.Database;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

    private Database db = new Database();

    // Initialise the CoursesController
    @FXML
    private void initialize() {
        displayCurrentCourses();
        db.initialiseDatabase();
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
        courseInputBox.setPadding(new Insets(20, 20, 20, 20));
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
            coursesList.add(newCourse); // Add new course to coursesList
            db.addCourse(courseName, courseDescription); // Add coursesList to database

            courseNameField.clear(); // Clear name input field
            courseDescriptionField.clear(); // Clear description input field
            displayCurrentCourses(); // Update current courses field
        } else { // Handle empty fields
            System.out.println("Please finish the form");
        }
    }

    private VBox createCourseCard(Course course) {
        VBox courseCard = new VBox();
        courseCard.setPadding(new Insets(10));
        courseCard.setSpacing(10);
        courseCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 5px;");

        Label courseNameLabel = new Label(course.getName());
        Label courseDescriptionLabel = new Label(course.getDescription());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            db.delete("courses", "name", course.getName()); // Delete course from database
            displayCurrentCourses(); // Refresh the course list
        });

        courseCard.getChildren().addAll(courseNameLabel, courseDescriptionLabel, deleteButton);
        return courseCard;
    }

    // Display current courses
    private void displayCurrentCourses() {
        currentCoursesContainer.getChildren().clear();

        // Retrieve courses from the database
        List<Course> coursesFromDb = db.getAllCourses();

        // If there are no courses, show a message
        if (coursesFromDb.isEmpty()) {
            Label emptyLabel = new Label("You have no current Courses");
            currentCoursesContainer.getChildren().add(emptyLabel);
        } else {
            // Display courses from the database
            for (Course course : coursesFromDb) {
                VBox courseCard = createCourseCard(course);
                currentCoursesContainer.getChildren().add(courseCard);
            }
        }
    }
}
