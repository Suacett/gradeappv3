package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class AssessmentController {

    public VBox assessmentContainer;
    @FXML
    private VBox currentAssessmentContainer;
    @FXML
    private VBox newAssessmentInputContainer;

    private Database db = new Database();

    @FXML
    private void initialize() {
        displayCurrentAssessments();
    }

    // Handle add assessment click event
    @FXML
    private void handleAddAssessmentButtonAction() {
        VBox assessmentInputBox = createAssessmentInputBox();
        newAssessmentInputContainer.getChildren().add(assessmentInputBox);
    }

    // Create input box for new assessment
    private VBox createAssessmentInputBox() {
        VBox assessmentInputBox = new VBox();
        assessmentInputBox.setPadding(new Insets(20, 20, 20, 20));
        assessmentInputBox.setSpacing(10);

        Label assessmentNameLabel = new Label("Assessment Name:");
        TextField assessmentNameField = new TextField();
        assessmentNameField.setPromptText("Assessment name");

        Label assessmentDescriptionLabel = new Label("Assessment Description:");
        TextField assessmentDescriptionField = new TextField();
        assessmentDescriptionField.setPromptText("Description");

        Label assessmentWeightLabel = new Label("Weight:");
        TextField assessmentWeightField = new TextField();
        assessmentWeightField.setPromptText("Weight (0-100)");

        Label assessmentMaxScoreLabel = new Label("Max Score:");
        TextField assessmentMaxScoreField = new TextField();
        assessmentMaxScoreField.setPromptText("Max score");

        Button submitButton = new Button("+ Add Assessment");
        submitButton.setOnAction(event -> handleSubmitAssessmentButtonAction(
                assessmentNameField,
                assessmentDescriptionField,
                assessmentWeightField,
                assessmentMaxScoreField
        ));

        assessmentInputBox.getChildren().addAll(
                assessmentNameLabel,
                assessmentNameField,
                assessmentDescriptionLabel,
                assessmentDescriptionField,
                assessmentWeightLabel,
                assessmentWeightField,
                assessmentMaxScoreLabel,
                assessmentMaxScoreField,
                submitButton
        );
        return assessmentInputBox;
    }

    // Handle submit button action for adding a new assessment
    private void handleSubmitAssessmentButtonAction(TextField assessmentNameField, TextField assessmentDescriptionField, TextField assessmentWeightField, TextField assessmentMaxScoreField) {
        String assessmentName = assessmentNameField.getText();
        String assessmentDescription = assessmentDescriptionField.getText();
        double assessmentWeight;
        double maxScore;

        try {
            assessmentWeight = Double.parseDouble(assessmentWeightField.getText());
            maxScore = Double.parseDouble(assessmentMaxScoreField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for weight or max score.");
            return;
        }

        if (!assessmentName.isEmpty() && !assessmentDescription.isEmpty()) {
            try {
                Assessment newAssessment = new Assessment(assessmentName, assessmentDescription, assessmentWeight, maxScore);
                db.addAssessment(newAssessment);
                assessmentNameField.clear();
                assessmentDescriptionField.clear();
                assessmentWeightField.clear();
                assessmentMaxScoreField.clear();
                displayCurrentAssessments();
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        } else {
            System.out.println("The form is incomplete...");
        }
    }

    // Create a card to display the current assessments
    private VBox createAssessmentCard(Assessment assessment) {
        VBox assessmentCard = new VBox();
        assessmentCard.setPadding(new Insets(10));
        assessmentCard.setSpacing(10);
        assessmentCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 5px;");

        Label assessmentNameLabel = new Label(assessment.getName());
        Label assessmentDescriptionLabel = new Label(assessment.getDescription());
        Label assessmentWeightLabel = new Label("Weight: " + assessment.getWeight());
        Label assessmentMaxScoreLabel = new Label("Max Score: " + assessment.getMaxScore());

        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> handleEditAssessmentButtonAction(assessment));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            db.delete("assessments", "id", String.valueOf(assessment.getId())); // Ensure assessmentId is unique
            displayCurrentAssessments();
        });

        buttonContainer.getChildren().addAll(editButton, deleteButton);
        assessmentCard.getChildren().addAll(assessmentNameLabel, assessmentDescriptionLabel, assessmentWeightLabel, assessmentMaxScoreLabel, buttonContainer);
        return assessmentCard;
    }

    // Handle edit button action
    private void handleEditAssessmentButtonAction(Assessment assessment) {
        TextField assessmentNameField = new TextField(assessment.getName());
        TextField assessmentDescriptionField = new TextField(assessment.getDescription());
        TextField assessmentWeightField = new TextField(Double.toString(assessment.getWeight()));
        TextField assessmentMaxScoreField = new TextField(Double.toString(assessment.getMaxScore()));

        Button saveButton = new Button("Save");

        saveButton.setOnAction(event -> {
            String newName = assessmentNameField.getText();
            String newDescription = assessmentDescriptionField.getText();
            double newWeight;
            double newMaxScore;

            try {
                newWeight = Double.parseDouble(assessmentWeightField.getText());
                newMaxScore = Double.parseDouble(assessmentMaxScoreField.getText());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for weight or max score.");
                return;
            }

            if (!newName.isEmpty() && !newDescription.isEmpty()) {
                assessment.setName(newName);
                assessment.setDescription(newDescription);
                assessment.setWeight(newWeight);
                assessment.setMaxScore(newMaxScore);
                db.updateAssessment(assessment);  // Ensure this method exists in the Database class
                displayCurrentAssessments();
            } else {
                System.out.println("The form is incomplete...");
            }
        });

        VBox editAssessmentBox = new VBox(10, new Label("Edit Assessment"), assessmentNameField, assessmentDescriptionField, assessmentWeightField, assessmentMaxScoreField, saveButton);
        currentAssessmentContainer.getChildren().clear();
        currentAssessmentContainer.getChildren().add(editAssessmentBox);
    }

    // Display current assessments
    private void displayCurrentAssessments() {
        currentAssessmentContainer.getChildren().clear();
        List<Assessment> assessmentsFromDb = db.getAllAssessments();
        if (assessmentsFromDb.isEmpty()) {
            Label emptyLabel = new Label("You have no current assessments");
            currentAssessmentContainer.getChildren().add(emptyLabel);
        } else {
            for (Assessment assessment : assessmentsFromDb) {
                VBox assessmentCard = createAssessmentCard(assessment);
                currentAssessmentContainer.getChildren().add(assessmentCard);
            }
        }
    }
}
