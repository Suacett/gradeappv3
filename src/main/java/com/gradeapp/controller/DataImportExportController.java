package com.gradeapp.controller;

import com.gradeapp.util.FileHandler;

public class DataImportExportController {
    private FileHandler fileHandler;

    public DataImportExportController() {
        this.fileHandler = new FileHandler();
    }

    public void importData(String filePath) {
        // Implement import logic using FileHandler
    }

    public void exportData(String filePath) {
        // Implement export logic using FileHandler
    }
}
