package com.gradeapp.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class HelloController {

    @FXML
    private AnchorPane content;

    @FXML
    public void showHomeContent() {
        loadContent("/org/example/demo3/home-view.fxml", "home-view.fxml");
    }

    @FXML
    public void showCoursesContent() {
        loadContent("/org/example/demo3/courses-view.fxml", "courses-view.fxml");
    }

    @FXML
    public void showClassesContent() {
        loadContent("/org/example/demo3/classes-view.fxml", "classes-view.fxml");
    }

    @FXML
    public void showStudentsContent() {
        loadContent("/org/example/demo3/students-view.fxml", "students-view.fxml");
    }

    @FXML
    public void showArchiveContent() {
        loadContent("/org/example/demo3/archive-view.fxml", "archive-view.fxml");
    }

    private void loadContent(String fxmlPath, String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            VBox view = loader.load();
            System.out.println("Loaded " + viewName + " successfully.");
            content.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + viewName + ": " + e.getMessage());
        }
    }
}