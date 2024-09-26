package com.gradeapp.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller class for managing student-related functionalities.
 * Handles adding, editing, deleting, viewing details, and importing students.
 */
public class StudentController {

    // ----------------------------- FXML UI Components -----------------------------
    
    @FXML
    private ScrollPane studentList;
    
    @FXML
    private VBox content;
    
    @FXML
    private ListView<Student> studentListView;
    
    @FXML
    private VBox studentDetailsContainer;
    
    @FXML
    private GridPane studentDetailsGrid;
    
    @FXML
    private ScrollPane studentScrollPane;
    
    @FXML
    private VBox studentListContainer;
    
    @FXML
    private TextField studentName;
    
    @FXML
    private TextField studentId;
    
    @FXML
    private TextField studentDescription;
    
    @FXML
    private ComboBox<Course> courseSelector;
    
    @FXML
    private Button viewDetailsButton;
    
    @FXML
    private Button deleteStudentButton;

    // ----------------------------- Non-UI Fields -----------------------------
    
    private Database db;
    private Student selectedStudent;
    private ObservableList<Student> students = FXCollections.observableArrayList();

    // ----------------------------- Initialization -----------------------------
    
    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up the student list container, initializes the database, and loads students.
     */
    @FXML
    private void initialize() {
        studentListContainer = new VBox();
        studentListContainer.setSpacing(10);
        studentListContainer.setPadding(new Insets(10));
        studentScrollPane.setContent(studentListContainer);
        db = new Database();
        loadStudents();
        displayCurrentStudents();
    }

    // ----------------------------- Data Loading Methods -----------------------------
    
    /**
     * Loads all students from the database into the observable list.
     */
    private void loadStudents() {
        students.setAll(db.getAllStudents());
    }

    /**
     * Displays the current list of students in the UI.
     * Creates a visual card for each student.
     */
    public void displayCurrentStudents() {
        studentListContainer.getChildren().clear();

        List<Student> studentsFromDb = db.getAllStudents();
        System.out.println("Students from DB: " + studentsFromDb.size());

        if (studentsFromDb.isEmpty()) {
            Label emptyLabel = new Label("You have no current students");
            studentListContainer.getChildren().add(emptyLabel);
        } else {
            for (Student student : studentsFromDb) {
                VBox studentCard = createStudentCard(student);
                studentListContainer.getChildren().add(studentCard);
                System.out.println("Added student card: " + student.getName());
            }
        }
    }

    // ----------------------------- Event Handlers -----------------------------
    
    /**
     * Handles the action of editing a selected student.
     * Opens a dialog to edit student details.
     */
    @FXML
    private void handleEditStudentButtonAction() {
        Student selectedStudent = getSelectedStudent();
        if (selectedStudent != null) {
            showEditStudentDialog(selectedStudent);
        } else {
            showAlert("Please select a student to edit.");
        }
    }

    /**
     * Handles the action of adding a new student.
     * Opens a dialog to input new student details.
     */
    @FXML
    private void handleAddStudentButtonAction() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");

        // Create input fields
        TextField nameField = new TextField();
        TextField idField = new TextField();

        // Arrange fields in a grid
        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Student ID:"), 0, 1);
        grid.add(idField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Define buttons
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Convert dialog result to a Student object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                String name = nameField.getText();
                String id = idField.getText();
                if (!name.isEmpty() && !id.isEmpty()) {
                    return new Student(name, id);
                }
            }
            return null;
        });

        // Show dialog and handle the result
        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(student -> {
            try {
                db.addStudent(student.getName(), student.getStudentId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadStudents();
            displayCurrentStudents();
        });
    }

    /**
     * Handles the action of importing students from a file.
     * Opens a file chooser to select the import file.
     */
    @FXML
    private void handleImportStudentsFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Student File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(studentScrollPane.getScene().getWindow());

        if (file != null) {
            DataImportExportController importExportController = new DataImportExportController();
            importExportController.importData(file.getAbsolutePath());
            loadStudents();
            displayCurrentStudents();
        }
    }

    /**
     * Handles the action of viewing details of a selected student.
     * Opens a new window with detailed information.
     */
    @FXML
    private void handleViewDetailsButtonAction() {
        if (selectedStudent != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/demo3/student-details-view.fxml"));

                if (loader.getLocation() == null) {
                    System.out.println("FXML file not found at specified path.");
                    showAlert("Error: FXML file not found at specified path.");
                    return;
                }

                Parent root = loader.load();

                StudentDetailsController controller = loader.getController();
                controller.initData(selectedStudent);

                Stage stage = new Stage();
                stage.setTitle("Student Details");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Refresh the student list after closing the details view
                loadStudents();
                displayCurrentStudents();
                updateActionButtons();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error loading student details view: " + e.getMessage());
            }
        } else {
            showAlert("Please select a student to view details.");
        }
    }

    /**
     * Handles the action of adding a grade to a selected student.
     * Opens a dialog to input grade details.
     */
    @FXML
    private void handleAddGradeButtonAction() {
        Student selectedStudent = getSelectedStudent();
        if (selectedStudent != null) {
            List<Course> courses = db.getCoursesForStudent(selectedStudent.getStudentId());
            if (!courses.isEmpty()) {
                Course course = courses.get(0);
                showAddGradeDialog(selectedStudent, course);
            } else {
                showAlert("The selected student is not assigned to any course.");
            }
        } else {
            showAlert("Please select a student to add a grade.");
        }
    }

    /**
     * Handles the action of deleting a selected student.
     * Prompts for confirmation before deletion.
     */
    @FXML
    private void handleDeleteStudentButtonAction() {
        if (selectedStudent != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Student");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete " + selectedStudent.getName() + "?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = db.deleteStudent(selectedStudent.getStudentId());
                if (deleted) {
                    showAlert("Student deleted successfully.");
                    selectedStudent = null;
                    loadStudents();
                    displayCurrentStudents();
                    updateActionButtons();
                } else {
                    showAlert("Failed to delete student. Please try again.");
                }
            }
        } else {
            showAlert("Please select a student to delete.");
        }
    }

    // ----------------------------- Helper Methods -----------------------------
    
    /**
     * Retrieves the currently selected student based on UI selection.
     *
     * @return The selected Student object, or null if none is selected.
     */
    private Student getSelectedStudent() {
        for (Node node : studentListContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox studentCard = (VBox) node;
                if (studentCard.getStyle().contains("-fx-border-color: #2196F3")) {
                    HBox infoBox = (HBox) studentCard.getChildren().get(0);
                    Label idLabel = (Label) infoBox.getChildren().get(1);
                    String studentId = idLabel.getText().replace("ID: ", "");
                    return db.getStudentById(studentId);
                }
            }
        }
        return null;
    }

    /**
     * Highlights the selected student card in the UI.
     *
     * @param selectedCard The VBox representing the selected student card.
     */
    private void highlightSelectedStudent(VBox selectedCard) {
        for (Node node : studentListContainer.getChildren()) {
            if (node instanceof VBox) {
                node.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px; -fx-background-color: white;");
            }
        }
        selectedCard.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2px; -fx-background-color: #e0f7fa;");
    }

    /**
     * Selects a student and updates the UI accordingly.
     *
     * @param student      The student to select.
     * @param selectedCard The VBox representing the selected student card.
     */
    private void selectStudent(Student student, VBox selectedCard) {
        selectedStudent = student;
        updateActionButtons();
        highlightSelectedStudent(selectedCard);
    }

    /**
     * Creates a visual representation (card) for a student.
     *
     * @param student The student for whom the card is created.
     * @return A VBox containing student information and a button to open the Mark Book.
     */
    private VBox createStudentCard(Student student) {
        VBox studentCard = new VBox();
        studentCard.getStyleClass().add("card");
        studentCard.setSpacing(10);
        studentCard.setPadding(new Insets(10));

        // Student Information
        HBox studentInfo = new HBox();
        studentInfo.setSpacing(10);

        Label nameLabel = new Label("Name: " + student.getName());
        Label idLabel = new Label("ID: " + student.getStudentId());

        studentInfo.getChildren().addAll(nameLabel, idLabel);
        studentCard.getChildren().add(studentInfo);

        // Event handler for selecting the student
        studentCard.setOnMouseClicked(event -> {
            selectStudent(student, studentCard);
        });

        return studentCard;
    }



    /**
     * Updates the state of action buttons based on whether a student is selected.
     */
    private void updateActionButtons() {
        boolean isSelected = selectedStudent != null;
        viewDetailsButton.setDisable(!isSelected);
        deleteStudentButton.setDisable(!isSelected);
    }

    // ----------------------------- Dialog Methods -----------------------------
    
    /**
     * Displays a dialog to edit the details of a selected student.
     *
     * @param student The student to edit.
     */
    private void showEditStudentDialog(Student student) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");

        // Create input fields with existing student data
        TextField nameField = new TextField(student.getName());
        TextField idField = new TextField(student.getStudentId());

        // Arrange fields in a grid
        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Student ID:"), 0, 1);
        grid.add(idField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Define buttons
        ButtonType editButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        // Convert dialog result to a Student object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == editButton) {
                String name = nameField.getText();
                String id = idField.getText();
                if (!name.isEmpty() && !id.isEmpty()) {
                    student.setName(name);
                    student.setStudentId(id);
                    return student;
                }
            }
            return null;
        });

        // Show dialog and handle the result
        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(updatedStudent -> {
            db.updateStudent(student.getStudentId(), updatedStudent.getName(), updatedStudent.getStudentId());
            loadStudents();
            displayStudentDetails(updatedStudent);
        });
    }

    /**
     * Displays a dialog to add a new grade for a student.
     *
     * @param student The student to whom the grade is added.
     * @param course  The course associated with the grade.
     */
    private void showAddGradeDialog(Student student, Course course) {
        Dialog<Grade> dialog = new Dialog<>();
        dialog.setTitle("Add Grade");
        dialog.setHeaderText("Add a new grade for " + student.getName());

        // Create input fields
        ComboBox<Assessment> assessmentComboBox = new ComboBox<>();
        ComboBox<AssessmentPart> partComboBox = new ComboBox<>();
        TextField scoreField = new TextField();
        TextArea feedbackArea = new TextArea();

        // Populate assessment combo box
        List<Assessment> assessments = db.getAssessmentsForCourse(course.getId());
        assessmentComboBox.setItems(FXCollections.observableArrayList(assessments));

        // Update part combo box based on selected assessment
        assessmentComboBox.setOnAction(e -> {
            Assessment selectedAssessment = assessmentComboBox.getValue();
            if (selectedAssessment != null) {
                List<AssessmentPart> parts = db.getAssessmentParts(selectedAssessment.getId());
                partComboBox.setItems(FXCollections.observableArrayList(parts));
                partComboBox.setDisable(parts.isEmpty());
            } else {
                partComboBox.getItems().clear();
                partComboBox.setDisable(true);
            }
        });

        // Arrange fields in a grid
        GridPane grid = new GridPane();
        grid.add(new Label("Assessment:"), 0, 0);
        grid.add(assessmentComboBox, 1, 0);
        grid.add(new Label("Part:"), 0, 1);
        grid.add(partComboBox, 1, 1);
        grid.add(new Label("Score:"), 0, 2);
        grid.add(scoreField, 1, 2);
        grid.add(new Label("Feedback:"), 0, 3);
        grid.add(feedbackArea, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Define buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Convert dialog result to a Grade object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Assessment assessment = assessmentComboBox.getValue();
                    AssessmentPart part = partComboBox.getValue();
                    double score = Double.parseDouble(scoreField.getText());
                    String feedback = feedbackArea.getText();

                    Grade grade = new Grade(student, assessment, part, score, feedback);
                    db.saveGrade(grade);
                    return grade;
                } catch (NumberFormatException e) {
                    showAlert("Invalid score. Please enter a valid number.");
                }
            }
            return null;
        });

        // Show dialog and handle the result
        dialog.showAndWait();
    }

    // ----------------------------- Detail Display Methods -----------------------------
    
    /**
     * Displays detailed information about a selected student.
     *
     * @param student The student whose details are to be displayed.
     */
    private void displayStudentDetails(Student student) {
        studentDetailsGrid.getChildren().clear();
        studentDetailsGrid.addRow(0, new Label("Name:"), new Label(student.getName()));
        studentDetailsGrid.addRow(1, new Label("ID:"), new Label(student.getStudentId()));

        // Display courses
        List<Course> courses = db.getCoursesForStudent(student.getStudentId());
        if (!courses.isEmpty()) {
            StringBuilder courseNames = new StringBuilder();
            for (Course course : courses) {
                if (courseNames.length() > 0) {
                    courseNames.append(", ");
                }
                courseNames.append(course.getName());
            }
            studentDetailsGrid.addRow(2, new Label("Courses:"), new Label(courseNames.toString()));
        } else {
            studentDetailsGrid.addRow(2, new Label("Courses:"), new Label("Not assigned"));
        }

        // Display classes
        List<Classes> classes = db.getClassesForStudent(student.getStudentId());
        if (!classes.isEmpty()) {
            StringBuilder classNames = new StringBuilder();
            for (Classes cls : classes) {
                if (classNames.length() > 0) {
                    classNames.append(", ");
                }
                classNames.append(cls.getName());
            }
            studentDetailsGrid.addRow(3, new Label("Classes:"), new Label(classNames.toString()));
        } else {
            studentDetailsGrid.addRow(3, new Label("Classes:"), new Label("Not assigned"));
        }

        // Display grades
        List<Grade> grades = db.getGradesForStudent(student.getStudentId());
        if (!grades.isEmpty()) {
            studentDetailsGrid.addRow(4, new Label("Grades:"), new Label());
            int row = 5;
            for (Grade grade : grades) {
                studentDetailsGrid.addRow(row++, new Label(grade.getAssessment().getName() + ":"),
                        new Label(String.format("%.2f", grade.getScore())));
            }
        } else {
            studentDetailsGrid.addRow(4, new Label("Grades:"), new Label("No grades available"));
        }

        // Make the details container visible
        studentDetailsContainer.setVisible(true);
        studentDetailsContainer.setManaged(true);
    }

    // ----------------------------- Utility Methods -----------------------------
    
    /**
     * Displays an informational alert to the user.
     *
     * @param message The message to display.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
