package com.gradeapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course within the grading application.
 * A course can have multiple outcomes, students, assessments, and an associated grade book.
 */
public class Course {
    private String id;
    private String name;
    private String description;
    private List<Outcome> outcomes;
    private GradeBook gradeBook;
    private List<Student> students;
    private List<Assessment> assessments;

    /**
     * Constructs a Course object with the specified details.
     *
     * @param id          The unique identifier of the course.
     * @param name        The name of the course.
     * @param description A brief description of the course.
     */
    public Course(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.outcomes = new ArrayList<>();
        this.gradeBook = new GradeBook();
        this.students = new ArrayList<>();
        this.assessments = new ArrayList<>();
    }

    // ----------------------------- Getters and Setters -----------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ----------------------------- Outcome Management -----------------------------

    /**
     * Retrieves a copy of the outcomes associated with this course.
     *
     * @return A list of Outcome objects.
     */
    public List<Outcome> getOutcomes() {
        return new ArrayList<>(outcomes);
    }

    /**
     * Sets the outcomes for this course.
     *
     * @param outcomes A list of Outcome objects to set.
     */
    public void setOutcomes(List<Outcome> outcomes) {
        this.outcomes = new ArrayList<>(outcomes);
    }

    /**
     * Adds an outcome to this course.
     *
     * @param outcome The Outcome to add.
     */
    public void addOutcome(Outcome outcome) {
        this.outcomes.add(outcome);
    }

    /**
     * Removes an outcome from this course.
     *
     * @param outcome The Outcome to remove.
     */
    public void removeOutcome(Outcome outcome) {
        this.outcomes.remove(outcome);
    }

    // ----------------------------- Grade Book Management -----------------------------

    /**
     * Retrieves the grade book associated with this course.
     *
     * @return The GradeBook object.
     */
    public GradeBook getGradeBook() {
        return gradeBook;
    }

    /**
     * Sets the grade book for this course.
     *
     * @param gradeBook The GradeBook object to set.
     */
    public void setGradeBook(GradeBook gradeBook) {
        this.gradeBook = gradeBook;
    }

    // ----------------------------- Student Management -----------------------------

    /**
     * Retrieves a copy of the students enrolled in this course.
     *
     * @return A list of Student objects.
     */
    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    /**
     * Adds a student to this course and sets the student's course reference.
     *
     * @param student The Student to add.
     */
    public void addStudent(Student student) {
        this.students.add(student);
        student.setCourse(this);
    }

    /**
     * Removes a student from this course and clears the student's course reference.
     *
     * @param student The Student to remove.
     */
    public void removeStudent(Student student) {
        this.students.remove(student);
        student.setCourse(null);
    }

    // ----------------------------- Assessment Management -----------------------------

    /**
     * Retrieves a copy of all grades recorded in the grade book for this course.
     *
     * @return A list of Grade objects.
     */
    public List<Grade> getAllGrades() {
        return this.gradeBook.getAllGrades();
    }

    /**
     * Retrieves a copy of the assessments associated with this course.
     *
     * @return A list of Assessment objects.
     */
    public List<Assessment> getAssessments() {
        return new ArrayList<>(assessments);
    }

    /**
     * Adds an assessment to this course after validating the total weight.
     *
     * @param assessment The Assessment to add.
     * @throws IllegalArgumentException if the total assessment weight exceeds 100%.
     */
    public void addAssessment(Assessment assessment) {
        double totalWeight = getTotalAssessmentWeight();
        if (totalWeight + assessment.getWeight() > 100) {
            throw new IllegalArgumentException("Total assessment weight cannot exceed 100%");
        }
        this.assessments.add(assessment);
    }

    /**
     * Calculates the total weight of all assessments in this course.
     *
     * @return The total assessment weight.
     */
    public double getTotalAssessmentWeight() {
        return assessments.stream().mapToDouble(Assessment::getWeight).sum();
    }

    /**
     * Removes an assessment from this course.
     *
     * @param assessment The Assessment to remove.
     */
    public void removeAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
    }
}
