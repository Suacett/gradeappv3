package com.gradeapp.model;

/**
 * Stores a student's score for a specific task or assessment.
 */
public class Grade {
    private Student student;
    private Assessment assessment;
    private double score;

    public Grade(Student student, Assessment assessment, double score) {
        this.student = student;
        this.assessment = assessment;
        this.setScore(score);
    }

// Getters and setters

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        if (score < 0 || score > assessment.getMaxScore()) {
            throw new IllegalArgumentException("Score must be between 0 and max score");
        }
        this.score = score;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}