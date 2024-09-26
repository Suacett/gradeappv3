package com.gradeapp.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a grade book that manages grades for students across various assessments.
 * Utilizes a nested map structure to associate students with their assessments and corresponding grades.
 */
public class GradeBook {
    private Map<Student, Map<Assessment, Grade>> grades;

    /**
     * Constructs an empty GradeBook.
     */
    public GradeBook() {
        this.grades = new HashMap<>();
    }

    // ----------------------------- Grade Management -----------------------------

    /**
     * Adds a grade to the grade book for a specific student and assessment.
     *
     * @param grade The Grade to add.
     */
    public void addGrade(Grade grade) {
        grades.computeIfAbsent(grade.getStudent(), k -> new HashMap<>())
              .put(grade.getAssessment(), grade);
    }

    /**
     * Removes a grade from the grade book for a specific student and assessment.
     *
     * @param grade The Grade to remove.
     */
    public void removeGrade(Grade grade) {
        Map<Assessment, Grade> studentGrades = grades.get(grade.getStudent());
        if (studentGrades != null) {
            studentGrades.remove(grade.getAssessment());
        }
    }

    // ----------------------------- Grade Retrieval -----------------------------

    /**
     * Retrieves a specific grade for a student and assessment.
     *
     * @param student     The Student whose grade is to be retrieved.
     * @param assessment  The Assessment associated with the grade.
     * @return The Grade if found, or null otherwise.
     */
    public Grade getGrade(Student student, Assessment assessment) {
        return grades.getOrDefault(student, Collections.emptyMap()).get(assessment);
    }

    /**
     * Retrieves all grades for a specific student.
     *
     * @param student The Student whose grades are to be retrieved.
     * @return A map of Assessments to Grades for the student.
     */
    public Map<Assessment, Grade> getStudentGrades(Student student) {
        return new HashMap<>(grades.getOrDefault(student, Collections.emptyMap()));
    }

    /**
     * Retrieves all grades in the grade book.
     *
     * @return A list of all Grade objects.
     */
    public List<Grade> getAllGrades() {
        return grades.values().stream()
                     .flatMap(m -> m.values().stream())
                     .collect(Collectors.toList());
    }

    /**
     * Retrieves all grades for a specific assessment across all students.
     *
     * @param assessment The Assessment whose grades are to be retrieved.
     * @return A list of Grade objects for the assessment.
     */
    public List<Grade> getGradesForAssessment(Assessment assessment) {
        return grades.values().stream()
                     .map(m -> m.get(assessment))
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());
    }

    // ----------------------------- Getters and Setters -----------------------------


    public Map<Student, Map<Assessment, Grade>> getGrades() {
        return grades;
    }

    public void setGrades(Map<Student, Map<Assessment, Grade>> grades) {
        this.grades = grades;
    }
}
