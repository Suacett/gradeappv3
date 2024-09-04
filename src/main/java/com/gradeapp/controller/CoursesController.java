package com.gradeapp.controller;

import com.gradeapp.model.Course;
import com.gradeapp.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class CoursesController {

    @FXML private VBox currentCourseContainer;
    @FXML private VBox content;
    @FXML private HBox buttons;

    private Database db = new Database();

    @FXML
    private void initialize() {
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
            } else {
                controller.setCourse(new Course("", "", ""));
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
        System.out.println("Courses fetched from DB: " + coursesFromDb.size()); // Debug print

        if (coursesFromDb.isEmpty()) {
            Label emptyLabel = new Label("You have no current Courses");
            currentCourseContainer.getChildren().add(emptyLabel);
        } else {
            for (Course course : coursesFromDb) {
                VBox courseCard = createCourseCard(course);
                currentCourseContainer.getChildren().add(courseCard);
                System.out.println("Added course card: " + course.getName()); // Debug print
            }
        }
    }
    private VBox createCourseCard(Course course) {
        VBox courseCard = new VBox();
        courseCard.getStyleClass().add("card");
        courseCard.setSpacing(10);
        // HBox to hold the course info and buttons
        HBox courseCardInfo = new HBox();
        courseCardInfo.setSpacing(10); // Add spacing between elements
        // Create a Region to act as a spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        // Labels for course information
        Label nameLabel = new Label("Name: " + course.getName());
        Label idLabel = new Label("ID: " + course.getId());
        // Create HBox to hold the buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setOnAction(event -> openCourseDetailsWindow(course));
        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> openCourseEditWindow(course));
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> {
            db.deleteCourse(course.getId());
            displayCurrentCourses();
        });
        buttonContainer.getChildren().addAll(viewDetailsButton, editButton, deleteButton);
        // Add the labels, spacer, and button container to the courseCardInfo HBox
        courseCardInfo.getChildren().addAll(nameLabel, idLabel, spacer, buttonContainer);
        // Add the courseCardInfo HBox to the courseCard VBox
        courseCard.getChildren().add(courseCardInfo);
        VBox.setMargin(courseCard, new Insets(0, 10, 10, 10));
        return courseCard;
    }
    
    private void openCourseDetailsWindow(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo3/course-details.fxml"));
            VBox courseDetailsView = loader.load();

            CourseDetailsController controller = loader.getController();
            controller.setCourse(course, currentCourseContainer, content);

            content.getChildren().setAll(courseDetailsView);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading course-details.fxml: " + e.getMessage());
        }
    }



}