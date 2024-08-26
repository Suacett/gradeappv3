package com.gradeapp.controller;

import com.gradeapp.database.Database;
import com.gradeapp.model.Classes;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ClassController {

    @FXML
    private VBox currentClassContainer;
    @FXML
    private VBox newClassInputContainer;

    private Database db = new Database();

    @FXML
    private void initialize() {
        displayCurrentClasses();
    }

    // Add Class click event
    @FXML
    private void handleAddClassButtonAction() {
        VBox classInputBox = createClassInputBox();
        newClassInputContainer.getChildren().add(classInputBox);
    }

    // New class inputs, appear on Add Class button click
    private VBox createClassInputBox() {
        VBox classInputBox = new VBox();
        classInputBox.setPadding(new Insets(20, 20, 20, 20));
        classInputBox.setSpacing(10);
        Label classNameLabel = new Label("Class Name:");
        TextField classNameField = new TextField();
        classNameField.setPromptText("Class name");
        TextField classIdField = new TextField();
        classIdField.setPromptText("Class Id");
        Button submitButton = new Button("+ Add Class"); // Submit button
        submitButton.setOnAction(event -> handleSubmitClassButtonAction(classNameField, classIdField));
        classInputBox.getChildren().addAll(classNameLabel, classNameField, classIdField, submitButton);
        return classInputBox;
    }

    // Submit new class click event
    private void handleSubmitClassButtonAction(TextField classNameField, TextField classIdField) {
        String className = classNameField.getText();
        String classId = classIdField.getText();
        if (!className.isEmpty() && !classId.isEmpty()) {
            Classes newClass = new Classes(className, classId); // New Class object
            db.addClass(className, classId); // Add class to db
            classNameField.clear(); // Clear inputs
            classIdField.clear();
            displayCurrentClasses(); // Display current classes
        } else {
            System.out.println("The form is incomplete...");
        }
    }

    // Class card, displays current classes
    private VBox createClassCard(Classes classes) {
        VBox classCard = new VBox();
        classCard.setPadding(new Insets(10));
        classCard.setSpacing(10);
        classCard.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 5px;");

        Label classNameLabel = new Label(classes.getName());  // Display the full name as it is
        Label classIdLabel = new Label(classes.getClassId());

        // Create HBox to hold the buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> handleEditClassButtonAction(classes));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            db.delete("classes", "classId", classes.getClassId()); // Ensure deletion is based on classId
            displayCurrentClasses();  // Refresh the class list after deletion
        });

        buttonContainer.getChildren().addAll(editButton, deleteButton);
        classCard.getChildren().addAll(classNameLabel, classIdLabel, buttonContainer);
        return classCard;
    }

    // Edit button action for classes
    private void handleEditClassButtonAction(Classes classes) {
        // Create text fields pre-populated with the current class's details
        TextField classNameField = new TextField(classes.getName());
        TextField classIdField = new TextField(classes.getClassId());
        Button saveButton = new Button("Save");

        // Set the action for the save button
        saveButton.setOnAction(event -> {
            String newName = classNameField.getText();
            String newId = classIdField.getText();
            if (!newName.isEmpty() && !newId.isEmpty()) {
                // Update the class in the database
                db.updateClass(classes.getClassId(), newName, newId);
                displayCurrentClasses(); // Refresh the UI to reflect changes
            } else {
                System.out.println("The form is incomplete...");
            }
        });

        // Display the edit form in the UI
        VBox editClassBox = new VBox(10, new Label("Edit Class"), classNameField, classIdField, saveButton);
        currentClassContainer.getChildren().clear(); // Clear the current view
        currentClassContainer.getChildren().add(editClassBox); // Display the edit form
    }


    // Display current classes
    private void displayCurrentClasses() {
        currentClassContainer.getChildren().clear();
        List<Classes> classesFromDb = db.getAllClasses(); // Get classes from db
        if (classesFromDb.isEmpty()) { // Display message if db empty
            Label emptyLabel = new Label("You have no current classes");
            currentClassContainer.getChildren().add(emptyLabel);
        } else {
            for (Classes classes : classesFromDb) {
                VBox classCard = createClassCard(classes);
                currentClassContainer.getChildren().add(classCard);
            }
        }
    }
}
