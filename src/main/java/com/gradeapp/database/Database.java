package com.gradeapp.database;

import com.gradeapp.model.Classes;
import com.gradeapp.model.Course;
import com.gradeapp.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlite:com.gradeapp.db"; // URL path for db

    // Initialise db, tables
    public void initialiseDatabase() { // Courses table
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
        // Connect to db, create tables
        try (Connection conn = this.connect();
             PreparedStatement stmtCourses = conn.prepareStatement(createCoursesTable);
             PreparedStatement stmtStudents = conn.prepareStatement(createStudentsTable);
             PreparedStatement stmtClasses = conn.prepareStatement(createClassesTable)) {
            stmtCourses.execute();
            stmtStudents.execute();
            stmtClasses.execute();
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
    // ADD course to db
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

    // UPDATE course details
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
    
    // GET courses from db
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

    // DELETE courses, students, classes from db
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
    // ADD student
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

    // UPDATE student details
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

    // GET students from db
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
    // ADD class to db
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

    // UPDATE class details
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


    // GET classes from db
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



}
