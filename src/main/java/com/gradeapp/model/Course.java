package com.gradeapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Course {
    private String name;
    private String description;
    private List<Student> students;
    private GradeBook gradeBook;
    private List<Assessment> assessments;
    private Set<Outcomes> outcomes;

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
        this.students = new ArrayList<>();
        this.gradeBook = new GradeBook();
        this.assessments = new ArrayList<>();
        this.outcomes = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Student> getStudents() {
        return students;
    }

    public GradeBook getGradeBook() {
        return gradeBook;
    }

    public List<Assessment> getAssessments() {
        return Collections.unmodifiableList(assessments);
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    public Set<Outcomes> getOutcomes() {
        return Collections.unmodifiableSet(outcomes);
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
