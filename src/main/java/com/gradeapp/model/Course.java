package com.gradeapp.model;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private String name;
    private String description;
    private List<Outcome> outcomes;
    private GradeBook gradeBook;
    private List<Student> students;
    private List<Assessment> assessments;

    public Course(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.outcomes = new ArrayList<>();
        this.gradeBook = new GradeBook();
        this.students = new ArrayList<>();
        this.assessments = new ArrayList<>();
    }

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
        double totalWeight = getTotalAssessmentWeight();
        if (totalWeight + assessment.getWeight() > 100) {
            throw new IllegalArgumentException("Total assessment weight cannot exceed 100%");
        }
        this.assessments.add(assessment);
    }

    public double getTotalAssessmentWeight() {
        return assessments.stream().mapToDouble(Assessment::getWeight).sum();
    }

    public void removeAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
    }
}