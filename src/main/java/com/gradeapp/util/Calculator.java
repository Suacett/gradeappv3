package com.gradeapp.util;

import com.gradeapp.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class Calculator {
    private static Calculator instance;

    private Calculator() {
    }

    public static Calculator getInstance() {
        if (instance == null) {
            instance = new Calculator();
        }
        return instance;
    }

    public static Map<String, Double> calculateStatistics(List<? extends Grade> grades) {
        Map<String, Double> stats = new HashMap<>();
        stats.put("mean", calculateMean(grades));
        stats.put("standardDeviation", calculateStandardDeviation(grades));
        stats.put("highest", getHighestGrade(grades).getScore());
        stats.put("lowest", getLowestGrade(grades).getScore());
        stats.put("median", calculateMedian(grades));
        return stats;
    }

    public static double calculateMean(List<? extends Grade> grades) {
        return grades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
    }

    public static double calculateStandardDeviation(List<? extends Grade> grades) {
        double mean = calculateMean(grades);
        double variance = grades.stream()
                .mapToDouble(g -> Math.pow(g.getScore() - mean, 2))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    public static Grade getHighestGrade(List<? extends Grade> grades) {
        return grades.stream().max(Comparator.comparingDouble(Grade::getScore)).orElse(null);
    }

    public static Grade getLowestGrade(List<? extends Grade> grades) {
        return grades.stream().min(Comparator.comparingDouble(Grade::getScore)).orElse(null);
    }

    public static double calculateMedian(List<? extends Grade> grades) {
        List<Double> sortedScores = grades.stream()
                .map(Grade::getScore)
                .sorted()
                .collect(Collectors.toList());
        int size = sortedScores.size();
        if (size % 2 == 0) {
            return (sortedScores.get(size / 2 - 1) + sortedScores.get(size / 2)) / 2.0;
        } else {
            return sortedScores.get(size / 2);
        }
    }

    public static Map<String, Integer> calculateGradeDistribution(List<? extends Grade> grades) {
        return grades.stream()
                .collect(Collectors.groupingBy(
                        g -> getGradeCategory(g.getScore()),
                        Collectors.summingInt(g -> 1)
                ));
    }

    private static String getGradeCategory(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    public static Map<Outcomes, Double> calculateOutcomeAchievement(Student student) {
        Map<Outcomes, Double> achievements = new HashMap<>();
        for (Outcomes outcome : student.getCourse().getOutcomes()) {
            double achievement = calculateOutcomeAchievement(student, outcome);
            achievements.put(outcome, achievement);
        }
        return achievements;
    }

    private static double calculateOutcomeAchievement(Student student, Outcomes outcome) {
        List<? extends Grade> relevantGrades = student.getGrades().stream()
                .filter(g -> g.getAssessment().getOutcomes().contains(outcome))
                .collect(Collectors.toList());
        return calculateWeightedAverage(relevantGrades, null);
    }

    public static Grade normalizeGrade(Grade grade, double classAverage, double desiredAverage) {
        double normalizedScore = grade.getScore() + (desiredAverage - classAverage);
        return new Grade(grade.getStudent(), grade.getAssessment(), normalizedScore, grade.getFeedback());
    }

    public static double calculateTaskCompletionRate(Assessment assessment) {
        List<Task> tasks = assessment.getTasks();
        long completedTasks = tasks.stream().filter(Task::isCompleted).count();
        return (double) completedTasks / tasks.size();
    }

    public static Map<Task, Double> calculateTaskCompletionRates(Assessment assessment) {
        return assessment.getTasks().stream()
                .collect(Collectors.toMap(
                        task -> task,
                        task -> task.isCompleted() ? 1.0 : 0.0
                ));
    }

    public static double calculateWeightedAverage(List<? extends Grade> grades, Assessment assessment) {
        return grades.stream()
                .filter(g -> g.getAssessment().equals(assessment))
                .mapToDouble(g -> g.getScore() * g.getAssessment().getWeight())
                .sum() / assessment.getWeight();
    }

    public static double calculateOverallGrade(Student student) {
        List<? extends Grade> grades = student.getGrades();
        return grades.stream()
                .mapToDouble(g -> g.getScore() * g.getAssessment().getWeight())
                .sum() / grades.stream().mapToDouble(g -> g.getAssessment().getWeight()).sum();
    }

    public static Map<Assessment, Double> calculateGradesByAssessment(Student student) {
        Map<Assessment, Double> gradesByAssessment = new HashMap<>();
        for (Assessment assessment : student.getCourse().getAssessments()) {
            List<? extends Grade> relevantGrades = student.getGrades().stream()
                    .filter(g -> g.getAssessment().equals(assessment))
                    .collect(Collectors.toList());
            double weightedAverage = calculateWeightedAverage(relevantGrades, assessment);
            gradesByAssessment.put(assessment, weightedAverage);
        }
        return gradesByAssessment;
    }
}
