package com.gradeapp.controller;

import com.gradeapp.database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;


import java.util.List;

public class HomeController {

    @FXML
    private ListView<String> recentActivitiesList;

    private Database db = new Database();

    @FXML
    public void initialize() {
        populateRecentActivities();
    }

    // Method to populate recent activities in the ListView
    private void populateRecentActivities() {
        List<String> recentActivities = fetchRecentActivities();
        recentActivitiesList.getItems().addAll(recentActivities);
    }

    // Dummy method to fetch recent activities from the database
    private List<String> fetchRecentActivities() {
        // This is a placeholder. Replace with actual database logic.
        return List.of(
                
        );
    }
}
