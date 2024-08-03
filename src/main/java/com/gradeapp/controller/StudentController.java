package com.gradeapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


// StudentController manages the Student category dynamic content.
public class StudentController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Hi, Brock!");
    }
    
}