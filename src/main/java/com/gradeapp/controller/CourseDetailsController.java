package com.gradeapp.controller;

import java.util.ArrayList;
import java.util.Optional;

import com.gradeapp.database.Database;
import com.gradeapp.model.Course;
import com.gradeapp.model.Outcome;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.converter.DoubleStringConverter;

public class CourseDetailsController {

    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField courseDescriptionField;
    @FXML private TableView<Outcome> outcomesTable;
    @FXML private TableColumn<Outcome, String> identifierColumn;
    @FXML private TableColumn<Outcome, String> descriptionColumn;
    @FXML private TableColumn<Outcome, Double> weightColumn;
    

    private Course course;
    private Database db = new Database();
    private Node previousView;
    private VBox content;
    private ObservableList<Outcome> outcomes;

    @FXML
    private void initialize() {
        setupOutcomesTable();
    }

    private void setupOutcomesTable() {
        identifierColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        identifierColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        weightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        identifierColumn.setOnEditCommit(event -> {
            event.getRowValue().setId(event.getNewValue());
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

        outcomesTable.setEditable(true);
    }


    public void setCourse(Course course, Node previousView, VBox content) {
        this.course = course;
        this.previousView = previousView;
        this.content = content;
        courseIdField.setText(course.getId());
        courseNameField.setText(course.getName());
        courseDescriptionField.setText(course.getDescription());
        outcomes = FXCollections.observableArrayList(course.getOutcomes());
        outcomesTable.setItems(outcomes);
    }

    private void updateOutcomesTable() {
        ObservableList<Outcome> outcomes = FXCollections.observableArrayList(course.getOutcomes());
        outcomesTable.setItems(outcomes);
    }

    @FXML
    private void saveCourseDetails() {
        String updatedId = courseIdField.getText();
        String updatedName = courseNameField.getText();
        String updatedDescription = courseDescriptionField.getText();
    
        if (!isValidCourseId(updatedId)) {
            showAlert("Invalid Course ID", "Please enter a valid course ID (e.g., 'CS101').");
            return;
        }
    
        if (!db.isCourseIdUnique(updatedId, course.getId())) {
            showAlert("Duplicate Course ID", "This course ID already exists. Please choose a unique ID.");
            return;
        }
    
        course.setId(updatedId);
        course.setName(updatedName);
        course.setDescription(updatedDescription);
        course.setOutcomes(new ArrayList<>(outcomes));
    
        db.saveCourse(course);
    
        content.getChildren().setAll(previousView);
    }

    private boolean isValidCourseId(String id) {
        return id.matches("^[A-Z]{2,4}[0-9]{1,4}$");
    }

    @FXML
    private void cancelEditing() {
        content.getChildren().setAll(previousView);
    }

@FXML
private void addOutcome() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Add New Outcome");
    dialog.setHeaderText("Enter Outcome ID");
    dialog.setContentText("Please enter the outcome ID (e.g., F5.1):");

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()) {
        String newId = result.get();
        Outcome newOutcome = new Outcome(newId, "New Outcome", "Description", 0.0);
        outcomes.add(newOutcome);
        outcomesTable.refresh();
    }
}
    

    @FXML
    private void removeSelectedOutcome() {
        Outcome selectedOutcome = outcomesTable.getSelectionModel().getSelectedItem();
        if (selectedOutcome != null) {
            outcomes.remove(selectedOutcome);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}