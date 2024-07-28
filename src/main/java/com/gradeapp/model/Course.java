package com.gradeapp.model;

import java.util.ArrayList;
import java.util.List;

/*
  Represents a course, containing lists of assessments and enrolled students.
 */
public class Course {
    private String name;
    private List<Student> students;
    private List<Outcomes> outcomes;
    private List<Assessment> assessments;
    private GradeBook gradeBook;

    public Course(String name) {
        this.name = name;
        this.students = new ArrayList<>();
        this.assessments = new ArrayList<>();
        this.outcomes = new ArrayList<>();
        this.gradeBook = new GradeBook();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    public void addGrade(Student student, Assessment assessment, double score) {
        gradeBook.addGrade(student, assessment, score);
    }

    public GradeBook getGradeBook() {
        return gradeBook;
    }

    public void setGradeBook(GradeBook gradeBook) {
        this.gradeBook = gradeBook;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Outcomes> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<Outcomes> outcomes) {
        this.outcomes = outcomes;
    }
}
