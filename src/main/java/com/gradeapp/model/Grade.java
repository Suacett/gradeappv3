package com.gradeapp.model;

import java.time.LocalDate;
import javafx.beans.property.SimpleDoubleProperty;

public class Grade {
    private Student student;
    private Assessment assessment;
    private SimpleDoubleProperty score;
    private String feedback;
    private LocalDate date;

    public Grade(Student student, Assessment assessment, double score, String feedback) {
        this.student = student;
        this.assessment = assessment;
        this.score = new SimpleDoubleProperty(score);
        this.feedback = feedback;
        this.date = LocalDate.now();
    }

    public double getScore() {
        return score.get();
    }

    public void setScore(double score) {
        this.score.set(score);
    }

    public SimpleDoubleProperty scoreProperty() {
        return score;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public Student getStudent() {
        return student;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void addToGradeBook() {
        student.getGradeBook().addGrade(this);
    }

    public void removeFromGradeBook() {
        student.getGradeBook().removeGrade(this);
    }
}
