package com.gradeapp.controller;

import com.gradeapp.model.Assessment;

/**
 * Interface for callback functionality when a new assessment is created.
 */
public interface AssessmentCreationCallback {

    /**
     * Invoked when a new assessment has been successfully created.
     *
     * @param newAssessment The newly created Assessment object.
     */
    void onAssessmentCreated(Assessment newAssessment);
}
