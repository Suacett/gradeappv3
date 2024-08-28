package com.gradeapp.model;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String name;
    private String description;
    private List<Outcome> outcomes;
    private GradeBook gradeBook;
    private List<Student> students;
    private List<Assessment> assessments;

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
        this.outcomes = new ArrayList<>();
        this.gradeBook = new GradeBook();
        this.students = new ArrayList<>();
        this.assessments = new ArrayList<>();
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

    public List<Outcome> getOutcomes() {
        return new ArrayList<>(outcomes);
    }

    public void setOutcomes(List<Outcome> outcomes) {
        this.outcomes = new ArrayList<>(outcomes);
    }

    public void addOutcome(Outcome outcome) {
        this.outcomes.add(outcome);
    }

    public void removeOutcome(Outcome outcome) {
        this.outcomes.remove(outcome);
    }

    public GradeBook getGradeBook() {
        return gradeBook;
    }

    public void setGradeBook(GradeBook gradeBook) {
        this.gradeBook = gradeBook;
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public void addStudent(Student student) {
        this.students.add(student);
        student.setCourse(this);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
        student.setCourse(null);
    }

    public List<Grade> getAllGrades() {
        return this.gradeBook.getAllGrades();
    }

    public List<Assessment> getAssessments() {
        return new ArrayList<>(assessments);
    }

    public void addAssessment(Assessment assessment) {
        this.assessments.add(assessment);
    }

    public void removeAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
    }
}