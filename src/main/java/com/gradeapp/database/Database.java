package com.gradeapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
// Importing necessary Java SQL and utility classes
import java.util.ArrayList;
import java.util.List;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.AssessmentPart;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Outcome;
import com.gradeapp.model.Student;

/**
 * Database class responsible for managing all database operations.
 * It handles the connection, initialization, and CRUD operations for various
 * entities.
 */
public class Database {
    private static final String URL = "jdbc:sqlite:com.gradeapp.db"; // SQLite database URL
    private static boolean isInitialized = false; // Flag to ensure the database is initialized only once

    /**
     * Constructor that initializes the database if it hasn't been initialized yet.
     */
    public Database() {
        if (!isInitialized) {
            initialiseDatabase();
            isInitialized = true;
        }
    }

    /**
     * Initializes the database by creating all the necessary tables.
     * Also handles any schema migrations or updates required.
     */
    public void initialiseDatabase() {
        // SQL statements to create tables if they do not exist
        String createCoursesTable = "CREATE TABLE IF NOT EXISTS courses ("
                + "id TEXT PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "description TEXT NOT NULL"
                + ");";
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS students ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "studentId TEXT NOT NULL UNIQUE"
                + ");";
        String createAssessmentPartOutcomesTable = "CREATE TABLE IF NOT EXISTS assessment_part_outcomes ("
                + "part_id INTEGER,"
                + "outcome_id TEXT,"
                + "weight REAL NOT NULL,"
                + "PRIMARY KEY (part_id, outcome_id),"
                + "FOREIGN KEY (part_id) REFERENCES assessment_parts(id),"
                + "FOREIGN KEY (outcome_id) REFERENCES outcomes(id)"
                + ");";
        String createGradesTable = "CREATE TABLE IF NOT EXISTS grades ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "student_id TEXT NOT NULL,"
                + "assessment_id INTEGER NOT NULL,"
                + "part_id INTEGER,"
                + "score REAL NOT NULL,"
                + "feedback TEXT,"
                + "UNIQUE(student_id, assessment_id, part_id),"
                + "FOREIGN KEY (student_id) REFERENCES students(studentId),"
                + "FOREIGN KEY (assessment_id) REFERENCES assessments(id),"
                + "FOREIGN KEY (part_id) REFERENCES assessment_parts(id)"
                + ");";
        String createClassesTable = "CREATE TABLE IF NOT EXISTS classes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "classId TEXT NOT NULL,"
                + "course_id TEXT,"
                + "FOREIGN KEY (course_id) REFERENCES courses(id)"
                + ");";
        String createAssessmentsTable = "CREATE TABLE IF NOT EXISTS assessments ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "course_id TEXT NOT NULL,"
                + "name TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "weight REAL NOT NULL,"
                + "maxScore REAL NOT NULL,"
                + "FOREIGN KEY (course_id) REFERENCES courses(id)"
                + ");";
        String createOutcomesTable = "CREATE TABLE IF NOT EXISTS outcomes ("
                + "id TEXT,"
                + "course_id TEXT,"
                + "name TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "weight REAL NOT NULL,"
                + "PRIMARY KEY (id, course_id),"
                + "FOREIGN KEY (course_id) REFERENCES courses(id)"
                + ");";
        String createAssessmentOutcomesTable = "CREATE TABLE IF NOT EXISTS assessment_outcomes ("
                + "assessment_id INTEGER,"
                + "outcome_id TEXT,"
                + "weight REAL NOT NULL,"
                + "PRIMARY KEY (assessment_id, outcome_id),"
                + "FOREIGN KEY (assessment_id) REFERENCES assessments(id),"
                + "FOREIGN KEY (outcome_id) REFERENCES outcomes(id)"
                + ");";
        String createAssessmentPartsTable = "CREATE TABLE IF NOT EXISTS assessment_parts ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "assessment_id INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "weight REAL NOT NULL,"
                + "max_score REAL NOT NULL,"
                + "FOREIGN KEY (assessment_id) REFERENCES assessments(id)"
                + ");";
        String createClassStudentsTable = "CREATE TABLE IF NOT EXISTS class_students ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "class_id INTEGER,"
                + "student_id INTEGER,"
                + "FOREIGN KEY (class_id) REFERENCES classes(id),"
                + "FOREIGN KEY (student_id) REFERENCES students(id)"
                + ");";
        String createStudentClassesTable = "CREATE TABLE IF NOT EXISTS student_classes ("
                + "student_id TEXT NOT NULL,"
                + "class_id TEXT NOT NULL,"
                + "PRIMARY KEY (student_id, class_id),"
                + "FOREIGN KEY (student_id) REFERENCES students(studentId),"
                + "FOREIGN KEY (class_id) REFERENCES classes(classId)"
                + ");";
        String createStudentMarkBookTable = "CREATE TABLE IF NOT EXISTS student_markbook ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "student_id TEXT NOT NULL,"
                + "assessment_id INTEGER NOT NULL,"
                + "part_id INTEGER,"
                + "outcome_id TEXT NOT NULL,"
                + "weight REAL NOT NULL,"
                + "score REAL NOT NULL,"
                + "percentage REAL NOT NULL,"
                + "FOREIGN KEY (student_id) REFERENCES students(studentId),"
                + "FOREIGN KEY (assessment_id) REFERENCES assessments(id),"
                + "FOREIGN KEY (part_id) REFERENCES assessment_parts(id)"
                + ");";

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
            // Executing SQL statements to create tables
            stmt.execute(createCoursesTable);
            stmt.execute(createStudentsTable);
            stmt.execute(createClassesTable);
            stmt.execute(createAssessmentsTable);
            stmt.execute(createOutcomesTable);
            stmt.execute(createAssessmentOutcomesTable);
            stmt.execute(createAssessmentPartsTable);
            stmt.execute(createClassStudentsTable);
            stmt.execute(createGradesTable);
            stmt.execute(createStudentClassesTable);
            stmt.execute(createAssessmentPartOutcomesTable);
            stmt.execute(createStudentMarkBookTable);
            createStudentClassTable(); // Ensures the student_classes table exists

            System.out.println("Database and tables initialized.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }

    }

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return Connection object if successful, null otherwise.
     */
    private Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("SQLite connection established.");
            return conn;
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            return null;
        }

    }

    // ----------------------------- STUDENT MARKBOOK -----------------------------

    /**
     * Saves or updates a student's markbook entry in the database.
     *
     * @param studentId    The ID of the student.
     * @param assessmentId The ID of the assessment.
     * @param partId       The ID of the assessment part.
     * @param outcomeId    The ID of the outcome.
     * @param weight       The weight of the outcome.
     * @param score        The score achieved.
     * @param percentage   The percentage score.
     */
    public void saveStudentMarkBook(String studentId, int assessmentId, int partId, String outcomeId, double weight,
            double score, double percentage) {
        String sql = "INSERT OR REPLACE INTO student_markbook (student_id, assessment_id, part_id, outcome_id, weight, score, percentage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setInt(2, assessmentId);
            pstmt.setInt(3, partId);
            pstmt.setString(4, outcomeId);
            pstmt.setDouble(5, weight);
            pstmt.setDouble(6, score);
            pstmt.setDouble(7, percentage);
            pstmt.executeUpdate();
            System.out.println("Student markbook saved successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving student markbook: " + e.getMessage());
        }
    }

    // ----------------------------- ASSESSMENT PARTS -----------------------------

    /**
     * Links an outcome to an assessment with a specified weight.
     *
     * @param assessmentId The ID of the assessment.
     * @param outcomeId    The ID of the outcome.
     * @param weight       The weight of the outcome in the assessment.
     */
    public void linkOutcomeToAssessment(int assessmentId, String outcomeId, double weight) {
        String sql = "INSERT OR REPLACE INTO assessment_outcomes (assessment_id, outcome_id, weight) VALUES (?, ?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            pstmt.setString(2, outcomeId);
            pstmt.setDouble(3, weight);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Linked outcome " + outcomeId + " to assessment " + assessmentId + " with weight "
                    + weight + ". Affected rows: " + affectedRows);
        } catch (SQLException e) {
            System.out.println("Error linking outcome to assessment: " + e.getMessage());
        }
    }

    /**
     * Links an outcome to an assessment part with a specified weight.
     *
     * @param partId    The ID of the assessment part.
     * @param outcomeId The ID of the outcome.
     * @param weight    The weight of the outcome in the assessment part.
     */
    public void linkOutcomeToAssessmentPart(int partId, String outcomeId, double weight) {
        String sql = "INSERT OR REPLACE INTO assessment_part_outcomes (part_id, outcome_id, weight) VALUES (?, ?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, partId);
            pstmt.setString(2, outcomeId);
            pstmt.setDouble(3, weight);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Linked outcome " + outcomeId + " to assessment part " + partId + " with weight "
                    + weight + ". Affected rows: " + affectedRows);
        } catch (SQLException e) {
            System.out.println("Error linking outcome to assessment part: " + e.getMessage());
        }
    }

    /**
     * Retrieves all outcomes linked to a specific assessment.
     *
     * @param assessmentId The ID of the assessment.
     * @return A list of linked Outcome objects.
     */
    public List<Outcome> getLinkedOutcomesForAssessment(int assessmentId) {
        List<Outcome> outcomes = new ArrayList<>();
        String sql = "SELECT o.*, ao.weight FROM outcomes o " +
                "JOIN assessment_outcomes ao ON o.id = ao.outcome_id " +
                "WHERE ao.assessment_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double weight = rs.getDouble("weight");
                Outcome outcome = new Outcome(id, name, description, weight);
                outcomes.add(outcome);
                System.out.println("Retrieved linked outcome for assessment " + assessmentId + ": " + id
                        + " with weight " + weight);
            }
        } catch (SQLException e) {
            System.out.println("Error getting linked outcomes for assessment: " + e.getMessage());
        }
        return outcomes;
    }

    /**
     * Retrieves the weight of an outcome for a specific assessment part.
     *
     * @param partId    The ID of the assessment part.
     * @param outcomeId The ID of the outcome.
     * @return The weight of the outcome in the assessment part.
     */
    public double getOutcomeWeightForPart(int partId, String outcomeId) {
        String sql = "SELECT weight FROM assessment_part_outcomes WHERE part_id = ? AND outcome_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, partId);
            pstmt.setString(2, outcomeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("weight");
            }
        } catch (SQLException e) {
            System.out.println("Error getting outcome weight for part: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Retrieves the weight of an outcome for a specific assessment.
     *
     * @param assessmentId The ID of the assessment.
     * @param outcomeId    The ID of the outcome.
     * @return The weight of the outcome in the assessment.
     */
    public double getOutcomeWeightForAssessment(int assessmentId, String outcomeId) {
        String sql = "SELECT weight FROM assessment_outcomes WHERE assessment_id = ? AND outcome_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            pstmt.setString(2, outcomeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("weight");
            }
        } catch (SQLException e) {
            System.out.println("Error getting outcome weight for assessment: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Retrieves all outcomes linked to a specific assessment part.
     *
     * @param partId The ID of the assessment part.
     * @return A list of linked Outcome objects.
     */
    public List<Outcome> getLinkedOutcomesForPart(int partId) {
        List<Outcome> outcomes = new ArrayList<>();
        String sql = "SELECT o.*, apo.weight FROM outcomes o " +
                "JOIN assessment_part_outcomes apo ON o.id = apo.outcome_id " +
                "WHERE apo.part_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, partId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double weight = rs.getDouble("weight");
                Outcome outcome = new Outcome(id, name, description, weight);
                outcomes.add(outcome);
                System.out
                        .println("Retrieved linked outcome for part " + partId + ": " + id + " with weight " + weight);
            }
        } catch (SQLException e) {
            System.out.println("Error getting linked outcomes for part: " + e.getMessage());
        }
        return outcomes;
    }

    /**
     * Adds a new assessment to the database.
     *
     * @param assessment The Assessment object to add.
     * @param courseId   The ID of the course the assessment belongs to.
     * @return The generated ID of the new assessment, or -1 if insertion failed.
     */
    public int addAssessment(Assessment assessment, String courseId) {
        String sql = "INSERT INTO assessments(course_id, name, description, weight, maxScore) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, courseId);
            pstmt.setString(2, assessment.getName());
            pstmt.setString(3, assessment.getDescription());
            pstmt.setDouble(4, assessment.getWeight());
            pstmt.setDouble(5, assessment.getMaxScore());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        assessment.setId(id);
                        System.out.println("Assessment added successfully with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding assessment: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Adds a new assessment part to the database.
     *
     * @param part         The AssessmentPart object to add.
     * @param assessmentId The ID of the parent assessment.
     * @return The generated ID of the new assessment part, or -1 if insertion
     *         failed.
     */
    public int addAssessmentPart(AssessmentPart part, int assessmentId) {
        String sql = "INSERT INTO assessment_parts (assessment_id, name, weight, max_score) VALUES (?, ?, ?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            pstmt.setString(2, part.getName());
            pstmt.setDouble(3, part.getWeight());
            pstmt.setDouble(4, part.getMaxScore());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding assessment part: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves all parts associated with a specific assessment.
     *
     * @param assessmentId The ID of the assessment.
     * @return A list of AssessmentPart objects.
     */
    public List<AssessmentPart> getAssessmentParts(int assessmentId) {
        List<AssessmentPart> parts = new ArrayList<>();
        String sql = "SELECT * FROM assessment_parts WHERE assessment_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double weight = rs.getDouble("weight");
                double maxScore = rs.getDouble("max_score");
                AssessmentPart part = new AssessmentPart(id, name, weight, maxScore);
                parts.add(part);
                System.out.println("Retrieved part: " + name + " (ID: " + id + ") for assessment ID: " + assessmentId);
            }
        } catch (SQLException e) {
            System.out.println("Error getting assessment parts: " + e.getMessage());
            e.printStackTrace();
        }
        return parts;
    }

    /**
     * Verifies and prints out all entries in the assessment_parts table.
     * Useful for debugging purposes.
     */
    public void verifyAssessmentParts() {
        String sql = "SELECT * FROM assessment_parts";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Contents of assessment_parts table:");
            while (rs.next()) {
                int id = rs.getInt("id");
                int assessmentId = rs.getInt("assessment_id");
                String name = rs.getString("name");
                double weight = rs.getDouble("weight");
                double maxScore = rs.getDouble("max_score");
                System.out.println("Part ID: " + id + ", Assessment ID: " + assessmentId + ", Name: " + name
                        + ", Weight: " + weight + ", Max Score: " + maxScore);
            }
        } catch (SQLException e) {
            System.out.println("Error verifying assessment parts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ----------------------------- COURSES -----------------------------

    /**
     * Updates an existing course in the database.
     *
     * @param course     The `Course` object containing the updated course
     *                   information.
     * @param originalId The original ID of the course to be updated.
     */
    public void updateCourse(Course course, String originalId) {
        String sql = "UPDATE courses SET id = ?, name = ?, description = ? WHERE id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set new course data
            pstmt.setString(1, course.getId());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getDescription());
            pstmt.setString(4, originalId);
            pstmt.executeUpdate();

            // Delete existing outcomes and add updated ones
            deleteOutcomesForCourse(originalId);
            for (Outcome outcome : course.getOutcomes()) {
                addOutcome(outcome, course.getId());
            }

            // Update class associations if the course ID has changed
            if (!course.getId().equals(originalId)) {
                updateClassCourseAssociations(originalId, course.getId());
            }

            System.out.println("Course updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes all outcomes associated with a specific course.
     *
     * @param courseId The ID of the course whose outcomes are to be deleted.
     */
    private void deleteOutcomesForCourse(String courseId) {
        String sql = "DELETE FROM outcomes WHERE course_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting outcomes for course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates class-course associations when a course ID changes.
     *
     * @param oldCourseId The original course ID.
     * @param newCourseId The new course ID to update in associated classes.
     */
    private void updateClassCourseAssociations(String oldCourseId, String newCourseId) {
        String sql = "UPDATE classes SET course_id = ? WHERE course_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newCourseId);
            pstmt.setString(2, oldCourseId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating class course associations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adds a new course to the database along with its outcomes.
     *
     * @param course The `Course` object to be added.
     */
    public void addCourse(Course course) {
        String sql = "INSERT INTO courses(id, name, description) VALUES(?, ?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set course data
            pstmt.setString(1, course.getId());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getDescription());
            pstmt.executeUpdate();

            // Add associated outcomes
            for (Outcome outcome : course.getOutcomes()) {
                addOutcome(outcome, course.getId());
            }
            System.out.println("Course added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    /**
     * Retrieves all outcomes for a given course.
     *
     * @param courseId The ID of the course.
     * @return A list of `Outcome` objects associated with the course.
     */
    private List<Outcome> getOutcomesForCourse(String courseId) {
        List<Outcome> outcomes = new ArrayList<>();
        String sql = "SELECT * FROM outcomes WHERE course_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                // Iterate through outcomes and add to the list
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double weight = rs.getDouble("weight");
                    outcomes.add(new Outcome(id, name, description, weight));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting outcomes for course " + courseId + ": " + e.getMessage());
        }
        return outcomes;
    }

    /**
     * Checks if a course ID is unique in the database.
     *
     * @param newId     The new course ID to check.
     * @param currentId The current course ID to exclude from the check.
     * @return `true` if the new ID is unique, `false` otherwise.
     */
    public boolean isCourseIdUnique(String newId, String currentId) {
        String sql = "SELECT COUNT(*) FROM courses WHERE id = ? AND id != ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newId);
            pstmt.setString(2, currentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking course ID uniqueness: " + e.getMessage());
        }
        return false;
    }

    /**
     * Saves a course to the database, replacing it if it already exists.
     *
     * @param course The `Course` object to be saved.
     */
    public void saveCourse(Course course) {
        String sql = "INSERT OR REPLACE INTO courses(id, name, description) VALUES(?, ?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set course data
            pstmt.setString(1, course.getId());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getDescription());
            pstmt.executeUpdate();

            // Remove existing outcomes and add new ones
            deleteOutcomesForCourse(course.getId());
            for (Outcome outcome : course.getOutcomes()) {
                addOutcome(outcome, course.getId());
            }
            System.out.println("Course and outcomes saved successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all courses from the database along with their outcomes.
     *
     * @return A list of `Course` objects.
     */
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            // Iterate through courses and populate the list
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Course course = new Course(id, name, description);
                course.setOutcomes(getOutcomesForCourse(id));
                courses.add(course);
                System.out.println("Retrieved course: " + name + ", Outcomes: " + course.getOutcomes().size());
            }
        } catch (SQLException e) {
            System.out.println("Error getting courses: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Deletes a course and its associated outcomes from the database.
     *
     * @param courseId The ID of the course to delete.
     */
    public void deleteCourse(String courseId) {
        try (Connection conn = this.connect()) {
            // Delete associated outcomes
            deleteOutcome(courseId, courseId);

            // Delete the course
            String sql = "DELETE FROM courses WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, courseId);
                pstmt.executeUpdate();
                System.out.println("Course deleted successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting course: " + e.getMessage());
        }
    }

    /**
     * Deletes a specific outcome associated with a course.
     *
     * @param courseId  The ID of the course.
     * @param outcomeId The ID of the outcome to delete.
     */
    public void deleteOutcome(String courseId, String outcomeId) {
        String sql = "DELETE FROM outcomes WHERE course_id = ? AND id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            pstmt.setString(2, outcomeId);
            pstmt.executeUpdate();
            System.out.println("Outcome deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting outcome: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adds a new outcome to a course.
     *
     * @param outcome  The `Outcome` object to add.
     * @param courseId The ID of the course to associate the outcome with.
     */
    public void addOutcome(Outcome outcome, String courseId) {
        String sql = "INSERT INTO outcomes (course_id, id, name, description, weight) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set outcome data
            pstmt.setString(1, courseId);
            pstmt.setString(2, outcome.getId());
            pstmt.setString(3, outcome.getName());
            pstmt.setString(4, outcome.getDescription());
            pstmt.setDouble(5, outcome.getWeight());
            pstmt.executeUpdate();
            System.out.println("Outcome added successfully to course ID: " + courseId);
        } catch (SQLException e) {
            System.out.println("Error adding outcome: " + e.getMessage());
        }
    }

    /**
     * Retrieves an outcome by its ID.
     *
     * @param outcomeId The ID of the outcome to retrieve.
     * @return The `Outcome` object if found, or `null` if not found.
     */
    public Outcome getOutcomeById(String outcomeId) {
        String sql = "SELECT * FROM outcomes WHERE id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, outcomeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Construct and return the Outcome object
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double weight = rs.getDouble("weight");
                return new Outcome(id, name, description, weight);
            }
        } catch (SQLException e) {
            System.out.println("Error getting outcome by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates an existing outcome in the database.
     *
     * @param courseId The ID of the course the outcome belongs to.
     * @param outcome  The `Outcome` object containing updated information.
     */
    public void updateOutcome(String courseId, Outcome outcome) {
        String sql = "UPDATE outcomes SET name = ?, description = ?, weight = ? WHERE course_id = ? AND id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set updated outcome data
            pstmt.setString(1, outcome.getName());
            pstmt.setString(2, outcome.getDescription());
            pstmt.setDouble(3, outcome.getWeight());
            pstmt.setString(4, courseId);
            pstmt.setString(5, outcome.getId());
            pstmt.executeUpdate();
            System.out.println("Outcome updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating outcome: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ----------------------------- STUDENTS -----------------------------

    /**
     * Adds a new student to the database.
     *
     * @param name      The name of the student.
     * @param studentId The unique student ID.
     * @throws SQLException If a database access error occurs.
     */
    public void addStudent(String name, String studentId) throws SQLException {
        String sql = "INSERT INTO students(name, studentId) VALUES(?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set student data
            pstmt.setString(1, name);
            pstmt.setString(2, studentId);
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
            throw e; // Rethrow exception to be handled by the caller
        }
    }

    /**
     * Updates an existing student's information.
     *
     * @param oldStudentId The current student ID.
     * @param newName      The new name of the student.
     * @param newStudentId The new student ID.
     */
    public void updateStudent(String oldStudentId, String newName, String newStudentId) {
        String sql = "UPDATE students SET name = ?, studentId = ? WHERE studentId = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set updated student data
            pstmt.setString(1, newName);
            pstmt.setString(2, newStudentId);
            pstmt.setString(3, oldStudentId);
            pstmt.executeUpdate();
            System.out.println("Student updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    /**
     * Retrieves all students from the database.
     *
     * @return A list of `Student` objects.
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            // Iterate through students and add to the list
            while (rs.next()) {
                String name = rs.getString("name");
                String studentId = rs.getString("studentId");
                students.add(new Student(name, studentId));
            }
        } catch (SQLException e) {
            System.out.println("Error getting students: " + e.getMessage());
        }
        return students;
    }

    // ----------------------------- CLASSES -----------------------------

    /**
     * Adds a new class to the database.
     *
     * @param name    The name of the class.
     * @param classId The unique class ID.
     */
    public void addClass(String name, String classId) {
        String sql = "INSERT INTO classes(name, classId) VALUES(?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set class data
            pstmt.setString(1, name);
            pstmt.setString(2, classId);
            pstmt.executeUpdate();
            System.out.println("Class added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding class: " + e.getMessage());
        }
    }

    /**
     * Adds a new class associated with a course.
     *
     * @param classObj The `Classes` object representing the class.
     * @param courseId The ID of the course to associate the class with.
     * @throws SQLException If a database access error occurs.
     */
    public void addClass(Classes classObj, String courseId) throws SQLException {
        String sql = "INSERT INTO classes(name, classId, course_id) VALUES(?, ?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set class data
            pstmt.setString(1, classObj.getName());
            pstmt.setString(2, classObj.getClassId());
            pstmt.setString(3, courseId);
            pstmt.executeUpdate();
            System.out.println("Class added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding class: " + e.getMessage());
            throw e; // Rethrow exception to be handled by the caller
        }
    }

    /**
     * Unlinks an outcome from an assessment part.
     *
     * @param partId    The ID of the assessment part.
     * @param outcomeId The ID of the outcome to unlink.
     */
    public void unlinkOutcomeFromAssessmentPart(int partId, String outcomeId) {
        String sql = "DELETE FROM assessment_part_outcomes WHERE part_id = ? AND outcome_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, partId);
            pstmt.setString(2, outcomeId);
            pstmt.executeUpdate();
            System.out.println("Outcome unlinked from assessment part successfully.");
        } catch (SQLException e) {
            System.out.println("Error unlinking outcome from assessment part: " + e.getMessage());
        }
    }

    /**
     * Retrieves all classes associated with a specific course.
     *
     * @param courseId The ID of the course.
     * @return A list of `Classes` objects.
     */
    public List<Classes> getClassesForCourse(String courseId) {
        List<Classes> classes = new ArrayList<>();
        String sql = "SELECT * FROM classes WHERE course_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            // Iterate through classes and add to the list
            while (rs.next()) {
                String name = rs.getString("name");
                String classId = rs.getString("classId");
                classes.add(new Classes(name, classId));
            }
        } catch (SQLException e) {
            System.out.println("Error getting classes for course: " + e.getMessage());
        }
        return classes;
    }

    /**
     * Retrieves all classes for a specific student within a particular course.
     *
     * @param studentId The ID of the student.
     * @param courseId  The ID of the course.
     * @return A list of `Classes` objects that the student is enrolled in for the
     *         given course.
     */
    public List<Classes> getClassesForStudentInCourse(String studentId, String courseId) {
        List<Classes> classes = new ArrayList<>();
        String sql = "SELECT c.* FROM classes c " +
                "JOIN student_classes sc ON c.classId = sc.class_id " +
                "WHERE sc.student_id = ? AND c.course_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String classId = rs.getString("classId");
                classes.add(new Classes(name, classId));
            }
        } catch (SQLException e) {
            System.out.println("Error getting classes for student in course: " + e.getMessage());
        }
        return classes;
    }

    /**
     * Updates the details of an existing class.
     *
     * @param oldClassId The current ID of the class to be updated.
     * @param newName    The new name for the class.
     * @param newClassId The new ID for the class.
     */
    public void updateClass(String oldClassId, String newName, String newClassId) {
        String sql = "UPDATE classes SET name = ?, classId = ? WHERE classId = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newClassId);
            pstmt.setString(3, oldClassId);
            pstmt.executeUpdate();
            System.out.println("Class updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating class: " + e.getMessage());
        }
    }

    /**
     * Retrieves all classes from the database.
     *
     * @return A list of all `Classes` objects.
     */
    public List<Classes> getAllClasses() {
        List<Classes> classes = new ArrayList<>();
        String sql = "SELECT name, classId FROM classes";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String classId = rs.getString("classId");
                classes.add(new Classes(name, classId));
            }
        } catch (SQLException e) {
            System.out.println("Error getting classes: " + e.getMessage());
        }
        return classes;
    }

    /**
     * Adds a student to a specific class.
     *
     * @param studentId The ID of the student to add.
     * @param classId   The ID of the class to which the student will be added.
     * @return `true` if the student was added successfully, `false` otherwise.
     */
    public boolean addStudentToClass(String studentId, String classId) {
        String sql = "INSERT OR IGNORE INTO student_classes (student_id, class_id) VALUES (?, ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, classId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding student to class: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a student from a specific class.
     *
     * @param studentId The ID of the student to remove.
     * @param classId   The ID of the class from which the student will be removed.
     */
    public void removeStudentFromClass(String studentId, String classId) {
        String sql = "DELETE FROM student_classes WHERE student_id = ? AND class_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, classId);
            pstmt.executeUpdate();
            System.out.println("Student removed from class successfully.");
        } catch (SQLException e) {
            System.out.println("Error removing student from class: " + e.getMessage());
        }
    }

    /**
     * Retrieves all students enrolled in a specific class.
     *
     * @param classId The ID of the class.
     * @return A list of `Student` objects enrolled in the class.
     */
    public List<Student> getStudentsInClass(String classId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.* FROM students s " +
                "JOIN student_classes sc ON s.studentId = sc.student_id " +
                "WHERE sc.class_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String studentId = rs.getString("studentId");
                students.add(new Student(name, studentId));
            }
            System.out.println("Found " + students.size() + " students for class ID: " + classId);
        } catch (SQLException e) {
            System.out.println("Error getting students in class: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Retrieves all grades for a specific student and assessment.
     *
     * @param studentId    The ID of the student.
     * @param assessmentId The ID of the assessment.
     * @return A list of `Grade` objects representing the student's grades for the
     *         assessment.
     */
    public List<Grade> getGradesForStudentAndAssessment(String studentId, int assessmentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ? AND assessment_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setInt(2, assessmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = getStudentById(studentId);
                Assessment assessment = getAssessmentById(assessmentId);
                AssessmentPart part = rs.getObject("part_id") != null ? getAssessmentPartById(rs.getInt("part_id"))
                        : null;
                double score = rs.getDouble("score");
                String feedback = rs.getString("feedback");
                Grade grade = new Grade(student, assessment, part, score, feedback);
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.out.println("Error getting grades for student and assessment: " + e.getMessage());
            e.printStackTrace();
        }
        return grades;
    }

    /**
     * Retrieves all students who are not enrolled in a specific class.
     *
     * @param classId The ID of the class.
     * @return A list of `Student` objects not enrolled in the class.
     */
    public List<Student> getStudentsNotInClass(String classId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE studentId NOT IN " +
                "(SELECT student_id FROM class_students WHERE class_id = ?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String studentId = rs.getString("studentId");
                students.add(new Student(name, studentId));
            }
        } catch (SQLException e) {
            System.out.println("Error getting students not in class: " + e.getMessage());
        }
        return students;
    }

    /**
     * Creates the `student_classes` table if it does not exist.
     * This table manages the many-to-many relationship between students and
     * classes.
     */
    public void createStudentClassTable() {
        String sql = "CREATE TABLE IF NOT EXISTS student_classes (" +
                "student_id TEXT NOT NULL, " +
                "class_id TEXT NOT NULL, " +
                "PRIMARY KEY (student_id, class_id), " +
                "FOREIGN KEY (student_id) REFERENCES students(studentId), " +
                "FOREIGN KEY (class_id) REFERENCES classes(classId))";
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("student_classes table created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating student_classes table: " + e.getMessage());
        }
    }

    /**
     * Retrieves a student by their unique student ID.
     *
     * @param studentId The unique ID of the student.
     * @return The `Student` object if found, or `null` if not found.
     */
    public Student getStudentById(String studentId) {
        String sql = "SELECT * FROM students WHERE studentId = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                return new Student(name, studentId);
            }
        } catch (SQLException e) {
            System.out.println("Error getting student by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all classes that a specific student is enrolled in.
     *
     * @param studentId The ID of the student.
     * @return A list of `Classes` objects the student is enrolled in.
     */
    public List<Classes> getClassesForStudent(String studentId) {
        List<Classes> classes = new ArrayList<>();
        String sql = "SELECT c.* FROM classes c " +
                "JOIN student_classes sc ON c.classId = sc.class_id " +
                "WHERE sc.student_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String classId = rs.getString("classId");
                classes.add(new Classes(name, classId));
            }
        } catch (SQLException e) {
            System.out.println("Error getting classes for student: " + e.getMessage());
        }
        return classes;
    }

    /**
     * Saves or updates a grade for a student.
     *
     * @param grade The `Grade` object containing grade details.
     */
    public void saveGrade(Grade grade) {
        String updateSql;
        if (grade.getAssessmentPart() != null) {
            updateSql = "UPDATE grades SET score = ?, feedback = ? WHERE student_id = ? AND assessment_id = ? AND part_id = ?";
        } else {
            updateSql = "UPDATE grades SET score = ?, feedback = ? WHERE student_id = ? AND assessment_id = ? AND part_id IS NULL";
        }
        String insertSql = "INSERT INTO grades (student_id, assessment_id, part_id, score, feedback) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = this.connect()) {
            conn.setAutoCommit(false); // Start transaction
            try {
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setDouble(1, grade.getScore());
                pstmt.setString(2, grade.getFeedback());
                pstmt.setString(3, grade.getStudent().getStudentId());
                pstmt.setInt(4, grade.getAssessment().getId());
                if (grade.getAssessmentPart() != null) {
                    pstmt.setInt(5, grade.getAssessmentPart().getId());
                }
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    // No existing grade found; insert new grade
                    pstmt = conn.prepareStatement(insertSql);
                    pstmt.setString(1, grade.getStudent().getStudentId());
                    pstmt.setInt(2, grade.getAssessment().getId());
                    if (grade.getAssessmentPart() != null) {
                        pstmt.setInt(3, grade.getAssessmentPart().getId());
                    } else {
                        pstmt.setNull(3, java.sql.Types.INTEGER);
                    }
                    pstmt.setDouble(4, grade.getScore());
                    pstmt.setString(5, grade.getFeedback());
                    pstmt.executeUpdate();
                }
                conn.commit(); // Commit transaction
                System.out.println("Grade saved for Student: " + grade.getStudent().getStudentId() +
                        ", Assessment: " + grade.getAssessment().getId() +
                        ", Part: " + (grade.getAssessmentPart() != null ? grade.getAssessmentPart().getId() : "null") +
                        ", Score: " + grade.getScore());
            } catch (SQLException e) {
                conn.rollback(); // Rollback transaction on error
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restore default commit behavior
            }
        } catch (SQLException e) {
            System.out.println("Error saving grade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all grades for a specific student.
     *
     * @param studentId The ID of the student.
     * @return A list of `Grade` objects representing all grades for the student.
     */
    public List<Grade> getGradesForStudent(String studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = getStudentById(rs.getString("student_id"));
                Assessment assessment = getAssessmentById(rs.getInt("assessment_id"));
                AssessmentPart part = rs.getObject("part_id") != null ? getAssessmentPartById(rs.getInt("part_id"))
                        : null;
                double score = rs.getDouble("score");
                String feedback = rs.getString("feedback");
                if (student != null && assessment != null) {
                    Grade grade = new Grade(student, assessment, part, score, feedback);
                    grades.add(grade);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting grades for student: " + e.getMessage());
        }
        return grades;
    }

    /**
     * Retrieves all grades for a specific student within a particular class.
     *
     * @param studentId The ID of the student.
     * @param classId   The ID of the class.
     * @return A list of `Grade` objects representing the student's grades in the
     *         class.
     */
    public List<Grade> getGradesForStudentInClass(String studentId, String classId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.*, a.name as assessment_name, ap.name as part_name " +
                "FROM grades g " +
                "JOIN assessments a ON g.assessment_id = a.id " +
                "JOIN classes c ON a.course_id = c.course_id " +
                "LEFT JOIN assessment_parts ap ON g.part_id = ap.id " +
                "JOIN student_classes sc ON sc.student_id = g.student_id AND sc.class_id = c.classId " +
                "WHERE g.student_id = ? AND c.classId = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, classId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = getStudentById(rs.getString("student_id"));
                Assessment assessment = new Assessment(
                        rs.getInt("assessment_id"),
                        rs.getString("assessment_name"),
                        "",
                        0,
                        0);
                AssessmentPart part = null;
                if (rs.getObject("part_id") != null) {
                    part = new AssessmentPart(
                            rs.getInt("part_id"),
                            rs.getString("part_name"),
                            0,
                            0);
                }
                double score = rs.getDouble("score");
                String feedback = rs.getString("feedback");
                Grade grade = new Grade(student, assessment, part, score, feedback);
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.out.println("Error getting grades for student in class: " + e.getMessage());
            e.printStackTrace();
        }
        return grades;
    }

    /**
     * Retrieves all grades associated with a specific course.
     *
     * @param courseId The ID of the course.
     * @return A list of `Grade` objects representing all grades for the course.
     */
    public List<Grade> getAllGradesForCourse(String courseId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.*, s.name as student_name, a.name as assessment_name, ap.name as part_name " +
                "FROM grades g " +
                "JOIN assessments a ON g.assessment_id = a.id " +
                "JOIN students s ON g.student_id = s.studentId " +
                "LEFT JOIN assessment_parts ap ON g.part_id = ap.id " +
                "WHERE a.course_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("student_name");
                Student student = new Student(studentName, studentId);

                int assessmentId = rs.getInt("assessment_id");
                String assessmentName = rs.getString("assessment_name");
                double assessmentMaxScore = getAssessmentById(assessmentId).getMaxScore();
                Assessment assessment = new Assessment(assessmentId, assessmentName, "", 0, assessmentMaxScore);

                AssessmentPart part = null;
                if (rs.getObject("part_id") != null) {
                    int partId = rs.getInt("part_id");
                    String partName = rs.getString("part_name");
                    double partMaxScore = getAssessmentPartById(partId).getMaxScore();
                    part = new AssessmentPart(partId, partName, 0, partMaxScore);
                }

                double score = rs.getDouble("score");
                String feedback = rs.getString("feedback");
                Grade grade = new Grade(student, assessment, part, score, feedback);
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all grades for course: " + e.getMessage());
        }
        return grades;
    }

    /**
     * Retrieves all grades associated with a specific class.
     *
     * @param classId The ID of the class.
     * @return A list of `Grade` objects representing all grades for the class.
     */
    public List<Grade> getAllGradesForClass(String classId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.*, s.name as student_name, a.name as assessment_name, ap.name as part_name " +
                "FROM grades g " +
                "JOIN students s ON g.student_id = s.studentId " +
                "JOIN student_classes sc ON s.studentId = sc.student_id " +
                "JOIN assessments a ON g.assessment_id = a.id " +
                "LEFT JOIN assessment_parts ap ON g.part_id = ap.id " +
                "WHERE sc.class_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("student_name");
                Student student = new Student(studentName, studentId);

                int assessmentId = rs.getInt("assessment_id");
                String assessmentName = rs.getString("assessment_name");
                double assessmentMaxScore = getAssessmentById(assessmentId).getMaxScore();
                Assessment assessment = new Assessment(assessmentId, assessmentName, "", 0, assessmentMaxScore);

                AssessmentPart part = null;
                if (rs.getObject("part_id") != null) {
                    int partId = rs.getInt("part_id");
                    String partName = rs.getString("part_name");
                    double partMaxScore = getAssessmentPartById(partId).getMaxScore();
                    part = new AssessmentPart(partId, partName, 0, partMaxScore);
                }

                double score = rs.getDouble("score");
                String feedback = rs.getString("feedback");
                Grade grade = new Grade(student, assessment, part, score, feedback);
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all grades for class: " + e.getMessage());
        }
        return grades;
    }

    /**
     * Retrieves a specific grade for a student, assessment, and optionally an
     * assessment part.
     *
     * @param studentId    The unique identifier of the student.
     * @param assessmentId The unique identifier of the assessment.
     * @param partId       The unique identifier of the assessment part (nullable).
     * @return The `Grade` object if found, or `null` if no matching grade exists.
     */
    public Grade getGrade(String studentId, int assessmentId, Integer partId) {
        String sql = "SELECT * FROM grades WHERE student_id = ? AND assessment_id = ? AND "
                + (partId != null ? "part_id = ?" : "part_id IS NULL");
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setInt(2, assessmentId);
            if (partId != null) {
                pstmt.setInt(3, partId);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double score = rs.getDouble("score");
                String feedback = rs.getString("feedback");
                Student student = getStudentById(studentId);
                Assessment assessment = getAssessmentById(assessmentId);
                AssessmentPart assessmentPart = partId != null ? getAssessmentPartById(partId) : null;
                return new Grade(student, assessment, assessmentPart, score, feedback);
            }
        } catch (SQLException e) {
            System.out.println("Error getting grade: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all grades for a specific class and assessment.
     *
     * @param classId      The unique identifier of the class.
     * @param assessmentId The unique identifier of the assessment.
     * @return A list of `Grade` objects representing the grades for the specified
     *         class and assessment.
     */
    public List<Grade> getGradesForClassAndAssessment(String classId, int assessmentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.*, s.name AS student_name, a.name AS assessment_name, ap.name AS part_name " +
                "FROM grades g " +
                "JOIN students s ON g.student_id = s.studentId " +
                "JOIN student_classes sc ON s.studentId = sc.student_id " +
                "JOIN assessments a ON g.assessment_id = a.id " +
                "LEFT JOIN assessment_parts ap ON g.part_id = ap.id " +
                "WHERE sc.class_id = ? AND g.assessment_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classId);
            pstmt.setInt(2, assessmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Retrieve student information
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("student_name");
                Student student = new Student(studentName, studentId);

                // Retrieve assessment information
                String assessmentName = rs.getString("assessment_name");
                double assessmentMaxScore = getAssessmentById(assessmentId).getMaxScore();
                Assessment assessment = new Assessment(assessmentId, assessmentName, "", 0, assessmentMaxScore);

                // Retrieve assessment part information if available
                AssessmentPart part = null;
                if (rs.getObject("part_id") != null) {
                    int partId = rs.getInt("part_id");
                    String partName = rs.getString("part_name");
                    double partMaxScore = getAssessmentPartById(partId).getMaxScore();
                    part = new AssessmentPart(partId, partName, 0, partMaxScore);
                }

                // Retrieve grade information
                double score = rs.getDouble("score");
                String feedback = rs.getString("feedback");
                Grade grade = new Grade(student, assessment, part, score, feedback);
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.out.println("Error getting grades for class and assessment: " + e.getMessage());
        }
        return grades;
    }

    /**
     * Retrieves all grades for a specific class, assessment, and assessment part.
     *
     * @param classId      The unique identifier of the class.
     * @param assessmentId The unique identifier of the assessment.
     * @param partId       The unique identifier of the assessment part.
     * @return A list of `Grade` objects representing the grades for the specified
     *         class, assessment, and part.
     */
    public List<Grade> getGradesForClassAssessmentAndPart(String classId, int assessmentId, int partId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.*, s.name AS student_name, a.name AS assessment_name, ap.name AS part_name " +
                "FROM grades g " +
                "JOIN students s ON g.student_id = s.studentId " +
                "JOIN student_classes sc ON s.studentId = sc.student_id " +
                "JOIN assessments a ON g.assessment_id = a.id " +
                "JOIN assessment_parts ap ON g.part_id = ap.id " +
                "WHERE sc.class_id = ? AND g.assessment_id = ? AND g.part_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classId);
            pstmt.setInt(2, assessmentId);
            pstmt.setInt(3, partId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Retrieve student information
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("student_name");
                Student student = new Student(studentName, studentId);

                // Retrieve assessment information
                String assessmentName = rs.getString("assessment_name");
                double assessmentMaxScore = getAssessmentById(assessmentId).getMaxScore();
                Assessment assessment = new Assessment(assessmentId, assessmentName, "", 0, assessmentMaxScore);

                // Retrieve assessment part information
                String partName = rs.getString("part_name");
                double partMaxScore = getAssessmentPartById(partId).getMaxScore();
                AssessmentPart part = new AssessmentPart(partId, partName, 0, partMaxScore);

                // Retrieve grade information
                double score = rs.getDouble("score");
                String feedback = rs.getString("feedback");
                Grade grade = new Grade(student, assessment, part, score, feedback);
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.out.println("Error getting grades for class, assessment, and part: " + e.getMessage());
        }
        return grades;
    }

    // ----------------------------- ASSESSMENTS -----------------------------

    /**
     * Deletes a record from a specified table based on the provided ID.
     *
     * @param table    The name of the table from which to delete the record.
     * @param idColumn The name of the ID column in the table.
     * @param id       The ID value of the record to delete.
     */
    public void delete(String table, String idColumn, String id) {
        String sql = "DELETE FROM " + table + " WHERE " + idColumn + " = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting from " + table + ": " + e.getMessage());
        }
    }

    /**
     * Retrieves all outcomes associated with a specific assessment part.
     *
     * @param partId The unique identifier of the assessment part.
     * @return A list of `Outcome` objects associated with the assessment part.
     */
    public List<Outcome> getOutcomesForAssessmentPart(int partId) {
        List<Outcome> outcomes = new ArrayList<>();
        String sql = "SELECT o.*, apo.weight as part_weight FROM outcomes o " +
                "JOIN assessment_part_outcomes apo ON o.id = apo.outcome_id " +
                "WHERE apo.part_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, partId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double weight = rs.getDouble("part_weight");
                Outcome outcome = new Outcome(id, name, description, weight);
                outcomes.add(outcome);
            }
        } catch (SQLException e) {
            System.out.println("Error getting outcomes for assessment part: " + e.getMessage());
        }
        return outcomes;
    }

    /**
     * Retrieves all outcomes associated with a specific assessment.
     *
     * @param assessmentId The unique identifier of the assessment.
     * @return A list of `Outcome` objects associated with the assessment.
     */
    public List<Outcome> getOutcomesForAssessment(int assessmentId) {
        List<Outcome> outcomes = new ArrayList<>();
        String sql = "SELECT o.*, ao.weight as assessment_weight FROM outcomes o " +
                "JOIN assessment_outcomes ao ON o.id = ao.outcome_id " +
                "WHERE ao.assessment_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double weight = rs.getDouble("assessment_weight");
                Outcome outcome = new Outcome(id, name, description, weight);
                outcomes.add(outcome);
            }
        } catch (SQLException e) {
            System.out.println("Error getting outcomes for assessment: " + e.getMessage());
        }
        return outcomes;
    }

    /**
     * Updates the details of an existing assessment in the database.
     *
     * @param assessment The `Assessment` object containing the updated assessment
     *                   information.
     */
    public void updateAssessment(Assessment assessment) {
        String sql = "UPDATE assessments SET name = ?, description = ?, weight = ?, maxScore = ? WHERE id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, assessment.getName());
            pstmt.setString(2, assessment.getDescription());
            pstmt.setDouble(3, assessment.getWeight());
            pstmt.setDouble(4, assessment.getMaxScore());
            pstmt.setInt(5, assessment.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating assessment: " + e.getMessage());
        }
    }

    /**
     * Retrieves all assessments from the database.
     *
     * @return A list of all `Assessment` objects.
     */
    public List<Assessment> getAllAssessments() {
        List<Assessment> assessments = new ArrayList<>();
        String sql = "SELECT * FROM assessments";
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Assessment assessment = new Assessment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("weight"),
                        rs.getDouble("maxScore"));
                assessments.add(assessment);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all assessments: " + e.getMessage());
        }
        return assessments;
    }

    /**
     * Retrieves an assessment by its unique ID.
     *
     * @param assessmentId The unique identifier of the assessment.
     * @return The `Assessment` object if found, or `null` if not found.
     */
    public Assessment getAssessmentById(int assessmentId) {
        String sql = "SELECT * FROM assessments WHERE id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Assessment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("weight"),
                        rs.getDouble("maxScore"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting assessment by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Deletes an assessment part from the database.
     *
     * @param partId The unique identifier of the assessment part to delete.
     */
    public void deleteAssessmentPart(int partId) {
        String sql = "DELETE FROM assessment_parts WHERE id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, partId);
            pstmt.executeUpdate();
            System.out.println("Assessment part deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting assessment part: " + e.getMessage());
        }
    }

    /**
     * Retrieves an assessment part by its unique ID.
     *
     * @param partId The unique identifier of the assessment part.
     * @return The `AssessmentPart` object if found, or `null` if not found.
     */
    public AssessmentPart getAssessmentPartById(Integer partId) {
        String sql = "SELECT * FROM assessment_parts WHERE id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, partId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new AssessmentPart(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("weight"),
                        rs.getDouble("max_score"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting assessment part by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Unlinks an outcome from an assessment.
     *
     * @param assessmentId The unique identifier of the assessment.
     * @param outcomeId    The unique identifier of the outcome to unlink.
     */
    public void unlinkOutcomeFromAssessment(int assessmentId, String outcomeId) {
        String sql = "DELETE FROM assessment_outcomes WHERE assessment_id = ? AND outcome_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            pstmt.setString(2, outcomeId);
            pstmt.executeUpdate();
            System.out.println("Outcome unlinked from assessment successfully.");
        } catch (SQLException e) {
            System.out.println("Error unlinking outcome from assessment: " + e.getMessage());
        }
    }

    /**
     * Deletes an assessment and its associated outcome links from the database.
     *
     * @param assessmentId The unique identifier of the assessment to delete.
     */
    public void deleteAssessment(int assessmentId) {
        try (Connection conn = this.connect()) {
            conn.setAutoCommit(false);
            try {

                String deleteLinksSQL = "DELETE FROM assessment_outcomes WHERE assessment_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteLinksSQL)) {
                    pstmt.setInt(1, assessmentId);
                    pstmt.executeUpdate();
                }

                String deleteAssessmentSQL = "DELETE FROM assessments WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteAssessmentSQL)) {
                    pstmt.setInt(1, assessmentId);
                    pstmt.executeUpdate();
                }

                conn.commit();
                System.out.println("Assessment and its outcome links deleted successfully.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting assessment: " + e.getMessage());
        }
    }

    /**
     * Retrieves all assessments associated with a specific course.
     *
     * @param courseId The unique identifier of the course.
     * @return A list of `Assessment` objects associated with the course.
     */
    public List<Assessment> getAssessmentsForCourse(String courseId) {
        List<Assessment> assessments = new ArrayList<>();
        String sql = "SELECT * FROM assessments WHERE course_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Assessment assessment = new Assessment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("weight"),
                            rs.getDouble("maxScore"));
                    List<AssessmentPart> parts = getAssessmentParts(assessment.getId());
                    for (AssessmentPart part : parts) {
                        assessment.addPart(part);
                    }
                    assessments.add(assessment);
                    System.out.println(
                            "Loaded assessment: " + assessment.getName() + " (ID: " + assessment.getId() + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting assessments for course " + courseId + ": " + e.getMessage());
        }
        return assessments;
    }

    /**
     * Retrieves all assessments associated with a specific class.
     *
     * @param classId The unique identifier of the class.
     * @return A list of `Assessment` objects associated with the class.
     */
    public List<Assessment> getAssessmentsForClass(String classId) {
        List<Assessment> assessments = new ArrayList<>();
        String sql = "SELECT * FROM assessments WHERE class_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Assessment assessment = new Assessment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("weight"),
                            rs.getDouble("maxScore"));
                    List<AssessmentPart> parts = getAssessmentParts(assessment.getId());
                    for (AssessmentPart part : parts) {
                        assessment.addPart(part);
                    }
                    assessments.add(assessment);
                    System.out.println(
                            "Loaded assessment: " + assessment.getName() + " (ID: " + assessment.getId() + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting assessments for course " + classId + ": " + e.getMessage());
        }
        return assessments;
    }

    /**
     * Retrieves the class associated with a specific student.
     *
     * @param studentId The unique identifier of the student.
     * @return The `Classes` object representing the class the student is enrolled
     *         in, or `null` if not found.
     */
    public Classes getClassForStudent(String studentId) {
        String sql = "SELECT c.* FROM classes c " +
                "JOIN student_classes sc ON c.classId = sc.class_id " +
                "WHERE sc.student_id = ?";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String classId = rs.getString("classId");
                String className = rs.getString("name");
                return new Classes(className, classId);
            }
        } catch (SQLException e) {
            System.out.println("Error getting class for student: " + e.getMessage());
        }

        return null;
    }

    /**
     * Retrieves all courses that a specific student is enrolled in.
     *
     * @param studentId The unique identifier of the student.
     * @return A list of `Course` objects representing the courses the student is
     *         enrolled in.
     */
    public List<Course> getCoursesForStudent(String studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT DISTINCT c.* FROM courses c " +
                "JOIN classes cl ON c.id = cl.course_id " +
                "JOIN student_classes sc ON cl.classId = sc.class_id " +
                "WHERE sc.student_id = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Course course = new Course(id, name, description);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Error getting courses for student: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Deletes a student from the database.
     *
     * @param studentId The unique identifier of the student to delete.
     * @return `true` if the student was deleted successfully, `false` otherwise.
     */
    public boolean deleteStudent(String studentId) {
        String sql = "DELETE FROM students WHERE studentId = ?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

}
