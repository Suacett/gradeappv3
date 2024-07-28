package com.gradeapp.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Allows users to configure application settings and preferences.
 */
public class SettingsView {
    private VBox root;
    private TextField courseNameField;
    private Button saveSettingsButton;

    public SettingsView() {
        root = new VBox(10);
        initializeUI();
    }

    private void initializeUI() {
        root.setPadding(new Insets(10));

        Label titleLabel = new Label("Settings");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label courseNameLabel = new Label("Course Name:");
        courseNameField = new TextField();

        saveSettingsButton = new Button("Save Settings");
        saveSettingsButton.setOnAction(e -> saveSettings());

        root.getChildren().addAll(titleLabel, courseNameLabel, courseNameField, saveSettingsButton);
    }

    private void saveSettings() {
        // Implement settings save logic
    }

    public VBox getRoot() {
        return root;
    }
}
