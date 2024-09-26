package com.gradeapp.controller;

import java.sql.SQLException;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;
import com.gradeapp.util.ChartGenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;

/**
 * Controller class for managing classes within a course.
 * Handles operations such as adding, editing, and deleting classes,
 * as well as managing students within classes and displaying class details.
 */
public class ClassController {

    // FXML UI components
    @FXML
    private VBox classContainer;

    @FXML
    private VBox currentClassContainer;

    @FXML
    private VBox newClassInputContainer;

    @FXML
    private ComboBox<Course> courseSelector;

    @FXML
    private ListView<Student> studentListView;

    @FXML
    private Label classStatisticsLabel;

    @FXML
    private ComboBox<Assessment> assessmentComboBox;

    @FXML
    private VBox classDetailsContainer;

    @FXML
    private ComboBox<AssessmentPart> partComboBox;

    @FXML
    private BarChart<String, Number> gradeBarChart;

    // Private fields
    private Database db = new Database();
    private Classes selectedClass;
    private ChartGenerator chartGenerator = new ChartGenerator();

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        setupCourseSelector();
        updateClassList();
        updateClassDetailsView();

        // Initially hide the class details container
        classDetailsContainer.setVisible(false);
        classDetailsContainer.setManaged(false);

        // Set event listeners for assessment and part selection
        assessmentComboBox.setOnAction(event -> {
            populateParts();
            updateGradeChart();
        });

        partComboBox.setOnAction(event -> updateGradeChart());
    }

    // ----------------------- Setup Methods -----------------------

    /**
     * Configures the course selector ComboBox with available courses.
     * Sets up custom cell factory and string converter for display.
     */
    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);

        // Custom cell factory to display course name and ID
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

        // String converter to display course name and ID in the ComboBox
        courseSelector.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course == null ? "" : course.getName() + " (" + course.getId() + ")";
            }

            @Override
            public Course fromString(String string) {
                return null; // Not needed
            }
        });

        // Event handler to update class list when a course is selected
        courseSelector.setOnAction(e -> updateClassList());

        // Select the first course by default if available
        if (!courses.isEmpty()) {
            courseSelector.getSelectionModel().selectFirst();
        }
    }

    // ----------------------- Event Handlers -----------------------

    /**
     * Handles the action of adding a new class.
     * Opens a dialog for inputting class details and adds the class to the
     * database.
     */
    @FXML
    private void handleAddClassButtonAction() {
        Course selectedCourse = courseSelector.getValue();
        if (selectedCourse == null) {
            showError("Please select a course first.");
            return;
        }

        // Create a new dialog for adding a class
        Dialog<Classes> dialog = new Dialog<>();
        dialog.setTitle("Add New Class");

        // Input fields for class name and ID
        TextField nameField = new TextField();
        TextField idField = new TextField();

        // Set dialog content
        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Class Name:"), nameField,
                new Label("Class ID:"), idField));

        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle dialog result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String name = nameField.getText();
                String id = idField.getText();
                if (name.isEmpty() || id.isEmpty()) {
                    showError("Please fill in all fields.");
                    return null;
                }
                Classes newClass = new Classes(name, id);
                try {
                    db.addClass(newClass, selectedCourse.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    showError("Error adding class: " + e.getMessage());
                    return null;
                }
                return newClass;
            }
            return null;
        });

        // Refresh class list after adding
        dialog.showAndWait().ifPresent(result -> updateClassList());
    }

    /**
     * Handles the action of viewing class details.
     *
     * @param classObj The class object to view details for.
     */
    @FXML
    public void handleViewClassDetailsAction(Classes classObj) {
        VBox classCard = (VBox) currentClassContainer.lookup("#" + classObj.getClassId());
        if (classCard != null) {
            selectClass(classObj);
        }
    }

    /**
     * Handles the action of editing an existing class.
     *
     * @param classes The class object to edit.
     */
    private void handleEditClassButtonAction(Classes classes) {
        // Input fields pre-filled with existing class details
        TextField classNameField = new TextField(classes.getName());
        TextField classIdField = new TextField(classes.getClassId());
        Button saveButton = new Button("Save");

        // Save button action to update class details
        saveButton.setOnAction(event -> {
            String newName = classNameField.getText();
            String newId = classIdField.getText();
            if (!newName.isEmpty() && !newId.isEmpty()) {
                db.updateClass(classes.getClassId(), newName, newId);
                updateClassList();
            } else {
                showError("Please fill in all fields.");
            }
        });

        // Create and display the edit dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Class");

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Edit Class"),
                new Label("Class Name:"), classNameField,
                new Label("Class ID:"), classIdField,
                saveButton));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * Handles the action of adding a student to the selected class.
     * Opens a dialog for selecting a student and adds them to the class.
     */
    @FXML
    public void handleAddStudentToClassAction() {
        if (selectedClass == null) {
            showError("Please select a class first.");
            return;
        }

        // Create a new dialog for adding a student
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add Student to Class");

        // Retrieve students not already in the class
        List<Student> availableStudents = db.getStudentsNotInClass(selectedClass.getClassId());
        if (availableStudents.isEmpty()) {
            showError("No students available to add to this class.");
            return;
        }

        // ComboBox to select a student
        ComboBox<Student> studentComboBox = new ComboBox<>(FXCollections.observableArrayList(availableStudents));

        // Custom cell factory for displaying student information
        studentComboBox.setCellFactory(lv -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getName() + " (" + student.getStudentId() + ")");
                }
            }
        });

        // String converter for the ComboBox
        studentComboBox.setConverter(new StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student == null ? "" : student.getName() + " (" + student.getStudentId() + ")";
            }

            @Override
            public Student fromString(String string) {
                return null; // Not needed
            }
        });

        // Set dialog content
        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Select Student:"), studentComboBox));

        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle dialog result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Student student = studentComboBox.getValue();
                if (student != null) {
                    if (db.addStudentToClass(student.getStudentId(), selectedClass.getClassId())) {
                        return student;
                    } else {
                        showError("Failed to add student to class. The student might already be in the class.");
                    }
                }
            }
            return null;
        });

        // Refresh student list after adding
        dialog.showAndWait().ifPresent(result -> updateStudentListView());
    }

    /**
     * Handles the action of removing a student from the selected class.
     */
    @FXML
    public void handleRemoveStudentFromClassAction() {
        Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null && selectedClass != null) {
            db.removeStudentFromClass(selectedStudent.getStudentId(), selectedClass.getClassId());
            updateStudentListView();
        }
    }

    // ----------------------- Update Methods -----------------------

    /**
     * Updates the list of classes based on the selected course.
     */
    private void updateClassList() {
        Course selectedCourse = courseSelector.getValue();
        if (selectedCourse != null) {
            List<Classes> classes = db.getClassesForCourse(selectedCourse.getId());
            displayClasses(classes);
        } else {
            currentClassContainer.getChildren().clear();
        }
        selectedClass = null;
        updateClassDetailsView();
    }

    /**
     * Displays the list of classes in the current class container.
     *
     * @param classes The list of classes to display.
     */
    private void displayClasses(List<Classes> classes) {
        currentClassContainer.getChildren().clear();
        selectedClass = null;
        for (Classes classObj : classes) {
            VBox classCard = createClassCard(classObj);
            currentClassContainer.getChildren().add(classCard);
        }
        updateClassDetailsView();
    }

    /**
     * Updates the grade chart based on the selected class, assessment, and part.
     */
    private void updateGradeChart() {
        gradeBarChart.getData().clear();

        if (selectedClass == null) {
            return;
        }

        Assessment selectedAssessment = assessmentComboBox.getValue();
        AssessmentPart selectedPart = partComboBox.getValue();

        List<Grade> grades;

        if (selectedAssessment == null) {
            grades = db.getAllGradesForClass(selectedClass.getClassId());
        } else if (selectedPart == null) {
            grades = db.getGradesForClassAndAssessment(selectedClass.getClassId(), selectedAssessment.getId());
        } else {
            grades = db.getGradesForClassAssessmentAndPart(
                    selectedClass.getClassId(), selectedAssessment.getId(), selectedPart.getId());
        }

        if (grades != null && !grades.isEmpty()) {
            XYChart.Series<String, Number> series = chartGenerator.createGradeDistributionSeries(grades);
            gradeBarChart.getData().add(series);
        } else {
            gradeBarChart.setTitle("No grades available for the selected options.");
        }
    }

    /**
     * Updates the class details view based on the selected class.
     */
    private void updateClassDetailsView() {
        if (selectedClass != null) {
            classStatisticsLabel.setText("Class Details: " + selectedClass.getName());
        } else {
            classStatisticsLabel.setText("No class selected");
        }
    }

    /**
     * Updates the student list view based on the selected class.
     * Clears the list if no class is selected.
     */
    private void updateStudentListView() {
        if (selectedClass == null) {
            studentListView.getItems().clear();
            return;
        }

        List<Student> students = db.getStudentsInClass(selectedClass.getClassId());
        ObservableList<Student> observableStudents = FXCollections.observableArrayList(students);
        studentListView.setItems(observableStudents);

        // Custom cell factory to display student information
        studentListView.setCellFactory(lv -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getName() + " (" + student.getStudentId() + ")");
                }
            }
        });
    }

    // ----------------------- Helper Methods -----------------------

    /**
     * Creates a visual card representation of a class.
     *
     * @param classObj The class object to represent.
     * @return A VBox containing the class details and action buttons.
     */
    private VBox createClassCard(Classes classObj) {
        VBox classCard = new VBox();
        classCard.setId(classObj.getClassId());
        classCard.getStyleClass().add("card");
        classCard.setSpacing(10);
        classCard.setPadding(new Insets(10));

        // HBox to hold class information and action buttons
        HBox classInfo = new HBox();
        classInfo.setSpacing(10);

        // Labels for class name and ID
        Label staticText = new Label("Class name: ");
        Label classNameText = new Label(classObj.getName());
        classNameText.getStyleClass().add("card-text");

        Region textSpacer = new Region();
        textSpacer.setMinWidth(20);

        Label staticIdText = new Label("Class ID: ");
        Label classIdText = new Label(classObj.getClassId());
        classIdText.getStyleClass().add("card-text");

        // TextFlow to neatly arrange labels
        TextFlow nameTextFlow = new TextFlow(staticText, classNameText, textSpacer, staticIdText, classIdText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // HBox to hold action buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setOnAction(event -> handleViewClassDetailsAction(classObj));

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> handleEditClassButtonAction(classObj));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> {
            db.delete("classes", "classId", classObj.getClassId());
            updateClassList();
        });

        // Add buttons to the container
        buttonContainer.getChildren().addAll(viewDetailsButton, editButton, deleteButton);

        // Add labels and buttons to the classInfo HBox
        classInfo.getChildren().addAll(nameTextFlow, spacer, buttonContainer);

        // Add classInfo to the classCard VBox
        classCard.getChildren().addAll(classInfo);

        // Set mouse click event to select the class
        classCard.setOnMouseClicked(event -> {
            // Reset styles for all class cards
            currentClassContainer.getChildren().forEach(node -> {
                if (node instanceof VBox) {
                    ((VBox) node).setStyle(
                            "-fx-border-color: transparent; -fx-border-width: 2px; -fx-background-color: white;");
                }
            });
            // Highlight the selected class card
            classCard.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2px; -fx-background-color: #e0e0e0;");
            selectClass(classObj);
        });

        return classCard;
    }

    /**
     * Selects a class and updates the UI to display its details.
     *
     * @param classObj The class object to select.
     */
    private void selectClass(Classes classObj) {
        selectedClass = classObj;
        classDetailsContainer.setVisible(true);
        classDetailsContainer.setManaged(true);
        populateAssessments();
        updateClassDetailsView();
        updateStudentListView();
        updateGradeChart();
    }

    /**
     * Populates the assessments ComboBox based on the selected class.
     */
    private void populateAssessments() {
        assessmentComboBox.getItems().clear();
        partComboBox.getItems().clear();

        if (selectedClass != null) {
            Course course = courseSelector.getValue();
            if (course != null) {
                List<Assessment> assessments = db.getAssessmentsForCourse(course.getId());
                assessmentComboBox.setItems(FXCollections.observableArrayList(assessments));
            }
        }
    }

    /**
     * Populates the parts ComboBox based on the selected assessment.
     */
    private void populateParts() {
        partComboBox.getItems().clear();
        Assessment selectedAssessment = assessmentComboBox.getValue();
        if (selectedAssessment != null) {
            List<AssessmentPart> parts = db.getAssessmentParts(selectedAssessment.getId());
            partComboBox.setItems(FXCollections.observableArrayList(parts));
        }
    }

    /**
     * Displays an error alert with the specified message.
     *
     * @param message The error message to display.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
