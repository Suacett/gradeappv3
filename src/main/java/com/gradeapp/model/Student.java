package com.gradeapp.model;

import java.util.*;

public class Student {
    private String name;
    private String studentId;
    private Course course;
    private List<Grade> grades;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
        this.grades = new ArrayList<>();
    }

    // Methods for managing grades
    public void addGrade(Assessment assessment, AssessmentPart part, double score, String feedback) {
        Grade grade = new Grade(this, assessment, part, score, feedback);
        addGrade(grade);
    }

    public void addGrade(Assessment assessment, double score, String feedback) {
        addGrade(assessment, null, score, feedback);
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
        if (course != null && course.getGradeBook() != null) {
            course.getGradeBook().addGrade(grade);
        }
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public void removeGrade(Grade grade) {
        grades.remove(grade);
        if (course != null && course.getGradeBook() != null) {
            course.getGradeBook().removeGrade(grade);
        }
    }

    public List<Grade> getGrades() {
        return new ArrayList<>(grades);
    }


    // Methods for calculating performance
    public double calculateOverallPerformance() {
        double totalWeightedScore = grades.stream()
                .mapToDouble(grade -> grade.getScore() * grade.getAssessment().getWeight())
                .sum();
        double totalWeight = grades.stream()
                .mapToDouble(grade -> grade.getAssessment().getWeight())
                .sum();
        return totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
    }

    // Getters and Setters
    public String getName() {
        return name;
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
}
