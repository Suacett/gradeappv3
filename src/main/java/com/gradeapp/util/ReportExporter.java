package com.gradeapp.util;

import com.gradeapp.controller.ReportController.ReportFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class ReportExporter {

    public void exportReport(Map<String, Object> report, String filePath, ReportFormat format) {
        switch (format) {
            case XLS:
                exportToExcel(report, filePath);
                break;
            case PDF:
                exportToPdf(report, filePath);
                break;
            case CSV:
                exportToCsv(report, filePath);
                break;
        }
    }

    private void exportToExcel(Map<String, Object> report, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report");
            int rowNum = 0;
            for (Map.Entry<String, Object> entry : report.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue().toString());
            }
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportToPdf(Map<String, Object> report, String filePath) {
        // Implement PDF export logic here

    }

    private void exportToCsv(Map<String, Object> report, String filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (Map.Entry<String, Object> entry : report.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue().toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
