package com.gradeapp.model;

import java.util.*;

public class Student {
    private String name;
    private String studentId;
    private Course course;
    private List<StudentGrade> grades;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
        this.grades = new ArrayList<>();
    }

    public String getName() { return name; }
    public String getStudentId() { return studentId; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public GradeBook getGradeBook() { 
        return course != null ? course.getGradeBook() : null; 
    }

    public void addGrade(Assessment assessment, double score, String feedback) {
        StudentGrade grade = new StudentGrade(this, assessment, score, feedback);
        grades.add(grade);
        if (getGradeBook() != null) {
            getGradeBook().addGrade(grade);
        }
    }

    public void removeGrade(StudentGrade grade) {
        grades.remove(grade);
        if (course != null) {
            course.getGradeBook().removeGrade(grade);
        }
    }

    public List<StudentGrade> getGrades() {
        return new ArrayList<>(grades);
    }

    public double calculateOverallPerformance() {
        return grades.stream()
                .mapToDouble(grade -> grade.getScore() * grade.getAssessment().getWeight())
                .sum() / grades.stream().mapToDouble(grade -> grade.getAssessment().getWeight()).sum();
    }
}
