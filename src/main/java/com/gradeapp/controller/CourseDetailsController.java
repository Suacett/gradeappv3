package com.gradeapp.controller;

import java.util.ArrayList;
import java.util.Optional;

import com.gradeapp.database.Database;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Outcome;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

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
    private Node previousView;
    private VBox content;
    private ObservableList<Outcome> outcomes;

    @FXML
    private void initialize() {
    }

    private void setupTables() {
        setupOutcomesTable();
        setupClassesTable();
        outcomes.addListener((ListChangeListener<Outcome>) c -> updateOutcomeWeights());
    }

    private void setupClassesTable() {
        classNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        classIdColumn.setCellValueFactory(cellData -> cellData.getValue().classIdProperty());

        java.util.List<Classes> classes = db.getClassesForCourse(course.getId());
        classesTable.setItems(FXCollections.observableArrayList(classes));
    }

    private void setupOutcomesTable() {
        identifierColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        weightColumn.setCellValueFactory(cellData -> cellData.getValue().weightProperty().asObject());

        outcomesTable.setEditable(true);
        identifierColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        weightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        identifierColumn.setOnEditCommit(event -> {
            event.getRowValue().setId(event.getNewValue());
            outcomesTable.refresh();
        });
        nameColumn.setOnEditCommit(event -> {
            event.getRowValue().setName(event.getNewValue());
            outcomesTable.refresh();
        });
        descriptionColumn.setOnEditCommit(event -> {
            event.getRowValue().setDescription(event.getNewValue());
            outcomesTable.refresh();
        });
        weightColumn.setOnEditCommit(event -> {
            event.getRowValue().setWeight(event.getNewValue());
            outcomesTable.refresh();
        });

        outcomesTable.setItems(outcomes);
    }

    public void setCourse(Course course, Node previousView, VBox content) {
        this.course = course;
        this.previousView = previousView;
        this.content = content;
        courseIdField.setText(course.getId());
        courseNameField.setText(course.getName());
        courseDescriptionField.setText(course.getDescription());
        outcomes = FXCollections.observableArrayList(course.getOutcomes());
        setupTables();
        updateOutcomeWeights();
    }

    public void setCoursesController(CoursesController coursesController) {
        this.coursesController = coursesController;
    }

    @FXML
    private void saveCourseDetails() {
        String updatedId = courseIdField.getText().trim();
        String updatedName = courseNameField.getText().trim();
        String updatedDescription = courseDescriptionField.getText().trim();

        if (updatedId.isEmpty() || updatedName.isEmpty()) {
            showAlert("Invalid Input", "Course ID and Name cannot be empty.");
            return;
        }

        if (!isValidCourseId(updatedId)) {
            showAlert("Invalid Course ID", "Please enter a valid course ID (e.g., 'CS101', 'MATH2A', 'ENG303').");
            return;
        }

        String originalId = course.getId();
        if (!originalId.equals(updatedId) && !db.isCourseIdUnique(updatedId, originalId)) {
            showAlert("Duplicate Course ID", "This course ID already exists. Please choose a unique ID.");
            return;
        }

        course.setId(updatedId);
        course.setName(updatedName);
        course.setDescription(updatedDescription);

        updateOutcomeWeights();

        course.setOutcomes(new ArrayList<>(outcomes));

        db.updateCourse(course, originalId);

        coursesController.displayCurrentCourses();

        returnToCoursesView();

        showAlert("Success", "Course details saved successfully.", Alert.AlertType.INFORMATION);
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

    private void returnToCoursesView() {
        if (coursesController != null && content != null) {
            coursesController.displayCurrentCourses();
            content.getChildren().setAll(coursesController.getCurrentCourseContainer());
        }
    }

    private boolean isValidCourseId(String id) {
        return id.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{3,10}$");
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
        content.getChildren().setAll(previousView);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}