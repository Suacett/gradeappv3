package com.gradeapp.controller;

import java.util.ArrayList;

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
        String updatedName = courseNameField.getText();
        String updatedDescription = courseDescriptionField.getText();

        course.setName(updatedName);
        course.setDescription(updatedDescription);
        course.setOutcomes(new ArrayList<>(outcomes));

        db.saveCourse(course);

        content.getChildren().setAll(previousView);
    }

    @FXML
    private void cancelEditing() {
        content.getChildren().setAll(previousView);
    }

    @FXML
    private void addOutcome() {
        Outcome newOutcome = new Outcome("", "New Outcome", "Description", 0.0);
        outcomes.add(newOutcome);
    }
    
    @FXML
    private void removeSelectedOutcome() {
        Outcome selectedOutcome = outcomesTable.getSelectionModel().getSelectedItem();
        if (selectedOutcome != null) {
            outcomes.remove(selectedOutcome);
        }
    }
}