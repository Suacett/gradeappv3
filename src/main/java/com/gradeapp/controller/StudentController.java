package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;

public class StudentController {

    @FXML private ListView<Student> studentListView;
    @FXML private VBox studentDetailsContainer;
    @FXML private GridPane studentDetailsGrid;

    private Database db = new Database();
    private ObservableList<Student> students = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadStudents();
        studentListView.setItems(students);
        studentListView.setCellFactory(lv -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                setText(empty ? null : student.getName() + " (" + student.getStudentId() + ")");
            }
        });
    }

    private void loadStudents() {
        students.setAll(db.getAllStudents());
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
        Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            displayStudentDetails(selectedStudent);
        } else {
            showAlert("Please select a student to view details.");
        }
    }

    @FXML
    private void handleAddGradeButtonAction() {
        Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            List<Course> courses = db.getCoursesForStudent(selectedStudent.getStudentId());
            if (!courses.isEmpty()) {
                // If there's more than one course, you might want to let the user choose
                // For now, we'll just use the first course
                Course course = courses.get(0);
                showAddGradeDialog(selectedStudent, course);
            } else {
                showAlert("The selected student is not assigned to any course.");
            }
        } else {
            showAlert("Please select a student to add a grade.");
        }
    }

    @FXML
    private void handleEditStudentButtonAction() {
        Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            showEditStudentDialog(selectedStudent);
        } else {
            showAlert("Please select a student to edit.");
        }
    }

    @FXML
    private void handleDeleteStudentButtonAction() {
        Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            if (showConfirmationDialog("Are you sure you want to delete this student?")) {
                boolean deleted = db.deleteStudent(selectedStudent.getStudentId());
                if (deleted) {
                    loadStudents();
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

        // Set up the dialog content
        ComboBox<Assessment> assessmentComboBox = new ComboBox<>();
        ComboBox<AssessmentPart> partComboBox = new ComboBox<>();
        TextField scoreField = new TextField();
        TextArea feedbackArea = new TextArea();

        // Populate assessments
        List<Assessment> assessments = db.getAssessmentsForCourse(course.getId());
        assessmentComboBox.setItems(FXCollections.observableArrayList(assessments));

        // Set up assessment selection listener
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

}