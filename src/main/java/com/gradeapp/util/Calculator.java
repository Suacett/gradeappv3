package com.gradeapp.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Outcome;
import com.gradeapp.model.Student;

/**
 * Provides various statistical and calculation methods for grades, assessments, and student performance.
 */
public class Calculator {

    /**
     * Constructs a Calculator instance.
     */
    public Calculator() {
    }

    // ----------------------------- Statistical Calculations -----------------------------

    /**
     * Calculates various statistical metrics for a list of grades.
     *
     * @param grades The list of grades to analyze.
     * @return A map containing statistical metrics such as mean, standard deviation, highest grade, lowest grade, and median.
     */
    public Map<String, Double> calculateStatistics(List<? extends Grade> grades) {
        Map<String, Double> stats = new HashMap<>();
        stats.put("mean", calculateMean(grades));
        stats.put("standardDeviation", calculateStandardDeviation(grades));
        stats.put("highest", getHighestGrade(grades).getScore());
        stats.put("lowest", getLowestGrade(grades).getScore());
        stats.put("median", calculateMedian(grades));
        return stats;
    }

    /**
     * Calculates the mean (average) score from a list of grades.
     *
     * @param grades The list of grades.
     * @return The mean score.
     */
    public double calculateMean(List<? extends Grade> grades) {
        return grades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
    }

    /**
     * Calculates the standard deviation of scores from a list of grades.
     *
     * @param grades The list of grades.
     * @return The standard deviation.
     */
    public double calculateStandardDeviation(List<? extends Grade> grades) {
        double mean = calculateMean(grades);
        double variance = grades.stream()
                .mapToDouble(g -> Math.pow(g.getScore() - mean, 2))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    /**
     * Retrieves the grade with the highest score from a list of grades.
     *
     * @param grades The list of grades.
     * @return The grade with the highest score, or null if the list is empty.
     */
    public Grade getHighestGrade(List<? extends Grade> grades) {
        return grades.stream().max(Comparator.comparingDouble(Grade::getScore)).orElse(null);
    }

    /**
     * Retrieves the grade with the lowest score from a list of grades.
     *
     * @param grades The list of grades.
     * @return The grade with the lowest score, or null if the list is empty.
     */
    public Grade getLowestGrade(List<? extends Grade> grades) {
        return grades.stream().min(Comparator.comparingDouble(Grade::getScore)).orElse(null);
    }

    /**
     * Calculates the median score from a list of grades.
     *
     * @param grades The list of grades.
     * @return The median score.
     */
    public double calculateMedian(List<? extends Grade> grades) {
        List<Double> sortedScores = grades.stream()
                .map(Grade::getScore)
                .sorted()
                .collect(Collectors.toList());
        int size = sortedScores.size();
        if (size == 0) {
            return 0.0;
        }
        if (size % 2 == 0) {
            return (sortedScores.get(size / 2 - 1) + sortedScores.get(size / 2)) / 2.0;
        } else {
            return sortedScores.get(size / 2);
        }
    }

    /**
     * Calculates the distribution of grades into categories (A, B, C, D, F).
     *
     * @param grades The list of grades.
     * @return A map where keys are grade categories and values are the counts.
     */
    public Map<String, Integer> calculateGradeDistribution(List<? extends Grade> grades) {
        return grades.stream()
                .collect(Collectors.groupingBy(
                        g -> getGradeCategory(g.getScore()),
                        Collectors.summingInt(g -> 1)));
    }

    /**
     * Determines the grade category based on the score.
     *
     * @param score The score to categorize.
     * @return The grade category as a string (A, B, C, D, F).
     */
    private String getGradeCategory(double score) {
        if (score >= 90)
            return "A";
        if (score >= 80)
            return "B";
        if (score >= 70)
            return "C";
        if (score >= 60)
            return "D";
        return "F";
    }

    // ----------------------------- Outcome Achievement -----------------------------

    /**
     * Calculates the achievement percentage for each outcome for a given student.
     *
     * @param student The student whose outcome achievements are to be calculated.
     * @return A map of Outcomes to their achievement percentages.
     */
    public Map<Outcome, Double> calculateOutcomeAchievement(Student student) {
        Map<Outcome, Double> achievements = new HashMap<>();
        for (Outcome outcome : student.getCourse().getOutcomes()) {
            double achievement = calculateOutcomeAchievement(student, outcome);
            achievements.put(outcome, achievement);
        }
        return achievements;
    }

    /**
     * Calculates the achievement percentage for a specific outcome for a given student.
     *
     * @param student The student.
     * @param outcome The outcome.
     * @return The achievement percentage.
     */
    private double calculateOutcomeAchievement(Student student, Outcome outcome) {
        List<Grade> relevantGrades = student.getGrades().stream()
                .filter(g -> g.getAssessment().getOutcomes().containsKey(outcome))
                .collect(Collectors.toList());
        return calculateWeightedAverage(relevantGrades, null);
    }

    // ----------------------------- Grade Normalization -----------------------------

    /**
     * Normalizes a grade by adjusting it based on class average and desired average.
     *
     * @param grade          The grade to normalize.
     * @param classAverage   The current class average.
     * @param desiredAverage The desired average.
     * @return A new Grade object with the normalized score.
     */
    public Grade normaliseGrade(Grade grade, double classAverage, double desiredAverage) {
        double normalisedScore = grade.getScore() + (desiredAverage - classAverage);
        return new Grade(grade.getStudent(), grade.getAssessment(), normalisedScore, grade.getFeedback());
    }

    // ----------------------------- Task Completion Rate -----------------------------

    /**
     * Calculates the completion rate of tasks (assessment parts) for a given assessment.
     *
     * @param assessment The assessment to evaluate.
     * @return The completion rate as a double between 0.0 and 1.0.
     */
    public double calculateTaskCompletionRate(Assessment assessment) {
        List<AssessmentPart> parts = assessment.getParts();
        if (parts.isEmpty()) {
            return 0.0;
        }
        long completedParts = parts.stream().filter(AssessmentPart::isCompleted).count();
        return (double) completedParts / parts.size();
    }

    // ----------------------------- Weighted Average Calculation -----------------------------

    /**
     * Calculates the weighted average score for a list of grades and an optional assessment.
     *
     * @param grades      The list of grades.
     * @param assessment  The assessment to consider for weighting (nullable).
     * @return The weighted average score.
     */
    public double calculateWeightedAverage(List<? extends Grade> grades, Assessment assessment) {
        return grades.stream()
                .filter(g -> assessment == null || g.getAssessment().equals(assessment))
                .mapToDouble(g -> g.getScore() * g.getAssessment().getWeight())
                .sum() / (assessment != null ? assessment.getWeight() : grades.stream()
                                                                            .mapToDouble(g -> g.getAssessment().getWeight())
                                                                            .sum());
    }

    // ----------------------------- Overall Grade Calculation -----------------------------

    /**
     * Calculates the overall grade for a student based on all their grades and assessment weights.
     *
     * @param student The student whose overall grade is to be calculated.
     * @return The overall grade as a double.
     */
    public double calculateOverallGrade(Student student) {
        List<? extends Grade> grades = student.getGrades();
        return grades.stream()
                .mapToDouble(g -> g.getScore() * g.getAssessment().getWeight())
                .sum() / grades.stream().mapToDouble(g -> g.getAssessment().getWeight()).sum();
    }

    // ----------------------------- Grades by Assessment -----------------------------

    /**
     * Calculates the weighted average grades for each assessment for a given student.
     *
     * @param student The student whose grades are to be calculated.
     * @return A map of Assessments to their weighted average scores.
     */
    public Map<Assessment, Double> calculateGradesByAssessment(Student student) {
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
