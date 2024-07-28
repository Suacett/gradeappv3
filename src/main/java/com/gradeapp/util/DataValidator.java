package com.gradeapp.util;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.Task;

/**
 * Offers methods to validate input data for consistency and correctness.
 */
public class DataValidator {
    public static boolean isValidWeight(double weight) {
        return weight >= 0 && weight <= 100;
    }

    public static boolean isValidScore(double score, double maxScore) {
        return score >= 0 && score <= maxScore;
    }

    public static boolean isValidAssessment(Assessment assessment) {
        double totalWeight = assessment.getTasks().stream().mapToDouble(Task::getWeight).sum();
        return Math.abs(totalWeight - 100) < 0.001; // Allow for floating-point imprecision
    }
}
