package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Course;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;

public class MarkingController {

    // Declare FXML elements
    @FXML
    private ComboBox<Course> courseSelector;
    @FXML
    private ComboBox<Course> classSelector;
    @FXML
    private ComboBox<Course> assessmentSelector;
    @FXML
    private Button openRubric;

    private Database db = new Database();

    @FXML
    private void initialize() {
        // load all students
    }

  
  
    // Method to select course from dropdown


    // Method to select class from dropdown, updates as course is selected


    // Method to select assessment from dropdown, updates as class is selected


    // Method to open selected assessment rubric on button click


    // Method to update student list as course and class are selected



    
}
