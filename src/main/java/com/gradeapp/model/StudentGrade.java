package com.gradeapp.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class StudentGrade {
    private final SimpleStringProperty studentName;
    private final SimpleStringProperty assessmentName;
    private final SimpleDoubleProperty score;

    public StudentGrade(String studentName, String assessmentName, double score) {
        this.studentName = new SimpleStringProperty(studentName);
        this.assessmentName = new SimpleStringProperty(assessmentName);
        this.score = new SimpleDoubleProperty(score);
    }

    public String getStudentName() {
        return studentName.get();
    }

    public SimpleStringProperty studentNameProperty() {
        return studentName;
    }

    public String getAssessmentName() {
        return assessmentName.get();
    }

    public SimpleStringProperty assessmentNameProperty() {
        return assessmentName;
    }

    public double getScore() {
        return score.get();
    }

    public SimpleDoubleProperty scoreProperty() {
        return score;
    }
}
