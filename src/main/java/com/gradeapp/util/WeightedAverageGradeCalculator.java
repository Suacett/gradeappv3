package com.gradeapp.util;

import com.gradeapp.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class WeightedAverageGradeCalculator {

    // Method to calculate weighted average grade
    public double calculateWeightedAverage(List<? extends Grade> grades, Assessment assessment) {
        double totalWeightedScore = 0;
        double totalWeight = 0;

        for (Grade grade : grades) {
            Assessment gradeAssessment = grade.getAssessment();
            if (gradeAssessment.getWeight() > 0) {
                totalWeightedScore += grade.getScore() * gradeAssessment.getWeight();
                totalWeight += gradeAssessment.getWeight();
            }
        }

        return totalWeight > 0 ? totalWeightedScore / totalWeight : 0;
    }

    // Method to calculate outcome achievement for a student
    public double calculateOutcomeAchievement(Student student, Outcome outcome) {
        List<? extends Grade> relevantGrades = student.getGrades().stream()
                .filter(grade -> grade.getAssessment().getOutcomes().contains(outcome))
                .collect(Collectors.toList());

        double totalScore = 0;
        double totalMaxScore = 0;

        for (Grade grade : relevantGrades) {
            totalScore += grade.getScore();
            totalMaxScore += grade.getAssessment().getMaxScore();
        }

        return totalMaxScore > 0 ? (totalScore / totalMaxScore) * 100 : 0;
    }
}
