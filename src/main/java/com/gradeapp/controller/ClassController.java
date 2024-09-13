package com.gradeapp.controller;

import java.sql.SQLException;
import java.util.List;

import com.gradeapp.database.Database;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Student;

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
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

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

    private Database db = new Database();
    private Classes selectedClass;

    @FXML
    private void initialize() {
        setupCourseSelector();
        updateClassList();
        updateClassDetailsView();
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

    @FXML
    public void handleViewClassDetailsAction(Classes classObj) {
        VBox classCard = (VBox) currentClassContainer.lookup("#" + classObj.getClassId());
        if (classCard != null) {
            selectClass(classObj);
        }
    }

    private void updateClassDetailsView() {
        if (selectedClass != null) {
            classStatisticsLabel.setText("Class Statistics: " + calculateClassStatistics());
            updateStudentListView();
        } else {
            classStatisticsLabel.setText("No class selected");
            studentListView.getItems().clear();
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

    // Class card, displays current classes
    private VBox createClassCard(Classes classObj) {
        VBox classCard = new VBox();
        classCard.setId(classObj.getClassId());
        classCard.getStyleClass().add("card");
        classCard.setSpacing(10);
        classCard.setPadding(new Insets(10));
        

        HBox classInfo = new HBox();
        classInfo.setSpacing(10);

        Label nameLabel = new Label("Name: " + classObj.getName());
        Label idLabel = new Label("ID: " + classObj.getClassId());

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
        classInfo.getChildren().addAll(nameLabel, idLabel);
        classCard.getChildren().addAll(classInfo, buttonContainer);

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
        updateClassDetailsView();
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
                System.out.println("The form is incomplete...");
            }
        });

        VBox editClassBox = new VBox(10, new Label("Edit Class"), classNameField, classIdField, saveButton);
        currentClassContainer.getChildren().clear();
        currentClassContainer.getChildren().add(editClassBox);
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
