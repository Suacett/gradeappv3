package com.gradeapp.controller;

import java.util.ArrayList;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.controller.HelloController;
import com.gradeapp.model.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CourseDetailsController {

    // CourseDetailsController manages the Add New Course form.

    // FXML ids
    @FXML
    private TextField courseName;

    @FXML
    private TextField courseId;

    private Database db = new Database();
    private List<Course> coursesList = new ArrayList<>();

    @FXML
    private void addCourseToDb() {
        String name = courseName.getText();
        String description = courseId.getText();
        coursesList.add(new Course(name, description));
        db.addCourse(name, description);
    }


}