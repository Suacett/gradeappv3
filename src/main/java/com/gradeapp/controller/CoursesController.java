package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Course;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class CoursesController {

    @FXML
    private VBox currentCourseContainer;
    @FXML
    private VBox newCourseInputContainer;

    private Database db = new Database();

    @FXML
    private void initialize() {
        displayCurrentCourses();
    }

    // Add Course click event
    @FXML
    private void handleAddCourseButtonAction() {
        VBox courseInputBox = createCourseInputBox();
        newCourseInputContainer.getChildren().add(courseInputBox);
    }

    // New course inputs, appear on Add Course button click
    private VBox createCourseInputBox() {
        VBox courseInputBox = new VBox();
        courseInputBox.setPadding(new Insets(20, 20, 20, 20));
        courseInputBox.setSpacing(10);
        Label courseNameLabel = new Label("Course Name:");
        TextField courseNameField = new TextField();
        courseNameField.setPromptText("Course name");
        TextField courseDescriptionField = new TextField();
        courseDescriptionField.setPromptText("Course Description");
        Button submitButton = new Button("+ Add Course"); // Submit button
        submitButton.setOnAction(event -> handleSubmitCourseButtonAction(courseNameField, courseDescriptionField));
        courseInputBox.getChildren().addAll(courseNameLabel, courseNameField, courseDescriptionField, submitButton);
        return courseInputBox;
    }

    // Submit new course click event
    private void handleSubmitCourseButtonAction(TextField courseNameField, TextField courseDescriptionField) {
        String courseName = courseNameField.getText();
        String courseDescription = courseDescriptionField.getText();
        if (!courseName.isEmpty() && !courseDescription.isEmpty()) {
            Course newCourse = new Course(courseName, courseDescription); // New Course object
            db.addCourse(courseName, courseDescription); // Add course to db
            courseNameField.clear(); // Clear inputs
            courseDescriptionField.clear();
            displayCurrentCourses(); // Display current courses
        } else {
            System.out.println("The form is incomplete...");
        }
    }

    // Course card, displays current courses
    private VBox createCourseCard(Course course) {
        VBox courseCard = new VBox();
        courseCard.setPadding(new Insets(10));
        courseCard.setSpacing(10);
        courseCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 5px;");

        Label courseNameLabel = new Label(course.getName());  // Display the full name as it is
        Label courseDescriptionLabel = new Label(course.getDescription());

        // Create HBox to hold the buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> handleEditCourseButtonAction(course));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            db.delete("courses", "name", course.getName()); // Ensure deletion is based on course name
            displayCurrentCourses();  // Refresh the course list after deletion
        });

        buttonContainer.getChildren().addAll(editButton, deleteButton);
        courseCard.getChildren().addAll(courseNameLabel, courseDescriptionLabel, buttonContainer);
        return courseCard;
    }

    // Edit button action for courses
    private void handleEditCourseButtonAction(Course course) {
        // Display an input form with the current course's details filled in
        TextField courseNameField = new TextField(course.getName());
        TextField courseDescriptionField = new TextField(course.getDescription());
        Button saveButton = new Button("Save");

        saveButton.setOnAction(event -> {
            String newName = courseNameField.getText();
            String newDescription = courseDescriptionField.getText();
            if (!newName.isEmpty() && !newDescription.isEmpty()) {
                // Update course information in the database
                db.updateCourse(course.getName(), newName, newDescription);
                displayCurrentCourses(); // Refresh the display
            } else {
                System.out.println("The form is incomplete...");
            }
        });

        VBox editCourseBox = new VBox(10, new Label("Edit Course"), courseNameField, courseDescriptionField, saveButton);
        currentCourseContainer.getChildren().clear();
        currentCourseContainer.getChildren().add(editCourseBox);
    }

    // Display current courses
    private void displayCurrentCourses() {
        currentCourseContainer.getChildren().clear();
        List<Course> coursesFromDb = db.getAllCourses(); // Get courses from db
        if (coursesFromDb.isEmpty()) { // Display message if db empty
            Label emptyLabel = new Label("You have no current courses");
            currentCourseContainer.getChildren().add(emptyLabel);
        } else {
            for (Course course : coursesFromDb) {
                VBox courseCard = createCourseCard(course);
                currentCourseContainer.getChildren().add(courseCard);
            }
        }
    }
}

