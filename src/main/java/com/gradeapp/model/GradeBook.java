package com.gradeapp.model;

import java.util.*;
import java.util.stream.Collectors;

public class GradeBook {
    private Map<Student, Map<Assessment, Grade>> grades;

    public GradeBook() {
        this.grades = new HashMap<>();
    }

    // Methods for adding and removing grades
    public void addGrade(Grade grade) {
        grades.computeIfAbsent(grade.getStudent(), k -> new HashMap<>())
                .put(grade.getAssessment(), grade);
    }

    public void removeGrade(Grade grade) {
        Map<Assessment, Grade> studentGrades = grades.get(grade.getStudent());
        if (studentGrades != null) {
            studentGrades.remove(grade.getAssessment());
        }
    }

    // Methods for retrieving grades
    public Grade getGrade(Student student, Assessment assessment) {
        return grades.getOrDefault(student, Collections.emptyMap()).get(assessment);
    }

    public Map<Assessment, Grade> getStudentGrades(Student student) {
        return new HashMap<>(grades.getOrDefault(student, Collections.emptyMap()));
    }

    public List<Grade> getAllGrades() {
        return grades.values().stream()
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toList());
    }

    public List<Grade> getGradesForAssessment(Assessment assessment) {
        return grades.values().stream()
                .map(m -> m.get(assessment))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Methods for calculating averages
    public double getAverageGradeForStudent(Student student) {
        Map<Assessment, Grade> studentGrades = grades.get(student);
        if (studentGrades == null || studentGrades.isEmpty()) {
            return 0.0;
        }
        return studentGrades.values().stream()
                .mapToDouble(Grade::getScore)
                .average()
                .orElse(0.0);
    }

    public Map<Student, Double> getAllStudentAverages() {
        return grades.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().values().stream()
                                .mapToDouble(Grade::getScore)
                                .average()
                                .orElse(0.0)
                ));
    }
}
