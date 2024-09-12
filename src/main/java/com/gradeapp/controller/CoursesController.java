package com.gradeapp.controller;

import java.io.IOException;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Course;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CoursesController {

    @FXML
    private VBox currentCourseContainer;
    @FXML
    private VBox content;
    @FXML
    private HBox buttons;

    private Database db = new Database();

    @FXML
    private void initialize() {
        displayCurrentCourses();
    }

    @FXML
    private void handleAddCourseButtonAction() {
        openCourseEditWindow(null);
    }

    public void displayCurrentCourses() {
        currentCourseContainer.getChildren().clear();

        List<Course> coursesFromDb = db.getAllCourses();
        System.out.println("Courses fetched from DB: " + coursesFromDb.size());

        if (coursesFromDb.isEmpty()) {
            Label emptyLabel = new Label("You have no current Courses");
            currentCourseContainer.getChildren().add(emptyLabel);
        } else {
            for (Course course : coursesFromDb) {
                VBox courseCard = createCourseCard(course);
                currentCourseContainer.getChildren().add(courseCard);
                System.out.println("Added course card: " + course.getName());
            }
        }
    }

    public VBox getCurrentCourseContainer() {
        return currentCourseContainer;
    }

    private VBox createCourseCard(Course course) {
        VBox courseCard = new VBox();
        courseCard.getStyleClass().add("card");
        courseCard.setSpacing(10);

        HBox courseCardInfo = new HBox();
        courseCardInfo.setSpacing(10);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label nameLabel = new Label("Name: " + course.getName());
        Label idLabel = new Label("ID: " + course.getId());

        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);
        Button viewEditButton = new Button("View/Edit Details");
        viewEditButton.setOnAction(event -> openCourseDetailsWindow(course));
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> {
            db.deleteCourse(course.getId());
            displayCurrentCourses();
        });
        buttonContainer.getChildren().addAll(viewEditButton, deleteButton);

        courseCardInfo.getChildren().addAll(nameLabel, idLabel, spacer, buttonContainer);

        courseCard.getChildren().add(courseCardInfo);
        VBox.setMargin(courseCard, new Insets(0, 10, 10, 10));
        return courseCard;
    }

    private void openCourseEditWindow(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo3/course-creation.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Cannot find course-creation.fxml");
            }
            VBox courseEditView = loader.load();

            CourseEditController controller = loader.getController();
            controller.setCourse(course);
            controller.setCoursesController(this);

            Stage stage = new Stage();
            stage.setTitle(course == null ? "Add New Course" : "Edit Course");
            stage.setScene(new Scene(courseEditView));
            stage.showAndWait();

            displayCurrentCourses();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading course-creation.fxml: " + e.getMessage());
        }
    }

    private void openCourseDetailsWindow(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo3/course-details.fxml"));
            Parent root = loader.load();

            CourseDetailsController controller = loader.getController();
            controller.setCourse(course);
            controller.setCoursesController(this);

            Stage stage = new Stage();
            stage.setTitle("Course Details: " + course.getName());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            displayCurrentCourses();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading course-details.fxml: " + e.getMessage());
        }
    }

}