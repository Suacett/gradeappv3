package com.gradeapp.controller;

import java.util.ArrayList;
import java.util.Optional;

import com.gradeapp.database.Database;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Outcome;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

/**
 * Controller class for editing course details in the GradeApp.
 * Manages UI interactions, data binding, and communication with the database
 * for creating or modifying courses and their associated outcomes.
 */
public class CourseEditController {

    // ----------------------------- FXML UI Components
    // -----------------------------

    // Text fields for course information
    @FXML
    private TextField courseNameField;

    @FXML
    private TextField courseIdField;

    @FXML
    private TextArea courseDescriptionField;

    // TableView and TableColumns for Outcomes
    @FXML
    private TableView<Outcome> outcomesTable;

    @FXML
    private TableColumn<Outcome, String> outcomeIdentifierColumn;

    @FXML
    private TableColumn<Outcome, String> outcomeNameColumn;

    @FXML
    private TableColumn<Outcome, String> outcomeDescriptionColumn;

    @FXML
    private TableColumn<Outcome, Double> outcomeWeightColumn;

    // Label to display total weight of outcomes
    @FXML
    private Label totalWeightLabel;

    // TableView and TableColumns for Classes
    @FXML
    private TableView<Classes> classesTable;

    @FXML
    private TableColumn<Classes, String> classNameColumn;

    @FXML
    private TableColumn<Classes, String> classIdColumn;

    // ----------------------------- Non-UI Fields -----------------------------

    // Reference to the parent CoursesController
    @SuppressWarnings("unused")
    private CoursesController coursesController;

    // The current course being edited or created
    private Course course;

    // Database instance for data operations
    private Database db = new Database();

    // ObservableList to hold Outcome objects for the Outcomes TableView
    private ObservableList<Outcome> outcomes = FXCollections.observableArrayList();

    // ----------------------------- Initialization -----------------------------

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the outcomes list
        outcomes = FXCollections.observableArrayList();

        // Setup the Outcomes TableView
        setupOutcomesTable();

        // Update the total weight label
        updateTotalWeight();

        // Debug message indicating initialization
        System.out.println("CourseEditController initialized");

        // Configure the TableColumn value factories for Outcomes
        outcomeIdentifierColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        outcomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        outcomeDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        outcomeWeightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
    }

    // ----------------------------- Setup Methods -----------------------------

    /**
     * Configures the Outcomes TableView by setting up cell value factories,
     * cell factories for editing, and handling edit commit events.
     */
    private void setupOutcomesTable() {
        // Bind Outcome properties to table columns
        outcomeIdentifierColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        outcomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        outcomeDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        outcomeWeightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        // Enable editing for the Outcomes TableView
        outcomesTable.setEditable(true);

        // Set cell factories to enable editing for each column
        outcomeIdentifierColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        outcomeNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        outcomeDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        outcomeWeightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // Handle edit commit events to update Outcome objects
        outcomeNameColumn.setOnEditCommit(event -> {
            Outcome outcome = event.getRowValue();
            outcome.setName(event.getNewValue());
            outcomesTable.refresh();
        });

        outcomeDescriptionColumn.setOnEditCommit(event -> {
            Outcome outcome = event.getRowValue();
            outcome.setDescription(event.getNewValue());
            outcomesTable.refresh();
        });

        outcomeIdentifierColumn.setOnEditCommit(event -> {
            Outcome outcome = event.getRowValue();
            outcome.setId(event.getNewValue());
            outcomesTable.refresh();
        });

        // Set the outcomes list to the Outcomes TableView
        outcomesTable.setItems(outcomes);

        // Set the table to be editable
        outcomesTable.setEditable(true);
    }

    // ----------------------------- Setter Methods -----------------------------

    /**
     * Sets the current course and initializes the UI fields and tables with
     * the course data.
     *
     * @param course The Course object to be displayed/edited. If null, a new course
     *               is being created.
     */
    public void setCourse(Course course) {
        this.course = course;
        if (course != null) {
            // Populate text fields with course details
            courseIdField.setText(course.getId());
            courseNameField.setText(course.getName());
            courseDescriptionField.setText(course.getDescription());

            // Populate outcomes list with course outcomes
            outcomes.setAll(course.getOutcomes());

            // Debug message indicating editing an existing course
            System.out.println("Editing existing course: " + course.getName() + ", Outcomes: " + outcomes.size());
        } else {
            // Clear all fields for creating a new course
            courseIdField.clear();
            courseNameField.clear();
            courseDescriptionField.clear();
            outcomes.clear();

            // Debug message indicating creating a new course
            System.out.println("Creating new course");
        }

        // Update the Outcomes TableView with the current outcomes
        outcomesTable.setItems(outcomes);

        // Update the total weight label
        updateTotalWeight();
    }

    /**
     * Sets the reference to the parent CoursesController.
     *
     * @param coursesController The parent CoursesController
     */
    public void setCoursesController(CoursesController coursesController) {
        this.coursesController = coursesController;
    }

    // ----------------------------- Action Handlers -----------------------------

    /**
     * Handles the action of adding a new outcome.
     * Prompts the user for an Outcome ID and adds a new Outcome to the list.
     */
    @FXML
    private void addOutcome() {
        String newId = "";
        // Create and configure the input dialog
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Outcome");
        dialog.setHeaderText("Enter Outcome ID");
        dialog.setContentText("Please enter the outcome ID:");

        // Show the dialog and capture the result
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            newId = result.get();
        } else {
            // User canceled the dialog
            return;
        }

        // Create a new Outcome with default values
        Outcome newOutcome = new Outcome(newId, "New Outcome", "Description", 0.0);
        outcomes.add(newOutcome);

        // Update weights and refresh the table
        updateTotalWeight();
        outcomesTable.refresh();

        // Debug message indicating a new outcome was added
        System.out.println("Outcome added: " + newOutcome);
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
            updateTotalWeight();
        }
    }

    /**
     * Handles the action of saving the course details to the database after
     * validation.
     */
    @FXML
    private void saveCourse() {
        if (validateCourse()) {
            // Retrieve input values from text fields
            String courseId = courseIdField.getText();
            if (course == null) {
                // Creating a new course
                course = new Course(courseId, courseNameField.getText(), courseDescriptionField.getText());
            } else {
                // Updating an existing course
                course.setId(courseId);
                course.setName(courseNameField.getText());
                course.setDescription(courseDescriptionField.getText());
            }

            // Update the outcomes in the course object
            updateTotalWeight();
            course.setOutcomes(new ArrayList<>(outcomes));

            try {
                // Save the course to the database
                db.saveCourse(course);

                // Debug messages indicating successful save
                System.out.println("Course saved: " + course.getName() + " (ID: " + course.getId() + ")");
                System.out.println("Number of outcomes saved: " + outcomes.size());

                // Close the editing window
                closeWindow();
            } catch (IllegalArgumentException e) {
                // Show alert if there's an invalid course ID
                showAlert("Invalid Course ID", e.getMessage());
            }
        }
    }

    /**
     * Handles the action of cancelling the editing process and closing the window.
     */
    @FXML
    private void cancel() {
        closeWindow();
    }

    // ----------------------------- Utility Methods -----------------------------

    /**
     * Updates the total weight label based on the number of outcomes.
     * Assigns equal weight to each outcome if there are any.
     */
    private void updateTotalWeight() {
        int outcomeCount = outcomes.size();
        if (outcomeCount > 0) {
            double equalWeight = 100.0 / outcomeCount;
            for (Outcome outcome : outcomes) {
                outcome.setWeight(equalWeight);
            }
        }
        // Update the total weight label with the current count of outcomes
        totalWeightLabel.setText(String.format("Total Outcomes: %d", outcomeCount));
        totalWeightLabel.setStyle("-fx-text-fill: black;");
    }

    /**
     * Validates the course details before saving.
     * Ensures that required fields are not empty.
     *
     * @return true if validation passes, false otherwise
     */
    private boolean validateCourse() {
        if (courseNameField.getText().isEmpty() || courseIdField.getText().isEmpty()) {
            showAlert("Course name and ID are required.", null);
            return false;
        }
        return true;
    }

    /**
     * Displays an alert dialog with the specified title and content.
     *
     * @param message The content/message of the alert
     * @param string  An additional string parameter
     */
    private void showAlert(String message, String string) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Closes the current window/stage.
     */
    private void closeWindow() {
        courseNameField.getScene().getWindow().hide();
    }
}
