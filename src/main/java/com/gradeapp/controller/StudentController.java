package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Student;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// StudentController manages the Student category dynamic content.
public class StudentController {

    public VBox studentsContainer;
    // FXML ids
    @FXML
    private VBox studentContainer;
    @FXML
    private VBox newStudentInputContainer;
    @FXML
    private VBox currentStudentContainer;
    @FXML
    private Button addStudentButton;

    private List<Student> studentList = new ArrayList<>();

    private Database db = new Database();

    // Initialise StudentController
    @FXML
    private void initialize() {
        displayCurrentStudent();
        db.initialiseDatabase();
    }

    // Add Student click event
    @FXML
    private void handleAddStudentButtonAction() {
        VBox studentInputBox = createStudentInputBox();
        newStudentInputContainer.getChildren().add(studentInputBox);
    }

    // New student inputs, appear on Add Student button click
    private VBox createStudentInputBox() {
        VBox studentInputBox = new VBox(); // Add VBox
        studentInputBox.setPadding(new Insets(20, 20, 20, 20));
        studentInputBox.setSpacing(10);
        Label studentNameLabel = new Label("Student Name:");
        TextField studentNameField = new TextField();
        studentNameField.setPromptText("Student name");
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student Id");
        Button submitButton = new Button("+ Add Student"); // Submit button
        submitButton.setOnAction(event -> handleSubmitButtonAction(studentNameField, studentIdField));
        studentInputBox.getChildren().addAll(studentNameLabel, studentNameField, studentIdField, submitButton);
        return studentInputBox;
    }

    // Submit new student click event
    private void handleSubmitButtonAction(TextField studentNameField, TextField studentIdField) {
        String studentName = studentNameField.getText();
        String studentId = studentIdField.getText();
        if (!studentName.isEmpty() && !studentId.isEmpty()) {
            Student newStudent = new Student(studentName, studentId); // New Student object
            studentList.add(newStudent); // Add to list
            db.addStudent(studentName, studentId); // Add list to db
            studentNameField.clear(); // Clear inputs
            studentIdField.clear();
            displayCurrentStudent(); // Display current students
        } else {
            System.out.println("The form is incomplete...");
        }
    }

// Student card, displays current students
private VBox createStudentCard(Student student) {
    VBox studentCard = new VBox();
    studentCard.getStyleClass().add("card");
    studentCard.setPadding(new Insets(10));
    studentCard.setSpacing(10);
    Label studentNameLabel = new Label(student.getName());  // Display the full name
    Label studentIdLabel = new Label(student.getStudentId());
    HBox buttonContainer = new HBox(); // Create HBox to hold the buttons
    buttonContainer.setSpacing(10);
    Button editButton = new Button("Edit");
    editButton.setOnAction(event -> handleEditButtonAction(student));
    Button deleteButton = new Button("Delete");
    deleteButton.getStyleClass().add("delete-button");
    deleteButton.setOnAction(event -> {
        db.delete("students", "studentId", student.getStudentId()); // Ensure deletion is based on studentId
        displayCurrentStudent();  // Refresh the student list after deletion
    });
    buttonContainer.getChildren().addAll(editButton, deleteButton);
    studentCard.getChildren().addAll(studentNameLabel, studentIdLabel, buttonContainer);
    // Set bottom margin
    VBox.setMargin(studentCard, new Insets(0, 0, 10, 0));
    return studentCard;
}

    // Edit button action
    private void handleEditButtonAction(Student student) {
        // Display an input form with the current student's details filled in
        TextField studentNameField = new TextField(student.getName());
        TextField studentIdField = new TextField(student.getStudentId());
        Button saveButton = new Button("Save");

        saveButton.setOnAction(event -> {
            String newName = studentNameField.getText();
            String newId = studentIdField.getText();
            if (!newName.isEmpty() && !newId.isEmpty()) {
                // Update student information in the database
                db.updateStudent(student.getStudentId(), newName, newId);
                displayCurrentStudent(); // Refresh the display
            } else {
                System.out.println("The form is incomplete...");
            }
        });

        VBox editStudentBox = new VBox(10, new Label("Edit Student"), studentNameField, studentIdField, saveButton);
        currentStudentContainer.getChildren().clear();
        currentStudentContainer.getChildren().add(editStudentBox);
    }

    @FXML
    private void handleImportStudentsFromFile() {
        // Logic to handle file import
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(addStudentButton.getScene().getWindow());

        if (file != null) {
            // Assuming you have a DataImportExportController set up
            DataImportExportController dataImportExportController = new DataImportExportController();
            dataImportExportController.importData(file.getAbsolutePath());

            // Refresh the student list after importing
            displayCurrentStudent();
        }
    }

    // Display current students
    private void displayCurrentStudent() {
        currentStudentContainer.getChildren().clear();
        List<Student> studentFromDb = db.getAllStudents(); // Get students from db
        if (studentFromDb.isEmpty()) { // Display message if db empty
            Label emptyLabel = new Label("You have no current students");
            currentStudentContainer.getChildren().add(emptyLabel);
        } else {
            for (Student student : studentFromDb) {
                VBox studentCard = createStudentCard(student);
                currentStudentContainer.getChildren().add(studentCard);
            }
        }
    }
}