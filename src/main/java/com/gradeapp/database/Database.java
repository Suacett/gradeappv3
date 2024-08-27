package com.gradeapp.database;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:sqlite:com.gradeapp.db"; // URL path for db

    // Initialise db, tables
    public void initialiseDatabase() {
        String createCoursesTable = "CREATE TABLE IF NOT EXISTS courses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT NOT NULL"
                + ");";
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" // Students table
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "studentId TEXT NOT NULL"
                + ");";
        String createClassesTable = "CREATE TABLE IF NOT EXISTS classes (" // Classes table
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "classId TEXT NOT NULL"
                + ");";
        String createAssessmentsTable = "CREATE TABLE IF NOT EXISTS assessments (" // Assessments table
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "weight REAL NOT NULL,"
                + "maxScore REAL NOT NULL"
                + ");";
        // Connect to db, create tables
        try (Connection conn = this.connect();
             PreparedStatement stmtCourses = conn.prepareStatement(createCoursesTable);
             PreparedStatement stmtStudents = conn.prepareStatement(createStudentsTable);
             PreparedStatement stmtClasses = conn.prepareStatement(createClassesTable);
             PreparedStatement stmtAssessments = conn.prepareStatement(createAssessmentsTable)) {
            stmtCourses.execute();
            stmtStudents.execute();
            stmtClasses.execute();
            stmtAssessments.execute();
            System.out.println("Db/tables working...");
        } catch (SQLException e) {
            System.out.println("Error :" + e.getMessage());
        }
    }

    // Db connection
    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("SQLite working...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Methods

    // COURSES
    public void addCourse(String name, String description) {
        String sql = "INSERT INTO courses(name, description) VALUES(?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
            System.out.println("Course added...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateCourse(String oldCourseName, String newName, String newDescription) {
        String sql = "UPDATE courses SET name = ?, description = ? WHERE name = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newDescription);
            pstmt.setString(3, oldCourseName);
            pstmt.executeUpdate();
            System.out.println("Course updated...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
                courses.add(new Course(name, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // DELETE courses, students, classes, assessments from db
    public void delete(String table, String column, String value) {
        String sql = "DELETE FROM " + table + " WHERE " + column + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.executeUpdate();
            System.out.println("Record deleted...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // STUDENTS
    public void addStudent(String name, String studentId) {
        String sql = "INSERT INTO students(name, studentId) VALUES(?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, studentId);
            pstmt.executeUpdate();
            System.out.println("Student added...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
            System.out.println("Student updated...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
            e.printStackTrace();
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
            System.out.println("Class added...");
        } catch (SQLException e) {
            System.out.println("Class not added..." + e.getMessage());
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
            System.out.println("Class updated...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Classes> getAllClasses() {
        List<Classes> classes = new ArrayList<>();
        String sql = "SELECT name, classId FROM classes"; // Fetch name and classId specifically
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String classId = rs.getString("classId");
                classes.add(new Classes(name, classId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    // ASSESSMENTS

    // ADD assessment to db
    public void addAssessment(Assessment assessment) {
        String sql = "INSERT INTO assessments(name, description, weight, maxScore) VALUES(?, ?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, assessment.getName());
            pstmt.setString(2, assessment.getDescription());
            pstmt.setDouble(3, assessment.getWeight());
            pstmt.setDouble(4, assessment.getMaxScore());
            pstmt.executeUpdate();
            System.out.println("Assessment added...");
        } catch (SQLException e) {
            System.out.println("Assessment not added..." + e.getMessage());
        }
    }

    // UPDATE assessment details
    public void updateAssessment(Assessment assessment) {
        String sql = "UPDATE assessments SET name = ?, description = ?, weight = ?, maxScore = ? WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, assessment.getName());
            pstmt.setString(2, assessment.getDescription());
            pstmt.setDouble(3, assessment.getWeight());
            pstmt.setDouble(4, assessment.getMaxScore());
            pstmt.setInt(5, getAssessmentIdByName(assessment.getName())); // Assuming the name is unique
            pstmt.executeUpdate();
            System.out.println("Assessment updated...");
        } catch (SQLException e) {
            System.out.println("Assessment not updated..." + e.getMessage());
        }
    }

    // GET assessments from db
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
            e.printStackTrace();
        }
        return assessments;
    }

    // Get the assessment ID by name (assuming name is unique)
    private int getAssessmentIdByName(String name) throws SQLException {
        String sql = "SELECT id FROM assessments WHERE name = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Assessment not found: " + name);
                }
            }
        }
    }
}
