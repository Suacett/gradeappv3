package com.gradeapp.controller;

import com.gradeapp.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentMarkbookController {

    @FXML
    private Label studentName;
    @FXML
    private Label studentId;

    private StudentMarkbookController studentMarkbookController;

    public void setStudent(Student student) {
        studentName.setText(student.getName());
        studentId.setText(student.getStudentId());
    }

    public void setMarkBookController(MarkingController markingController) {
        this.studentMarkbookController = studentMarkbookController;
    }

}