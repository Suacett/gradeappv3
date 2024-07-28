package com.gradeapp.view;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.gradeapp.controller.DataImportExportController;
import com.gradeapp.controller.GradingController;
import com.gradeapp.controller.ReportController;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides the main user interface for the application, integrating all other views and menu functionality.
 */
public class MainView {
    private Stage stage;
    private BorderPane root;
    private TabPane tabPane;
    private GradingView gradingView;
    private ReportView reportView;
    private SettingsView settingsView;
    private DataImportExportController dataController;
    
    private static final Logger logger = LogManager.getLogger(MainView.class);

    public MainView() {
        try {
            this.stage = new Stage();
            this.dataController = new DataImportExportController();
            this.gradingView = new GradingView(new GradingController());
            this.reportView = new ReportView(new ReportController());
            this.settingsView = new SettingsView();
            logger.info("MainView components initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing MainView components", e);
            showErrorAlert("Initialization Error", "Failed to initialize application components. Please restart the application.");
        }
    }

    public void initialize() {
        initializeUI();
    }

    private void initializeUI() {
        root = new BorderPane();

        // Create menu
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Initialize sub-views and create tabs
        tabPane = new TabPane();

        Tab gradingTab = new Tab("Grading");
        if (gradingView != null) {
            gradingTab.setContent(gradingView.getRoot());
        }
        Tab reportTab = new Tab("Reports");
        if (reportView != null) {
            reportTab.setContent(reportView.getRoot());
        }
        Tab settingsTab = new Tab("Settings", settingsView != null ? settingsView.getRoot() : new VBox());

        tabPane.getTabs().addAll(gradingTab, reportTab, settingsTab);

        // Prevent tabs from being closed
        tabPane.getTabs().forEach(tab -> tab.setClosable(false));

        // Set the TabPane as the center content
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1024, 768);
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu viewMenu = new Menu("View");
        Menu helpMenu = new Menu("Help");

        MenuItem importItem = new MenuItem("Import Data");
        importItem.setOnAction(e -> importData());

        MenuItem exportItem = new MenuItem("Export Data");
        exportItem.setOnAction(e -> exportData());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close());

        fileMenu.getItems().addAll(importItem, exportItem, new SeparatorMenuItem(), exitItem);
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);

        return menuBar;
    }

    private void importData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            // Call the DataImportExportController to handle the import
            dataController.importData(file.getPath());
        }
    }

    private void exportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            // Call the DataImportExportController to handle the export
            dataController.exportData(file.getPath());
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Stage getStage() {
        return stage;
    }
}
