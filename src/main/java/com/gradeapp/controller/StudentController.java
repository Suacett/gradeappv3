package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Course;
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

    // Method to handle 'Add Student' button click
    @FXML
    private void handleAddStudentButtonAction() {
        VBox studentInputBox = createStudentInputBox();
        newStudentInputContainer.getChildren().add(studentInputBox);
    }

    // Create a new student input box
    private VBox createStudentInputBox() {
        VBox studentInputBox = new VBox();
        studentInputBox.setPadding(new Insets(20, 20, 20, 20));
        studentInputBox.setSpacing(10);

        Label studentNameLabel = new Label("Student Name:");
        TextField studentNameField = new TextField();
        studentNameField.setPromptText("Student name");

        Label studentDescriptionLabel = new Label("Student Id:");
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student Id");

        Button submitButton = new Button("+ Add Student");
        submitButton.setOnAction(event -> handleSubmitButtonAction(studentNameField, studentIdField));

        studentInputBox.getChildren().addAll(studentNameLabel, studentNameField, studentDescriptionLabel, studentIdField, submitButton);
        return studentInputBox;
    }

    // Method to handle 'Submit' new student button click
    private void handleSubmitButtonAction(TextField studentNameField, TextField studentIdField) {
        String studentName = studentNameField.getText();
        String studentId = studentIdField.getText();
        if (!studentName.isEmpty() && !studentId.isEmpty()) {
            Student newStudent = new Student(studentName, studentId); // Create a new Student object
            studentList.add(newStudent); // Add new student to studentList
            db.addStudent(studentName, studentId); // Add studentList to database

            studentNameField.clear(); // Clear name input field
            studentIdField.clear(); // Clear id input field
            displayCurrentStudent(); // Update current student field
        } else { // Handle empty fields
            System.out.println("The form is incomplete");
        }
    }

    // Create student card to display current students
    private VBox createStudentCard(Student student) {
        VBox studentCard = new VBox();
        studentCard.setPadding(new Insets(10));
        studentCard.setSpacing(10);
        studentCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 5px;");

        Label studentNameLabel = new Label(student.getName());
        Label studentIdLabel = new Label(student.getStudentId());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            db.delete("students", "name", student.getName()); // Delete student from database
            displayCurrentStudent(); // Refresh the course list
        });

        studentCard.getChildren().addAll(studentNameLabel, studentIdLabel, deleteButton);
        return studentCard;
    }


    // Display current student
    private void displayCurrentStudent() {
        currentStudentContainer.getChildren().clear();

        // Retrieve student from the database
        List<Student> studentFromDb = db.getAllStudents();

        // If there are no student, show a message
        if (studentFromDb.isEmpty()) {
            Label emptyLabel = new Label("You have no current students");
            currentStudentContainer.getChildren().add(emptyLabel);
        } else {
            // Display student from the database
            for (Student student : studentFromDb) {
                VBox studentCard = createStudentCard(student);
                currentStudentContainer.getChildren().add(studentCard);
            }
        }
    }
    
}