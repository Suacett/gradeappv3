package com.gradeapp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Student;
import com.gradeapp.util.FileHandler;

import javafx.scene.control.Alert;

/**
 * Controller class for handling data import and export functionalities.
 * Manages importing student data from a file and exporting student data to a file.
 */
public class DataImportExportController {
    private FileHandler fileHandler; // Handles file operations
    private Database db;             // Interface to the database

    /**
     * Constructor initializes the FileHandler and Database instances.
     */
    public DataImportExportController() {
        this.fileHandler = new FileHandler();
        this.db = new Database();
    }

    /**
     * Imports student data from the specified file path.
     * Handles duplicates and provides feedback on the import process.
     *
     * @param filePath The path to the file containing student data.
     */
    public void importData(String filePath) {
        try {
            // Import students from the file
            List<Student> importedStudents = fileHandler.importStudents(filePath);
            List<String> duplicateStudents = new ArrayList<>();
            int successfulImports = 0;

            // Attempt to add each student to the database
            for (Student student : importedStudents) {
                try {
                    db.addStudent(student.getName(), student.getStudentId());
                    successfulImports++;
                } catch (SQLException e) {
                    // Handle duplicate entries based on SQL exception message
                    if (e.getMessage().contains("UNIQUE constraint failed")) {
                        duplicateStudents.add(student.getStudentId());
                    } else {
                        throw e; // Rethrow if it's a different SQL exception
                    }
                }
            }

            // Build the import results message
            StringBuilder message = new StringBuilder();
            message.append(successfulImports).append(" students imported successfully.\n");

            if (!duplicateStudents.isEmpty()) {
                message.append(duplicateStudents.size())
                       .append(" duplicate students were not imported:\n")
                       .append(String.join(", ", duplicateStudents));
            }

            // Display the import results to the user
            showInfoDialog("Import Results", message.toString());

        } catch (IllegalArgumentException e) {
            // Handle invalid arguments
            showErrorDialog(e.getMessage());
        } catch (IOException e) {
            // Handle file reading errors
            showErrorDialog("Error reading file: " + e.getMessage());
        } catch (SQLException e) {
            // Handle general database errors
            showErrorDialog("Database error: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            System.err.println("Error importing data from " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error importing data: " + e.getMessage());
        }
    }

    /**
     * Exports the given list of students to the specified file path.
     *
     * @param students The list of students to export.
     * @param filePath The path to the file where data will be exported.
     */
    public void exportData(List<Student> students, String filePath) {
        try {
            // Export students to the file
            fileHandler.exportStudents(students, filePath);
            // Notify the user of successful export
            showInfoDialog("Export Successful", "Data exported successfully to " + filePath);
        } catch (IOException e) {
            // Handle file writing errors
            showErrorDialog("Error exporting data: " + e.getMessage());
        }
    }

    // ----------------------------- Helper Methods -----------------------------

    /**
     * Displays an informational dialog to the user.
     *
     * @param title   The title of the dialog.
     * @param message The message content of the dialog.
     */
    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error dialog to the user.
     *
     * @param message The error message to display.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Import Error");
        alert.setHeaderText("Failed to Import Students");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
