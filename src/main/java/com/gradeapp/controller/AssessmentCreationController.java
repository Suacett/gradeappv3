package com.gradeapp.controller;

import com.gradeapp.model.*;
import com.gradeapp.database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssessmentCreationController {
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField weightField;
    @FXML private TextField maxScoreField;
    @FXML private VBox outcomeCheckboxContainer;
    
    private Course selectedCourse;
    private Assessment newAssessment;
    private Map<Outcome, TextField> outcomeWeightFields = new HashMap<>();
    private Database db = new Database();
    private AssessmentCreationCallback callback;

    @FXML
    public void initialize() {
        // Initialization code if needed
    }

    @FXML
    private void saveAssessment() {
        try {
            String name = nameField.getText();
            String description = descriptionField.getText();
            double weight = Double.parseDouble(weightField.getText());
            double maxScore = Double.parseDouble(maxScoreField.getText());

            newAssessment = new Assessment(name, description, weight, maxScore);
            int assessmentId = db.addAssessment(newAssessment, selectedCourse.getId());
            newAssessment.setId(assessmentId);

            // Save linked outcomes
            for (Map.Entry<Outcome, TextField> entry : outcomeWeightFields.entrySet()) {
                Outcome outcome = entry.getKey();
                TextField weightField = entry.getValue();
                CheckBox checkbox = (CheckBox) outcomeCheckboxContainer.getChildren().get(
                    outcomeCheckboxContainer.getChildren().indexOf(weightField) - 1);
                
                if (checkbox.isSelected()) {
                    try {
                        double outcomeWeight = Double.parseDouble(weightField.getText());
                        newAssessment.setOutcomeWeight(outcome, outcomeWeight);
                        db.linkOutcomeToAssessment(assessmentId, outcome.getId(), outcomeWeight);
                    } catch (NumberFormatException e) {
                        showAlert("Invalid weight for outcome: " + outcome.getName());
                    }
                }
            }

            if (callback != null) {
                callback.onAssessmentCreated(newAssessment);
            }

            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Invalid input for weight or max score.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        nameField.getScene().getWindow().hide();
    }

    public void setCourse(Course course) {
        this.selectedCourse = course;
        populateOutcomes();
    }

    public void setCallback(AssessmentCreationCallback callback) {
        this.callback = callback;
    }

    private void populateOutcomes() {
        outcomeCheckboxContainer.getChildren().clear();
        outcomeWeightFields.clear();
        if (selectedCourse != null) {
            for (Outcome outcome : selectedCourse.getOutcomes()) {
                CheckBox cb = new CheckBox(outcome.getName());
                TextField weightField = new TextField();
                weightField.setPromptText("Weight");
                outcomeCheckboxContainer.getChildren().addAll(cb, weightField);
                outcomeWeightFields.put(outcome, weightField);
            }
        }
    }
}