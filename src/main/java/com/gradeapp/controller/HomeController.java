package com.gradeapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


// HomeController manages the Home category dynamic content.
public class HomeController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Home");
    }
    
}