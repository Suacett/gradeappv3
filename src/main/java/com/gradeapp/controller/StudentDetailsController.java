package com.gradeapp.controller;

import java.util.ArrayList;
import java.util.List;
import com.gradeapp.database.Database;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Controller class for managing and displaying detailed information about a student.
 * Handles initializing student data, loading courses and classes, displaying grades, and saving updates.
 */
public class StudentDetailsController {

    // ----------------------------- FXML UI Components -----------------------------

    @FXML
    private TextField studentName; // Displays the student's name

    @FXML
    private TextField studentId; // Displays the student's ID

    @FXML
    private ComboBox<Course> courseSelector; // Dropdown to select a course

    @FXML
    private TabPane classesTabPane; // Pane containing tabs for each class

    @FXML
    private Button cancel; // Button to cancel and close the dialog

    @FXML
    private Button saveStudent; // Button to save student details

    // ----------------------------- Non-UI Fields -----------------------------

    private Database db; // Instance of the database for data operations
    private Student currentStudent; // The student whose details are being managed

    // ----------------------------- Initialization -----------------------------

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up button actions and course selector listener.
     */
    public void initialize() {
        db = new Database();

        // Set up cancel button action
        if (cancel != null) {
            cancel.setOnAction(event -> handleCancel());
        }

        // Set up save student button action
        if (saveStudent != null) {
            saveStudent.setOnAction(event -> handleSaveStudent());
        }

        // Set up course selector listener to load classes upon selection
        if (courseSelector != null) {
            courseSelector.setOnAction(event -> loadClasses());
        }
    }

    // ----------------------------- Data Initialization -----------------------------

    /**
     * Initializes the controller with the selected student's data.
     *
     * @param student The student whose details are to be displayed and managed.
     */
    public void initData(Student student) {
        currentStudent = student;
        if (studentName != null) {
            studentName.setText(student.getName());
        }
        if (studentId != null) {
            studentId.setText(student.getStudentId());
        }
        loadCourses();
    }

    // ----------------------------- Course and Class Loading -----------------------------

    /**
     * Loads the courses associated with the current student into the course selector.
     * Sets up the display format and selects the first course by default.
     */
    private void loadCourses() {
        if (courseSelector != null) {
            List<Course> courses = db.getCoursesForStudent(currentStudent.getStudentId());
            ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
            courseSelector.setItems(courseList);

            // Define how courses are displayed in the dropdown
            courseSelector.setCellFactory(lv -> new ListCell<Course>() {
                @Override
                protected void updateItem(Course course, boolean empty) {
                    super.updateItem(course, empty);
                    setText(empty || course == null ? null : course.getName());
                }
            });

            // Define the converter for the selected value display
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

            // Select the first course and load its classes if available
            if (!courseList.isEmpty()) {
                courseSelector.getSelectionModel().selectFirst();
                loadClasses();
            }
        }
    }

    /**
     * Loads and displays the classes associated with the selected course and student.
     * Creates a tab for each class containing the student's grades.
     */
    private void loadClasses() {
        if (classesTabPane != null && courseSelector.getValue() != null) {
            classesTabPane.getTabs().clear(); // Clear existing tabs
            String courseId = courseSelector.getValue().getId();

            // Retrieve classes the student is enrolled in and matches the selected course
            List<Classes> studentClasses = db.getClassesForStudent(currentStudent.getStudentId());
            List<Classes> courseClasses = db.getClassesForCourse(courseId);

            List<Classes> classes = new ArrayList<>();
            for (Classes sc : studentClasses) {
                for (Classes cc : courseClasses) {
                    if (sc.getClassId().equals(cc.getClassId())) {
                        classes.add(sc);
                        break;
                    }
                }
            }

            // Create a tab for each class and populate it with grades
            for (Classes cls : classes) {
                Tab classTab = new Tab(cls.getName());
                classTab.setContent(createClassContent(cls));
                classesTabPane.getTabs().add(classTab);
            }
        } else {
            System.out.println(
                    "Unable to load classes: " + (classesTabPane == null ? "TabPane is null" : "No course selected"));
        }
    }

    // ----------------------------- UI Content Creation -----------------------------

    /**
     * Creates the content for a class tab, displaying the student's grades in a table.
     *
     * @param cls The class for which to create the content.
     * @return A Node containing the grade table.
     */
    private Node createClassContent(Classes cls) {
        TableView<Grade> gradeTable = new TableView<>();

        // Define table columns
        TableColumn<Grade, String> assessmentNameCol = new TableColumn<>("Assessment Name");
        TableColumn<Grade, String> assessmentPartCol = new TableColumn<>("Part");
        TableColumn<Grade, Double> gradeCol = new TableColumn<>("Mark");

        // Set up cell value factories for each column
        assessmentNameCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getAssessment().getName()));
        assessmentPartCol.setCellValueFactory(
                cellData -> {
                    AssessmentPart part = cellData.getValue().getAssessmentPart();
                    return new SimpleStringProperty(part != null ? part.getName() : "");
                });
        gradeCol.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getScore()).asObject());

        // Add columns to the table
        gradeTable.getColumns().addAll(assessmentNameCol, assessmentPartCol, gradeCol);

        // Retrieve and set the grades for the student in the specific class
        List<Grade> gradesInClass = db.getGradesForStudentInClass(currentStudent.getStudentId(), cls.getClassId());
        gradeTable.setItems(FXCollections.observableArrayList(gradesInClass));

        // Arrange the table within a VBox with padding
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.getChildren().add(gradeTable);
        return vbox;
    }

    // ----------------------------- Button Handlers -----------------------------

    /**
     * Handles the action of cancelling and closing the student details window.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the action of saving the updated student details.
     * Updates the student information in the database and closes the window.
     */
    @FXML
    private void handleSaveStudent() {
        String oldStudentId = currentStudent.getStudentId();
        currentStudent.setName(studentName.getText());
        currentStudent.setStudentId(studentId.getText());
        db.updateStudent(oldStudentId, currentStudent.getName(), currentStudent.getStudentId());
        Stage stage = (Stage) saveStudent.getScene().getWindow();
        stage.close();
    }
}
