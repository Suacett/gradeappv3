package com.gradeapp.util;

import com.gradeapp.model.Course;
import com.gradeapp.model.Student;

public interface GradeCalculator {
    double calculateFinalGrade(Student student, Course course);
}

