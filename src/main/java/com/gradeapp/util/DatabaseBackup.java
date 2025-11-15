package com.gradeapp.util;

import com.gradeapp.config.AppConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for backing up and restoring the application database.
 * Provides automatic backup functionality with timestamped backup files.
 */
public class DatabaseBackup {
    private static final String BACKUP_FOLDER_NAME = "backups";
    private static final String BACKUP_PREFIX = "gradeapp_backup_";
    private static final String BACKUP_EXTENSION = ".db";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final int MAX_BACKUPS = 10; // Maximum number of backups to keep

    private final Path backupDirectory;
    private final AppConfig config;

    /**
     * Creates a new DatabaseBackup instance.
     */
    public DatabaseBackup() {
        this.config = AppConfig.getInstance();
        this.backupDirectory = config.getAppDirectory().resolve(BACKUP_FOLDER_NAME);

        // Create backup directory if it doesn't exist
        try {
            Files.createDirectories(backupDirectory);
        } catch (IOException e) {
            System.err.println("Failed to create backup directory: " + e.getMessage());
        }
    }

    /**
     * Creates a backup of the current database.
     *
     * @return Path to the backup file, or null if backup failed
     * @throws IOException if an I/O error occurs
     */
    public Path createBackup() throws IOException {
        Path databasePath = config.getDatabasePath();

        // Check if database file exists
        if (!Files.exists(databasePath)) {
            throw new IOException("Database file does not exist: " + databasePath);
        }

        // Generate backup filename with timestamp
        String timestamp = DATE_FORMAT.format(new Date());
        String backupFileName = BACKUP_PREFIX + timestamp + BACKUP_EXTENSION;
        Path backupPath = backupDirectory.resolve(backupFileName);

        // Copy database file to backup location
        Files.copy(databasePath, backupPath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Database backed up to: " + backupPath);

        // Clean up old backups
        cleanupOldBackups();

        return backupPath;
    }

    /**
     * Creates an automatic backup with error handling.
     * Safe to call without exception handling.
     *
     * @return true if backup was successful, false otherwise
     */
    public boolean createBackupSafe() {
        try {
            createBackup();
            return true;
        } catch (IOException e) {
            System.err.println("Automatic backup failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Restores the database from a backup file.
     *
     * @param backupPath Path to the backup file
     * @throws IOException if an I/O error occurs
     */
    public void restoreBackup(Path backupPath) throws IOException {
        if (!Files.exists(backupPath)) {
            throw new IOException("Backup file does not exist: " + backupPath);
        }

        Path databasePath = config.getDatabasePath();

        // Create a safety backup before restoring
        if (Files.exists(databasePath)) {
            Path safetyBackup = backupDirectory.resolve("pre_restore_" +
                DATE_FORMAT.format(new Date()) + BACKUP_EXTENSION);
            Files.copy(databasePath, safetyBackup, StandardCopyOption.REPLACE_EXISTING);
        }

        // Restore the backup
        Files.copy(backupPath, databasePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Database restored from: " + backupPath);
    }

    /**
     * Gets a list of all available backup files.
     *
     * @return List of backup file paths, sorted by date (newest first)
     * @throws IOException if an I/O error occurs
     */
    public List<Path> getBackupList() throws IOException {
        if (!Files.exists(backupDirectory)) {
            return new ArrayList<>();
        }

        try (Stream<Path> paths = Files.list(backupDirectory)) {
            return paths
                .filter(path -> path.getFileName().toString().startsWith(BACKUP_PREFIX))
                .filter(path -> path.getFileName().toString().endsWith(BACKUP_EXTENSION))
                .sorted((p1, p2) -> {
                    try {
                        return Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1));
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
        }
    }

    /**
     * Deletes old backups, keeping only the most recent MAX_BACKUPS files.
     *
     * @throws IOException if an I/O error occurs
     */
    private void cleanupOldBackups() throws IOException {
        List<Path> backups = getBackupList();

        if (backups.size() > MAX_BACKUPS) {
            // Delete oldest backups
            for (int i = MAX_BACKUPS; i < backups.size(); i++) {
                Files.deleteIfExists(backups.get(i));
                System.out.println("Deleted old backup: " + backups.get(i).getFileName());
            }
        }
    }

    /**
     * Deletes a specific backup file.
     *
     * @param backupPath Path to the backup file to delete
     * @return true if the file was deleted, false otherwise
     */
    public boolean deleteBackup(Path backupPath) {
        try {
            return Files.deleteIfExists(backupPath);
        } catch (IOException e) {
            System.err.println("Failed to delete backup: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the total size of all backup files.
     *
     * @return Total size in bytes
     */
    public long getTotalBackupSize() {
        try {
            List<Path> backups = getBackupList();
            long totalSize = 0;

            for (Path backup : backups) {
                totalSize += Files.size(backup);
            }

            return totalSize;
        } catch (IOException e) {
            System.err.println("Failed to calculate backup size: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Formats a file size in bytes to a human-readable string.
     *
     * @param bytes Size in bytes
     * @return Formatted string (e.g., "1.5 MB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Gets the backup directory path.
     *
     * @return Path to the backup directory
     */
    public Path getBackupDirectory() {
        return backupDirectory;
    }
}
