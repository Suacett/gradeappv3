package com.gradeapp.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gradeapp.model.*;

import javafx.scene.control.Alert;

public class FileHandler {
    private Map<String, Student> studentMap = new HashMap<>();
    private Map<String, Assessment> assessmentMap = new HashMap<>();

    public List<Student> importStudents(String filePath) throws IOException {
        if (filePath.endsWith(".csv")) {
            return importStudentsFromCsv(filePath);
        } else if (filePath.endsWith(".xlsx")) {
            return importStudentsFromXlsx(filePath);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please use CSV or XLSX.");
        }
    }

    private List<Student> importStudentsFromCsv(String filePath) throws IOException {
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

    private List<Student> importStudentsFromXlsx(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        List<Integer> skippedRows = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (InputStream inputStream = Files.newInputStream(path);
                Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row))
                    continue;

                String studentCode = getCellValue(row.getCell(0));
                String firstName = getCellValue(row.getCell(1));
                String lastName = getCellValue(row.getCell(2));

                if (isNullOrEmpty(studentCode) && isNullOrEmpty(firstName) && isNullOrEmpty(lastName)) {
                    continue;
                }

                if (isNullOrEmpty(studentCode) || isNullOrEmpty(firstName) || isNullOrEmpty(lastName)) {
                    skippedRows.add(i + 1);
                    continue;
                }

                String fullName = firstName + " " + lastName;
                students.add(new Student(fullName, studentCode));
            }
        }

        if (!skippedRows.isEmpty()) {
            String skippedRowsStr = skippedRows.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            showErrorDialog("The following rows were skipped due to missing values: " + skippedRowsStr);
        }

        return students;
    }

    public void exportStudents(List<Student> students, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student ID");
            headerRow.createCell(1).setCellValue("Name");

            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getName());
            }

            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    public List<StudentGrade> importGrades(String filePath) throws IOException {
        List<StudentGrade> grades = new ArrayList<>();
        Path path = Paths.get(filePath);
        try (InputStream inputStream = Files.newInputStream(path);
                Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

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
                for (Grade grade : student.getGrades()) {
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

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    // Utility methods
    private boolean isRowEmpty(Row row) {
        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValue(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String getCellValue(Cell cell) {
        if (cell == null)
            return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Import Error");
        alert.setHeaderText("Some rows were skipped due to null values");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // File operations methods
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

    // Utility methods for finding entities
    public Student findStudentById(String studentId) {
        return studentMap.get(studentId);
    }

    public Assessment findAssessmentByName(String assessmentName) {
        return assessmentMap.get(assessmentName);
    }

    // Enum for file formats
    public enum FileFormat {
        CSV, EXCEL
    }
}