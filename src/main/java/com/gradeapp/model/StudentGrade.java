package com.gradeapp.model;

public class StudentGrade extends Grade {

    public StudentGrade(Student student, Assessment assessment, double score, String feedback) {
        super(student, assessment, score, feedback);
    }

    public StudentGrade(String studentName, String assessmentName, double score) {
        super(new Student(studentName, ""), new Assessment(assessmentName, "", 100, 100), score, "");
    }
}
