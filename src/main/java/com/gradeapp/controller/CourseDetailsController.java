package com.gradeapp.controller;

import java.util.ArrayList;
import java.util.Optional;

import com.gradeapp.database.Database;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Outcome;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class CourseDetailsController {

    @FXML
    private TextField courseIdField;
    @FXML
    private TextField courseNameField;
    @FXML
    private TextField courseDescriptionField;
    @FXML
    private TableView<Outcome> outcomesTable;
    @FXML
    private TableColumn<Outcome, String> identifierColumn;
    @FXML
    private TableColumn<Outcome, String> descriptionColumn;
    @FXML
    private TableColumn<Outcome, Double> weightColumn;
    @FXML
    private TableView<Classes> classesTable;
    @FXML
    private TableColumn<Classes, String> classNameColumn;
    @FXML
    private TableColumn<Classes, String> classIdColumn;

    @FXML
    private TableColumn<Outcome, String> nameColumn;

    private CoursesController coursesController;

    private Course course;
    private Database db = new Database();

    private ObservableList<Outcome> outcomes;

    @FXML
    private void initialize() {
    }

    private void setupClassesTable() {
        classNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        classIdColumn.setCellValueFactory(cellData -> cellData.getValue().classIdProperty());

        java.util.List<Classes> classes = db.getClassesForCourse(course.getId());
        classesTable.setItems(FXCollections.observableArrayList(classes));
    }

    public void setCourse(Course course) {
        this.course = course;
        courseIdField.setText(course.getId());
        courseNameField.setText(course.getName());
        courseDescriptionField.setText(course.getDescription());
        outcomes = FXCollections.observableArrayList(course.getOutcomes());
        outcomesTable.setItems(outcomes);
        setupClassesTable();
        updateOutcomeWeights();
    }

    public void setCoursesController(CoursesController coursesController) {
        this.coursesController = coursesController;
    }

    @FXML
    private void saveCourseDetails() {
        if (validateCourse()) {
            String updatedId = courseIdField.getText().trim();
            String updatedName = courseNameField.getText().trim();
            String updatedDescription = courseDescriptionField.getText().trim();

            course.setId(updatedId);
            course.setName(updatedName);
            course.setDescription(updatedDescription);
            course.setOutcomes(new ArrayList<>(outcomes));

            try {
                db.updateCourse(course, course.getId());
                if (coursesController != null) {
                    coursesController.displayCurrentCourses();
                }
                closeWindow();
            } catch (Exception e) {
                showAlert("Error", "Failed to save course details: " + e.getMessage());
            }
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) courseIdField.getScene().getWindow();
        stage.close();
    }

    private boolean validateCourse() {
        if (courseNameField.getText().trim().isEmpty() || courseIdField.getText().trim().isEmpty()) {
            showAlert("Invalid Input", "Course name and ID cannot be empty.");
            return false;
        }

        double totalWeight = outcomes.stream().mapToDouble(Outcome::getWeight).sum();
        if (Math.abs(totalWeight - 100.0) > 0.01) {
            showAlert("Invalid Outcomes", "The total weight of outcomes must equal 100%.");
            return false;
        }

        return true;
    }

    private void updateOutcomeWeights() {
        int outcomeCount = outcomes.size();
        if (outcomeCount > 0) {
            double equalWeight = 100.0 / outcomeCount;
            for (Outcome outcome : outcomes) {
                outcome.setWeight(equalWeight);
            }
            outcomesTable.refresh();
        }
    }

    @FXML
    private void addOutcome() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Outcome");
        dialog.setHeaderText("Enter Outcome ID");
        dialog.setContentText("Please enter the outcome ID:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newId = result.get();
            Outcome newOutcome = new Outcome(newId, "New Outcome", "Description", 0.0);
            outcomes.add(newOutcome);
            updateOutcomeWeights();
            outcomesTable.refresh();
        }
    }

    @FXML
    private void removeSelectedOutcome() {
        Outcome selectedOutcome = outcomesTable.getSelectionModel().getSelectedItem();
        if (selectedOutcome != null) {
            outcomes.remove(selectedOutcome);
            updateOutcomeWeights();
        }
    }

    @FXML
    private void cancelEditing() {
        if (coursesController != null) {
            coursesController.displayCurrentCourses();
        }
        closeWindow();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}