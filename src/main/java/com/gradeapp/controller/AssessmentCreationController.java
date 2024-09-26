package com.gradeapp.controller;

import java.util.HashMap;
import java.util.Map;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.Course;
import com.gradeapp.model.Outcome;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controller class for creating new assessments.
 * Manages the user interface for inputting assessment details and linking
 * outcomes.
 */
public class AssessmentCreationController {

    // ----------------------- FXML UI Components -----------------------

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField maxScoreField;

    @FXML
    private VBox outcomeCheckboxContainer;

    // ----------------------- Private Fields -----------------------

    // Selected course and the new assessment being created
    private Course selectedCourse;
    private Assessment newAssessment;

    // Maps each Outcome to its corresponding weight input field
    private Map<Outcome, TextField> outcomeWeightFields = new HashMap<>();

    // Database instance for data operations
    private Database db = new Database();

    // Callback to notify when a new assessment is created
    private AssessmentCreationCallback callback;

    // ----------------------- Initialization -----------------------

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Initialization logic can be added here if needed in the future.
    }

    // ----------------------- Setter Methods -----------------------

    /**
     * Sets the course for which the assessment is being created.
     * Also populates the outcomes associated with the selected course.
     *
     * @param course The selected Course object.
     */
    public void setCourse(Course course) {
        this.selectedCourse = course;
        populateOutcomes();
    }

    /**
     * Sets the callback to be invoked when a new assessment is created.
     *
     * @param callback An implementation of AssessmentCreationCallback.
     */
    public void setCallback(AssessmentCreationCallback callback) {
        this.callback = callback;
    }

    // ----------------------- Event Handlers -----------------------

    /**
     * Handles the action of saving a new assessment.
     * Validates input fields, creates the assessment, links selected outcomes,
     * and notifies the callback.
     */
    @FXML
    private void saveAssessment() {
        try {
            // Retrieve and parse input values
            String name = nameField.getText();
            String description = descriptionField.getText();
            double weight = Double.parseDouble(weightField.getText());
            double maxScore = Double.parseDouble(maxScoreField.getText());

            // Validate input fields
            if (name.isEmpty() || description.isEmpty()) {
                showAlert("Please fill in all the required fields.");
                return;
            }

            // Create a new Assessment object
            newAssessment = new Assessment(name, description, weight, maxScore);

            // Add the assessment to the database and retrieve its generated ID
            int assessmentId = db.addAssessment(newAssessment, selectedCourse.getId());
            newAssessment.setId(assessmentId);

            // Iterate through each outcome and link if selected
            for (Map.Entry<Outcome, TextField> entry : outcomeWeightFields.entrySet()) {
                Outcome outcome = entry.getKey();
                TextField weightInputField = entry.getValue();

                // Retrieve the corresponding checkbox for the outcome
                CheckBox checkbox = (CheckBox) outcomeCheckboxContainer.getChildren().get(
                        outcomeCheckboxContainer.getChildren().indexOf(weightInputField) - 1);

                if (checkbox.isSelected()) {
                    try {
                        // Parse the weight for the selected outcome
                        double outcomeWeight = Double.parseDouble(weightInputField.getText());

                        // Validate outcome weight
                        if (outcomeWeight < 0 || outcomeWeight > 100) {
                            showAlert("Weight for outcome '" + outcome.getName() + "' must be between 0 and 100.");
                            continue;
                        }

                        // Set the weight in the Assessment object and link to the database
                        newAssessment.setOutcomeWeight(outcome, outcomeWeight);
                        db.linkOutcomeToAssessment(assessmentId, outcome.getId(), outcomeWeight);
                    } catch (NumberFormatException e) {
                        showAlert("Invalid weight for outcome: " + outcome.getName());
                    }
                }
            }

            // Notify the callback if it is set
            if (callback != null) {
                callback.onAssessmentCreated(newAssessment);
            }

            // Close the assessment creation window
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Invalid input for weight or max score. Please enter valid numbers.");
        } catch (Exception e) {
            showAlert("An unexpected error occurred: " + e.getMessage());
        }
    }

    // ----------------------- Helper Methods -----------------------

    /**
     * Populates the outcomes with checkboxes and corresponding weight input fields
     * based on the selected course.
     */
    private void populateOutcomes() {
        outcomeCheckboxContainer.getChildren().clear();
        outcomeWeightFields.clear();

        if (selectedCourse != null) {
            for (Outcome outcome : selectedCourse.getOutcomes()) {
                // Create a checkbox for the outcome
                CheckBox cb = new CheckBox(outcome.getName());

                // Create a text field for inputting the weight of the outcome
                TextField weightInputField = new TextField();
                weightInputField.setPromptText("Weight (%)");

                // Add the checkbox and weight input field to the container
                outcomeCheckboxContainer.getChildren().addAll(cb, weightInputField);

                // Map the outcome to its weight input field for later reference
                outcomeWeightFields.put(outcome, weightInputField);
            }
        }
    }

    /**
     * Displays an error alert with the specified message.
     *
     * @param message The error message to display.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        nameField.getScene().getWindow().hide();
    }
}
