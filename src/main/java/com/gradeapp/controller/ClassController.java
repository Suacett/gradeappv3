package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
public class ClassController {

    @FXML private VBox classContainer;
    @FXML private VBox currentClassContainer;
    @FXML private VBox newClassInputContainer;
    @FXML private ComboBox<Course> courseSelector;
    @FXML private ListView<Student> studentListView;
    @FXML private Label classStatisticsLabel;

    
    private Database db = new Database();
    private Classes selectedClass;
    
    @FXML
    private void initialize() {
        setupCourseSelector();
        displayCurrentClasses();
    }


    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);
        courseSelector.setOnAction(e -> updateClassList());
    }

    private void updateClassList() {
        Course selectedCourse = courseSelector.getValue();
        if (selectedCourse != null) {
            ObservableList<Classes> classes = FXCollections.observableArrayList(db.getClassesForCourse(selectedCourse.getId()));
        }
    }

    // Add Class click event
    @FXML
    private void handleAddClassButtonAction() throws SQLException {
        Dialog<Classes> dialog = new Dialog<>();
        dialog.setTitle("Add New Class");

        TextField nameField = new TextField();
        TextField idField = new TextField();
        ComboBox<Course> courseComboBox = new ComboBox<>(FXCollections.observableArrayList(db.getAllCourses()));

        dialog.getDialogPane().setContent(new VBox(10, 
            new Label("Class Name:"), nameField,
            new Label("Class ID:"), idField,
            new Label("Course:"), courseComboBox
        ));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String name = nameField.getText();
                String id = idField.getText();
                Course course = courseComboBox.getValue();
                if (name.isEmpty() || id.isEmpty() || course == null) {
                    showError("Please fill in all fields and select a course.");
                    return null;
                }
                Classes newClass = new Classes(name, id);
                try {
                    db.addClass(newClass, course.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return newClass;
            }
            return null;
        });
        dialog.showAndWait().ifPresent(result -> displayCurrentClasses());
    }

    @FXML
    public void handleViewClassDetailsAction(Classes classObj) {
        selectedClass = classObj;
        updateClassDetailsView();
    }

    private void updateClassDetailsView() {
        if (selectedClass != null) {
            classStatisticsLabel.setText("Class Statistics: " + calculateClassStatistics());
            updateStudentListView();
        }
    }

    private String calculateClassStatistics() {
        // Implement logic to calculate class statistics
        return "Average Grade: X, Passing Rate: Y%";
    }

    private void updateStudentListView() {
        ObservableList<Student> students = FXCollections.observableArrayList(db.getStudentsInClass(selectedClass.getClassId()));
        studentListView.setItems(students);
    }

    @FXML
    public void handleAddStudentToClassAction() throws SQLException {
        if (selectedClass == null) {
            showError("Please select a class first.");
            return;
        }

        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add Student to Class");

        ComboBox<Student> studentComboBox = new ComboBox<>(FXCollections.observableArrayList(db.getAllStudents()));

        dialog.getDialogPane().setContent(new VBox(10, 
            new Label("Select Student:"), studentComboBox
        ));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Student student = studentComboBox.getValue();
                if (student != null) {
                    db.addStudentToClass(student.getStudentId(), selectedClass.getClassId());
                    return student;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> updateStudentListView());
    }

    // New class inputs, appear on Add Class button click
    private VBox createClassInputBox() {
        VBox classInputBox = new VBox();
        classInputBox.setPadding(new Insets(20, 20, 20, 20));
        classInputBox.setSpacing(10);
        Label classNameLabel = new Label("Class Name:");
        TextField classNameField = new TextField();
        classNameField.setPromptText("Class name");
        TextField classIdField = new TextField();
        classIdField.setPromptText("Class Id");
        Button submitButton = new Button("+ Add Class"); // Submit button
        submitButton.setOnAction(event -> handleSubmitClassButtonAction(classNameField, classIdField));
        classInputBox.getChildren().addAll(classNameLabel, classNameField, classIdField, submitButton);
        return classInputBox;
    }

    // Submit new class click event
    private void handleSubmitClassButtonAction(TextField classNameField, TextField classIdField) {
        String className = classNameField.getText();
        String classId = classIdField.getText();
        if (!className.isEmpty() && !classId.isEmpty()) {
            Classes newClass = new Classes(className, classId); // New Class object
            db.addClass(className, classId); // Add class to db
            classNameField.clear(); // Clear inputs
            classIdField.clear();
            displayCurrentClasses(); // Display current classes
        } else {
            System.out.println("The form is incomplete...");
        }
    }

    // Class card, displays current classes
    private VBox createClassCard(Classes classes) {
        VBox classCard = new VBox();
        classCard.getStyleClass().add("card");
        classCard.setPadding(new Insets(10));
        classCard.setSpacing(10);

        Label classNameLabel = new Label(classes.getName());  // Display the full name as it is
        Label classIdLabel = new Label(classes.getClassId());

        // Create HBox to hold the buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);

        
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setOnAction(event -> handleViewClassDetailsAction(classes));

        buttonContainer.getChildren().add(viewDetailsButton);
        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> handleEditClassButtonAction(classes));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> {
            db.delete("classes", "classId", classes.getClassId()); // Ensure deletion is based on classId
            displayCurrentClasses();  // Refresh the class list after deletion
        });

        buttonContainer.getChildren().addAll(editButton, deleteButton);
        classCard.getChildren().addAll(classNameLabel, classIdLabel, buttonContainer);
        VBox.setMargin(classCard, new Insets(0, 0, 10, 0));
        return classCard;
    }

    // Edit button action for classes
    private void handleEditClassButtonAction(Classes classes) {
        // Create text fields pre-populated with the current class's details
        TextField classNameField = new TextField(classes.getName());
        TextField classIdField = new TextField(classes.getClassId());
        Button saveButton = new Button("Save");

        // Set the action for the save button
        saveButton.setOnAction(event -> {
            String newName = classNameField.getText();
            String newId = classIdField.getText();
            if (!newName.isEmpty() && !newId.isEmpty()) {
                // Update the class in the database
                db.updateClass(classes.getClassId(), newName, newId);
                displayCurrentClasses(); // Refresh the UI to reflect changes
            } else {
                System.out.println("The form is incomplete...");
            }
        });

        // Display the edit form in the UI
        VBox editClassBox = new VBox(10, new Label("Edit Class"), classNameField, classIdField, saveButton);
        currentClassContainer.getChildren().clear(); // Clear the current view
        currentClassContainer.getChildren().add(editClassBox); // Display the edit form
    }


    // Display current classes
    private void displayCurrentClasses() {
        currentClassContainer.getChildren().clear();
        List<Classes> classes = db.getAllClasses();
        for (Classes classObj : classes) {
            VBox classCard = createClassCard(classObj);
            currentClassContainer.getChildren().add(classCard);
        }
    }

    @FXML
    public void handleRemoveStudentFromClassAction() {
        Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null && selectedClass != null) {
            db.removeStudentFromClass(selectedStudent.getStudentId(), selectedClass.getClassId());
            updateStudentListView();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
