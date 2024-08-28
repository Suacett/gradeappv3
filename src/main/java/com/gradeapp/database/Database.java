package com.gradeapp.database;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Student;
import com.gradeapp.model.Outcome;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:sqlite:com.gradeapp.db";

    public void initialiseDatabase() {
        String createCoursesTable = "CREATE TABLE IF NOT EXISTS courses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT NOT NULL"
                + ");";
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS students ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "studentId TEXT NOT NULL"
                + ");";
        String createClassesTable = "CREATE TABLE IF NOT EXISTS classes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "classId TEXT NOT NULL"
                + ");";
        String createAssessmentsTable = "CREATE TABLE IF NOT EXISTS assessments ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "weight REAL NOT NULL,"
                + "maxScore REAL NOT NULL"
                + ");";
        String createOutcomesTable = "CREATE TABLE IF NOT EXISTS outcomes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "course_id INTEGER,"
                + "identifier TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "weight REAL NOT NULL,"
                + "FOREIGN KEY (course_id) REFERENCES courses(id)"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createCoursesTable);
            stmt.execute(createStudentsTable);
            stmt.execute(createClassesTable);
            stmt.execute(createAssessmentsTable);
            stmt.execute(createOutcomesTable);
            System.out.println("Database and tables initialized.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

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

    // COURSES
    public void addCourse(Course course) {
        String sql = "INSERT INTO courses(name, description) VALUES(?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, course.getName());
            pstmt.setString(2, course.getDescription());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int courseId = generatedKeys.getInt(1);
                    addOutcomes(courseId, course.getOutcomes());
                }
            }
            System.out.println("Course added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    public void updateCourse(Course course) {
        String sql = "UPDATE courses SET name = ?, description = ? WHERE name = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getName());
            pstmt.setString(2, course.getDescription());
            pstmt.setString(3, course.getName());
            pstmt.executeUpdate();

            // Update outcomes
            int courseId = getCourseId(course.getName());
            deleteOutcomes(courseId);
            addOutcomes(courseId, course.getOutcomes());

            System.out.println("Course updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating course: " + e.getMessage());
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                Course course = new Course(name, description);
                course.setOutcomes(getOutcomesForCourse(rs.getInt("id")));
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Error getting courses: " + e.getMessage());
        }
        return courses;
    }

    public void deleteCourse(String courseName) {
        try (Connection conn = this.connect()) {
            int courseId = getCourseId(courseName);
            deleteOutcomes(courseId);

            String sql = "DELETE FROM courses WHERE name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, courseName);
                pstmt.executeUpdate();
                System.out.println("Course deleted successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting course: " + e.getMessage());
        }
    }


    private void deleteOutcomes(int courseId) {
        String sql = "DELETE FROM outcomes WHERE course_id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting outcomes: " + e.getMessage());
        }
    }

    private void addOutcomes(int courseId, List<Outcome> outcomes) {
        String sql = "INSERT INTO outcomes(course_id, id, name, description, weight) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Outcome outcome : outcomes) {
                pstmt.setInt(1, courseId);
                pstmt.setString(2, outcome.getId());
                pstmt.setString(3, outcome.getName());
                pstmt.setString(4, outcome.getDescription());
                pstmt.setDouble(5, outcome.getWeight());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error adding outcomes: " + e.getMessage());
        }
    }

    private List<Outcome> getOutcomesForCourse(int courseId) {
        List<Outcome> outcomes = new ArrayList<>();
        String sql = "SELECT * FROM outcomes WHERE course_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double weight = rs.getDouble("weight");
                    outcomes.add(new Outcome(id, name, description, weight));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting outcomes: " + e.getMessage());
        }
        return outcomes;
    }

    private int getCourseId(String courseName) throws SQLException {
        String sql = "SELECT id FROM courses WHERE name = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Course not found: " + courseName);
    }

    // STUDENTS
    public void addStudent(String name, String studentId) {
        String sql = "INSERT INTO students(name, studentId) VALUES(?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, studentId);
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    public void updateStudent(String oldStudentId, String newName, String newStudentId) {
        String sql = "UPDATE students SET name = ?, studentId = ? WHERE studentId = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newStudentId);
            pstmt.setString(3, oldStudentId);
            pstmt.executeUpdate();
            System.out.println("Student updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
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

    // CLASSES
    public void addClass(String name, String classId) {
        String sql = "INSERT INTO classes(name, classId) VALUES(?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, classId);
            pstmt.executeUpdate();
            System.out.println("Class added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding class: " + e.getMessage());
        }
    }

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

    // ASSESSMENTS
    public void addAssessment(Assessment assessment) {
        String sql = "INSERT INTO assessments(name, description, weight, maxScore) VALUES(?, ?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, assessment.getName());
            pstmt.setString(2, assessment.getDescription());
            pstmt.setDouble(3, assessment.getWeight());
            pstmt.setDouble(4, assessment.getMaxScore());
            pstmt.executeUpdate();
            System.out.println("Assessment added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding assessment: " + e.getMessage());
        }
    }

    public void updateAssessment(Assessment assessment) {
        String sql = "UPDATE assessments SET name = ?, description = ?, weight = ?, maxScore = ? WHERE name = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, assessment.getName());
            pstmt.setString(2, assessment.getDescription());
            pstmt.setDouble(3, assessment.getWeight());
            pstmt.setDouble(4, assessment.getMaxScore());
            pstmt.setString(5, assessment.getName());
            pstmt.executeUpdate();
            System.out.println("Assessment updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating assessment: " + e.getMessage());
        }
    }

    public List<Assessment> getAllAssessments() {
        List<Assessment> assessments = new ArrayList<>();
        String sql = "SELECT * FROM assessments";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                double weight = rs.getDouble("weight");
                double maxScore = rs.getDouble("maxScore");
                assessments.add(new Assessment(name, description, weight, maxScore));
            }
        } catch (SQLException e) {
            System.out.println("Error getting assessments: " + e.getMessage());
        }
        return assessments;
    }

    // Generic delete method
    public void delete(String table, String column, String value) {
        String sql = "DELETE FROM " + table + " WHERE " + column + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.executeUpdate();
            System.out.println("Record deleted successfully from " + table + ".");
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }
}