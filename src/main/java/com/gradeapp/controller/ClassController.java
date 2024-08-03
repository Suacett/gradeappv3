package com.gradeapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


// ClassController manages the Class category dynamic content.
public class ClassController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Hi, Ben!");
    }

}
