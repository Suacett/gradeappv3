package com.gradeapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a class within a course in the grading application.
 * A class can have multiple assessments, outcomes, and students enrolled.
 */
public class Classes {
    private final StringProperty name;
    private final StringProperty classId;
    private List<Classes> classes;
    private List<Assessment> assessments;
    private Set<Outcome> outcomes;
    private Set<Student> students;

    /**
     * Constructs a Classes object with the specified name and class ID.
     *
     * @param name    The name of the class.
     * @param classId The unique identifier for the class.
     */
    public Classes(String name, String classId) {
        this.name = new SimpleStringProperty(name);
        this.classId = new SimpleStringProperty(classId);
        this.classes = new ArrayList<>();
        this.assessments = new ArrayList<>();
        this.outcomes = new HashSet<>();
        this.students = new HashSet<>();
    }

    // ----------------------------- Getters and Setters -----------------------------

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getClassId() {
        return classId.get();
    }

    public StringProperty classIdProperty() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId.set(classId);
    }

    // ----------------------------- Class Management -----------------------------

    /**
     * Retrieves the list of nested classes.
     *
     * @return A list of Classes objects.
     */
    public List<Classes> getClasses() {
        return classes;
    }

    /**
     * Retrieves an unmodifiable list of assessments associated with this class.
     *
     * @return An unmodifiable list of Assessment objects.
     */
    public List<Assessment> getAssessments() {
        return Collections.unmodifiableList(assessments);
    }

    /**
     * Adds an assessment to this class.
     *
     * @param assessment The Assessment to add.
     */
    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    /**
     * Retrieves an unmodifiable set of outcomes associated with this class.
     *
     * @return An unmodifiable set of Outcome objects.
     */
    public Set<Outcome> getOutcomes() {
        return Collections.unmodifiableSet(outcomes);
    }

    /**
     * Adds an outcome to this class.
     *
     * @param outcome The Outcome to add.
     */
    public void addOutcome(Outcome outcome) {
        outcomes.add(outcome);
    }

    // ----------------------------- Student Management -----------------------------

    /**
     * Adds a student to this class.
     *
     * @param student The Student to add.
     */
    public void addStudent(Student student) {
        students.add(student);
    }

    /**
     * Removes a student from this class.
     *
     * @param student The Student to remove.
     */
    public void removeStudent(Student student) {
        students.remove(student);
    }

    /**
     * Retrieves an unmodifiable set of students enrolled in this class.
     *
     * @return An unmodifiable set of Student objects.
     */
    public Set<Student> getStudents() {
        return Collections.unmodifiableSet(students);
    }
}
