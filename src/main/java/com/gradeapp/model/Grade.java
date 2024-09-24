
package com.gradeapp.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Grade {
    private Student student;
    private Assessment assessment;
    private AssessmentPart assessmentPart;
    private DoubleProperty score;
    private String feedback;
    private DoubleProperty percentage;

    // Constructor with AssessmentPart
    public Grade(Student student, Assessment assessment, AssessmentPart assessmentPart, double score, String feedback) {
        this.student = student;
        this.assessment = assessment;
        this.assessmentPart = assessmentPart;
        this.score = new SimpleDoubleProperty(score);
        this.feedback = feedback;
        this.percentage = new SimpleDoubleProperty(calculatePercentage());
    }

    // Constructor without AssessmentPart
    public Grade(Student student, Assessment assessment, double score, String feedback) {
        this(student, assessment, null, score, feedback);
    }

    // Getters and setters
    public Student getStudent() {
        return student;
    }

    public double getPercentage() {
        return percentage.get();
    }

    public DoubleProperty percentageProperty() {
        return percentage;
    }

    private double calculatePercentage() {
        double maxScore = assessmentPart != null ? assessmentPart.getMaxScore() : assessment.getMaxScore();
        return maxScore > 0 ? (getScore() / maxScore) * 100 : 0;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public AssessmentPart getAssessmentPart() {
        return assessmentPart;
    }

    public double getScore() {
        return score.get();
    }

    public void setScore(double score) {
        this.score.set(score);
        this.percentage.set(calculatePercentage());
    }

    public DoubleProperty scoreProperty() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
