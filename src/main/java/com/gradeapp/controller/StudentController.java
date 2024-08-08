package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Student;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


// StudentController manages the Student category dynamic content.
public class StudentController {

// FXML ids
    @FXML
    private VBox studentContainer;
    @FXML
    private Button addstudentButton;
    @FXML
    private VBox newStudentInputContainer;
    @FXML
    private VBox currentStudentContainer;

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
        Label studentDescriptionLabel = new Label("Student Id:");
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student Id");
        Button submitButton = new Button("+ Add Student"); // Submit button
        submitButton.setOnAction(event -> handleSubmitButtonAction(studentNameField, studentIdField));
        studentInputBox.getChildren().addAll(studentNameLabel, studentNameField, studentDescriptionLabel, studentIdField, submitButton);
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
        VBox studentCard = new VBox(); // Add VBox
        studentCard.setPadding(new Insets(10));
        studentCard.setSpacing(10);
        studentCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 5px;");
        Label studentNameLabel = new Label(student.getName());
        Label studentIdLabel = new Label(student.getStudentId());
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            db.delete("students", "name", student.getName()); // Delete from db
            displayCurrentStudent(); // Display current courses
        });
        studentCard.getChildren().addAll(studentNameLabel, studentIdLabel, deleteButton);
        return studentCard;
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