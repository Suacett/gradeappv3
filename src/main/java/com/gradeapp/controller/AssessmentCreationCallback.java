package com.gradeapp.controller;

import com.gradeapp.model.Assessment;

public interface AssessmentCreationCallback {
    void onAssessmentCreated(Assessment newAssessment);
}
