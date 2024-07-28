package com.gradeapp.util;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Outcomes;
import com.gradeapp.model.Student;

import java.util.List;

public class WeightedAverageGradeCalculator {

    public double calculateWeightedAverage(List<Grade> grades, Assessment assessment) {
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

    public double calculateOutcomeAchievement(Student student, Outcomes outcome) {
        List<Grade> relevantGrades = student.getGrades().stream()
                .filter(grade -> grade.getAssessment().getOutcomes().contains(outcome))
                .toList();

        double totalScore = 0;
        double totalMaxScore = 0;

        for (Grade grade : relevantGrades) {
            totalScore += grade.getScore();
            totalMaxScore += grade.getAssessment().getMaxScore();
        }

        return totalMaxScore > 0 ? (totalScore / totalMaxScore) * 100 : 0;
    }
}
