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

/**
 * Controller class for managing courses in the GradeApp.
 * Handles displaying current courses, adding new courses,
 * viewing/editing course details, and deleting courses.
 */
public class CoursesController {

    // ----------------------------- FXML UI Components --------------------

    @FXML
    private VBox currentCourseContainer; // Container to display the list of current courses

    @FXML
    private VBox content; // General content area 

    @FXML
    private HBox buttons; // Container for buttons

    // ----------------------------- Non-UI Fields -----------------------------

    private Database db = new Database(); // Database instance for data operations

    // ----------------------------- Initialization -----------------------------

    /**
     * Initializes the controller after its root element has been completely
     * processed.
     * Populates the current courses displayed in the UI.
     */
    @FXML
    private void initialize() {
        displayCurrentCourses();
    }

    // ----------------------------- Action Handlers -----------------------------

    /**
     * Handles the action when the "Add Course" button is clicked.
     * Opens the course edit window for creating a new course.
     */
    @FXML
    private void handleAddCourseButtonAction() {
        openCourseEditWindow(null);
    }

    // ----------------------------- Primary Methods -----------------------------

    /**
     * Displays all current courses fetched from the database.
     * Clears the existing course display and repopulates it.
     * If no courses are available, shows a message indicating no current courses.
     */
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

    /**
     * Creates a visual card (VBox) representing a single course.
     * Includes course name, ID, and action buttons for viewing/editing and
     * deleting.
     *
     * @param course The Course object to be represented in the card
     * @return A VBox containing the course information and action buttons
     */
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

    /**
     * Opens the course edit window for creating a new course or editing an existing
     * one.
     *
     * @param course The Course object to be edited. If null, a new course is being
     *               created.
     */
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

    /**
     * Opens the course details window to view or edit the selected course.
     *
     * @param course The Course object whose details are to be viewed/edited
     */
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

    // ----------------------------- Getter Methods -----------------------------

    /**
     * Provides access to the currentCourseContainer VBox.
     *
     * @return The VBox that contains the list of current courses
     */
    public VBox getCurrentCourseContainer() {
        return currentCourseContainer;
    }

}
