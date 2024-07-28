package com.gradeapp.util;

import com.gradeapp.model.Grade;

import java.util.Comparator;
import java.util.List;

/**
 * Provides utility methods for complex grade and statistical calculations.
 */
public class Calculator {
    public static double calculateMean(List<Grade> grades) {
        return grades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
    }

    public static double calculateStandardDeviation(List<Grade> grades) {
        double mean = calculateMean(grades);
        double variance = grades.stream()
                .mapToDouble(g -> Math.pow(g.getScore() - mean, 2))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    public static Grade getHighestGrade(List<Grade> grades) {
        return grades.stream().max(Comparator.comparingDouble(Grade::getScore)).orElse(null);
    }

    public static Grade getLowestGrade(List<Grade> grades) {
        return grades.stream().min(Comparator.comparingDouble(Grade::getScore)).orElse(null);
    }
}