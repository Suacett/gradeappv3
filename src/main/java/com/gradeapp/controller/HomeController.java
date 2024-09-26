package com.gradeapp.controller;

import java.util.List;

import com.gradeapp.database.Database;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

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
        return List.of(

        );
    }
}
