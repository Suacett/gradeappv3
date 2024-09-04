package com.gradeapp.database;

import com.gradeapp.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:sqlite:com.gradeapp.db";
    private static boolean isInitialized = false;

    public Database() {
        if (!isInitialized) {
            initialiseDatabase();
            isInitialized = true;
        }
    }

    public void initialiseDatabase() {
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
        String createGradesTable = "CREATE TABLE IF NOT EXISTS grades ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "student_id TEXT NOT NULL,"
                + "assessment_id INTEGER NOT NULL,"
                + "part_id INTEGER,"
                + "score REAL NOT NULL,"
                + "feedback TEXT,"
                + "date DATE NOT NULL,"
                + "FOREIGN KEY (student_id) REFERENCES students(studentId),"
                + "FOREIGN KEY (assessment_id) REFERENCES assessments(id)"
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
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
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

                try {
                    stmt.execute("ALTER TABLE classes ADD COLUMN course_id TEXT REFERENCES courses(id)");
                } catch (SQLException e) {
                    // If the column already exists, this is not an error we need to worry about
                    if (!e.getMessage().contains("duplicate column name")) {
                        throw e; // Re-throw if it's a different error
                    }
                }

                
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

    // ASSESSMENT PARTS

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
            // Get the last inserted ID
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
                System.out.println("Part ID: " + id + ", Assessment ID: " + assessmentId + ", Name: " + name + ", Weight: " + weight + ", Max Score: " + maxScore);
            }
        } catch (SQLException e) {
            System.out.println("Error verifying assessment parts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // COURSES
    public void addCourse(Course course) {
        String sql = "INSERT INTO courses(id, name, description) VALUES(?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getId());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getDescription());
            pstmt.executeUpdate();
            
            // Add outcomes for the course
            for (Outcome outcome : course.getOutcomes()) {
                addOutcome(outcome, course.getId());
            }
            System.out.println("Course added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    private List<Outcome> getOutcomesForCourse(String courseId) {
        List<Outcome> outcomes = new ArrayList<>();
        String sql = "SELECT * FROM outcomes WHERE course_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
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
            System.out.println("Error getting outcomes for course " + courseId + ": " + e.getMessage());
        }
        return outcomes;
    }

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
    
    public void saveCourse(Course course) {
        String sql = "INSERT OR REPLACE INTO courses(id, name, description) VALUES(?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getId());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getDescription());
            pstmt.executeUpdate();

            // Delete existing outcomes for this course
            sql = "DELETE FROM outcomes WHERE course_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(sql)) {
                deleteStmt.setString(1, course.getId());
                deleteStmt.executeUpdate();
            }

            // Save new outcomes
            sql = "INSERT INTO outcomes(id, course_id, name, description, weight) VALUES(?, ?, ?, ?, ?)";
            try (PreparedStatement outcomeStmt = conn.prepareStatement(sql)) {
                for (Outcome outcome : course.getOutcomes()) {
                    outcomeStmt.setString(1, outcome.getId());
                    outcomeStmt.setString(2, course.getId());
                    outcomeStmt.setString(3, outcome.getName());
                    outcomeStmt.setString(4, outcome.getDescription());
                    outcomeStmt.setDouble(5, outcome.getWeight());
                    outcomeStmt.executeUpdate();
                }
            }
            System.out.println("Course and outcomes saved successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Course course = new Course(id, name, description);
                course.setOutcomes(getOutcomesForCourse(id));
                courses.add(course);
                System.out.println("Retrieved course: " + name + ", Outcomes: " + course.getOutcomes().size()); // Debug print
            }
        } catch (SQLException e) {
            System.out.println("Error getting courses: " + e.getMessage());
        }
        return courses;
    }

    public void deleteCourse(String courseId) {
        try (Connection conn = this.connect()) {
            deleteOutcome(courseId, courseId);

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

    public void addOutcome(Outcome outcome, String courseId) {
        String sql = "INSERT INTO outcomes (course_id, id, name, description, weight) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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


    public void updateOutcome(String courseId, Outcome outcome) {
        String sql = "UPDATE outcomes SET name = ?, description = ?, weight = ? WHERE course_id = ? AND id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    public void addClass(Classes classObj, String courseId) throws SQLException {
        String sql = "INSERT INTO classes(name, classId, course_id) VALUES(?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classObj.getName());
            pstmt.setString(2, classObj.getClassId());
            pstmt.setString(3, courseId);
            pstmt.executeUpdate();
            System.out.println("Class added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding class: " + e.getMessage());
            throw e; // Rethrow the exception for the controller to handle
        }
    }

    public List<Classes> getClassesForCourse(String courseId) {
        List<Classes> classes = new ArrayList<>();
        String sql = "SELECT * FROM classes WHERE course_id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            ResultSet rs = pstmt.executeQuery();
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

    public List<Student> getStudentsInClass(String classId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.* FROM students s " +
                     "JOIN class_students cs ON s.studentId = cs.student_id " +
                     "WHERE cs.class_id = ?";
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
            System.out.println("Error getting students in class: " + e.getMessage());
        }
        return students;
    }

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

    public void saveGrade(Grade grade) {
        String sql = "INSERT INTO grades (student_id, assessment_id, part_id, score, feedback, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, grade.getStudent().getStudentId());
            pstmt.setInt(2, grade.getAssessment().getId());
            if (grade.getPart() != null) {
                pstmt.setInt(3, grade.getPart().getId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.setDouble(4, grade.getScore());
            pstmt.setString(5, grade.getFeedback());
            pstmt.setDate(6, java.sql.Date.valueOf(grade.getDate()));
            pstmt.executeUpdate();
            System.out.println("Grade saved to database successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving grade: " + e.getMessage());
        }
    }
    
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
                double score = rs.getDouble("score");
                String feedback = rs.getString("feedback");
                LocalDate date = rs.getDate("date").toLocalDate();
                if (student != null && assessment != null) {
                    Grade grade = new Grade(student, assessment, score, feedback);
                    grade.setDate(date);
                    grades.add(grade);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting grades for student: " + e.getMessage());
        }
        return grades;
    }

    // ASSESSMENTS

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
                    rs.getDouble("maxScore")
                );
                assessments.add(assessment);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all assessments: " + e.getMessage());
        }
        return assessments;
    }
    
    public void linkOutcomeToAssessment(int assessmentId, String outcomeId, double weight) {
        String sql = "INSERT INTO assessment_outcomes (assessment_id, outcome_id, weight) VALUES (?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentId);
            pstmt.setString(2, outcomeId);
            pstmt.setDouble(3, weight);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error linking outcome to assessment: " + e.getMessage());
        }
    }
    
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
                    rs.getDouble("maxScore")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error getting assessment by ID: " + e.getMessage());
        }
        return null;
    }
    
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

    public void deleteAssessment(int assessmentId) {
        try (Connection conn = this.connect()) {
            conn.setAutoCommit(false);
            try {
                // First, delete all outcome links for this assessment
                String deleteLinksSQL = "DELETE FROM assessment_outcomes WHERE assessment_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteLinksSQL)) {
                    pstmt.setInt(1, assessmentId);
                    pstmt.executeUpdate();
                }

                // Then, delete the assessment itself
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
                        rs.getDouble("maxScore")
                    );
                    List<AssessmentPart> parts = getAssessmentParts(assessment.getId());
                    for (AssessmentPart part : parts) {
                        assessment.addPart(part);
                    }
                    assessments.add(assessment);
                    System.out.println("Loaded assessment: " + assessment.getName() + " (ID: " + assessment.getId() + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting assessments for course " + courseId + ": " + e.getMessage());
        }
        return assessments;
    }

    public Classes getClassForStudent(String studentId) {
        String sql = "SELECT c.* FROM classes c " +
                     "JOIN student_class sc ON c.classId = sc.classId " +
                     "WHERE sc.studentId = ?";
        
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

    private void loadOutcomesForCourse(Course course) {
        String sql = "SELECT * FROM outcomes WHERE courseId = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String outcomeId = rs.getString("outcomeId");
                String outcomeName = rs.getString("name");
                String outcomeDescription = rs.getString("description");
                double weight = rs.getDouble("weight");
                
                Outcome outcome = new Outcome(outcomeId, outcomeName, outcomeDescription, weight);
                course.addOutcome(outcome);
            }
        } catch (SQLException e) {
            System.out.println("Error loading outcomes for course: " + e.getMessage());
        }
    }

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