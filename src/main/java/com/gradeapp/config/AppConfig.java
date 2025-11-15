package com.gradeapp.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Application configuration manager that handles platform-specific settings
 * and provides cross-platform compatibility for file paths and database locations.
 */
public class AppConfig {
    private static final String APP_NAME = "MarkBookPlus";
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String DB_FILE_NAME = "gradeapp.db";

    private static AppConfig instance;
    private Properties properties;
    private Path appDirectory;
    private Path configFilePath;
    private Path databasePath;

    /**
     * Private constructor for singleton pattern.
     * Initializes the configuration and creates necessary directories.
     */
    private AppConfig() {
        initializeAppDirectory();
        loadConfiguration();
    }

    /**
     * Gets the singleton instance of AppConfig.
     * @return The AppConfig instance
     */
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    /**
     * Initializes the application directory based on the operating system.
     * Windows: %APPDATA%/MarkBookPlus
     * Linux/Mac: ~/.markbookplus
     */
    private void initializeAppDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            // Windows: Use AppData/Roaming
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                appDirectory = Paths.get(appData, APP_NAME);
            } else {
                appDirectory = Paths.get(userHome, "AppData", "Roaming", APP_NAME);
            }
        } else if (os.contains("mac")) {
            // macOS: Use Application Support
            appDirectory = Paths.get(userHome, "Library", "Application Support", APP_NAME);
        } else {
            // Linux and others: Use hidden directory in home
            appDirectory = Paths.get(userHome, ".markbookplus");
        }

        // Create directory if it doesn't exist
        try {
            Files.createDirectories(appDirectory);
        } catch (IOException e) {
            System.err.println("Failed to create application directory: " + e.getMessage());
            // Fallback to current directory
            appDirectory = Paths.get(System.getProperty("user.dir"));
        }

        configFilePath = appDirectory.resolve(CONFIG_FILE_NAME);
        databasePath = appDirectory.resolve(DB_FILE_NAME);
    }

    /**
     * Loads the configuration from the properties file.
     * Creates a default configuration if the file doesn't exist.
     */
    private void loadConfiguration() {
        properties = new Properties();

        if (Files.exists(configFilePath)) {
            try (FileInputStream fis = new FileInputStream(configFilePath.toFile())) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Failed to load configuration: " + e.getMessage());
                createDefaultConfiguration();
            }
        } else {
            createDefaultConfiguration();
            saveConfiguration();
        }
    }

    /**
     * Creates default configuration properties.
     */
    private void createDefaultConfiguration() {
        properties.setProperty("app.version", "1.0");
        properties.setProperty("database.path", databasePath.toString());
        properties.setProperty("import.default.format", "xlsx");
        properties.setProperty("export.default.format", "xlsx");
        properties.setProperty("ui.theme", "default");
        properties.setProperty("ui.maximize.on.start", "true");
    }

    /**
     * Saves the current configuration to the properties file.
     */
    public void saveConfiguration() {
        try (FileOutputStream fos = new FileOutputStream(configFilePath.toFile())) {
            properties.store(fos, "MarkBook+ Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }

    /**
     * Gets the application directory path.
     * @return Path to the application directory
     */
    public Path getAppDirectory() {
        return appDirectory;
    }

    /**
     * Gets the database file path.
     * @return Path to the database file
     */
    public Path getDatabasePath() {
        // Check if custom database path is set in properties
        String customPath = properties.getProperty("database.path");
        if (customPath != null && !customPath.isEmpty()) {
            return Paths.get(customPath);
        }
        return databasePath;
    }

    /**
     * Gets the JDBC URL for the SQLite database.
     * @return JDBC connection string
     */
    public String getDatabaseUrl() {
        return "jdbc:sqlite:" + getDatabasePath().toString();
    }

    /**
     * Sets a custom database path.
     * @param path The new database path
     */
    public void setDatabasePath(String path) {
        properties.setProperty("database.path", path);
        saveConfiguration();
    }

    /**
     * Gets a property value.
     * @param key The property key
     * @return The property value, or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Gets a property value with a default.
     * @param key The property key
     * @param defaultValue The default value if key not found
     * @return The property value or default
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Sets a property value.
     * @param key The property key
     * @param value The property value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Gets the operating system name.
     * @return The OS name
     */
    public String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    /**
     * Checks if the application is running on Windows.
     * @return true if Windows, false otherwise
     */
    public boolean isWindows() {
        return getOperatingSystem().toLowerCase().contains("win");
    }

    /**
     * Checks if the application is running on Linux.
     * @return true if Linux, false otherwise
     */
    public boolean isLinux() {
        return getOperatingSystem().toLowerCase().contains("linux");
    }

    /**
     * Checks if the application is running on macOS.
     * @return true if macOS, false otherwise
     */
    public boolean isMacOS() {
        return getOperatingSystem().toLowerCase().contains("mac");
    }

    /**
     * Gets system information for debugging.
     * @return String containing system information
     */
    public String getSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Operating System: ").append(getOperatingSystem()).append("\n");
        info.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        info.append("JavaFX Version: ").append(System.getProperty("javafx.version", "Unknown")).append("\n");
        info.append("App Directory: ").append(appDirectory).append("\n");
        info.append("Database Path: ").append(getDatabasePath()).append("\n");
        return info.toString();
    }
}
