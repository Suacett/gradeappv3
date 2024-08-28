package com.gradeapp.controller;

import com.gradeapp.model.Course;
import com.gradeapp.model.Outcome;
import com.gradeapp.database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.converter.DoubleStringConverter;

public class CourseEditController {

    @FXML private TextField courseNameField;
    @FXML private TextField courseIdField;
    @FXML private TextArea courseDescriptionField;
    @FXML private TableView<Outcome> outcomesTable;
    @FXML private TableColumn<Outcome, String> outcomeIdentifierColumn;
    @FXML private TableColumn<Outcome, String> outcomeNameColumn;
    @FXML private TableColumn<Outcome, String> outcomeDescriptionColumn;
    @FXML private TableColumn<Outcome, Double> outcomeWeightColumn;
    @FXML private Label totalWeightLabel;

    private Course course;
    private Database db = new Database();
    private ObservableList<Outcome> outcomes = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupOutcomesTable();
        updateTotalWeight();
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

        outcomeIdentifierColumn.setOnEditCommit(event -> {
            event.getRowValue().setId(event.getNewValue());
            updateTotalWeight();
        });
        outcomeNameColumn.setOnEditCommit(event -> {
            event.getRowValue().setName(event.getNewValue());
            updateTotalWeight();
        });
        outcomeDescriptionColumn.setOnEditCommit(event -> {
            event.getRowValue().setDescription(event.getNewValue());
            updateTotalWeight();
        });
        outcomeWeightColumn.setOnEditCommit(event -> {
            event.getRowValue().setWeight(event.getNewValue());
            updateTotalWeight();
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
        }
        updateTotalWeight();
    }

    @FXML
    private void addOutcome() {
        Outcome newOutcome = new Outcome("", "", "", 0.0);
        outcomes.add(newOutcome);
        updateTotalWeight();
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
        double totalWeight = outcomes.stream().mapToDouble(Outcome::getWeight).sum();
        totalWeightLabel.setText(String.format("Total Weight: %.2f%%", totalWeight));
        totalWeightLabel.setStyle(totalWeight == 100 ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    @FXML
    private void saveCourse() {
        if (validateCourse()) {
            if (course == null) {
                course = new Course(courseIdField.getText(), courseNameField.getText(), courseDescriptionField.getText());
            } else {
                course.setId(courseIdField.getText());
                course.setName(courseNameField.getText());
                course.setDescription(courseDescriptionField.getText());
            }
            course.setOutcomes(outcomes);
            db.updateCourse(course);
            closeWindow();
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private boolean validateCourse() {
        if (courseNameField.getText().isEmpty() || courseIdField.getText().isEmpty()) {
            showAlert("Course name and ID are required.");
            return false;
        }
        double totalWeight = outcomes.stream().mapToDouble(Outcome::getWeight).sum();
        if (Math.abs(totalWeight - 100) > 0.01) {
            showAlert("Total outcome weight must equal 100%.");
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
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