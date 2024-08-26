package com.gradeapp.util;

import com.gradeapp.model.Assessment;
import com.gradeapp.model.Student;
import com.gradeapp.model.StudentGrade;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.scene.control.Alert;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileHandler {
    private Map<String, Student> studentMap = new HashMap<>();
    private Map<String, Assessment> assessmentMap = new HashMap<>();

//    public List<Student> importStudents(String filePath) throws IOException {
//        List<Student> students = new ArrayList<>();
//        List<Integer> skippedRows = new ArrayList<>();
//        Path path = Paths.get(filePath);
//
//        try (InputStream inputStream = Files.newInputStream(path);
//             Workbook workbook = WorkbookFactory.create(inputStream)) {
//
//            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
//
//            // Iterate starting from the second row (index 1), assuming row 0 is the header
//            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                if (row == null) continue;
//
//                String studentCode = getCellValue(row.getCell(0)); // "Student Code" column
//                String firstName = getCellValue(row.getCell(1)); // "Student First Name" column
//                String lastName = getCellValue(row.getCell(2)); // "Student Surname" column
//
//                // Check if the relevant cells are all empty (we treat these rows as non-existent)
//                if ((studentCode == null || studentCode.trim().isEmpty()) &&
//                        (firstName == null || firstName.trim().isEmpty()) &&
//                        (lastName == null || lastName.trim().isEmpty())) {
//                    continue; // Skip this row
//                }
//
//                // If any of the critical fields are null or empty, skip and record the row
//                if (studentCode == null || studentCode.trim().isEmpty() ||
//                        firstName == null || firstName.trim().isEmpty() ||
//                        lastName == null || lastName.trim().isEmpty()) {
//                    skippedRows.add(i + 1); // Add row number to skippedRows (1-based index)
//                    continue; // Skip this row
//                }
//
//                // Combine first and last names for the full name
//                String fullName = firstName + " " + lastName;
//
//                Student student = new Student(fullName, studentCode);
//                students.add(student);
//            }
//        }
//
//        // If there are skipped rows, show an error dialog
//        if (!skippedRows.isEmpty()) {
//            String skippedRowsStr = skippedRows.stream()
//                    .map(String::valueOf)
//                    .collect(Collectors.joining(", "));
//            showErrorDialog("The following rows were skipped due to missing values: " + skippedRowsStr);
//        }
//
//        return students;
//    }

    public List<Student> importStudents(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        List<Integer> skippedRows = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (InputStream inputStream = Files.newInputStream(path);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Start iterating from the third row (index 2) to skip headers
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                String studentCode = getCellValue(row.getCell(0)); // "Student Code" column
                String firstName = getCellValue(row.getCell(1)); // "Student First Name" column
                String lastName = getCellValue(row.getCell(2)); // "Student Surname" column

                // Check if the relevant cells are all empty (we treat these rows as non-existent)
                if (isNullOrEmpty(studentCode) && isNullOrEmpty(firstName) && isNullOrEmpty(lastName)) {
                    continue; // Skip this row
                }

                // If any of the critical fields are null or empty, skip and record the row
                if (isNullOrEmpty(studentCode) || isNullOrEmpty(firstName) || isNullOrEmpty(lastName)) {
                    skippedRows.add(i + 1); // Add row number to skippedRows (1-based index)
                    continue; // Skip this row
                }

                // Combine first and last names for the full name
                String fullName = firstName + " " + lastName;

                Student student = new Student(fullName, studentCode);
                students.add(student);
            }
        }

        // If there are skipped rows, show an error dialog
        if (!skippedRows.isEmpty()) {
            String skippedRowsStr = skippedRows.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            showErrorDialog("The following rows were skipped due to missing values: " + skippedRowsStr);
        }

        return students;
    }

    private boolean isRowEmpty(Row row) {
        // Check if all cells in a row are empty
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

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Import Error");
        alert.setHeaderText("Some rows were skipped due to null values, please check that all the cells are correct and that there are no null values.");
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void exportStudents(List<Student> students, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student ID");
            headerRow.createCell(1).setCellValue("Name");

            // Add data rows
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

            // Write the workbook to the file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }


    // Utility method to get cell value
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

    public List<StudentGrade> importGrades(String filePath) throws IOException {
        List<StudentGrade> grades = new ArrayList<>();
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

    // Data export methods
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
