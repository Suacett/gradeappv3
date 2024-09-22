package com.gradeapp.controller;

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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class StudentDetailsController {

    @FXML
    private TextField studentName;
    @FXML
    private TextField studentId;
    @FXML
    private TextField studentDescription;
    @FXML
    private ComboBox<Course> courseSelector;
    @FXML
    private TabPane classesTabPane;
    @FXML
    private TableView<Grade> gradeTable;
    @FXML
    private TableColumn<Grade, String> assessmentName;
    @FXML
    private TableColumn<Grade, String> assessmentPart;
    @FXML
    private TableColumn<Grade, Double> grade;
    @FXML
    private Button cancel;
    @FXML
    private Button saveStudent;

    private Database db;
    private Student currentStudent;

    public void initialize() {
        db = new Database();
        if (gradeTable != null) {
            assessmentName.setCellValueFactory(
                    cellData -> new SimpleStringProperty(cellData.getValue().getAssessment().getName()));
            assessmentPart.setCellValueFactory(cellData -> {
                AssessmentPart part = cellData.getValue().getAssessmentPart();
                return new SimpleStringProperty(part != null ? part.getName() : "");
            });
            grade.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getScore()).asObject());
        }
        if (cancel != null) {
            cancel.setOnAction(event -> handleCancel());
        }
        if (saveStudent != null) {
            saveStudent.setOnAction(event -> handleSaveStudent());
        }
        if (courseSelector != null) {
            courseSelector.setOnAction(event -> loadClasses());
        }
    }

    public void initData(Student student) {
        currentStudent = student;
        if (studentName != null) {
            studentName.setText(student.getName());
        }
        if (studentId != null) {
            studentId.setText(student.getStudentId());
        }
        loadCourses();
        if (courseSelector != null) {
            courseSelector.setOnAction(event -> loadClasses());
        }
    }

    private void loadCourses() {
        if (courseSelector != null) {
            List<Course> courses = db.getCoursesForStudent(currentStudent.getStudentId());
            ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
            courseSelector.setItems(courseList);
            courseSelector.setCellFactory(lv -> new ListCell<Course>() {
                @Override
                protected void updateItem(Course course, boolean empty) {
                    super.updateItem(course, empty);
                    setText(empty || course == null ? null : course.getName());
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
            if (!courseList.isEmpty()) {
                courseSelector.getSelectionModel().selectFirst();
                loadClasses();
            }
        }
    }

    private void loadClasses() {
        if (classesTabPane != null && courseSelector.getValue() != null) {
            classesTabPane.getTabs().clear();
            String courseId = courseSelector.getValue().getId();
            List<Classes> classes = db.getClassesForCourse(courseId);
            for (Classes cls : classes) {
                Tab classTab = new Tab(cls.getName());
                classTab.setContent(createClassContent(cls));
                classesTabPane.getTabs().add(classTab);
            }
            System.out.println("Loaded " + classes.size() + " classes for course " + courseId);
        } else {
            System.out.println(
                    "Unable to load classes: " + (classesTabPane == null ? "TabPane is null" : "No course selected"));
        }
    }

    private Node createClassContent(Classes cls) {
        Label classInfo = new Label("Class ID: " + cls.getClassId() + "\nClass Name: " + cls.getName());
        return new VBox(classInfo);
    }

    private void loadGrades() {
        if (gradeTable != null) {
            List<Grade> grades = db.getGradesForStudent(currentStudent.getStudentId());
            ObservableList<Grade> gradeData = FXCollections.observableArrayList(grades);
            gradeTable.setItems(gradeData);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

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