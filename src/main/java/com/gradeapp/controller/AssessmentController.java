package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import java.io.IOException;
import java.util.List;


public class AssessmentController implements AssessmentCreationCallback {

    @FXML private VBox currentAssessmentContainer;
    @FXML private VBox newAssessmentInputContainer;
    @FXML private TableView<Assessment> assessmentTable;
    @FXML private TableView<AssessmentPart> partsTable;
    @FXML private TableColumn<AssessmentPart, String> partNameColumn;
    @FXML private TableColumn<AssessmentPart, Double> partWeightColumn;
    @FXML private TableColumn<AssessmentPart, Double> partMaxScoreColumn;
    @FXML private TableView<Outcome> outcomeTable;
    @FXML private VBox outcomeInputContainer;
    @FXML private ComboBox<Course> courseSelector;


    

    private Database db = new Database();
    private Assessment currentAssessment;
    private Course selectedCourse;

    @FXML
    private void initialize() {
        setupCourseSelector();
        setupAssessmentTable();
        setupPartsTable();
        setupOutcomeTable();
    
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCourse = newSelection;
                updateAssessmentTable();
                updateOutcomeTable();
            }
        });
    
        assessmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected assessment changed to: " + newSelection.getName() + " (ID: " + newSelection.getId() + ")");
                currentAssessment = newSelection;
                updatePartsTable();
                updateOutcomeTableForAssessment(newSelection);
            }
        });
    }
    
    
    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);
        courseSelector.setCellFactory(lv -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getName() + " (" + course.getId() + ")");
                }
            }
        });
    
        courseSelector.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course == null ? "" : course.getName() + " (" + course.getId() + ")";
            }
    
            @Override
            public Course fromString(String string) {
                return null; // Not needed for this use case
            }
        });
    
        courseSelector.setOnAction(e -> {
            if (courseSelector.getValue() != null) {
                selectedCourse = courseSelector.getValue();
                updateAssessmentTable();
                updateOutcomeTable();
            }
        });
    }

    @FXML
    private void handleAddAssessmentButtonAction() {
        if (selectedCourse == null) {
            showAlert("Please select a course first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo3/assessment-creation.fxml"));
            Parent root = loader.load();
            AssessmentCreationController controller = loader.getController();
            controller.setCourse(selectedCourse);
            controller.setCallback(this);

            Stage stage = new Stage();
            stage.setTitle("Create New Assessment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading assessment creation view.");
        }
    }

    @Override
    public void onAssessmentCreated(Assessment newAssessment) {
        System.out.println("New assessment created: " + newAssessment.getName() + " (ID: " + newAssessment.getId() + ")");
        refreshAssessmentTable();
        assessmentTable.getSelectionModel().select(newAssessment);
        updatePartsTable();
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
                db.addAssessment(newAssessment, assessmentDescription);
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
        deleteButton.getStyleClass().add("delete-button");
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
        }


        private void updateAssessmentTable() {
            if (selectedCourse != null) {
                List<Assessment> assessments = db.getAssessmentsForCourse(selectedCourse.getId());
                ObservableList<Assessment> observableAssessments = FXCollections.observableArrayList(assessments);
                assessmentTable.setItems(observableAssessments);
                System.out.println("Loaded " + assessments.size() + " assessments for course: " + selectedCourse.getName());
            } else {
                assessmentTable.getItems().clear();
            }
        }

    @FXML
    private void setupPartsTable() {
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partWeightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        partMaxScoreColumn.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
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
            
            updateOutcomeTableForAssessment(selectedAssessment);
        } catch (NumberFormatException e) {
            showAlert("Invalid weight. Please enter a number between 0 and 100.");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML
    private void setupOutcomeTable() {
        TableColumn<Outcome, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Outcome, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Outcome, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Outcome, Number> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        outcomeTable.getColumns().addAll(idColumn, nameColumn, descriptionColumn, weightColumn);
    }

    private void updatePartsTable() {
        if (currentAssessment != null) {
            List<AssessmentPart> parts = db.getAssessmentParts(currentAssessment.getId());
            ObservableList<AssessmentPart> observableParts = FXCollections.observableArrayList(parts);
            partsTable.setItems(observableParts);
            System.out.println("Updating parts table for assessment: " + currentAssessment.getName() + " (ID: " + currentAssessment.getId() + ")");
            System.out.println("Number of parts: " + parts.size());
            
            // Debug: Print out each part
            for (AssessmentPart part : parts) {
                System.out.println("Part: " + part.getName() + " (ID: " + part.getId() + ")");
            }
        } else {
            partsTable.getItems().clear();
        }
    }


    private void refreshAssessmentTable() {
        if (selectedCourse != null) {
            ObservableList<Assessment> assessments = FXCollections.observableArrayList(db.getAssessmentsForCourse(selectedCourse.getId()));
            assessmentTable.setItems(assessments);
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
                    db.addAssessment(childAssessment, description);
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
    if (currentAssessment == null) {
        showAlert("Please select an assessment first.");
        return;
    }

    Dialog<AssessmentPart> dialog = new Dialog<>();
    dialog.setTitle("Add Assessment Part");
    dialog.setHeaderText("Create a new part for " + currentAssessment.getName());

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
                AssessmentPart newPart = new AssessmentPart(-1, name, weight, maxScore);
                int partId = db.addAssessmentPart(newPart, currentAssessment.getId());
                newPart.setId(partId);
                currentAssessment.addPart(newPart);
                return newPart;
            } catch (NumberFormatException e) {
                showAlert("Invalid input. Please enter valid numbers for weight and max score.");
                return null;
            }
        }
        return null;
    });

    dialog.showAndWait().ifPresent(part -> {
        updatePartsTable();
    });
}

    private void updateOutcomeTable() {
        if (selectedCourse != null) {
            ObservableList<Outcome> outcomes = FXCollections.observableArrayList(selectedCourse.getOutcomes());
            outcomeTable.setItems(outcomes);
        } else {
            outcomeTable.getItems().clear();
        }
    }

    private void updateOutcomeTableForAssessment(Assessment assessment) {
        if (assessment != null) {
            List<Outcome> outcomes = db.getOutcomesForAssessment(assessment.getId());
            ObservableList<Outcome> observableOutcomes = FXCollections.observableArrayList(outcomes);
            outcomeTable.setItems(observableOutcomes);
        } else {
            outcomeTable.getItems().clear();
        }
    }


    @FXML
    private void handleLinkOutcomeButtonAction() {
        Assessment selectedAssessment = assessmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssessment == null) {
            showAlert("Please select an assessment first.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Link Outcome");
        dialog.setHeaderText("Link an outcome to " + selectedAssessment.getName());

        ComboBox<Outcome> outcomeComboBox = new ComboBox<>();
        outcomeComboBox.setItems(FXCollections.observableArrayList(selectedCourse.getOutcomes()));
        TextField weightField = new TextField();

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Select Outcome:"), outcomeComboBox,
                new Label("Weight:"), weightField
        ));

        ButtonType linkButtonType = new ButtonType("Link", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(linkButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == linkButtonType) {
                try {
                    Outcome selectedOutcome = outcomeComboBox.getValue();
                    double weight = Double.parseDouble(weightField.getText());
                    if (selectedOutcome != null) {
                        selectedAssessment.setOutcomeWeight(selectedOutcome, weight);
                        db.linkOutcomeToAssessment(selectedAssessment.getId(), selectedOutcome.getId(), weight);
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid input. Please enter a valid number for weight.");
                }
            }
            return null;
        });

        dialog.showAndWait();
        updateOutcomeTableForAssessment(selectedAssessment);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
private void handleDeleteAssessmentButtonAction() {
    Assessment selectedAssessment = assessmentTable.getSelectionModel().getSelectedItem();
    if (selectedAssessment != null) {
        if (showConfirmationDialog("Are you sure you want to delete this assessment?")) {
            db.deleteAssessment(selectedAssessment.getId());
            updateAssessmentTable();
        }
    } else {
        showAlert("Please select an assessment to delete.");
    }
}

@FXML
private void handleDeletePartButtonAction() {
    AssessmentPart selectedPart = partsTable.getSelectionModel().getSelectedItem();
    if (selectedPart != null) {
        if (showConfirmationDialog("Are you sure you want to delete this assessment part?")) {
            db.deleteAssessmentPart(selectedPart.getId());
            updatePartsTable();
        }
    } else {
        showAlert("Please select a part to delete.");
    }
}

@FXML
private void handleUnlinkOutcomeButtonAction() {
    Outcome selectedOutcome = outcomeTable.getSelectionModel().getSelectedItem();
    if (selectedOutcome != null && currentAssessment != null) {
        if (showConfirmationDialog("Are you sure you want to unlink this outcome?")) {
            db.unlinkOutcomeFromAssessment(currentAssessment.getId(), selectedOutcome.getId());
            updateOutcomeTableForAssessment(currentAssessment);
        }
    } else {
        showAlert("Please select an outcome to unlink.");
    }
}

private boolean showConfirmationDialog(String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmation");
    alert.setHeaderText(null);
    alert.setContentText(message);
    return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
}


}
