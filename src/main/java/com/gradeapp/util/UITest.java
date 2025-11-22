package com.gradeapp.util;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * Test application to verify UI improvements work correctly.
 * This class demonstrates all UIHelper methods and CSS styles.
 * Run this to test the UI before deploying.
 */
public class UITest extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create main container
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = UIHelper.createHeading1("MarkBook+ UI Test");
        root.getChildren().add(title);

        // Create tab pane for different test sections
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Buttons
        Tab buttonsTab = new Tab("Buttons");
        buttonsTab.setContent(createButtonsTest());

        // Tab 2: Alerts & Dialogs
        Tab alertsTab = new Tab("Alerts");
        alertsTab.setContent(createAlertsTest());

        // Tab 3: Form Controls
        Tab formsTab = new Tab("Forms");
        formsTab.setContent(createFormsTest());

        // Tab 4: Tables & Lists
        Tab tablesTab = new Tab("Tables");
        tablesTab.setContent(createTablesTest());

        // Tab 5: Cards
        Tab cardsTab = new Tab("Cards");
        cardsTab.setContent(createCardsTest());

        tabPane.getTabs().addAll(buttonsTab, alertsTab, formsTab, tablesTab, cardsTab);

        root.getChildren().add(tabPane);

        // Create scene
        Scene scene = new Scene(root, 900, 700);

        // Load stylesheet
        scene.getStylesheets().add(
            getClass().getResource("/org/example/demo3/styles.css").toExternalForm()
        );

        primaryStage.setTitle("MarkBook+ UI Component Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates button test section.
     */
    private VBox createButtonsTest() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("section-style");

        Label heading = UIHelper.createHeading2("Button Styles");
        container.getChildren().add(heading);

        // Default buttons
        HBox defaultRow = new HBox(10);
        defaultRow.setAlignment(Pos.CENTER_LEFT);
        Button defaultBtn = new Button("Default Button");
        UIHelper.addTooltip(defaultBtn, "This is a default button");
        defaultRow.getChildren().addAll(new Label("Default:"), defaultBtn);

        // Primary button
        HBox primaryRow = new HBox(10);
        primaryRow.setAlignment(Pos.CENTER_LEFT);
        Button primaryBtn = UIHelper.createPrimaryButton("Primary Button");
        UIHelper.addTooltip(primaryBtn, "This is a primary action");
        primaryRow.getChildren().addAll(new Label("Primary:"), primaryBtn);

        // Success button
        HBox successRow = new HBox(10);
        successRow.setAlignment(Pos.CENTER_LEFT);
        Button successBtn = UIHelper.createSuccessButton("Success Button");
        UIHelper.addTooltip(successBtn, "This represents a successful action");
        successRow.getChildren().addAll(new Label("Success:"), successBtn);

        // Warning button
        HBox warningRow = new HBox(10);
        warningRow.setAlignment(Pos.CENTER_LEFT);
        Button warningBtn = UIHelper.createWarningButton("Warning Button");
        UIHelper.addTooltip(warningBtn, "This is a warning action");
        warningRow.getChildren().addAll(new Label("Warning:"), warningBtn);

        // Delete button
        HBox deleteRow = new HBox(10);
        deleteRow.setAlignment(Pos.CENTER_LEFT);
        Button deleteBtn = UIHelper.createDeleteButton("Delete Button");
        UIHelper.addTooltip(deleteBtn, "This is a destructive action");
        deleteRow.getChildren().addAll(new Label("Delete:"), deleteBtn);

        // Sizes
        Label sizeLabel = UIHelper.createHeading3("Button Sizes");
        HBox sizesRow = new HBox(10);
        sizesRow.setAlignment(Pos.CENTER_LEFT);

        Button smallBtn = UIHelper.createPrimaryButton("Small");
        smallBtn.getStyleClass().add("small-button");

        Button normalBtn = UIHelper.createPrimaryButton("Normal");

        Button largeBtn = UIHelper.createPrimaryButton("Large");
        largeBtn.getStyleClass().add("large-button");

        sizesRow.getChildren().addAll(smallBtn, normalBtn, largeBtn);

        // Disabled state
        HBox disabledRow = new HBox(10);
        disabledRow.setAlignment(Pos.CENTER_LEFT);
        Button disabledBtn = UIHelper.createPrimaryButton("Disabled");
        disabledBtn.setDisable(true);
        disabledRow.getChildren().addAll(new Label("Disabled:"), disabledBtn);

        container.getChildren().addAll(
            defaultRow, primaryRow, successRow, warningRow, deleteRow,
            new Separator(), sizeLabel, sizesRow, disabledRow
        );

        return container;
    }

    /**
     * Creates alerts test section.
     */
    private VBox createAlertsTest() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("section-style");

        Label heading = UIHelper.createHeading2("Alerts & Dialogs");
        container.getChildren().add(heading);

        // Success alert
        Button successAlert = UIHelper.createSuccessButton("Show Success Alert");
        successAlert.setOnAction(e ->
            UIHelper.showSuccessAlert("Success", "Operation completed successfully!"));

        // Error alert
        Button errorAlert = UIHelper.createDeleteButton("Show Error Alert");
        errorAlert.setOnAction(e ->
            UIHelper.showErrorAlert("Error", "An error occurred during the operation."));

        // Warning alert
        Button warningAlert = UIHelper.createWarningButton("Show Warning Alert");
        warningAlert.setOnAction(e ->
            UIHelper.showWarningAlert("Warning", "Please review before proceeding."));

        // Info alert
        Button infoAlert = new Button("Show Info Alert");
        infoAlert.setOnAction(e ->
            UIHelper.showInfoAlert("Information", "Here is some useful information."));

        // Confirmation dialog
        Button confirmDialog = new Button("Show Confirmation");
        confirmDialog.setOnAction(e -> {
            if (UIHelper.showConfirmDialog("Confirm Action", "Are you sure you want to proceed?")) {
                UIHelper.showSuccessAlert("Confirmed", "You clicked OK!");
            }
        });

        // Yes/No dialog
        Button yesNoDialog = new Button("Show Yes/No Dialog");
        yesNoDialog.setOnAction(e -> {
            if (UIHelper.showYesNoDialog("Question", "Do you want to continue?")) {
                UIHelper.showSuccessAlert("Answer", "You clicked Yes!");
            } else {
                UIHelper.showInfoAlert("Answer", "You clicked No!");
            }
        });

        // Input dialog
        Button inputDialog = new Button("Show Input Dialog");
        inputDialog.setOnAction(e -> {
            String result = UIHelper.showInputDialog("Name", "Enter your name:", "John Doe");
            if (result != null) {
                UIHelper.showSuccessAlert("Input Received", "You entered: " + result);
            }
        });

        // Choice dialog
        Button choiceDialog = new Button("Show Choice Dialog");
        choiceDialog.setOnAction(e -> {
            String result = UIHelper.showChoiceDialog(
                "Select Course",
                "Choose a course:",
                Arrays.asList("Mathematics", "Science", "English", "History"),
                "Mathematics"
            );
            if (result != null) {
                UIHelper.showSuccessAlert("Choice Made", "You selected: " + result);
            }
        });

        VBox alertsBox = new VBox(10);
        alertsBox.getChildren().addAll(
            successAlert, errorAlert, warningAlert, infoAlert,
            new Separator(),
            confirmDialog, yesNoDialog, inputDialog, choiceDialog
        );

        container.getChildren().add(alertsBox);

        return container;
    }

    /**
     * Creates forms test section.
     */
    private VBox createFormsTest() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("section-style");

        Label heading = UIHelper.createHeading2("Form Controls");
        container.getChildren().add(heading);

        // Text field
        VBox textBox = new VBox(5);
        Label textLabel = new Label("Name:");
        TextField textField = new TextField();
        textField.setPromptText("Enter your name");
        UIHelper.addTooltip(textField, "Enter your full name here");
        textBox.getChildren().addAll(textLabel, textField);

        // Text area
        VBox areaBox = new VBox(5);
        Label areaLabel = new Label("Description:");
        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter description");
        textArea.setPrefRowCount(3);
        UIHelper.addTooltip(textArea, "Enter a detailed description");
        areaBox.getChildren().addAll(areaLabel, textArea);

        // ComboBox
        VBox comboBox = new VBox(5);
        Label comboLabel = new Label("Course:");
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Mathematics", "Science", "English", "History");
        combo.setPromptText("Select a course");
        UIHelper.addTooltip(combo, "Choose a course from the list");
        comboBox.getChildren().addAll(comboLabel, combo);

        // CheckBox
        CheckBox checkbox1 = new CheckBox("I agree to the terms");
        CheckBox checkbox2 = new CheckBox("Send me notifications");
        UIHelper.addTooltip(checkbox1, "Check this to agree");

        // RadioButtons
        VBox radioBox = new VBox(5);
        Label radioLabel = new Label("Select grade level:");
        ToggleGroup group = new ToggleGroup();
        RadioButton radio1 = new RadioButton("Beginner");
        RadioButton radio2 = new RadioButton("Intermediate");
        RadioButton radio3 = new RadioButton("Advanced");
        radio1.setToggleGroup(group);
        radio2.setToggleGroup(group);
        radio3.setToggleGroup(group);
        radio1.setSelected(true);
        radioBox.getChildren().addAll(radioLabel, radio1, radio2, radio3);

        // Validation test
        Label validationLabel = UIHelper.createHeading3("Validation Test");
        TextField validationField = new TextField();
        validationField.setPromptText("Required field");
        Button validateBtn = UIHelper.createPrimaryButton("Validate");
        validateBtn.setOnAction(e -> {
            if (UIHelper.validateNotEmpty(validationField, "Test Field")) {
                UIHelper.showSuccessAlert("Valid", "Field is not empty!");
            }
        });

        container.getChildren().addAll(
            textBox, areaBox, comboBox,
            new Separator(),
            checkbox1, checkbox2,
            new Separator(),
            radioBox,
            new Separator(),
            validationLabel, validationField, validateBtn
        );

        return container;
    }

    /**
     * Creates tables test section.
     */
    private VBox createTablesTest() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("section-style");

        Label heading = UIHelper.createHeading2("Tables & Lists");
        container.getChildren().add(heading);

        // Simple ListView
        Label listLabel = UIHelper.createHeading3("List View");
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(
            "Student 1 - Grade: 85%",
            "Student 2 - Grade: 92%",
            "Student 3 - Grade: 78%",
            "Student 4 - Grade: 95%",
            "Student 5 - Grade: 88%"
        );
        listView.setPrefHeight(150);

        // TableView
        Label tableLabel = UIHelper.createHeading3("Table View");
        TableView<String> tableView = new TableView<>();
        TableColumn<String, String> col1 = new TableColumn<>("Name");
        TableColumn<String, String> col2 = new TableColumn<>("Grade");
        TableColumn<String, String> col3 = new TableColumn<>("Status");
        tableView.getColumns().addAll(col1, col2, col3);
        tableView.setPrefHeight(150);

        container.getChildren().addAll(listLabel, listView, new Separator(), tableLabel, tableView);

        return container;
    }

    /**
     * Creates cards test section.
     */
    private VBox createCardsTest() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));

        Label heading = UIHelper.createHeading2("Cards & Sections");
        container.getChildren().add(heading);

        // Card 1
        VBox card1 = new VBox(10);
        card1.getStyleClass().add("card");
        Label cardTitle1 = UIHelper.createHeading3("Student Statistics");
        Label cardText1 = new Label("Total Students: 150");
        cardText1.getStyleClass().add("card-text");
        Label cardText2 = new Label("Average Grade: 85.5%");
        cardText2.getStyleClass().add("card-text");
        card1.getChildren().addAll(cardTitle1, cardText2, cardText1);

        // Card 2
        VBox card2 = new VBox(10);
        card2.getStyleClass().add("card");
        Label cardTitle2 = UIHelper.createHeading3("Recent Activities");
        Label cardText3 = new Label("• 5 new students added");
        Label cardText4 = new Label("• 12 grades updated");
        Label cardText5 = new Label("• 3 assessments created");
        card2.getChildren().addAll(cardTitle2, cardText3, cardText4, cardText5);

        // Section with badges
        VBox section = new VBox(10);
        section.getStyleClass().add("section-style");
        Label sectionTitle = UIHelper.createHeading3("Status Badges");

        HBox badgesRow = new HBox(10);
        Label badge1 = new Label("New");
        badge1.getStyleClass().add("badge");
        Label badge2 = new Label("Success");
        badge2.getStyleClass().addAll("badge", "success");
        Label badge3 = new Label("Warning");
        badge3.getStyleClass().addAll("badge", "warning");
        Label badge4 = new Label("Error");
        badge4.getStyleClass().addAll("badge", "danger");
        badgesRow.getChildren().addAll(badge1, badge2, badge3, badge4);

        section.getChildren().addAll(sectionTitle, badgesRow);

        // Text styles
        VBox textSection = new VBox(10);
        textSection.getStyleClass().add("section-style");
        Label textTitle = UIHelper.createHeading3("Text Styles");
        Label successText = UIHelper.createSuccessLabel("✓ Operation successful");
        Label warningText = UIHelper.createWarningLabel("⚠ Please review");
        Label errorText = UIHelper.createErrorLabel("✗ Error occurred");
        Label mutedText = new Label("Additional information");
        mutedText.getStyleClass().add("muted-text");
        textSection.getChildren().addAll(textTitle, successText, warningText, errorText, mutedText);

        container.getChildren().addAll(card1, card2, section, textSection);

        return container;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
