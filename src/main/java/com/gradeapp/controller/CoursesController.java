package com.gradeapp.controller;

import com.gradeapp.model.Course;
import com.gradeapp.database.Database;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CoursesController {

    @FXML private VBox currentCourseContainer;
    @FXML private VBox content;

    private Database db = new Database();

    @FXML
    private void initialize() {
        db.initialiseDatabase();
        displayCurrentCourses();
    }

    @FXML
    private void handleAddCourseButtonAction() {
        openCourseEditWindow(null);
    }

    private void openCourseEditWindow(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo3/course-creation.fxml"));
            VBox courseEditView = loader.load();

            CourseEditController controller = loader.getController();
            
            if (course != null) {
                controller.setCourse(course);
            }

            Stage stage = new Stage();
            stage.setTitle(course == null ? "Add New Course" : "Edit Course");
            stage.setScene(new Scene(courseEditView));
            stage.showAndWait();

            // Refresh the course list after the window is closed
            displayCurrentCourses();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading course-creation.fxml: " + e.getMessage());
        }
    }

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

    private VBox createCourseCard(Course course) {
        VBox courseCard = new VBox(10);
        courseCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px;");

        Label nameLabel = new Label("Name: " + course.getName());
        Label idLabel = new Label("ID: " + course.getId());
        Label descriptionLabel = new Label("Description: " + course.getDescription());

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> openCourseEditWindow(course));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            db.deleteCourse(course.getId());
            displayCurrentCourses();
        });

        courseCard.getChildren().addAll(nameLabel, idLabel, descriptionLabel, editButton, deleteButton);
        return courseCard;
    }
}