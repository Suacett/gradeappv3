package com.gradeapp.controller;

import com.gradeapp.model.Course;
import com.gradeapp.model.Outcome;
import com.gradeapp.database.Database;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CoursesController {

    @FXML
    private VBox currentCourseContainer;

    @FXML
    private VBox newCourseInputContainer;

    @FXML
    private VBox content;

    private Database db = new Database();

    @FXML
    private void initialize() {
        db.initialiseDatabase();
        displayCurrentCourses();
    }

    @FXML
    private void handleAddCourseButtonAction() {
        VBox courseInputBox = createCourseInputBox();
        newCourseInputContainer.getChildren().add(courseInputBox);
    }

    private VBox createCourseInputBox() {
        VBox courseInputBox = new VBox();
        courseInputBox.setPadding(new Insets(20));
        courseInputBox.setSpacing(10);

        TextField courseNameField = new TextField();
        courseNameField.setPromptText("Course name");

        TextField courseDescriptionField = new TextField();
        courseDescriptionField.setPromptText("Course description");

        TableView<Outcome> outcomesTable = new TableView<>();
        setupOutcomesTable(outcomesTable);

        Button addOutcomeButton = new Button("Add Outcome");
        addOutcomeButton.setOnAction(e -> addOutcome(outcomesTable));

        Button submitButton = new Button("+ Add Course");
        submitButton.setOnAction(event -> handleSubmitButtonAction(courseNameField, courseDescriptionField, outcomesTable));

        courseInputBox.getChildren().addAll(
                new Label("Course Name:"), courseNameField,
                new Label("Course Description:"), courseDescriptionField,
                new Label("Outcomes:"), outcomesTable,
                addOutcomeButton, submitButton
        );
        return courseInputBox;
    }

    private void setupOutcomesTable(TableView<Outcome> table) {
        TableColumn<Outcome, String> idColumn = new TableColumn<>("Identifier");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<Outcome, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Outcome, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<Outcome, Double> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellValueFactory(cellData -> cellData.getValue().weightProperty().asObject());

        table.getColumns().addAll(idColumn, nameColumn, descriptionColumn, weightColumn);
        table.setEditable(true);
    }

    private void addOutcome(TableView<Outcome> table) {
        Outcome newOutcome = new Outcome("", "", "", 0.0);
        table.getItems().add(newOutcome);
    }

    private void handleSubmitButtonAction(TextField courseNameField, TextField courseDescriptionField, TableView<Outcome> outcomesTable) {
        String courseName = courseNameField.getText();
        String courseDescription = courseDescriptionField.getText();
        if (!courseName.isEmpty() && !courseDescription.isEmpty()) {
            Course newCourse = new Course(courseName, courseDescription);

            for (Outcome outcome : outcomesTable.getItems()) {
                newCourse.addOutcome(outcome);
            }

            db.addCourse(newCourse);

            courseNameField.clear();
            courseDescriptionField.clear();
            outcomesTable.getItems().clear();
            displayCurrentCourses();
        } else {
            showAlert();
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("Please finish the form");
        alert.showAndWait();
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
