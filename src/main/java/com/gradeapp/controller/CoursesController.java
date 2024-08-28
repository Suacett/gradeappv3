package com.gradeapp.controller;

import com.gradeapp.model.Course;
import com.gradeapp.database.Database;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CoursesController {

    @FXML
    private VBox currentCourseContainer;

    @FXML
    private VBox newCourseInputContainer;

    @FXML
    private VBox content; // Reference to the main content area

    private Database db = new Database();

    // Initialise the CoursesController
    @FXML
    private void initialize() {
        db.initialiseDatabase();
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
            Course newCourse = new Course(courseName, courseDescription);
            db.addCourse(newCourse);

            courseNameField.clear();
            courseDescriptionField.clear();
            displayCurrentCourses();
        } else {
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

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> openCourseDetails(course));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            db.delete("courses", "name", course.getName());
            displayCurrentCourses();
        });

        courseCard.getChildren().addAll(courseNameLabel, courseDescriptionLabel, editButton, deleteButton);
        return courseCard;
    }

    // Navigate to CourseDetails
    private void openCourseDetails(Course course) {
        try {
            // Load the FXML file using the correct path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo3/course-details.fxml"));
            VBox courseDetailsView = loader.load();

            // Get the controller associated with the FXML
            CourseDetailsController controller = loader.getController();
            controller.setCourse(course, currentCourseContainer, content);

            // Set the loaded view to the content area
            content.getChildren().setAll(courseDetailsView);

        } catch (IOException e) {
            // Print stack trace for debugging
            e.printStackTrace();
        }
    }

    // Display current courses
    private void displayCurrentCourses() {
        currentCourseContainer.getChildren().clear();

        List<Course> coursesFromDb = db.getAllCourses();

        if (coursesFromDb.isEmpty()) {
            Label emptyLabel = new Label("You have no current Courses");
            currentCourseContainer.getChildren().add(emptyLabel);
        } else {
            for (Course course : coursesFromDb) {
                VBox courseCard = createCourseCard(course);
                currentCourseContainer.getChildren().add(courseCard);
            }
        }
    }
}
