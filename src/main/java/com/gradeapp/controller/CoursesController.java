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
        VBox courseCard = new VBox(10);
        HBox buttons = new HBox(10);
        courseCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px;");

        Label nameLabel = new Label("Name: " + course.getName());
        Label idLabel = new Label("ID: " + course.getId());
        Label descriptionLabel = new Label("Description: " + course.getDescription());

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
        buttons.getChildren().addAll(viewDetailsButton, editButton, deleteButton);

        courseCard.getChildren().addAll(nameLabel, idLabel, descriptionLabel, buttons);
        VBox.setMargin(courseCard, new Insets(0, 0, 10, 0));
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