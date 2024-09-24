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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

public class CourseEditController {

    @FXML
    private TextField courseNameField;
    @FXML
    private TextField courseIdField;
    @FXML
    private TextArea courseDescriptionField;
    @FXML
    private TableView<Outcome> outcomesTable;
    @FXML
    private TableColumn<Outcome, String> outcomeIdentifierColumn;
    @FXML
    private TableColumn<Outcome, String> outcomeNameColumn;
    @FXML
    private TableColumn<Outcome, String> outcomeDescriptionColumn;
    @FXML
    private TableColumn<Outcome, Double> outcomeWeightColumn;
    @FXML
    private Label totalWeightLabel;
    @FXML
    private TableView<Classes> classesTable;
    @FXML
    private TableColumn<Classes, String> classNameColumn;
    @FXML
    private TableColumn<Classes, String> classIdColumn;

    @SuppressWarnings("unused")
    private CoursesController coursesController;

    private Course course;
    private Database db = new Database();
    private ObservableList<Outcome> outcomes = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        outcomes = FXCollections.observableArrayList();
        setupOutcomesTable();
        updateTotalWeight();
        System.out.println("CourseEditController initialized");

        outcomeIdentifierColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        outcomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        outcomeDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        outcomeWeightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
    }

    private void setupOutcomesTable() {
        outcomeIdentifierColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        outcomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        outcomeDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        outcomeWeightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        outcomeIdentifierColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        outcomeNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        outcomeDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        outcomeWeightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        outcomeNameColumn.setOnEditCommit(event -> {
            Outcome outcome = event.getRowValue();
            outcome.setName(event.getNewValue());
            outcomesTable.refresh();
        });
        outcomeDescriptionColumn.setOnEditCommit(event -> {
            Outcome outcome = event.getRowValue();
            outcome.setDescription(event.getNewValue());
            outcomesTable.refresh();
        });
        outcomeIdentifierColumn.setOnEditCommit(event -> {
            Outcome outcome = event.getRowValue();
            outcome.setId(event.getNewValue());
            outcomesTable.refresh();
        });
        outcomesTable.setItems(outcomes);
        outcomesTable.setEditable(true);
    }

    public void setCourse(Course course) {
        this.course = course;
        if (course != null) {
            courseIdField.setText(course.getId());
            courseNameField.setText(course.getName());
            courseDescriptionField.setText(course.getDescription());
            outcomes.setAll(course.getOutcomes());
            System.out.println("Editing existing course: " + course.getName() + ", Outcomes: " + outcomes.size());
        } else {
            courseIdField.clear();
            courseNameField.clear();
            courseDescriptionField.clear();
            outcomes.clear();
            System.out.println("Creating new course");
        }
        outcomesTable.setItems(outcomes);
        updateTotalWeight();
    }

    public void setCoursesController(CoursesController coursesController) {
        this.coursesController = coursesController;
    }

    @FXML
    private void addOutcome() {
        String newId = "";
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Outcome");
        dialog.setHeaderText("Enter Outcome ID");
        dialog.setContentText("Please enter the outcome ID:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            newId = result.get();
        } else {
            return;
        }

        Outcome newOutcome = new Outcome(newId, "New Outcome", "Description", 0.0);
        outcomes.add(newOutcome);
        updateTotalWeight();
        outcomesTable.refresh();
        System.out.println("Outcome added: " + newOutcome);
    }

    @FXML
    private void removeSelectedOutcome() {
        Outcome selectedOutcome = outcomesTable.getSelectionModel().getSelectedItem();
        if (selectedOutcome != null) {
            outcomes.remove(selectedOutcome);
            updateTotalWeight();
        }
    }

    private void updateTotalWeight() {
        int outcomeCount = outcomes.size();
        if (outcomeCount > 0) {
            double equalWeight = 100.0 / outcomeCount;
            for (Outcome outcome : outcomes) {
                outcome.setWeight(equalWeight);
            }
        }
        totalWeightLabel.setText(String.format("Total Outcomes: %d", outcomeCount));
        totalWeightLabel.setStyle("-fx-text-fill: black;");
    }

    @FXML
    private void saveCourse() {
        if (validateCourse()) {
            String courseId = courseIdField.getText();
            if (course == null) {
                course = new Course(courseId, courseNameField.getText(), courseDescriptionField.getText());
            } else {
                course.setId(courseId);
                course.setName(courseNameField.getText());
                course.setDescription(courseDescriptionField.getText());
            }
            updateTotalWeight();
            course.setOutcomes(new ArrayList<>(outcomes));
            try {
                db.saveCourse(course);
                System.out.println("Course saved: " + course.getName() + " (ID: " + course.getId() + ")");
                System.out.println("Number of outcomes saved: " + outcomes.size());
                closeWindow();
            } catch (IllegalArgumentException e) {
                showAlert("Invalid Course ID", e.getMessage());
            }
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private boolean validateCourse() {
        if (courseNameField.getText().isEmpty() || courseIdField.getText().isEmpty()) {
            showAlert("Course name and ID are required.", null);
            return false;
        }
        return true;
    }

    private void showAlert(String message, String string) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        courseNameField.getScene().getWindow().hide();
    }
}