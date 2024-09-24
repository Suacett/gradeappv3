package com.gradeapp.controller;

import java.io.IOException;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class MarkingController {

    // FXML elements
    @FXML
    private ComboBox<Course> courseSelector;
    @FXML
    private ComboBox<Classes> classSelector;
    @FXML
    private ComboBox<Assessment> assessmentSelector;
    @FXML
    private VBox studentsInClass;
    @FXML
    private Label studentName;
    @FXML
    private Label studentId;

    private Database db = new Database();
    private Course selectedCourse;

    @FXML
    private void initialize() {
        setupCourseSelector();
        setupClassSelector();
        setupAssessmentSelector();

        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateClassSelector(newSelection);
                updateAssessmentSelector();
            }
        });

        classSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateStudentList(newSelection);
            }
        });

        assessmentSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateStudentList(classSelector.getSelectionModel().getSelectedItem());
            }
        });
    }

    // Method to select course from dropdown
    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCourse = newSelection; // Store selected course
                updateClassSelector(newSelection);
                updateAssessmentSelector(); // Update assessments based on selected course
            }
        });

        courseSelector.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course == null ? "" : course.getName();
            }

            @Override
            public Course fromString(String string) {
                return null;
            }
        });
        // Update classSelector when course is selected
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateClassSelector(newSelection);
            }
        });
        if (!courses.isEmpty()) { // Select first course in list
            courseSelector.getSelectionModel().selectFirst();
        }
    }

    // Method to update classSelector based on selected course
    private void updateClassSelector(Course selectedCourse) {
        ObservableList<Classes> classes = FXCollections
                .observableArrayList(db.getClassesForCourse(selectedCourse.getId()));
        classSelector.setItems(classes);
        classSelector.getSelectionModel().selectFirst();
        if (!classes.isEmpty()) {
            updateStudentList(classes.get(0));
        }
    }

    // Method to update assessmentSelector based on selected class
    private void updateAssessmentSelector() {
        Course selectedCourse = courseSelector.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            ObservableList<Assessment> assessments = FXCollections
                    .observableArrayList(db.getAssessmentsForCourse(selectedCourse.getId()));
            assessmentSelector.setItems(assessments);
            assessmentSelector.getSelectionModel().selectFirst();
        }
    }

    // Method to update the student list based on the selected class
    private void updateStudentList(Classes selectedClass) {
        if (selectedClass == null) {
            System.out.println("No class selected.");
            return;
        }
        System.out.println("Updating student list for class: " + selectedClass.getName());
        ObservableList<Student> students = FXCollections
                .observableArrayList(db.getStudentsInClass(selectedClass.getClassId()));
        studentsInClass.getChildren().clear();
        if (students.isEmpty()) {
            System.out.println("No students found for class: " + selectedClass.getName());
            Label noStudentsLabel = new Label("No students in this class.");
            studentsInClass.getChildren().add(noStudentsLabel);
        } else {
            for (Student student : students) {
                HBox studentCard = createStudentCard(student);
                studentsInClass.getChildren().add(studentCard);
            }
        }
        System.out.println("Found " + students.size() + " students for class: " + selectedClass.getName());
    }

    private void setupAssessmentSelector() {
        assessmentSelector.setConverter(new StringConverter<Assessment>() {
            @Override
            public String toString(Assessment assessment) {
                return assessment == null ? "" : assessment.getName();
            }

            @Override
            public Assessment fromString(String string) {
                return null;
            }
        });
    }

    // Method to create a student card
    private HBox createStudentCard(Student student) {
        HBox studentCard = new HBox();
        studentCard.getStyleClass().add("student-card");
        studentCard.setSpacing(10);

        VBox infoBox = new VBox(5);
        Label nameLabel = new Label("Name: " + student.getName());
        Label idLabel = new Label("ID: " + student.getStudentId());
        infoBox.getChildren().addAll(nameLabel, idLabel);

        VBox gradeBox = new VBox(5);
        gradeBox.getStyleClass().add("grade-box");
        Assessment selectedAssessment = assessmentSelector.getSelectionModel().getSelectedItem();
        if (selectedAssessment != null) {
            List<Grade> grades = db.getGradesForStudentAndAssessment(student.getStudentId(),
                    selectedAssessment.getId());
            if (grades.isEmpty()) {
                gradeBox.getChildren().add(new Label("No grades available"));
            } else {
                double totalWeightedScore = 0;
                double totalWeight = 0;

                for (Grade grade : grades) {
                    HBox gradeRow = new HBox(10);
                    Label partLabel = new Label(
                            grade.getAssessmentPart() != null ? grade.getAssessmentPart().getName() : "Overall");
                    double maxScore = grade.getAssessmentPart() != null ? grade.getAssessmentPart().getMaxScore()
                            : selectedAssessment.getMaxScore();
                    Label scoreLabel = new Label(String.format("%.2f / %.2f", grade.getScore(), maxScore));
                    Label percentageLabel = new Label(String.format("%.2f%%", (grade.getScore() / maxScore) * 100));
                    gradeRow.getChildren().addAll(partLabel, scoreLabel, percentageLabel);
                    gradeBox.getChildren().add(gradeRow);

                    double weight = grade.getAssessmentPart() != null ? grade.getAssessmentPart().getWeight() : 1;
                    totalWeightedScore += (grade.getScore() / maxScore) * weight;
                    totalWeight += weight;
                }

                double overallPercentage = (totalWeightedScore / totalWeight) * 100;
                Label overallLabel = new Label(String.format("Overall: %.2f%%", overallPercentage));
                gradeBox.getChildren().add(overallLabel);
            }
        } else {
            gradeBox.getChildren().add(new Label("No assessment selected"));
        }

        Button markBookButton = new Button("Mark Book");
        markBookButton.setOnAction(event -> openMarkBook(student));

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        HBox.setHgrow(gradeBox, Priority.ALWAYS);

        studentCard.getChildren().addAll(infoBox, gradeBox, markBookButton);
        return studentCard;
    }

    // Method to open the Mark Book view
    private void openMarkBook(Student student) {
        try {
            Assessment selectedAssessment = assessmentSelector.getSelectionModel().getSelectedItem();
            if (selectedAssessment == null) {
                showAlert("Please select an assessment.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo3/student-markbook.fxml"));
            Parent root = loader.load();

            StudentMarkbookController controller = loader.getController();
            controller.initializeData(student, selectedAssessment);

            Stage stage = new Stage();
            stage.setTitle("Mark Book for " + student.getName());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh the student list to show updated grades
            updateStudentList(classSelector.getSelectionModel().getSelectedItem());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading student's MarkBook: " + e.getMessage());
        }
    }

    private void setupClassSelector() {
        classSelector.setConverter(new StringConverter<Classes>() {
            @Override
            public String toString(Classes classObj) {
                return classObj == null ? "" : classObj.getName();
            }

            @Override
            public Classes fromString(String string) {
                return null;
            }
        });
    }

    // Method to set details in student-markbook.fxml
    private void setStudent(Student student) {
        studentName.setText(student.getName());
        studentId.setText(student.getStudentId());
    }

    // Method to show an alert
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}