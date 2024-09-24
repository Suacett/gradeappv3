package com.gradeapp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Student;
import com.gradeapp.util.FileHandler;

import javafx.scene.control.Alert;

public class DataImportExportController {
    private FileHandler fileHandler;
    private Database db;

    public DataImportExportController() {
        this.fileHandler = new FileHandler();
        this.db = new Database();
    }

    public void importData(String filePath) {
        try {
            List<Student> importedStudents = fileHandler.importStudents(filePath);
            List<String> duplicateStudents = new ArrayList<>();
            int successfulImports = 0;

            for (Student student : importedStudents) {
                try {
                    db.addStudent(student.getName(), student.getStudentId());
                    successfulImports++;
                } catch (SQLException e) {
                    if (e.getMessage().contains("UNIQUE constraint failed")) {
                        duplicateStudents.add(student.getStudentId());
                    } else {
                        throw e;
                    }
                }
            }

            StringBuilder message = new StringBuilder();
            message.append(successfulImports).append(" students imported successfully.\n");

            if (!duplicateStudents.isEmpty()) {
                message.append(duplicateStudents.size()).append(" duplicate students were not imported:\n");
                message.append(String.join(", ", duplicateStudents));
            }

            showInfoDialog("Import Results", message.toString());

        } catch (IllegalArgumentException e) {
            showErrorDialog(e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Error reading file: " + e.getMessage());
        } catch (SQLException e) {
            showErrorDialog("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error importing data from " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error importing data: " + e.getMessage());
        }
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Import Error");
        alert.setHeaderText("Failed to Import Students");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void exportData(List<Student> students, String filePath) {
        try {
            fileHandler.exportStudents(students, filePath);
            showInfoDialog("Export Successful", "Data exported successfully to " + filePath);
        } catch (IOException e) {
            showErrorDialog("Error exporting data: " + e.getMessage());
        }
    }
}