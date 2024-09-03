package com.gradeapp.model;

import java.time.LocalDate;

public class Grade {
    private Student student;
    private Assessment assessment;
    private AssessmentPart part;
    private double score;
    private String feedback;
    private LocalDate date;

    public Grade(Student student, Assessment assessment, double score, String feedback) {
        this(student, assessment, null, score, feedback);
    }

    public Grade(Student student, Assessment assessment, AssessmentPart part, double score, String feedback) {
        this.student = student;
        this.assessment = assessment;
        this.part = part;
        this.score = score;
        this.feedback = feedback;
        this.date = LocalDate.now();
    }
    


    // Methods for managing the grade book
    public void addToGradeBook() {
        student.getGradeBook().addGrade(this);
    }

    public void removeFromGradeBook() {
        student.getGradeBook().removeGrade(this);
    }

    // Getters and Setters
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public AssessmentPart getPart() {
        return part;
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

    @Override
    public String toString() {
        return String.format("Grade(student=%s, assessment=%s, part=%s, score=%.2f, feedback=%s, date=%s)",
            student.getName(), assessment.getName(), 
            (part != null ? part.getName() : "N/A"), 
            score, feedback, date);
    }
}

