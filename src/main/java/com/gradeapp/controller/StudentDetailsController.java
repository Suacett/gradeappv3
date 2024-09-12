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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
                AssessmentPart part = cellData.getValue().getPart();
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
        loadClasses();
        loadGrades();
    }

    private void loadCourses() {
        if (courseSelector != null) {
            List<Course> courses = db.getCoursesForStudent(currentStudent.getStudentId());
            courseSelector.setItems(FXCollections.observableArrayList(courses));
        }
    }

    private void loadClasses() {
        if (classesTabPane != null) {
            List<Classes> classes = db.getClassesForStudent(currentStudent.getStudentId());
            classesTabPane.getTabs().clear();
            for (Classes cls : classes) {
                Tab classTab = new Tab(cls.getName());

                classesTabPane.getTabs().add(classTab);
            }
        }
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