package com.gradeapp.view;

import com.gradeapp.controller.GradingController;
import com.gradeapp.model.StudentGrade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

/**
 * Offers an interface for inputting and displaying student grades.
 */
public class GradingView {
    private VBox root;
    private TableView<StudentGrade> gradeTable;
    private Button addGradeButton;
    private GradingController gradingController;

    public GradingView(GradingController gradingController) {
        this.gradingController = gradingController;
        this.root = new VBox(10);
        initializeUI();
    }

    private void initializeUI() {
        root = new VBox(10);
        root.setPadding(new Insets(10));

        Label titleLabel = new Label("Student Grades");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        gradeTable = createGradeTable();
        
        HBox buttonBox = new HBox(10);
        addGradeButton = new Button("Add Grade");
        addGradeButton.setOnAction(e -> addGrade());
        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> saveChanges());
        buttonBox.getChildren().addAll(addGradeButton, saveButton);

        root.getChildren().addAll(titleLabel, gradeTable, buttonBox);
        root.setPadding(new Insets(10));
    }

    private TableView<StudentGrade> createGradeTable() {
        TableView<StudentGrade> table = new TableView<>();
        
        TableColumn<StudentGrade, String> studentNameCol = new TableColumn<>("Student Name");
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        TableColumn<StudentGrade, String> assessmentNameCol = new TableColumn<>("Assessment");
        assessmentNameCol.setCellValueFactory(new PropertyValueFactory<>("assessmentName"));
        assessmentNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        TableColumn<StudentGrade, Double> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        
        table.getColumns().addAll(studentNameCol, assessmentNameCol, scoreCol);

        ObservableList<StudentGrade> data = FXCollections.observableArrayList(
            new StudentGrade("John Doe", "Midterm", 85.5),
            new StudentGrade("Jane Smith", "Final", 92.0)
        );
        table.setItems(data);
        table.setEditable(true);
        
        return table;
    }

    private void addGrade() {
        Dialog<StudentGrade> dialog = new Dialog<>();
        dialog.setTitle("Add New Grade");
        dialog.setHeaderText("Enter student grade details");

        // ... Create dialog content and set up result converter ...

        dialog.showAndWait().ifPresent(result -> {
            gradeTable.getItems().add(result);
        });
    }

    private void saveChanges() {
        // Call the GradingController to save the changes
        gradingController.saveGrades(gradeTable.getItems());
    }

    public VBox getRoot() {
        return root;
    }
}
