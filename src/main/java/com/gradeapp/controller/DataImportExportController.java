package com.gradeapp.controller;

import com.gradeapp.model.Student;
import com.gradeapp.util.FileHandler;

import java.util.List;

public class DataImportExportController {
    private FileHandler fileHandler;

    public DataImportExportController() {
        this.fileHandler = new FileHandler();
    }

    // Methods for importing and exporting data
    public void importData(String filePath) {
        try {
            fileHandler.importData(filePath);
            System.out.println("Data imported successfully from " + filePath);
        } catch (Exception e) {
            System.err.println("Error importing data from " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void exportData(List<Student> students, String filePath) {
        try {
            fileHandler.exportData(students, filePath);
            System.out.println("Data exported successfully to " + filePath);
        } catch (Exception e) {
            System.err.println("Error exporting data to " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
