package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Outcome;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.util.List;

public class AssessmentController {

    @FXML private VBox currentAssessmentContainer;
    @FXML private VBox newAssessmentInputContainer;
    @FXML private TableView<Assessment> assessmentTable;
    @FXML private TableView<AssessmentPart> partsTable;
    @FXML private TableView<Outcome> outcomeTable;
    @FXML private VBox outcomeInputContainer;


    

    private Database db = new Database();
    private Assessment currentAssessment;

    @FXML
    private void initialize() {
        displayCurrentAssessments();
        setupAssessmentTable();
        setupPartsTable();
        setupOutcomeTable();
        setupOutcomeInputForm();

        assessmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updatePartsTable(newSelection);
                updateOutcomeTable(newSelection);
            }
        });
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

        @FXML
    private void setupAssessmentTable() {
        TableColumn<Assessment, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Assessment, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Assessment, Double> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        TableColumn<Assessment, Double> maxScoreColumn = new TableColumn<>("Max Score");
        maxScoreColumn.setCellValueFactory(new PropertyValueFactory<>("maxScore"));

        assessmentTable.getColumns().addAll(nameColumn, descriptionColumn, weightColumn, maxScoreColumn);

        // Populate the table
        ObservableList<Assessment> assessments = FXCollections.observableArrayList(db.getAllAssessments());
        assessmentTable.setItems(assessments);
    }

    @FXML
    private void setupPartsTable() {
        TableColumn<AssessmentPart, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<AssessmentPart, Double> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        TableColumn<AssessmentPart, Double> maxScoreColumn = new TableColumn<>("Max Score");
        maxScoreColumn.setCellValueFactory(new PropertyValueFactory<>("maxScore"));

        partsTable.getColumns().addAll(nameColumn, weightColumn, maxScoreColumn);

        // The parts table will be populated when an assessment is selected
    }

    private void setupOutcomeInputForm() {
        if (outcomeInputContainer == null) {
            System.err.println("Warning: outcomeInputContainer is null. Check your FXML file.");
            return;
        }

        TextField outcomeIdField = new TextField();
        outcomeIdField.setPromptText("Outcome ID");

        TextField outcomeNameField = new TextField();
        outcomeNameField.setPromptText("Outcome Name");

        TextField outcomeDescriptionField = new TextField();
        outcomeDescriptionField.setPromptText("Outcome Description");

        TextField outcomeWeightField = new TextField();
        outcomeWeightField.setPromptText("Outcome Weight (0-100)");

        Button addOutcomeButton = new Button("Add Outcome");
        addOutcomeButton.setOnAction(e -> handleAddOutcome(
                outcomeIdField.getText(),
                outcomeNameField.getText(),
                outcomeDescriptionField.getText(),
                outcomeWeightField.getText()
        ));

        outcomeInputContainer.getChildren().addAll(
                outcomeIdField, outcomeNameField, outcomeDescriptionField, outcomeWeightField, addOutcomeButton
        );
    }

    private void handleAddOutcome(String id, String name, String description, String weightStr) {
        Assessment selectedAssessment = assessmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssessment == null) {
            showAlert("Please select an assessment first.");
            return;
        }
    
        try {
            double weight = Double.parseDouble(weightStr);
            if (weight < 0 || weight > 100) {
                throw new IllegalArgumentException("Weight must be between 0 and 100");
            }
    
            Outcome outcome = new Outcome(id, name, description, weight);
            selectedAssessment.addOutcome(outcome, weight);
            
            db.addOutcome(outcome, String.valueOf(selectedAssessment.getId()));
            
            updateOutcomeTable(selectedAssessment);
        } catch (NumberFormatException e) {
            showAlert("Invalid weight. Please enter a number between 0 and 100.");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
        }
    }

    private void updateOutcomeTable(Assessment assessment) {
        ObservableList<Outcome> outcomes = FXCollections.observableArrayList(assessment.getOutcomeWeights().keySet());
        outcomeTable.setItems(outcomes);
        outcomeTable.refresh();
    }

    @FXML
    private void setupOutcomeTable() {
        TableColumn<Outcome, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<Outcome, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Outcome, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<Outcome, Number> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellValueFactory(cellData -> {
            Assessment selectedAssessment = assessmentTable.getSelectionModel().getSelectedItem();
            if (selectedAssessment != null) {
                return new SimpleDoubleProperty(selectedAssessment.getOutcomeWeight(cellData.getValue()));
            }
            return new SimpleDoubleProperty(0);
        });

        outcomeTable.getColumns().addAll(idColumn, nameColumn, descriptionColumn, weightColumn);
    }


    // Method to update parts table when an assessment is selected
    private void updatePartsTable(Assessment selectedAssessment) {
        if (selectedAssessment != null) {
            ObservableList<AssessmentPart> parts = selectedAssessment.getParts();
            partsTable.setItems(parts);
        } else {
            partsTable.getItems().clear();
        }
    }

    @FXML
    private void handleAddChildAssessmentButtonAction() {
        Assessment selectedAssessment = assessmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssessment == null) {
            showAlert("Please select a parent assessment first.");
            return;
        }

        Dialog<Assessment> dialog = new Dialog<>();
        dialog.setTitle("Add Child Assessment");
        dialog.setHeaderText("Create a new child assessment for " + selectedAssessment.getName());

        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        TextField weightField = new TextField();
        TextField maxScoreField = new TextField();

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Name:"), nameField,
                new Label("Description:"), descriptionField,
                new Label("Weight:"), weightField,
                new Label("Max Score:"), maxScoreField
        ));

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    String description = descriptionField.getText();
                    double weight = Double.parseDouble(weightField.getText());
                    double maxScore = Double.parseDouble(maxScoreField.getText());
                    Assessment childAssessment = new Assessment(name, description, weight, maxScore);
                    selectedAssessment.addChildAssessment(childAssessment);
                    db.addAssessment(childAssessment); // Assuming this method exists in your Database class
                    return childAssessment;
                } catch (NumberFormatException e) {
                    showAlert("Invalid input. Please enter valid numbers for weight and max score.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(childAssessment -> {
            updateAssessmentTable();
        });
    }

    @FXML
    private void handleAddPartButtonAction() {
        Assessment selectedAssessment = assessmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssessment == null) {
            showAlert("Please select an assessment first.");
            return;
        }

        Dialog<AssessmentPart> dialog = new Dialog<>();
        dialog.setTitle("Add Assessment Part");
        dialog.setHeaderText("Create a new part for " + selectedAssessment.getName());

        TextField nameField = new TextField();
        TextField weightField = new TextField();
        TextField maxScoreField = new TextField();

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Name:"), nameField,
                new Label("Weight:"), weightField,
                new Label("Max Score:"), maxScoreField
        ));

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    double weight = Double.parseDouble(weightField.getText());
                    double maxScore = Double.parseDouble(maxScoreField.getText());
                    return new AssessmentPart(-1, name, weight, maxScore); // Using -1 as a temporary ID
                } catch (NumberFormatException e) {
                    showAlert("Invalid input. Please enter valid numbers for weight and max score.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(part -> {
            selectedAssessment.addPart(part);
            updatePartsTable(selectedAssessment);
        });
    }

    @FXML
    private void handleLinkOutcomeButtonAction() {
        Assessment selectedAssessment = assessmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssessment == null) {
            showAlert("Please select an assessment first.");
            return;
        }

        Dialog<Outcome> dialog = new Dialog<>();
        dialog.setTitle("Link Outcome");
        dialog.setHeaderText("Link an outcome to " + selectedAssessment.getName());

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        TextField weightField = new TextField();

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("ID:"), idField,
                new Label("Name:"), nameField,
                new Label("Description:"), descriptionField,
                new Label("Weight:"), weightField
        ));

        ButtonType linkButtonType = new ButtonType("Link", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(linkButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == linkButtonType) {
                try {
                    String id = idField.getText();
                    String name = nameField.getText();
                    String description = descriptionField.getText();
                    double weight = Double.parseDouble(weightField.getText());
                    return new Outcome(id, name, description, weight);
                } catch (NumberFormatException e) {
                    showAlert("Invalid input. Please enter a valid number for weight.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(outcome -> {
            selectedAssessment.addOutcome(outcome, outcome.getWeight());
            updateOutcomeTable(selectedAssessment);
        });
    }

    private void updateAssessmentTable() {
        ObservableList<Assessment> assessments = FXCollections.observableArrayList(db.getAllAssessments());
        assessmentTable.setItems(assessments);
        assessmentTable.refresh();
    }

}
