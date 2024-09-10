package com.gradeapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Classes {
    private final StringProperty name;
    private final StringProperty classId;
    private List<Classes> classes;
    private List<Assessment> assessments;
    private Set<Outcome> outcomes;
    private Set<Student> students;

    public Classes(String name, String classId) {
        this.name = new SimpleStringProperty(name);
        this.classId = new SimpleStringProperty(classId);
        this.classes = new ArrayList<>();
        this.assessments = new ArrayList<>();
        this.outcomes = new HashSet<>();
        this.students = new HashSet<>();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getClassId() {
        return classId.get();
    }

    public StringProperty classIdProperty() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId.set(classId);
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
