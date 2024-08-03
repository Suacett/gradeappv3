package com.gradeapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


// ArchiveController manages the Archive category dynamic content.
public class ArchiveController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Archives");
    }
    
}
