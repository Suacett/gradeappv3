package com.gradeapp.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a student within the grading application.
 * Each student has a name, unique student ID, enrolled course, and a list of grades.
 */
public class Student {
    private final StringProperty nameProperty;
    private String studentId;
    private Course course;
    private List<Grade> grades;

    /**
     * Constructs a Student with the specified name and student ID.
     *
     * @param name      The name of the student.
     * @param studentId The unique identifier of the student.
     */
    public Student(String name, String studentId) {
        this.nameProperty = new SimpleStringProperty(name);
        this.studentId = studentId;
        this.grades = new ArrayList<>();
    }

    // ----------------------------- Grade Management -----------------------------

    /**
     * Adds a grade to the student's list of grades for a specific assessment part.
     *
     * @param assessment      The Assessment associated with the grade.
     * @param part            The AssessmentPart associated with the grade (nullable).
     * @param score           The score achieved by the student.
     * @param feedback        Feedback provided for the grade.
     */
    public void addGrade(Assessment assessment, AssessmentPart part, double score, String feedback) {
        Grade grade = new Grade(this, assessment, part, score, feedback);
        addGrade(grade);
    }

    /**
     * Adds a grade to the student's list of grades for a specific assessment without an assessment part.
     *
     * @param assessment The Assessment associated with the grade.
     * @param score      The score achieved by the student.
     * @param feedback   Feedback provided for the grade.
     */
    public void addGrade(Assessment assessment, double score, String feedback) {
        addGrade(assessment, null, score, feedback);
    }

    /**
     * Adds a Grade object to the student's list of grades and updates the course's grade book if applicable.
     *
     * @param grade The Grade to add.
     */
    public void addGrade(Grade grade) {
        grades.add(grade);
        if (course != null && course.getGradeBook() != null) {
            course.getGradeBook().addGrade(grade);
        }
    }

    /**
     * Removes a grade from the student's list of grades and updates the course's grade book if applicable.
     *
     * @param grade The Grade to remove.
     */
    public void removeGrade(Grade grade) {
        grades.remove(grade);
        if (course != null && course.getGradeBook() != null) {
            course.getGradeBook().removeGrade(grade);
        }
    }

    /**
     * Retrieves a copy of the student's list of grades.
     *
     * @return A new list containing the student's grades.
     */
    public List<Grade> getGrades() {
        return new ArrayList<>(grades);
    }

    // ----------------------------- Performance Calculation -----------------------------

    /**
     * Calculates the student's overall performance based on their grades and assessment weights.
     *
     * @return The calculated overall performance as a double.
     */
    public double calculateOverallPerformance() {
        double totalWeightedScore = grades.stream()
                .mapToDouble(grade -> grade.getScore() * grade.getAssessment().getWeight())
                .sum();
        double totalWeight = grades.stream()
                .mapToDouble(grade -> grade.getAssessment().getWeight())
                .sum();
        return totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
    }

    // ----------------------------- Getters and Setters -----------------------------

    public String getName() {
        return nameProperty.get();
    }

    public String getStudentId() {
        return studentId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public GradeBook getGradeBook() {
        return course != null ? course.getGradeBook() : null;
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public void setName(String name) {
        this.nameProperty.set(name);
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
