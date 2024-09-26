package com.gradeapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gradeapp.database.Database;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Outcome;
import com.gradeapp.model.Student;


/**
 * Controller class for handling grading functionalities.
 * Includes methods for calculating grades, managing grades, and retrieving
 * student performance.
 */
public class GradingController {

    private Database db; // Database instance for data operations

    /**
     * Constructor initializes the grade calculator and database instances.
     */
    public GradingController() {

        this.db = new Database();
    }





    // ----------------------------- Grade Management Methods ---------------------

    /**
     * Removes a grade from a student's record.
     *
     * @param student The student from whom the grade is to be removed.
     * @param grade   The grade to be removed.
     */
    public void removeGrade(Student student, Grade grade) {
        student.removeGrade(grade);
    }

    /**
     * Retrieves a specific grade for a student and assessment.
     *
     * @param student    The student whose grade is to be retrieved.
     * @param assessment The assessment for which the grade is retrieved.
     * @return The grade if found, otherwise null.
     */
    public Grade getGrade(Student student, Assessment assessment) {
        return student.getGrades().stream()
                .filter(grade -> grade.getAssessment().equals(assessment))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all grades for a specific student.
     *
     * @param student The student whose grades are to be retrieved.
     * @return A list of grades.
     */
    public List<Grade> getStudentGrades(Student student) {
        return db.getGradesForStudent(student.getStudentId());
    }

    /**
     * Calculates the average grade for a student.
     *
     * @param student The student whose average grade is to be calculated.
     * @return The average grade.
     */
    public double getAverageGradeForStudent(Student student) {
        return student.calculateOverallPerformance();
    }

    /**
     * Retrieves the average grades for all students in a course.
     *
     * @param course The course for which student averages are to be retrieved.
     * @return A map of students to their average grades.
     */
    public Map<Student, Double> getAllStudentAverages(Course course) {
        Map<Student, Double> averages = new HashMap<>();
        for (Student student : course.getStudents()) {
            averages.put(student, student.calculateOverallPerformance());
        }
        return averages;
    }

    /**
     * Adds a grade for a student in a specific assessment and part.
     *
     * @param student    The student to whom the grade is added.
     * @param assessment The assessment for which the grade is added.
     * @param part       The part of the assessment (can be null).
     * @param score      The score obtained.
     * @param feedback   Feedback related to the grade.
     * @return The added grade.
     */
    public Grade addGrade(Student student, Assessment assessment, AssessmentPart part, double score, String feedback) {
        Grade grade = new Grade(student, assessment, part, score, feedback);
        student.addGrade(grade);
        db.saveGrade(grade);
        System.out.println("Grade added: " + grade);
        return grade;
    }

    /**
     * Overloaded method to add a grade without specifying an assessment part.
     *
     * @param student    The student to whom the grade is added.
     * @param assessment The assessment for which the grade is added.
     * @param score      The score obtained.
     * @param feedback   Feedback related to the grade.
     * @return The added grade.
     */
    public Grade addGrade(Student student, Assessment assessment, double score, String feedback) {
        return addGrade(student, assessment, null, score, feedback);
    }

    /**
     * Calculates the grade for a student in a specific assessment.
     *
     * @param student    The student whose grade is calculated.
     * @param assessment The assessment for which the grade is calculated.
     * @return The calculated assessment grade.
     */
    public double calculateAssessmentGrade(Student student, Assessment assessment) {
        Map<AssessmentPart, Double> partScores = new HashMap<>();
        for (AssessmentPart part : assessment.getParts()) {
            Grade partGrade = getGrade(student, part);
            if (partGrade != null) {
                partScores.put(part, partGrade.getScore());
            }
        }
        return assessment.calculateGrade(partScores);
    }

    /**
     * Calculates the grades for each outcome for a student in a specific
     * assessment.
     *
     * @param student    The student whose outcome grades are calculated.
     * @param assessment The assessment for which the outcome grades are calculated.
     * @return A map of outcomes to their corresponding grades.
     */
    public Map<Outcome, Double> calculateOutcomeGrades(Student student, Assessment assessment) {
        Map<Outcome, Double> outcomeGrades = new HashMap<>();
        Map<AssessmentPart, Double> partScores = new HashMap<>();

        for (AssessmentPart part : assessment.getParts()) {
            Grade partGrade = getGrade(student, part);
            if (partGrade != null) {
                partScores.put(part, partGrade.getScore());
            }
        }

        for (Outcome outcome : assessment.getOutcomeWeights().keySet()) {
            double outcomeGrade = assessment.calculateOutcomeGrade(outcome, partScores);
            outcomeGrades.put(outcome, outcomeGrade);
        }

        return outcomeGrades;
    }

    // ----------------------------- Private Helper Methods -------------------

    /**
     * Retrieves a grade for a student in a specific assessment part.
     *
     * @param student The student whose grade is to be retrieved.
     * @param part    The assessment part for which the grade is retrieved.
     * @return The grade if found, otherwise null.
     */
    private Grade getGrade(Student student, AssessmentPart part) {
        return student.getGrades().stream()
                .filter(grade -> grade.getAssessment().getParts().contains(part))
                .findFirst()
                .orElse(null);
    }
}
