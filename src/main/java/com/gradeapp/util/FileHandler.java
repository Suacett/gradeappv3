package com.gradeapp.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;

import javafx.scene.control.Alert;

/**
 * Handles file operations related to importing and exporting students, assessments, and grades.
 * Supports CSV and XLSX file formats for import/export functionalities.
 */
public class FileHandler {
    private Map<String, Student> studentMap = new HashMap<>();
    private Map<String, Assessment> assessmentMap = new HashMap<>();

    /**
     * Imports students from a specified file path.
     * Supports CSV and XLSX file formats.
     *
     * @param filePath The path to the file containing student data.
     * @return A list of imported Student objects.
     * @throws IOException If an I/O error occurs during file reading.
     * @throws IllegalArgumentException If the file format is unsupported.
     */
    public List<Student> importStudents(String filePath) throws IOException {
        if (filePath.endsWith(".csv")) {
            return importStudentsFromCsv(filePath);
        } else if (filePath.endsWith(".xlsx")) {
            return importStudentsFromXlsx(filePath);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please use CSV or XLSX.");
        }
    }

    /**
     * Imports students from a CSV file.
     *
     * @param filePath The path to the CSV file.
     * @return A list of imported Student objects.
     * @throws IOException If an I/O error occurs during file reading.
     */
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

    /**
     * Imports students from an XLSX (Excel) file.
     *
     * @param filePath The path to the XLSX file.
     * @return A list of imported Student objects.
     * @throws IOException If an I/O error occurs during file reading.
     */
    private List<Student> importStudentsFromXlsx(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        List<Integer> skippedRows = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (InputStream inputStream = Files.newInputStream(path);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 2; i <= sheet.getLastRowNum(); i++) { // Assuming first two rows are headers
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
                    skippedRows.add(i + 1); // Row numbers start at 1
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

    /**
     * Exports a list of students to an XLSX file.
     *
     * @param students The list of students to export.
     * @param filePath The destination file path for the exported XLSX file.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void exportStudents(List<Student> students, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student ID");
            headerRow.createCell(1).setCellValue("Name");

            // Populate student data
            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getName());
            }

            // Auto-size columns for better readability
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    /**
     * Exports grades of students to an XLSX file.
     *
     * @param students The list of students whose grades are to be exported.
     * @param filePath The destination file path for the exported XLSX file.
     * @param format   The file format (CSV or EXCEL) for export.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void exportGrades(List<Student> students, String filePath, FileFormat format) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Grades");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student Name");
            headerRow.createCell(1).setCellValue("Student ID");
            headerRow.createCell(2).setCellValue("Assessment");
            headerRow.createCell(3).setCellValue("Grade");

            // Populate grade data
            int rowNum = 1;
            for (Student student : students) {
                for (Grade grade : student.getGrades()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(student.getName());
                    row.createCell(1).setCellValue(
                            student.getStudentId() != null ? student.getStudentId() : "");
                    row.createCell(2).setCellValue(grade.getAssessment().getName());
                    row.createCell(3).setCellValue(grade.getScore());
                }
            }

            // Auto-size columns for better readability
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    /**
     * Exports a list of assessments to an XLSX file.
     *
     * @param assessments The list of assessments to export.
     * @param filePath    The destination file path for the exported XLSX file.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void exportAssessments(List<Assessment> assessments, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Assessments");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Assessment Name");
            headerRow.createCell(1).setCellValue("Weight");
            headerRow.createCell(2).setCellValue("Max Score");

            // Populate assessment data
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

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    // ----------------------------- Utility Methods -----------------------------

    /**
     * Checks if a given row in an Excel sheet is empty.
     *
     * @param row The Excel row to check.
     * @return True if the row is empty; false otherwise.
     */
    private boolean isRowEmpty(Row row) {
        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValue(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if a string is null or empty after trimming.
     *
     * @param value The string to check.
     * @return True if the string is null or empty; false otherwise.
     */
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Retrieves the string value of an Excel cell based on its type.
     *
     * @param cell The Excel cell.
     * @return The string representation of the cell's value.
     */
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

    /**
     * Displays an error dialog with the specified message.
     *
     * @param message The error message to display.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Import Error");
        alert.setHeaderText("Some rows were skipped due to null values");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ----------------------------- File Operations -----------------------------

    /**
     * Creates a directory at the specified path, including any necessary parent directories.
     *
     * @param dirPath The path of the directory to create.
     * @throws IOException If an I/O error occurs during directory creation.
     */
    public void createDirectory(String dirPath) throws IOException {
        Files.createDirectories(Paths.get(dirPath));
    }

    // ----------------------------- File Format Enumeration -----------------------------

    /**
     * Enumeration representing supported file formats for import/export operations.
     */
    public enum FileFormat {
        CSV, EXCEL
    }
}
