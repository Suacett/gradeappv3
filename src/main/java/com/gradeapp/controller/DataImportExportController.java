package com.gradeapp.controller;

import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Student;
import com.gradeapp.util.FileHandler;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
            for (Student student : importedStudents) {
                db.addStudent(student.getName(), student.getStudentId());
            }
            System.out.println("Data imported successfully from " + filePath);
        } catch (IllegalArgumentException e) {

            showErrorDialog(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error importing data from " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Import Error");
        alert.setHeaderText("Failed to Import Students");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void exportData(List<Student> students, String filePath) {
        try {
            fileHandler.exportStudents(students, filePath);
            System.out.println("Data exported successfully to " + filePath);
        } catch (Exception e) {
            System.err.println("Error exporting data to " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
