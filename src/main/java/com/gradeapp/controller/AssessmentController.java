package com.gradeapp.controller;

import java.io.IOException;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Course;
import com.gradeapp.model.Outcome;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * AssessmentController manages the creation and manipulation of assessments,
 * including their parts and linked outcomes.
 */
public class AssessmentController implements AssessmentCreationCallback {

    // ----------------------- FXML UI Components -------------------------

    @FXML
    private VBox currentAssessmentContainer;

    @FXML
    private VBox newAssessmentInputContainer;

    @FXML
    private TableView<Assessment> assessmentTable;

    @FXML
    private TableView<AssessmentPart> partsTable;

    @FXML
    private TableColumn<AssessmentPart, String> partNameColumn;

    @FXML
    private TableColumn<AssessmentPart, Double> partWeightColumn;

    @FXML
    private TableColumn<AssessmentPart, Double> partMaxScoreColumn;

    @FXML
    private TableView<Outcome> outcomeTable;

    @FXML
    private VBox outcomeInputContainer;

    @FXML
    private ComboBox<Course> courseSelector;

    @FXML
    private TableView<Outcome> linkedOutcomesForPartTable;

    @FXML
    private TableColumn<Outcome, String> linkedOutcomeIdColumn;

    @FXML
    private TableColumn<Outcome, String> linkedOutcomeNameColumn;

    @FXML
    private TableColumn<Outcome, Double> linkedOutcomeWeightColumn;

    @FXML
    private TableColumn<Assessment, String> assessmentNameColumn;

    @FXML
    private TableColumn<Assessment, String> assessmentDescriptionColumn;

    @FXML
    private TableColumn<Assessment, Double> assessmentWeightColumn;

    @FXML
    private TableColumn<Assessment, Double> assessmentMaxScoreColumn;

    @FXML
    private TableView<Outcome> linkedOutcomesForAssessmentTable;

    @FXML
    private TableColumn<Outcome, String> linkedAssessmentOutcomeIdColumn;

    @FXML
    private TableColumn<Outcome, String> linkedAssessmentOutcomeNameColumn;

    @FXML
    private TableColumn<Outcome, Double> linkedAssessmentOutcomeWeightColumn;

    // ----------------------- Private Fields -----------------------

    // Database instance for data operations
    private Database db = new Database();

    // Currently selected assessment and course
    private Assessment currentAssessment;
    private Course selectedCourse;

    // ----------------------- Initialization -----------------------

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        setupCourseSelector();
        setupAssessmentTable();
        setupPartsTable();
        setupLinkedOutcomesForPartTable();
        setupLinkedOutcomesForAssessmentTable();

        // Listener for assessment table selection changes
        assessmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentAssessment = newSelection;
                updatePartsTable();
                updateLinkedOutcomesForAssessmentTable();
            }
        });

        // Listener for course selector changes
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCourse = newSelection;
                updateAssessmentTable();
            }
        });

        // Listener for parts table selection changes
        partsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateLinkedOutcomesForPartTable(newSelection);
            }
        });
    }

    // ----------------------- Setup Methods -----------------------

    /**
     * Sets up the course selector ComboBox with available courses.
     * Configures a custom StringConverter to display course names.
     */
    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);

        // Set a custom StringConverter to display the course name
        courseSelector.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course != null ? course.getName() : "";
            }

            @Override
            public Course fromString(String string) {
                return courses.stream()
                        .filter(course -> course.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    /**
     * Configures the assessment table columns.
     */
    private void setupAssessmentTable() {
        assessmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        assessmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        assessmentWeightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        assessmentMaxScoreColumn.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
    }

    /**
     * Configures the parts table columns.
     */
    private void setupPartsTable() {
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partWeightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        partMaxScoreColumn.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
    }

    /**
     * Configures the linked outcomes table for assessment parts.
     */
    private void setupLinkedOutcomesForPartTable() {
        linkedOutcomeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        linkedOutcomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        linkedOutcomeWeightColumn.setCellValueFactory(cellData -> {
            AssessmentPart selectedPart = partsTable.getSelectionModel().getSelectedItem();
            if (selectedPart != null) {
                double weight = db.getOutcomeWeightForPart(selectedPart.getId(), cellData.getValue().getId());
                return new SimpleDoubleProperty(weight).asObject();
            }
            return new SimpleDoubleProperty(0).asObject();
        });
    }

    /**
     * Configures the linked outcomes table for assessments.
     */
    private void setupLinkedOutcomesForAssessmentTable() {
        linkedAssessmentOutcomeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        linkedAssessmentOutcomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        linkedAssessmentOutcomeWeightColumn.setCellValueFactory(cellData -> {
            if (currentAssessment != null) {
                double weight = db.getOutcomeWeightForAssessment(currentAssessment.getId(),
                        cellData.getValue().getId());
                return new SimpleDoubleProperty(weight).asObject();
            }
            return new SimpleDoubleProperty(0).asObject();
        });
    }

    /**
     * Sets up the outcome table with appropriate columns.
     */
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

    // ----------------------- Event Handlers -----------------------

    /**
     * Handles the action when the "Add Assessment" button is clicked.
     * Opens a dialog to create a new assessment.
     */
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

    /**
     * Handles the action to link an outcome to an assessment.
     */
    @FXML
    private void handleLinkOutcomeToAssessmentButtonAction() {
        if (currentAssessment == null) {
            showAlert("Please select an assessment first.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Link Outcome to Assessment");
        dialog.setHeaderText("Link an outcome to " + currentAssessment.getName());

        ComboBox<Outcome> outcomeComboBox = new ComboBox<>();
        outcomeComboBox.setItems(FXCollections.observableArrayList(selectedCourse.getOutcomes()));
        TextField weightField = new TextField();

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Select Outcome:"), outcomeComboBox,
                new Label("Weight (%):"), weightField));

        ButtonType linkButtonType = new ButtonType("Link", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(linkButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == linkButtonType) {
                try {
                    Outcome selectedOutcome = outcomeComboBox.getValue();
                    double weight = Double.parseDouble(weightField.getText());
                    if (selectedOutcome != null) {
                        db.linkOutcomeToAssessment(currentAssessment.getId(), selectedOutcome.getId(), weight);
                        updateLinkedOutcomesForAssessmentTable();
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid input. Please enter a valid number for weight.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Handles the action to unlink an outcome from an assessment.
     */
    @FXML
    private void handleUnlinkOutcomeFromAssessmentButtonAction() {
        if (currentAssessment == null) {
            showAlert("Please select an assessment first.");
            return;
        }

        Outcome selectedOutcome = linkedOutcomesForAssessmentTable.getSelectionModel().getSelectedItem();
        if (selectedOutcome == null) {
            showAlert("Please select an outcome to unlink.");
            return;
        }

        if (showConfirmationDialog("Are you sure you want to unlink this outcome from the assessment?")) {
            db.unlinkOutcomeFromAssessment(currentAssessment.getId(), selectedOutcome.getId());
            updateLinkedOutcomesForAssessmentTable();
        }
    }

    /**
     * Handles the action to unlink an outcome from an assessment part.
     */
    @FXML
    private void handleUnlinkOutcomeFromPartButtonAction() {
        AssessmentPart selectedPart = partsTable.getSelectionModel().getSelectedItem();
        Outcome selectedOutcome = linkedOutcomesForPartTable.getSelectionModel().getSelectedItem();
        if (selectedPart != null && selectedOutcome != null) {
            if (showConfirmationDialog("Are you sure you want to unlink this outcome from the part?")) {
                db.unlinkOutcomeFromAssessmentPart(selectedPart.getId(), selectedOutcome.getId());
                updateLinkedOutcomesForPartTable(selectedPart);
            }
        } else {
            showAlert("Please select a part and an outcome to unlink.");
        }
    }

    /**
     * Handles the action to delete the selected assessment.
     */
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

    /**
     * Handles the action to delete the selected assessment part.
     */
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

    /**
     * Handles the action to add a new part to the selected assessment.
     */
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
                new Label("Max Score:"), maxScoreField));

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

    // ----------------------- Callback Methods -----------------------

    /**
     * Callback method invoked when a new assessment is created.
     *
     * @param newAssessment The newly created assessment.
     */
    @Override
    public void onAssessmentCreated(Assessment newAssessment) {
        System.out
                .println("New assessment created: " + newAssessment.getName() + " (ID: " + newAssessment.getId() + ")");
        refreshAssessmentTable();
        assessmentTable.getSelectionModel().select(newAssessment);
        updatePartsTable();
    }

    // ----------------------- Update Methods -----------------------

    /**
     * Updates the assessment table based on the selected course.
     */
    private void updateAssessmentTable() {
        if (selectedCourse != null) {
            List<Assessment> assessments = db.getAssessmentsForCourse(selectedCourse.getId());
            assessmentTable.setItems(FXCollections.observableArrayList(assessments));
        } else {
            assessmentTable.getItems().clear();
        }
    }

    /**
     * Updates the parts table based on the currently selected assessment.
     */
    private void updatePartsTable() {
        if (currentAssessment != null) {
            List<AssessmentPart> parts = db.getAssessmentParts(currentAssessment.getId());
            partsTable.setItems(FXCollections.observableArrayList(parts));
        } else {
            partsTable.getItems().clear();
        }
    }

    /**
     * Updates the linked outcomes table for a specific assessment part.
     *
     * @param part The assessment part whose linked outcomes are to be displayed.
     */
    private void updateLinkedOutcomesForPartTable(AssessmentPart part) {
        if (part != null) {
            List<Outcome> linkedOutcomes = db.getLinkedOutcomesForPart(part.getId());
            linkedOutcomesForPartTable.setItems(FXCollections.observableArrayList(linkedOutcomes));
            linkedOutcomesForPartTable.refresh();
        } else {
            linkedOutcomesForPartTable.getItems().clear();
        }
    }

    /**
     * Updates the linked outcomes table for the currently selected assessment.
     */
    private void updateLinkedOutcomesForAssessmentTable() {
        if (currentAssessment != null) {
            List<Outcome> linkedOutcomes = db.getLinkedOutcomesForAssessment(currentAssessment.getId());
            linkedOutcomesForAssessmentTable.setItems(FXCollections.observableArrayList(linkedOutcomes));
            linkedOutcomesForAssessmentTable.refresh();
        } else {
            linkedOutcomesForAssessmentTable.getItems().clear();
        }
    }

    /**
     * Refreshes the assessment table with the latest data from the database.
     */
    private void refreshAssessmentTable() {
        if (selectedCourse != null) {
            ObservableList<Assessment> assessments = FXCollections
                    .observableArrayList(db.getAssessmentsForCourse(selectedCourse.getId()));
            assessmentTable.setItems(assessments);
        }
    }

    // ----------------------- Helper Methods -----------------------

    /**
     * Creates a visual card representation of an assessment.
     *
     * @param assessment The assessment to represent.
     * @return A VBox containing the assessment details and action buttons.
     */
    private VBox createAssessmentCard(Assessment assessment) {
        VBox assessmentCard = new VBox();
        assessmentCard.setPadding(new Insets(10));
        assessmentCard.setSpacing(10);
        assessmentCard
                .setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 5px;");

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
            db.delete("assessments", "id", String.valueOf(assessment.getId()));
            displayCurrentAssessments();
        });

        buttonContainer.getChildren().addAll(editButton, deleteButton);
        assessmentCard.getChildren().addAll(assessmentNameLabel, assessmentDescriptionLabel, assessmentWeightLabel,
                assessmentMaxScoreLabel, buttonContainer);

        return assessmentCard;
    }

    /**
     * Handles the action to edit an existing assessment.
     *
     * @param assessment The assessment to edit.
     */
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
                db.updateAssessment(assessment);
                displayCurrentAssessments();
            } else {
                System.out.println("The form is incomplete...");
            }
        });

        VBox editAssessmentBox = new VBox(10, new Label("Edit Assessment"), assessmentNameField,
                assessmentDescriptionField, assessmentWeightField, assessmentMaxScoreField, saveButton);
        currentAssessmentContainer.getChildren().clear();
        currentAssessmentContainer.getChildren().add(editAssessmentBox);
    }

    /**
     * Displays the current assessments in the UI.
     * If no assessments are available, shows a message indicating so.
     */
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

    /**
     * Handles the action to link an outcome to an assessment part.
     */
    @FXML
    private void handleLinkOutcomeToPartButtonAction() {
        AssessmentPart selectedPart = partsTable.getSelectionModel().getSelectedItem();
        if (selectedPart == null) {
            showAlert("Please select an assessment part first.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Link Outcome to Part");
        dialog.setHeaderText("Link an outcome to " + selectedPart.getName());

        ComboBox<Outcome> outcomeComboBox = new ComboBox<>();
        outcomeComboBox.setItems(FXCollections.observableArrayList(selectedCourse.getOutcomes()));
        TextField weightField = new TextField();

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Select Outcome:"), outcomeComboBox,
                new Label("Weight (%):"), weightField));

        ButtonType linkButtonType = new ButtonType("Link", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(linkButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == linkButtonType) {
                try {
                    Outcome selectedOutcome = outcomeComboBox.getValue();
                    double weight = Double.parseDouble(weightField.getText());
                    if (selectedOutcome != null) {
                        db.linkOutcomeToAssessmentPart(selectedPart.getId(), selectedOutcome.getId(), weight);
                        updatePartsTable();
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid input. Please enter a valid number for weight.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // ----------------------- Utility Methods -----------------------

    /**
     * Displays an information alert with the specified message.
     *
     * @param message The message to display in the alert.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog with the specified message.
     *
     * @param message The confirmation message.
     * @return True if the user confirms, false otherwise.
     */
    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
