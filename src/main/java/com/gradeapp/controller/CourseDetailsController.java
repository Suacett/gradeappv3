package com.gradeapp.controller;

import java.util.ArrayList;
import java.util.Optional;

import com.gradeapp.database.Database;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Outcome;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

/**
 * Controller class for managing course details in the GradeApp.
 * Handles UI interactions, data binding, and communication with the database.
 */
public class CourseDetailsController {

    // FXML UI components for course details input fields
    @FXML
    private TextField courseIdField;
    @FXML
    private TextField courseNameField;
    @FXML
    private TextField courseDescriptionField;

    // FXML UI components for Outcomes TableView and its columns
    @FXML
    private TableView<Outcome> outcomesTable;
    @FXML
    private TableColumn<Outcome, String> identifierColumn;
    @FXML
    private TableColumn<Outcome, String> nameColumn;
    @FXML
    private TableColumn<Outcome, String> descriptionColumn;
    @FXML
    private TableColumn<Outcome, Double> weightColumn;

    // FXML UI components for Classes TableView and its columns
    @FXML
    private TableView<Classes> classesTable;
    @FXML
    private TableColumn<Classes, String> classNameColumn;
    @FXML
    private TableColumn<Classes, String> classIdColumn;

    // Reference to the parent CoursesController
    private CoursesController coursesController;

    // The current course being viewed/edited
    private Course course;

    // Database instance for data operations
    private Database db = new Database();

    // ObservableList to hold Outcome objects for the Outcomes TableView
    private ObservableList<Outcome> outcomes;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialization logic can be added here if needed
    }

    /**
     * Sets up the Classes TableView by configuring its columns and populating
     * it with classes related to the current course.
     */
    private void setupClassesTable() {
        // Bind class name and ID properties to their respective columns
        classNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        classIdColumn.setCellValueFactory(cellData -> cellData.getValue().classIdProperty());

        // Retrieve classes from the database and set them in the table
        java.util.List<Classes> classes = db.getClassesForCourse(course.getId());
        classesTable.setItems(FXCollections.observableArrayList(classes));
    }

    /**
     * Sets the current course and initializes the UI fields and tables with
     * the course data.
     *
     * @param course The Course object to be displayed/edited
     */
    public void setCourse(Course course) {
        this.course = course;

        // Populate text fields with course details
        courseIdField.setText(course.getId());
        courseNameField.setText(course.getName());
        courseDescriptionField.setText(course.getDescription());

        // Initialize outcomes list and set it to the Outcomes TableView
        outcomes = FXCollections.observableArrayList(course.getOutcomes());
        outcomesTable.setItems(outcomes);

        // Setup the Outcomes and Classes tables
        setupOutcomesTable();
        setupClassesTable();

        // Ensure outcome weights sum up to 100%
        updateOutcomeWeights();
    }

    /**
     * Sets the reference to the parent CoursesController.
     *
     * @param coursesController The parent CoursesController
     */
    public void setCoursesController(CoursesController coursesController) {
        this.coursesController = coursesController;
    }

    /**
     * Configures the Outcomes TableView by setting up cell value factories,
     * cell factories for editing, and handling edit commit events.
     */
    private void setupOutcomesTable() {
        // Bind Outcome properties to table columns
        identifierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        weightColumn
                .setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getWeight()).asObject());

        // Make the Outcomes TableView editable
        outcomesTable.setEditable(true);

        // Set cell factories to enable editing for each column
        identifierColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        weightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // Handle edit commit events to update Outcome objects
        identifierColumn.setOnEditCommit(event -> {
            event.getRowValue().setId(event.getNewValue());
            outcomesTable.refresh();
        });
        nameColumn.setOnEditCommit(event -> {
            event.getRowValue().setName(event.getNewValue());
            outcomesTable.refresh();
        });
        descriptionColumn.setOnEditCommit(event -> {
            event.getRowValue().setDescription(event.getNewValue());
            outcomesTable.refresh();
        });
        weightColumn.setOnEditCommit(event -> {
            event.getRowValue().setWeight(event.getNewValue());
            outcomesTable.refresh();
            updateOutcomeWeights();
        });
    }

    /**
     * Saves the updated course details to the database after validation.
     * Updates the parent CoursesController and closes the editing window upon
     * success.
     */
    @FXML
    private void saveCourseDetails() {
        if (validateCourse()) {
            // Retrieve updated values from text fields
            String updatedId = courseIdField.getText().trim();
            String updatedName = courseNameField.getText().trim();
            String updatedDescription = courseDescriptionField.getText().trim();

            // Update course object with new values
            course.setId(updatedId);
            course.setName(updatedName);
            course.setDescription(updatedDescription);
            course.setOutcomes(new ArrayList<>(outcomes));

            try {
                // Update the course in the database
                db.updateCourse(course, course.getId());

                // Refresh the courses list in the parent controller
                if (coursesController != null) {
                    coursesController.displayCurrentCourses();
                }

                // Close the editing window
                closeWindow();
            } catch (Exception e) {
                // Show error alert if saving fails
                showAlert("Error", "Failed to save course details: " + e.getMessage());
            }
        }
    }

    /**
     * Closes the current window/stage.
     */
    private void closeWindow() {
        Stage stage = (Stage) courseIdField.getScene().getWindow();
        stage.close();
    }

    /**
     * Validates the course details before saving.
     * Ensures that required fields are not empty and that outcome weights sum to
     * 100%.
     *
     * @return true if validation passes, false otherwise
     */
    private boolean validateCourse() {
        // Check if course name and ID fields are not empty
        if (courseNameField.getText().trim().isEmpty() ||
                courseIdField.getText().trim().isEmpty()) {
            showAlert("Invalid Input", "Course name and ID cannot be empty.");
            return false;
        }

        // Calculate the total weight of all outcomes
        double totalWeight = outcomes.stream()
                .mapToDouble(Outcome::getWeight)
                .sum();
        // Allow a small margin for floating-point precision
        if (Math.abs(totalWeight - 100.0) > 0.01) {
            showAlert("Invalid Outcomes", "The total weight of outcomes must equal 100%.");
            return false;
        }

        return true;
    }

    /**
     * Updates the weights of all outcomes to ensure they sum up to 100%.
     * Assigns equal weight to each outcome.
     */
    private void updateOutcomeWeights() {
        int outcomeCount = outcomes.size();
        if (outcomeCount > 0) {
            double equalWeight = 100.0 / outcomeCount;
            for (Outcome outcome : outcomes) {
                outcome.setWeight(equalWeight);
            }
            outcomesTable.refresh();
        }
    }

    /**
     * Handles the action of adding a new outcome.
     * Prompts the user for an Outcome ID and adds a new Outcome to the list.
     */
    @FXML
    private void addOutcome() {
        // Create a dialog to input the new Outcome ID
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Outcome");
        dialog.setHeaderText("Enter Outcome ID");
        dialog.setContentText("Please enter the outcome ID:");

        // Show the dialog and wait for user input
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newId = result.get();

            // Create a new Outcome with default values
            Outcome newOutcome = new Outcome(newId, "New Outcome", "Description", 0.0);
            outcomes.add(newOutcome);

            // Update weights and refresh the table
            updateOutcomeWeights();
            outcomesTable.refresh();

            // Update the course object and save to the database
            course.setOutcomes(new ArrayList<>(outcomes));
            db.saveCourse(course);
        }
    }

    /**
     * Handles the action of removing the selected outcome from the Outcomes
     * TableView.
     */
    @FXML
    private void removeSelectedOutcome() {
        // Get the selected Outcome
        Outcome selectedOutcome = outcomesTable.getSelectionModel().getSelectedItem();
        if (selectedOutcome != null) {
            // Remove the selected Outcome from the list
            outcomes.remove(selectedOutcome);

            // Update weights and refresh the table
            updateOutcomeWeights();
        }
    }

    /**
     * Cancels the editing process and closes the window.
     * Refreshes the courses list in the parent controller if available.
     */
    @FXML
    private void cancelEditing() {
        if (coursesController != null) {
            coursesController.displayCurrentCourses();
        }
        closeWindow();
    }

    /**
     * Displays an alert dialog with the specified title and content.
     *
     * @param title   The title of the alert dialog
     * @param content The content/message of the alert
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
