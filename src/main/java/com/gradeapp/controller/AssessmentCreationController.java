package com.gradeapp.controller;

import com.gradeapp.model.*;
import com.gradeapp.database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import java.util.HashMap;
import java.util.Map;

public class AssessmentCreationController {
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField weightField;
    @FXML private TextField maxScoreField;
    @FXML private VBox outcomeCheckboxContainer;
    @FXML private TableView<AssessmentPart> partsTable;
    @FXML private Label totalWeightLabel;
    
    private Course selectedCourse;
    private Assessment newAssessment;
    private Map<Outcome, TextField> outcomeWeightFields = new HashMap<>();
    private ObservableList<AssessmentPart> parts = FXCollections.observableArrayList();
    private Database db = new Database();

    @FXML
    public void initialize() {
        setupPartsTable();
        updateTotalWeight();
    }

    private void setupPartsTable() {
        partsTable.setEditable(true);

        TableColumn<AssessmentPart, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
        });

        TableColumn<AssessmentPart, Double> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        weightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        weightColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setWeight(event.getNewValue());
            updateTotalWeight();
        });

        TableColumn<AssessmentPart, Double> maxScoreColumn = new TableColumn<>("Max Score");
        maxScoreColumn.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
        maxScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        maxScoreColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setMaxScore(event.getNewValue());
        });

        partsTable.getColumns().addAll(nameColumn, weightColumn, maxScoreColumn);
        partsTable.setItems(parts);
    }

    public void setCourse(Course course) {
        this.selectedCourse = course;
        populateOutcomes();
    }

    private void populateOutcomes() {
        outcomeCheckboxContainer.getChildren().clear();
        outcomeWeightFields.clear();
        for (Outcome outcome : selectedCourse.getOutcomes()) {
            CheckBox cb = new CheckBox(outcome.getName());
            TextField weightField = new TextField();
            weightField.setPromptText("Weight");
            outcomeCheckboxContainer.getChildren().addAll(cb, weightField);
            outcomeWeightFields.put(outcome, weightField);
        }
    }

    @FXML
    private void addPart() {
        AssessmentPart newPart = new AssessmentPart(-1, "New Part", 0, 0);
        parts.add(newPart);
        updateTotalWeight();
    }

    @FXML
    private void saveAssessment() {
        try {
            String name = nameField.getText();
            String description = descriptionField.getText();
            double weight = Double.parseDouble(weightField.getText());
            double maxScore = Double.parseDouble(maxScoreField.getText());

            if (name.isEmpty() || description.isEmpty()) {
                showAlert("Please enter a name and description for the assessment.");
                return;
            }

            newAssessment = new Assessment(name, description, weight, maxScore);

            for (Map.Entry<Outcome, TextField> entry : outcomeWeightFields.entrySet()) {
                CheckBox cb = (CheckBox) outcomeCheckboxContainer.getChildren().get(
                        outcomeCheckboxContainer.getChildren().indexOf(entry.getValue()) - 1);
                if (cb.isSelected() && !entry.getValue().getText().isEmpty()) {
                    double outcomeWeight = Double.parseDouble(entry.getValue().getText());
                    newAssessment.addOutcome(entry.getKey(), outcomeWeight);
                }
            }

            for (AssessmentPart part : parts) {
                newAssessment.addPart(part);
            }

            selectedCourse.addAssessment(newAssessment);
            db.addAssessment(newAssessment, selectedCourse.getId());

            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Invalid input. Please enter valid numbers for weight and max score.");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
        }
    }

    private void updateTotalWeight() {
        double totalWeight = parts.stream().mapToDouble(AssessmentPart::getWeight).sum();
        totalWeightLabel.setText(String.format("Total Weight: %.2f%%", totalWeight));
        totalWeightLabel.setStyle(totalWeight == 100 ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        nameField.getScene().getWindow().hide();
    }
}