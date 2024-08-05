package com.gradeapp.database;

import com.gradeapp.model.Course;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class database {
    private static final String URL = "jdbc:sqlite:com.gradeapp.db"; // url path for db

    // Initialize database and create tables if they don't exist
    public void initializeDatabase() {
        String createCoursesTable = "CREATE TABLE IF NOT EXISTS courses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT NOT NULL"
                + ");";

        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(createCoursesTable)) {
            stmt.execute();
            System.out.println("Database and tables initialized successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to connect to the database
    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    // Method to add a course to the database
    public void addCourse(String name, String description) {
        String sql = "INSERT INTO courses(name, description) VALUES(?, ?)";

        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
            System.out.println("Course added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


// Get course
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


// Delete course
    public void deleteCourse(String name) {
        String sql = "DELETE FROM courses WHERE name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}