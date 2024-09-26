package com.gradeapp.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Represents a grade assigned to a student for a specific assessment or assessment part.
 * Contains information about the student's score, feedback, and calculated percentage.
 */
public class Grade {
    private Student student;
    private Assessment assessment;
    private AssessmentPart assessmentPart;
    private DoubleProperty score;
    private String feedback;
    private DoubleProperty percentage;

    /**
     * Constructs a Grade with the specified student, assessment part, score, and feedback.
     *
     * @param student          The Student who received the grade.
     * @param assessment       The Assessment associated with the grade.
     * @param assessmentPart   The AssessmentPart associated with the grade (nullable).
     * @param score            The score achieved by the student.
     * @param feedback         Feedback provided for the grade.
     */
    public Grade(Student student, Assessment assessment, AssessmentPart assessmentPart, double score, String feedback) {
        this.student = student;
        this.assessment = assessment;
        this.assessmentPart = assessmentPart;
        this.score = new SimpleDoubleProperty(score);
        this.feedback = feedback;
        this.percentage = new SimpleDoubleProperty(calculatePercentage());
    }

    /**
     * Constructs a Grade with the specified student, assessment, score, and feedback.
     * This constructor is used when there is no specific assessment part.
     *
     * @param student    The Student who received the grade.
     * @param assessment The Assessment associated with the grade.
     * @param score      The score achieved by the student.
     * @param feedback   Feedback provided for the grade.
     */
    public Grade(Student student, Assessment assessment, double score, String feedback) {
        this(student, assessment, null, score, feedback);
    }

    // ----------------------------- Getters and Setters -----------------------------

    public Student getStudent() {
        return student;
    }

    public double getPercentage() {
        return percentage.get();
    }

    public DoubleProperty percentageProperty() {
        return percentage;
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

    // ----------------------------- Utility Methods -----------------------------

    /**
     * Calculates the percentage score based on the achieved score and the maximum possible score.
     *
     * @return The calculated percentage.
     */
    private double calculatePercentage() {
        double maxScore = assessmentPart != null ? assessmentPart.getMaxScore() : assessment.getMaxScore();
        return maxScore > 0 ? (getScore() / maxScore) * 100 : 0;
    }
}
