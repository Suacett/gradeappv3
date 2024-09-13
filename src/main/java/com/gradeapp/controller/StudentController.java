package com.gradeapp.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StudentController {

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

    private Database db = new Database();

    private ObservableList<Student> students = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadStudents();
        displayCurrentStudents();
        studentListContainer = new VBox();
        studentListContainer.setSpacing(10);
        studentListContainer.setPadding(new Insets(10));
        studentScrollPane.setContent(studentListContainer);
        loadStudents();
        displayCurrentStudents();
    }

    private void loadStudents() {
        students.setAll(db.getAllStudents());
    }

    @FXML
    private void handleEditStudentButtonAction() {
        Student selectedStudent = getSelectedStudent();
        if (selectedStudent != null) {
            showEditStudentDialog(selectedStudent);
        } else {
            showAlert("Please select a student to edit.");
        }
    }

    @FXML
    private void handleAddStudentButtonAction() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");

        TextField nameField = new TextField();
        TextField idField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Student ID:"), 0, 1);
        grid.add(idField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

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

        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(student -> {
            db.addStudent(student.getName(), student.getStudentId());
            loadStudents();
            displayCurrentStudents();
        });
    }

    @FXML
    private void handleImportStudentsFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Student CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 2) {
                        String name = data[0].trim();
                        String id = data[1].trim();
                        db.addStudent(name, id);
                    }
                }
                loadStudents();
                showAlert("Students imported successfully.");
            } catch (Exception e) {
                showAlert("Error importing students: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleViewDetailsButtonAction() {
        Student selectedStudent = getSelectedStudent();
        if (selectedStudent != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/demo3/student-details-view.fxml"));
                Parent root = loader.load();

                StudentDetailsController detailsController = loader.getController();
                detailsController.initData(selectedStudent);

                Stage stage = new Stage();
                stage.setTitle("Student Details: " + selectedStudent.getName());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error opening student details view: " + e.getMessage());
            }
        } else {
            showAlert("Please select a student to view details.");
        }
    }

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

    @FXML
    private void handleDeleteStudentButtonAction() {
        Student selectedStudent = getSelectedStudent();
        if (selectedStudent != null) {
            if (showConfirmationDialog("Are you sure you want to delete this student?")) {
                boolean deleted = db.deleteStudent(selectedStudent.getStudentId());
                if (deleted) {
                    loadStudents();
                    displayCurrentStudents();
                    showAlert("Student deleted successfully.");
                } else {
                    showAlert("Failed to delete student. Please try again.");
                }
            }
        } else {
            showAlert("Please select a student to delete.");
        }
    }

    private void displayStudentDetails(Student student) {
        studentDetailsGrid.getChildren().clear();
        studentDetailsGrid.addRow(0, new Label("Name:"), new Label(student.getName()));
        studentDetailsGrid.addRow(1, new Label("ID:"), new Label(student.getStudentId()));

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

        studentDetailsContainer.setVisible(true);
        studentDetailsContainer.setManaged(true);
    }

    private void showAddGradeDialog(Student student, Course course) {
        Dialog<Grade> dialog = new Dialog<>();
        dialog.setTitle("Add Grade");
        dialog.setHeaderText("Add a new grade for " + student.getName());

        ComboBox<Assessment> assessmentComboBox = new ComboBox<>();
        ComboBox<AssessmentPart> partComboBox = new ComboBox<>();
        TextField scoreField = new TextField();
        TextArea feedbackArea = new TextArea();

        List<Assessment> assessments = db.getAssessmentsForCourse(course.getId());
        assessmentComboBox.setItems(FXCollections.observableArrayList(assessments));

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

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

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

        dialog.showAndWait();
    }

    private void showEditStudentDialog(Student student) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");

        TextField nameField = new TextField(student.getName());
        TextField idField = new TextField(student.getStudentId());

        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Student ID:"), 0, 1);
        grid.add(idField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType editButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

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

        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(updatedStudent -> {
            db.updateStudent(student.getStudentId(), updatedStudent.getName(), updatedStudent.getStudentId());
            loadStudents();
            displayStudentDetails(updatedStudent);
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // Current students list - create student card
    private VBox createStudentCard(Student student) {
        VBox studentCard = new VBox();
        studentCard.getStyleClass().add("card");
        studentCard.setSpacing(10);
        studentCard.setPadding(new Insets(10));

        HBox studentInfo = new HBox();
        studentInfo.setSpacing(10);

        Label nameLabel = new Label("Name: " + student.getName());
        Label idLabel = new Label("ID: " + student.getStudentId());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);

        Button viewEditButton = new Button("View/edit Details");
        viewEditButton.setOnAction(event -> handleViewDetailsButtonAction());

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> handleDeleteStudentAction(student));

        buttonContainer.getChildren().addAll(viewEditButton, deleteButton);
        studentInfo.getChildren().addAll(nameLabel, idLabel);
        studentCard.getChildren().addAll(studentInfo, spacer, buttonContainer);

        studentCard.setOnMouseClicked(event -> {
            studentListContainer.getChildren().forEach(node -> {
                if (node instanceof VBox) {
                    ((VBox) node).setStyle(
                            "-fx-border-color: transparent; -fx-border-width: 2px; -fx-background-color: white;");
                }
            });
            studentCard.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2px; -fx-background-color: #e0e0e0;");
            // handleViewDetailsButtonAction(); When student card is clicked, this will open
            // details automatically depends if this is needed or not
        });

        return studentCard;
    }

    private Object handleDeleteStudentAction(Student student) {
        if (student != null) {
            if (showConfirmationDialog("Are you sure you want to delete " + student.getName() + "?")) {
                boolean success = db.deleteStudent(student.getStudentId());
                if (success) {
                    loadStudents();
                    displayCurrentStudents();
                    showAlert("Student '" + student.getName() + "' deleted successfully.");
                } else {
                    showAlert("Error: Unable to delete student. Please try again.");
                }
            }
        } else {
            showAlert("Please select a student to delete.");
        }
        return null;
    }

    // Display current students list
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

}