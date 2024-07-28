package com.gradeapp.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeBook {
    private Map<Student, Map<Assessment, Grade>> grades;

    public GradeBook() {
        this.grades = new HashMap<>();
    }

    public void addGrade(Student student, Assessment assessment, double score) {
        grades.computeIfAbsent(student, k -> new HashMap<>())
                .put(assessment, new Grade(student, assessment, score));
    }

    public Grade getGrade(Student student, Assessment assessment) {
        return grades.getOrDefault(student, Collections.emptyMap()).get(assessment);
    }

    public Map<Assessment, Grade> getStudentGrades(Student student) {
        return grades.getOrDefault(student, Collections.emptyMap());
    }

    public List<Grade> getAllGrades() {
        return grades.values().stream()
                .flatMap(m -> m.values().stream())
                .toList();
    }
}
