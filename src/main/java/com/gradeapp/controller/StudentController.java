package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Outcome;
import com.gradeapp.model.Student;

import java.util.Map;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    @FXML private VBox studentDetailsContent;
    @FXML private Student selectedStudent;

    private List<Student> studentList = new ArrayList<>();

    private Database db = new Database();

    // Initialise StudentController
    @FXML
    private void initialize() {
        displayCurrentStudent();
        db.initialiseDatabase();
        
        if (studentDetailsContent == null) {
            System.err.println("studentDetailsContent is null after initialization");
        } else {
            System.out.println("studentDetailsContent initialized successfully");
        }
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
    
    Label studentNameLabel = new Label(student.getName());
    Label studentIdLabel = new Label(student.getStudentId());
    
    studentCard.setOnMouseClicked(event -> {
        this.selectedStudent = student;
        displayStudentDetails(student);
    });

    HBox buttonContainer = new HBox();
    buttonContainer.setSpacing(10);
    
    Button editButton = new Button("Edit");
    editButton.setOnAction(event -> handleEditButtonAction(student));
    
    Button deleteButton = new Button("Delete");
    deleteButton.getStyleClass().add("delete-button");
    deleteButton.setOnAction(event -> {
        db.delete("students", "studentId", student.getStudentId());
        displayCurrentStudent();
    });
    
    Button viewDetailsButton = new Button("View Details");
    viewDetailsButton.setOnAction(event -> displayStudentDetails(student));

    buttonContainer.getChildren().addAll(editButton, deleteButton, viewDetailsButton);
    
    
    // Clear the button container before adding buttons
    buttonContainer.getChildren().clear();
    buttonContainer.getChildren().addAll(editButton, deleteButton, viewDetailsButton);
    
    studentCard.getChildren().addAll(studentNameLabel, studentIdLabel, buttonContainer);
    
    VBox.setMargin(studentCard, new Insets(0, 0, 10, 0));
    return studentCard;
}

private void displayStudentDetails(Student student) {
    if (studentDetailsContent == null) {
        System.err.println("studentDetailsContent is null. Check FXML file and controller initialization.");
        return;
    }

    studentDetailsContent.getChildren().clear();

    Label nameLabel = new Label("Name: " + student.getName());
    Label idLabel = new Label("ID: " + student.getStudentId());

    studentDetailsContent.getChildren().addAll(nameLabel, idLabel);

    // Get classes for the student
    List<Classes> classes = db.getClassesForStudent(student.getStudentId());
    VBox classesBox = new VBox(5);
    classesBox.getChildren().add(new Label("Classes:"));
    if (!classes.isEmpty()) {
        for (Classes cls : classes) {
            classesBox.getChildren().add(new Label("  - " + cls.getName()));
        }
    } else {
        classesBox.getChildren().add(new Label("  Not enrolled in any classes."));
    }
    studentDetailsContent.getChildren().add(classesBox);

    VBox gradesBox = new VBox(5);
    gradesBox.getChildren().add(new Label("Grades:"));
    
    List<Grade> studentGrades = student.getGrades();
    
    if (!studentGrades.isEmpty()) {
        for (Grade grade : studentGrades) {
            Assessment assessment = grade.getAssessment();
            String gradeInfo = String.format("  - %s: %.2f", assessment.getName(), grade.getScore());
            if (grade.getPart() != null) {
                gradeInfo += String.format(" (Part: %s)", grade.getPart().getName());
            }
            gradesBox.getChildren().add(new Label(gradeInfo));
        }
        
        double averageGrade = student.calculateOverallPerformance();
        gradesBox.getChildren().add(new Label(String.format("Average Grade: %.2f", averageGrade)));
    } else {
        gradesBox.getChildren().add(new Label("  No grades available."));
    }

    studentDetailsContent.getChildren().add(gradesBox);
}

@FXML
private void handleAddGradeButtonAction() {
    Student selectedStudent = getSelectedStudent();
    if (selectedStudent == null) {
        showAlert("Please select a student first.");
        return;
    }
    
    Dialog<Grade> dialog = new Dialog<>();
    dialog.setTitle("Add Grade");
    dialog.setHeaderText("Add a new grade for " + selectedStudent.getName());
    
        ComboBox<Assessment> assessmentComboBox = new ComboBox<>(FXCollections.observableArrayList(db.getAllAssessments()));
        ComboBox<AssessmentPart> partComboBox = new ComboBox<>();
        TextField scoreField = new TextField();
        TextArea feedbackArea = new TextArea();
    
        assessmentComboBox.setCellFactory(lv -> new ListCell<Assessment>() {
            @Override
            protected void updateItem(Assessment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
        assessmentComboBox.setButtonCell(assessmentComboBox.getCellFactory().call(null));
    
        assessmentComboBox.setOnAction(e -> {
            Assessment selectedAssessment = assessmentComboBox.getValue();
            if (selectedAssessment != null) {
                partComboBox.setItems(FXCollections.observableArrayList(selectedAssessment.getParts()));
                partComboBox.setDisable(selectedAssessment.getParts().isEmpty());
            }
        });
    
        dialog.getDialogPane().setContent(new VBox(10,
            new Label("Assessment:"), assessmentComboBox,
            new Label("Part (optional):"), partComboBox,
            new Label("Score:"), scoreField,
            new Label("Feedback:"), feedbackArea
        ));
    
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Assessment assessment = assessmentComboBox.getValue();
                    AssessmentPart part = partComboBox.getValue();
                    if (assessment == null) {
                        showAlert("Please select an assessment.");
                        return null;
                    }
                    double score = Double.parseDouble(scoreField.getText());
                    String feedback = feedbackArea.getText();
                    
                    Grade grade;
                    if (part != null) {
                        grade = new Grade(selectedStudent, assessment, part, score, feedback);
                    } else {
                        grade = new Grade(selectedStudent, assessment, score, feedback);
                    }
                    
                    selectedStudent.addGrade(grade);
                    db.saveGrade(grade);
                    
                    return grade;
                } catch (NumberFormatException e) {
                    showAlert("Invalid input. Please enter a valid number for the score.");
                    return null;
                }
            }
            return null;
        });
    
        dialog.showAndWait().ifPresent(grade -> {
            System.out.println("Grade added: " + grade);
            displayStudentDetails(selectedStudent);
        });
    }

private Student getSelectedStudent() {
    return this.selectedStudent;
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
        this.selectedStudent = null; // Clear the selected student
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

private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

}