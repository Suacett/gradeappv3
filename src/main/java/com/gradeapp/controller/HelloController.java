package com.gradeapp.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class HelloController {

    @FXML
    private VBox content;

    public VBox getContent() {
        return content;
    }

    // Home category click event
    @FXML
    public void showHomeContent() {
        loadContent("/org/example/demo3/home-view.fxml", "home-view.fxml");
    }

    // Courses category click event
    @FXML
    public void showCoursesContent() {
        loadContent("/org/example/demo3/courses-view.fxml", "courses-view.fxml");
    }

    // Classes category click event
    @FXML
    public void showClassesContent() {
        loadContent("/org/example/demo3/classes-view.fxml", "classes-view.fxml");
    }

    // Students category click event
    @FXML
    public void showStudentsContent() {
        loadContent("/org/example/demo3/students-view.fxml", "students-view.fxml");
    }

    // Archive category click event
    @FXML
    public void showArchiveContent() {
        loadContent("/org/example/demo3/archive-view.fxml", "archive-view.fxml");
    }

    // Assessments category click event
    @FXML
    public void showAssessmentsContent() {
        loadContent("/org/example/demo3/assessment-view.fxml", "assessment-view.fxml");
    }

    // Loads FXML content into the center content area
    private void loadContent(String fxmlPath, String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            VBox view = loader.load();
            System.out.println("Loaded " + viewName + " successfully...");
            content.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + viewName + ": " + e.getMessage());
        }
    }
}
