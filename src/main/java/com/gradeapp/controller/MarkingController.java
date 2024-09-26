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

/**
 * Controller class for managing the marking process.
 * Handles course, class, and assessment selection, and displays student grades.
 */
public class MarkingController {

    // ----------------------------- FXML UI Components -----------------------------

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

    // ----------------------------- Non-UI Fields -----------------------------

    private Database db = new Database();
    private Course selectedCourse;

    // ----------------------------- Initialization -----------------------------

    /**
     * Initializes the controller after its root element has been completely
     * processed.
     * Sets up selectors and their listeners.
     */
    @FXML
    private void initialize() {
        setupCourseSelector();
        setupClassSelector();
        setupAssessmentSelector();

        // Listener for course selection changes
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateClassSelector(newSelection);
                updateAssessmentSelector();
            }
        });

        // Listener for class selection changes
        classSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateStudentList(newSelection);
            }
        });

        // Listener for assessment selection changes
        assessmentSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateStudentList(classSelector.getSelectionModel().getSelectedItem());
            }
        });
    }

    // ----------------------------- Setup Methods -----------------------------

    /**
     * Configures the course selector with available courses from the database.
     */
    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);
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

        // Select the first course by default if available
        if (!courses.isEmpty()) {
            courseSelector.getSelectionModel().selectFirst();
        }
    }

    /**
     * Configures the class selector based on the selected course.
     *
     * @param selectedCourse The course selected by the user.
     */
    private void updateClassSelector(Course selectedCourse) {
        ObservableList<Classes> classes = FXCollections
                .observableArrayList(db.getClassesForCourse(selectedCourse.getId()));
        classSelector.setItems(classes);
        classSelector.getSelectionModel().selectFirst();
        if (!classes.isEmpty()) {
            updateStudentList(classes.get(0));
        }
    }

    /**
     * Configures the assessment selector based on the selected course.
     */
    private void updateAssessmentSelector() {
        Course selectedCourse = courseSelector.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            ObservableList<Assessment> assessments = FXCollections
                    .observableArrayList(db.getAssessmentsForCourse(selectedCourse.getId()));
            assessmentSelector.setItems(assessments);
            if (!assessments.isEmpty()) {
                assessmentSelector.getSelectionModel().selectFirst();
            }
        }
    }

    /**
     * Configures the class selector's display format.
     */
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

    /**
     * Configures the assessment selector's display format.
     */
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

    // ----------------------------- Update Methods -----------------------------

    /**
     * Updates the student list based on the selected class.
     *
     * @param selectedClass The class selected by the user.
     */
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

    // ----------------------------- UI Creation Methods -----------------------------

    /**
     * Creates a visual representation (card) for a student.
     *
     * @param student The student for whom the card is created.
     * @return An HBox containing student information and a button to open the Mark
     *         Book.
     */
    private HBox createStudentCard(Student student) {
        HBox studentCard = new HBox();
        studentCard.getStyleClass().add("card");
        studentCard.setSpacing(10);

        // Student Information
        VBox infoBox = new VBox(5);
        Label nameLabel = new Label("Name: " + student.getName());
        Label idLabel = new Label("ID: " + student.getStudentId());
        infoBox.getChildren().addAll(nameLabel, idLabel);

        // Grades Information
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

        // Button to open Mark Book
        Button markBookButton = new Button("MarkBook");
        markBookButton.setOnAction(event -> openMarkBook(student));

        // Adjust layout priorities
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        HBox.setHgrow(gradeBox, Priority.ALWAYS);

        studentCard.getChildren().addAll(infoBox, gradeBox, markBookButton);
        return studentCard;
    }

    // ----------------------------- Navigation Methods -----------------------------

    /**
     * Opens the Mark Book view for a specific student.
     *
     * @param student The student whose Mark Book is to be opened.
     */
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
