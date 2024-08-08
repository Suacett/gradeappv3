package com.gradeapp.controller;

import com.gradeapp.model.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CourseDetailsController {

    // Currently CourseDetailsController for testing purposes only.

// FXML ids
    @FXML
    private Label courseNameLabel;
    @FXML
    private Label courseDescriptionLabel;

    private Course course;

    // Display details
    public void setCourse(Course course) {
        this.course = course;
        displayCourseDetails();
    }

    // Display course details
    private void displayCourseDetails() {
        if (course != null) {
            courseNameLabel.setText(course.getName());
            courseDescriptionLabel.setText(course.getDescription());

        }
    }
}