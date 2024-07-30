package com.gradeapp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Course {
    private String name;
    private List<Student> students;
    private GradeBook gradeBook;
    private List<Assessment> assessments;
    private Set<Outcomes> outcomes;

    public Course(String name) {
        this.name = name;
        this.students = new ArrayList<>();
        this.gradeBook = new GradeBook();
        this.assessments = new ArrayList<>();
        this.outcomes = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public GradeBook getGradeBook() {
        return gradeBook;
    }

    public List<Assessment> getAssessments() {
        return new ArrayList<>(assessments);
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    public Set<Outcomes> getOutcomes() {
        return new HashSet<>(outcomes);
    }

    public void addOutcome(Outcomes outcome) {
        outcomes.add(outcome);
    }

    public void addStudent(Student student) {
        students.add(student);
        student.setCourse(this);
    }

    public List<Grade> getAllGrades() {
        return students.stream()
            .flatMap(student -> student.getGrades().stream())
            .collect(Collectors.toList());
    }
}
