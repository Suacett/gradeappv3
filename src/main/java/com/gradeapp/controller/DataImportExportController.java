package com.gradeapp.controller;

import com.gradeapp.util.FileHandler;

public class DataImportExportController {
    private FileHandler fileHandler;

    public DataImportExportController() {
        this.fileHandler = new FileHandler();
    }

    public void importData(String filePath) {
        try {
            fileHandler.importData(filePath);
        } catch (Exception e) {
            // Handle exception (e.g., log error, show error message)
        }
    }

    public void exportData(String filePath) {
        try {
            fileHandler.exportData(filePath);
        } catch (Exception e) {
            // Handle exception (e.g., log error, show error message)
        }
    }
}
