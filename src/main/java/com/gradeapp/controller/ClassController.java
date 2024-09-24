package com.gradeapp.controller;

import java.sql.SQLException;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;
import com.gradeapp.util.ChartGenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class ClassController {

    @FXML
    private VBox classContainer;
    @FXML
    private VBox currentClassContainer;
    @FXML
    private VBox newClassInputContainer;
    @FXML
    private ComboBox<Course> courseSelector;
    @FXML
    private ListView<Student> studentListView;
    @FXML
    private Label classStatisticsLabel;
    @FXML
    private ComboBox<Assessment> assessmentComboBox;
    @FXML
    private VBox classDetailsContainer;
    @FXML
    private ComboBox<AssessmentPart> partComboBox;
    @FXML
    private BarChart<String, Number> gradeBarChart;


    private Database db = new Database();
    private Classes selectedClass;
    private ChartGenerator chartGenerator = new ChartGenerator();

    @FXML
    private void initialize() {
        setupCourseSelector();
        updateClassList();
        updateClassDetailsView();

        // Hide class details container initially
        classDetailsContainer.setVisible(false);
        classDetailsContainer.setManaged(false);

        assessmentComboBox.setOnAction(event -> {
            populateParts();
            updateGradeChart();
        });

        partComboBox.setOnAction(event -> updateGradeChart());
    }

    private void setupCourseSelector() {
        ObservableList<Course> courses = FXCollections.observableArrayList(db.getAllCourses());
        courseSelector.setItems(courses);
        courseSelector.setCellFactory(lv -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getName() + " (" + course.getId() + ")");
                }
            }
        });
        courseSelector.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course == null ? "" : course.getName() + " (" + course.getId() + ")";
            }

            @Override
            public Course fromString(String string) {
                return null;
            }
        });

        courseSelector.setOnAction(e -> updateClassList());
        if (!courses.isEmpty()) { // Select first course in list
            courseSelector.getSelectionModel().selectFirst();
        }
    }

    private void updateClassList() {
        Course selectedCourse = courseSelector.getValue();
        if (selectedCourse != null) {
            List<Classes> classes = db.getClassesForCourse(selectedCourse.getId());
            displayClasses(classes);
        } else {
            currentClassContainer.getChildren().clear();
        }
        selectedClass = null;
        updateClassDetailsView();
    }

    private void displayClasses(List<Classes> classes) {
        currentClassContainer.getChildren().clear();
        selectedClass = null;
        for (Classes classObj : classes) {
            VBox classCard = createClassCard(classObj);
            currentClassContainer.getChildren().add(classCard);
        }
        updateClassDetailsView();
    }

    @FXML
    private void handleAddClassButtonAction() {
        Course selectedCourse = courseSelector.getValue();
        if (selectedCourse == null) {
            showError("Please select a course first.");
            return;
        }

        Dialog<Classes> dialog = new Dialog<>();
        dialog.setTitle("Add New Class");

        TextField nameField = new TextField();
        TextField idField = new TextField();

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Class Name:"), nameField,
                new Label("Class ID:"), idField));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String name = nameField.getText();
                String id = idField.getText();
                if (name.isEmpty() || id.isEmpty()) {
                    showError("Please fill in all fields.");
                    return null;
                }
                Classes newClass = new Classes(name, id);
                try {
                    db.addClass(newClass, selectedCourse.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    showError("Error adding class: " + e.getMessage());
                    return null;
                }
                return newClass;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> updateClassList());
    }

    private void updateGradeChart() {
        gradeBarChart.getData().clear();
    
        if (selectedClass == null) {
            return;
        }
    
        Assessment selectedAssessment = assessmentComboBox.getValue();
        AssessmentPart selectedPart = partComboBox.getValue();
    
        List<Grade> grades = null;
    
        if (selectedAssessment == null) {
            grades = db.getAllGradesForClass(selectedClass.getClassId());
        } else if (selectedPart == null) {
            grades = db.getGradesForClassAndAssessment(selectedClass.getClassId(), selectedAssessment.getId());
        } else {
            grades = db.getGradesForClassAssessmentAndPart(
                    selectedClass.getClassId(), selectedAssessment.getId(), selectedPart.getId());
        }
    
        if (grades != null && !grades.isEmpty()) {
            XYChart.Series<String, Number> series = chartGenerator.createGradeDistributionSeries(grades);
            gradeBarChart.getData().add(series);
        } else {
            gradeBarChart.setTitle("No grades available for the selected options.");
        }
    }
    

    @FXML
    public void handleViewClassDetailsAction(Classes classObj) {
        VBox classCard = (VBox) currentClassContainer.lookup("#" + classObj.getClassId());
        if (classCard != null) {
            selectClass(classObj);
        }
    }

    private void populateAssessments() {
        assessmentComboBox.getItems().clear();
        partComboBox.getItems().clear();

        if (selectedClass != null) {
            Course course = courseSelector.getValue();
            if (course != null) {
                List<Assessment> assessments = db.getAssessmentsForCourse(course.getId());
                assessmentComboBox.setItems(FXCollections.observableArrayList(assessments));
            }
        }
    }

    private void populateParts() {
        partComboBox.getItems().clear();
        Assessment selectedAssessment = assessmentComboBox.getValue();
        if (selectedAssessment != null) {
            List<AssessmentPart> parts = db.getAssessmentParts(selectedAssessment.getId());
            partComboBox.setItems(FXCollections.observableArrayList(parts));
        }
    }

    private void updateClassDetailsView() {
        if (selectedClass != null) {
            classStatisticsLabel.setText("Class Details: " + selectedClass.getName());
        } else {
            classStatisticsLabel.setText("No class selected");
        }
    }

    private String calculateClassStatistics() {
        List<Student> students = db.getStudentsInClass(selectedClass.getClassId());
        int totalStudents = students.size();

        return String.format("Total Students: %d", totalStudents);
    }

    private void updateStudentListView() {
        if (selectedClass == null) {
            studentListView.getItems().clear();
            return;
        }

        List<Student> students = db.getStudentsInClass(selectedClass.getClassId());
        ObservableList<Student> observableStudents = FXCollections.observableArrayList(students);
        studentListView.setItems(observableStudents);

        studentListView.setCellFactory(lv -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getName() + " (" + student.getStudentId() + ")");
                }
            }
        });
    }

    @FXML
    public void handleAddStudentToClassAction() {
        if (selectedClass == null) {
            showError("Please select a class first.");
            return;
        }

        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add Student to Class");

        List<Student> availableStudents = db.getStudentsNotInClass(selectedClass.getClassId());
        if (availableStudents.isEmpty()) {
            showError("No students available to add to this class.");
            return;
        }

        ComboBox<Student> studentComboBox = new ComboBox<>(FXCollections.observableArrayList(availableStudents));

        studentComboBox.setCellFactory(lv -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getName() + " (" + student.getStudentId() + ")");
                }
            }
        });

        studentComboBox.setConverter(new StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student == null ? "" : student.getName() + " (" + student.getStudentId() + ")";
            }

            @Override
            public Student fromString(String string) {
                return null;
            }
        });

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Select Student:"), studentComboBox));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Student student = studentComboBox.getValue();
                if (student != null) {
                    if (db.addStudentToClass(student.getStudentId(), selectedClass.getClassId())) {
                        return student;
                    } else {
                        showError("Failed to add student to class. The student might already be in the class.");
                    }
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> updateStudentListView());
    }

    // Create class card
    private VBox createClassCard(Classes classObj) {
        VBox classCard = new VBox();
        classCard.setId(classObj.getClassId());
        classCard.getStyleClass().add("card");
        classCard.setSpacing(10);
        classCard.setPadding(new Insets(10));
        // classCard.setTextAlignment(TextAlignment.CENTER);

        HBox classInfo = new HBox();
        classInfo.setSpacing(10);

        Label staticText = new Label("Class name: ");
        Label classNameText = new Label(classObj.getName());
        classNameText.getStyleClass().add("card-text");

        Region textSpacer = new Region();
        textSpacer.setMinWidth(20);

        // Label idLabel = new Label("Class ID: " + classObj.getClassId());
        Label staticIdText = new Label("Class ID: ");
        Label classIdText = new Label(classObj.getClassId());
        classIdText.getStyleClass().add("card-text");

        // Create TextFlow and add Text nodes
        TextFlow nameTextFlow = new TextFlow(staticText, classNameText, textSpacer, staticIdText, classIdText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setOnAction(event -> handleViewClassDetailsAction(classObj));

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> handleEditClassButtonAction(classObj));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> {
            db.delete("classes", "classId", classObj.getClassId());
            updateClassList();
        });

        buttonContainer.getChildren().addAll(viewDetailsButton, editButton, deleteButton);
        classInfo.getChildren().addAll(nameTextFlow, spacer, buttonContainer);
        classCard.getChildren().addAll(classInfo);

        classCard.setOnMouseClicked(event -> {
            currentClassContainer.getChildren().forEach(node -> {
                if (node instanceof VBox) {
                    ((VBox) node).setStyle(
                            "-fx-border-color: transparent; -fx-border-width: 2px; -fx-background-color: white;");
                }
            });
            classCard.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2px; -fx-background-color: #e0e0e0;");
            selectClass(classObj);
        });

        return classCard;
    }

    private void selectClass(Classes classObj) {
        selectedClass = classObj;
        classDetailsContainer.setVisible(true);
        classDetailsContainer.setManaged(true);
        populateAssessments();
        updateClassDetailsView();
        updateStudentListView();
        updateGradeChart();
    }

    private void handleEditClassButtonAction(Classes classes) {

        TextField classNameField = new TextField(classes.getName());
        TextField classIdField = new TextField(classes.getClassId());
        Button saveButton = new Button("Save");

        saveButton.setOnAction(event -> {
            String newName = classNameField.getText();
            String newId = classIdField.getText();
            if (!newName.isEmpty() && !newId.isEmpty()) {
                db.updateClass(classes.getClassId(), newName, newId);
                updateClassList();
            } else {
                showError("Please fill in all fields.");
            }
        });

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Class");

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Edit Class"),
                new Label("Class Name:"), classNameField,
                new Label("Class ID:"), classIdField,
                saveButton));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    public void handleRemoveStudentFromClassAction() {
        Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null && selectedClass != null) {
            db.removeStudentFromClass(selectedStudent.getStudentId(), selectedClass.getClassId());
            updateStudentListView();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
