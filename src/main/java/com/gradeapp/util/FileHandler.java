package com.gradeapp.util;

import com.gradeapp.controller.DataImportExportController;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Assessment;
import com.gradeapp.model.Student;
import com.gradeapp.model.StudentGrade;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages file operations, including reading and writing CSV and Excel files.
 */
public class FileHandler {
    private Map<String, Student> studentMap = new HashMap<>();
    private Map<String, Assessment> assessmentMap = new HashMap<>();

    public List<Student> importStudents(String filePath, FileFormat format) throws IOException {
        // Implementation for importing students

        // ... rest of the code ...
        return List.of();
    }

    public void createDirectory(String dirPath) throws IOException {
        Files.createDirectories(Paths.get(dirPath));
    }

    public boolean deleteFile(String filePath) throws IOException {
        return Files.deleteIfExists(Paths.get(filePath));
    }

    public List<String> listFiles(String dirPath) throws IOException {
        return Files.list(Paths.get(dirPath))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    public void copyFile(String sourcePath, String destPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
    }

    public void moveFile(String sourcePath, String destPath) throws IOException {
        Files.move(Paths.get(sourcePath), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
    }

    public String getFileExtension(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public void exportToCsv(List<Student> students, String filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write("Student ID,Name,Grade\n");
            for (Student student : students) {
                for (StudentGrade grade : student.getGrades()) {
                    writer.write(String.format("%s,%s,%f\n",
                            student.getStudentId(), student.getName(), grade.getScore()));
                }
            }
        }
    }

    public List<Student> importFromCsv(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    students.add(new Student(data[1], data[0]));
                }
            }
        }
        return students;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return ""; // Handle unexpected types
        }
    }

    public void exportGrades(List<Student> students, String filePath, FileFormat format) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Grades");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student Name");
            headerRow.createCell(1).setCellValue("Student ID");
            headerRow.createCell(2).setCellValue("Assessment");
            headerRow.createCell(3).setCellValue("Grade");

            int rowNum = 1;
            for (Student student : students) {
                for (StudentGrade grade : student.getGrades()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(student.getName());
                    row.createCell(1).setCellValue(student.getStudentId() != null ? student.getStudentId() : "");
                    row.createCell(2).setCellValue(grade.getAssessment().getName());
                    row.createCell(3).setCellValue(grade.getScore());
                }
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    public List<StudentGrade> importGrades(String filePath) throws IOException {
        List<StudentGrade> grades = new ArrayList<>();

        // Similar logic to import student data
        Path path = Paths.get(filePath);
        try (InputStream inputStream = Files.newInputStream(path);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String studentId = getCellValue(row.getCell(0));
                String assessmentName = getCellValue(row.getCell(1));
                double score = Double.parseDouble(getCellValue(row.getCell(2)));

                Student student = findStudentById(studentId);
                Assessment assessment = findAssessmentByName(assessmentName);

                if (student != null && assessment != null) {
                    grades.add(new StudentGrade(student, assessment, score, ""));
                }
            }
        }

        return grades;
    }

    public void exportAssessments(List<Assessment> assessments, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Assessments");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Assessment Name");
            headerRow.createCell(1).setCellValue("Weight");
            headerRow.createCell(2).setCellValue("Max Score");

            int rowNum = 1;
            for (Assessment assessment : assessments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(assessment.getName());
                row.createCell(1).setCellValue(assessment.getWeight());
                row.createCell(2).setCellValue(assessment.getMaxScore());
            }

            // Auto-size columns for better readability
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    public Student findStudentById(String studentId) {
        return studentMap.get(studentId);
    }

    public Assessment findAssessmentByName(String assessmentName) {
        return assessmentMap.get(assessmentName);
    }

    public void importData(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                String studentId = getCellValue(row.getCell(0));
                String name = getCellValue(row.getCell(1));
                // Add more fields as needed
                Student student = new Student(name, studentId);
                // Add the student to your data structure or database
            }
        }
    }

    public void exportData(String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student ID");
            headerRow.createCell(1).setCellValue("Name");
            // Add more header cells as needed

            // Add data rows here
            // Example: int rowNum = 1;
            // for (Student student : students) {
            //     Row row = sheet.createRow(rowNum++);
            //     row.createCell(0).setCellValue(student.getStudentId());
            //     row.createCell(1).setCellValue(student.getName());
            // }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    public enum FileFormat {
        CSV, EXCEL
    }
}
