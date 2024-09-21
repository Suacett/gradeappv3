package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
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
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;

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

    @FXML
    private void initialize() {
        setupCourseSelector();
    }

    // Method to select course from dropdown
    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);
        courseSelector.setCellFactory(lv -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getName());
                }
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
        classSelector.setCellFactory(lv -> new ListCell<Classes>() {
            @Override
            protected void updateItem(Classes classObj, boolean empty) {
                super.updateItem(classObj, empty);
                if (empty || classObj == null) {
                    setText(null);
                } else {
                    setText(classObj.getName());
                }
            }
        });
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
        // Update assessmentSelector and student list when class is selected
        classSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateAssessmentSelector(newSelection);
                updateStudentList(newSelection);
            }
        });
        if (!classes.isEmpty()) { // Select first class in list
            classSelector.getSelectionModel().selectFirst();
        }
    }

    // Method to update assessmentSelector based on selected class
    private void updateAssessmentSelector(Classes selectedClass) {
        ObservableList<Assessment> assessments = FXCollections
                .observableArrayList(db.getAssessmentsForClass(selectedClass.getClassId()));
        assessmentSelector.setItems(assessments);
        assessmentSelector.setCellFactory(lv -> new ListCell<Assessment>() {
            @Override
            protected void updateItem(Assessment assessment, boolean empty) {
                super.updateItem(assessment, empty);
                if (empty || assessment == null) {
                    setText(null);
                } else {
                    setText(assessment.getName());
                }
            }
        });
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
        if (!assessments.isEmpty()) {
            assessmentSelector.getSelectionModel().selectFirst(); // Select first assessment in list
        }
    }

    // Method to update the student list based on the selected class
    private void updateStudentList(Classes selectedClass) {
        ObservableList<Student> students = FXCollections.observableArrayList(db.getStudentsInClass(selectedClass.getClassId()));
        studentsInClass.getChildren().clear();
        for (Student student : students) {
            HBox studentCard = createStudentCard(student);
            studentsInClass.getChildren().add(studentCard);
        }
    }

    // Method to create a student card
    private HBox createStudentCard(Student student) {
        HBox studentCard = new HBox();
        studentCard.getStyleClass().add("card");
        studentCard.setSpacing(10);
        Label nameLabel = new Label("Name: " + student.getName());
        Label idLabel = new Label("ID: " + student.getStudentId());
        HBox buttonContainer = new HBox();
        Button markBookButton = new Button("Mark Book");
        markBookButton.setOnAction(event -> openMarkBook(student)); // Set event handler
        buttonContainer.getChildren().add(markBookButton);
        Region spacer = new Region(); // Space between items
        HBox.setHgrow(spacer, Priority.ALWAYS);
        studentCard.getChildren().addAll(nameLabel, idLabel, spacer, buttonContainer);
        return studentCard;
    }

    // Method to open the Mark Book view
    private void openMarkBook(Student student) {
        try {
            String fxmlPath = "/org/example/demo3/student-markbook.fxml";
            System.out.println("Loading FXML from: " + fxmlPath);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("Cannot find " + fxmlPath);
            }
            
            VBox studentMarkBook = loader.load();

            // Get the controller and pass the student data
            StudentMarkbookController controller = loader.getController();
            controller.setStudent(student);
            controller.setMarkBookController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Mark Book for " + student.getName());
            stage.setScene(new Scene(studentMarkBook));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading student's MarkBook: " + e.getMessage());
        }
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