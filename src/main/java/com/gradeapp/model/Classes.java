package com.gradeapp.model;

import java.util.*;

public class Classes {
    private String name;
    private String classId;
    private List<Classes> classes;
    private List<Assessment> assessments;
    private Set<Outcome> outcomes;
    private Set<Student> students;

    public Classes(String name, String classId) {
        this.name = name;
        this.classId = classId;
        this.classes = new ArrayList<>();
        this.assessments = new ArrayList<>();
        this.outcomes = new HashSet<>();
        this.students = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getClassId() {
        return classId;
    }

    public List<Classes> getClasses() {
        return classes;
    }

    public List<Assessment> getAssessments() {
        return Collections.unmodifiableList(assessments);
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    public Set<Outcome> getOutcomes() {
        return Collections.unmodifiableSet(outcomes);
    }

    public void addOutcome(Outcome outcome) {
        outcomes.add(outcome);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public Set<Student> getStudents() {
        return Collections.unmodifiableSet(students);
    }

}
