package com.gradeapp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;

public class DataValidator {
    public static boolean isValidWeight(double weight) {
        return weight >= 0 && weight <= 100;
    }

    public static boolean isValidScore(double score, double maxScore) {
        return score >= 0 && score <= maxScore;
    }

    public static boolean isValidAssessment(Assessment assessment) {
        double totalWeight = assessment.getParts().stream().mapToDouble(AssessmentPart::getWeight).sum();
        return Math.abs(totalWeight - 100) < 0.001; // Allow for floating-point imprecision
    }

    public static List<String> validateImportData(List<Student> students) {
        List<String> errors = new ArrayList<>();
        for (Student student : students) {
            if (student.getName() == null || student.getName().isEmpty()) {
                errors.add("Invalid name for student ID: " + student.getStudentId());
            }

        }
        return errors;
    }

    public static List<String> validateExportData(List<Grade> grades) {
        List<String> errors = new ArrayList<>();
        for (Grade grade : grades) {
            if (!isValidScore(grade.getScore(), grade.getAssessment().getMaxScore())) {
                errors.add("Invalid score for student: " + grade.getStudent().getName() +
                        ", assessment: " + grade.getAssessment().getName());
            }

        }
        return errors;
    }

    public static boolean isValidFileFormat(String fileName, String... allowedExtensions) {
        String lowercaseFileName = fileName.toLowerCase();
        return Arrays.stream(allowedExtensions)
                .anyMatch(ext -> lowercaseFileName.endsWith(ext));
    }

    public static void ensureDataIntegrity(Object data) throws IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

    }

    public static String generateErrorReport(List<String> errors) {
        StringBuilder report = new StringBuilder("Validation Errors:\n");
        for (int i = 0; i < errors.size(); i++) {
            report.append(i + 1).append(". ").append(errors.get(i)).append("\n");
        }
        return report.toString();
    }
}
