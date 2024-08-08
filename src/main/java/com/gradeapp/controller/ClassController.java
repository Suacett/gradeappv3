package com.gradeapp.controller;

import java.util.ArrayList;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Classes;


import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

// ClassController manages the Class category content.
public class ClassController {

// FXML ids
    @FXML
    private VBox classContainer;
    @FXML
    private Button addClassButton;
    @FXML
    private VBox newClassInputContainer;
    @FXML
    private VBox currentClassContainer;

    private List<Classes> classList = new ArrayList<>();

    private Database db = new Database();

    // Initialise ClassController
    @FXML
    private void initialize() {
        db.initialiseDatabase();
        displayCurrentClass();
    }

    // Add class click event
    @FXML
    private void handleAddClassButtonAction() {
        VBox classInputBox = createClassInputBox();
        newClassInputContainer.getChildren().add(classInputBox);
    }

    // Add new class inputs
    private VBox createClassInputBox() {
        VBox classInputBox = new VBox(); // Add VBox
        classInputBox.setPadding(new Insets(20, 20, 20, 20));
        classInputBox.setSpacing(10);
        Label classNameLabel = new Label("Class Name:");
        TextField classNameField = new TextField();
        classNameField.setPromptText("Class name");
        Label classIdLabel = new Label("Class Id:");
        TextField classIdField = new TextField();
        classIdField.setPromptText("Class Id");
        Button submitButton = new Button("+ Add Class");
        submitButton.setOnAction(event -> handleSubmitButtonAction(classNameField, classIdField));
        classInputBox.getChildren().addAll(classNameLabel, classNameField, classIdLabel, classIdField, submitButton);
        return classInputBox;
    }

    // Submit new class click event
    private void handleSubmitButtonAction(TextField classNameField, TextField classIdField) {
        String className = classNameField.getText();
        String classId = classIdField.getText();
        if (!className.isEmpty() && !classId.isEmpty()) {
            Classes newClass = new Classes(className, classId); // New class object
            classList.add(newClass); // Add new class to list
            db.addClass(className, classId); // Add to db
            classNameField.clear(); // Clear name
            classIdField.clear(); // Clear id
            displayCurrentClass(); // Display current classes
        } else {
            System.out.println("Incomplete form...");
        }
    }

    // Class card, displays current classes
    private VBox createClassCard(Classes classes) {
        VBox classCard = new VBox(); // Add VBox
        classCard.setPadding(new Insets(10));
        classCard.setSpacing(10);
        classCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 5px;");
        Label classNameLabel = new Label(classes.getName());
        Label classIdLabel = new Label(classes.getClassId());
        Button deleteButton = new Button("Delete"); // Delete button
        deleteButton.setOnAction(event -> {
            db.delete("classes", "name", classes.getName()); // Delete from db
            displayCurrentClass(); // Display current classes
        });
        classCard.getChildren().addAll(classNameLabel, classIdLabel, deleteButton);
        return classCard;
    }

    // Display current classes
    private void displayCurrentClass() {
        currentClassContainer.getChildren().clear();
        List<Classes> classFromDb = db.getAllClasses(); // Get classes from db
        if (classFromDb.isEmpty()) { // Display message if db empty
            Label emptyLabel = new Label("You have no current classes.");
            currentClassContainer.getChildren().add(emptyLabel);
        } else {
            for (Classes classes : classFromDb) {
                VBox classCard = createClassCard(classes);
                currentClassContainer.getChildren().add(classCard);
            }
        }
    }

}
