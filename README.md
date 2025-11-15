# MarkBook+ 📚

**Cross-Platform Educational Grading and Assessment Management System**

MarkBook+ is a comprehensive desktop application designed for teachers and educators to manage students, courses, assessments, and grades efficiently. Built with JavaFX, it provides a powerful yet user-friendly interface for educational data management.

![Java](https://img.shields.io/badge/Java-21-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.4-blue)
![License](https://img.shields.io/badge/License-MIT-green)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey)

---

## ✨ Features

### Core Functionality
- **Student Management**: Add, edit, and organize students with CSV/XLSX import support
- **Course Management**: Create courses with customizable learning outcomes linked to educational standards
- **Assessment Creation**: Design assessments with weighted rubrics and multi-part structures
- **Grade Tracking**: Record grades with detailed feedback and automatic calculations
- **Data Analysis**: Statistical analysis with visual charts and performance metrics
- **Import/Export**: Seamless CSV and Excel file handling for data operations
- **Archive System**: Archive completed courses and maintain historical data

### New Cross-Platform Features
- **Universal Database Path Management**: Automatic platform-specific data storage
  - Windows: `%APPDATA%\MarkBookPlus`
  - macOS: `~/Library/Application Support/MarkBookPlus`
  - Linux: `~/.markbookplus`
- **Automatic Database Backups**: Keeps up to 10 timestamped backups
- **Enhanced Logging**: File and console logging for debugging and error tracking
- **Configuration System**: Platform-aware settings management
- **Cross-Platform Launchers**: Easy-to-use scripts for all operating systems
- **Improved Build System**: Comprehensive Maven configuration with packaging support

---

## 🖥️ System Requirements

### Required
- **Java**: Version 21 or higher ([Download here](https://adoptium.net/))
- **Memory**: Minimum 512MB RAM (1GB+ recommended)
- **Storage**: 500MB free disk space
- **OS**: Windows 10+, macOS 10.14+, or Linux (Ubuntu 20.04+, Fedora 30+, etc.)

### Optional (for building from source)
- **Maven**: Version 3.8+ ([Download here](https://maven.apache.org/))
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code (with Java extensions)

---

## 🚀 Quick Start

### Option 1: Using Pre-built Application (Easiest)

#### Windows
```batch
run-markbook.bat
```

#### Linux/macOS
```bash
./run-markbook.sh
```

### Option 2: Building from Source

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/gradeappv3.git
cd gradeappv3
```

#### 2. Build with Maven
```bash
# Linux/macOS
./build.sh

# Or manually
mvn clean package
```

#### 3. Run the Application
```bash
# Using Maven
mvn javafx:run

# Or using the launcher script
./run-markbook.sh  # Linux/macOS
run-markbook.bat   # Windows
```

---

## 📖 Detailed Usage Guide

### Running the Application

#### Linux/macOS
```bash
# Run with default settings
./run-markbook.sh

# Build before running
./run-markbook.sh --build

# Run using Maven
./run-markbook.sh --maven

# Run from JAR file
./run-markbook.sh --jar

# Show help
./run-markbook.sh --help
```

#### Windows
```batch
REM Run with default settings
run-markbook.bat

REM Build before running
run-markbook.bat --build

REM Run using Maven
run-markbook.bat --maven

REM Show help
run-markbook.bat --help
```

### First Time Setup

1. **Launch the application** using one of the methods above
2. **Database initialization**: The app will automatically create a database in the appropriate location for your OS
3. **Start using**: Begin by creating a course or importing students

### Managing Students

1. **Add Students Manually**: Click "Add Student" and enter details
2. **Import from File**:
   - Prepare CSV/XLSX with columns: Student ID, Name
   - Click "Import Students" and select your file
   - Review and confirm import
3. **Organize into Classes**: Create classes and assign students

### Creating Assessments

1. **Navigate to Courses** and select a course
2. **Click "Add Assessment"**
3. **Configure**:
   - Name and description
   - Weight (percentage of final grade)
   - Maximum score
   - Link to learning outcomes with weighted rubrics
4. **Add Parts** (optional): Break down complex assessments into parts

### Recording Grades

1. **Select an Assessment** from the marking view
2. **Choose a Student** from the class roster
3. **Enter Grade** and optional feedback
4. **Automatic Calculation**: Weighted scores are calculated automatically
5. **View Statistics**: See class performance metrics and charts

### Importing/Exporting Data

#### Import Students
- **Supported Formats**: CSV, XLSX
- **Required Columns**: Student ID, Name (First Last)
- **Menu**: File → Import Students

#### Export Data
- **Export Students**: File → Export Students (XLSX)
- **Export Grades**: File → Export Grades (XLSX)
- **Export Assessments**: File → Export Assessments (XLSX)

### Database Backups

Backups are created automatically and stored in:
- **Windows**: `%APPDATA%\MarkBookPlus\backups`
- **macOS**: `~/Library/Application Support/MarkBookPlus/backups`
- **Linux**: `~/.markbookplus/backups`

To manually create a backup, use the backup utility in the application or access the backup directory.

---

## 🛠️ Development

### Project Structure

```
gradeappv3/
├── src/main/
│   ├── java/
│   │   ├── org/example/demo3/          # Main application entry
│   │   └── com/gradeapp/
│   │       ├── config/                  # Configuration management
│   │       │   └── AppConfig.java       # Cross-platform config
│   │       ├── controller/              # UI controllers (17 files)
│   │       ├── model/                   # Data models (7 files)
│   │       ├── database/                # Database operations
│   │       │   └── Database.java        # SQLite database manager
│   │       └── util/                    # Utility classes
│   │           ├── FileHandler.java     # Import/export logic
│   │           ├── Calculator.java      # Grade calculations
│   │           ├── ChartGenerator.java  # Data visualization
│   │           ├── DatabaseBackup.java  # Backup utility
│   │           └── Logger.java          # Logging system
│   └── resources/
│       └── org/example/demo3/
│           ├── *.fxml                   # UI layouts (16 files)
│           ├── styles.css               # Styling
│           └── icons/                   # Application icons
├── run-markbook.sh                      # Linux/macOS launcher
├── run-markbook.bat                     # Windows launcher
├── build.sh                             # Build automation script
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

### Technology Stack

- **Language**: Java 21
- **UI Framework**: JavaFX 21.0.4
- **Database**: SQLite 3.43.0.0 (embedded)
- **Build Tool**: Maven 3.x
- **Design Pattern**: Model-View-Controller (MVC)

### Key Dependencies

- **JavaFX**: UI framework with controls, FXML, graphics
- **SQLite JDBC**: Database connectivity
- **Apache POI**: Excel file handling
- **ControlsFX**: Enhanced UI controls
- **ValidatorFX**: Input validation
- **Jackson**: JSON processing
- **Apache Commons**: Utilities (IO, Text, Compress)

### Building from Source

#### Compile Only
```bash
mvn compile
```

#### Run Tests
```bash
mvn test
```

#### Package Application
```bash
mvn clean package
```

#### Run with Maven
```bash
mvn javafx:run
```

#### Create Distribution
```bash
./build.sh              # Full build with archive
./build.sh --skip-tests # Skip tests
```

### Code Style

- Follow standard Java naming conventions
- Use Javadoc for all public methods
- Maintain MVC separation of concerns
- Write unit tests for new features

---

## 🤝 Contributing

We welcome contributions! Here's how to get started:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/YourFeature
   ```
3. **Make your changes**
   - Write clean, documented code
   - Add tests if applicable
   - Update README if needed
4. **Commit your changes**
   ```bash
   git commit -m "Add: Your feature description"
   ```
5. **Push to your fork**
   ```bash
   git push origin feature/YourFeature
   ```
6. **Create a Pull Request**

### Contribution Guidelines

- Ensure cross-platform compatibility (test on Windows, Linux, or macOS)
- Follow existing code style and patterns
- Document new features in README
- Add Javadoc comments for public APIs
- Test thoroughly before submitting

---

## 🐛 Troubleshooting

### Application won't start

**Check Java version:**
```bash
java -version
# Should show version 21 or higher
```

**Solution**: Install or update Java from [Adoptium](https://adoptium.net/)

### Database errors

**Symptoms**: "Failed to connect to database" or similar errors

**Solutions**:
1. Check write permissions in the application data directory
2. Ensure no other instance is running
3. Check log file at `~/.markbookplus/markbook.log` (Linux/Mac) or `%APPDATA%\MarkBookPlus\markbook.log` (Windows)

### Import/Export issues

**Problem**: Files won't import or export

**Solutions**:
1. Verify file format (CSV or XLSX)
2. Check file permissions
3. Ensure required columns are present for imports
4. Check log file for specific errors

### Build failures

**Problem**: Maven build fails

**Solutions**:
```bash
# Clear Maven cache
mvn clean

# Update dependencies
mvn dependency:purge-local-repository

# Rebuild
mvn clean install
```

### Platform-Specific Issues

**Linux**: If JavaFX native libraries fail to load:
```bash
sudo apt install openjfx
# Or
sudo dnf install openjfx
```

**macOS**: If security prevents running:
```bash
xattr -cr run-markbook.sh
chmod +x run-markbook.sh
```

**Windows**: If script execution is disabled:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

---

## 📝 Version History

### Version 1.0 (Current)
- ✅ Cross-platform compatibility (Windows, Linux, macOS)
- ✅ Platform-specific data storage
- ✅ Automatic database backups
- ✅ Enhanced logging system
- ✅ Configuration management
- ✅ Launcher scripts for all platforms
- ✅ Improved build system
- ✅ Comprehensive documentation

### Previous Versions
- Initial release with core functionality

---

## 👥 Authors & Contributors

- **Tom Baldwin** - Backend development and database architecture
- **Ben Cartland** - Project management and documentation
- **Contributors** - See [CONTRIBUTORS.md](CONTRIBUTORS.md) for full list

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

```
MIT License

Copyright (c) 2024 MarkBook+ Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

## 🙏 Acknowledgments

- **JavaFX Community** for the excellent UI framework
- **Apache Software Foundation** for POI and other libraries
- **SQLite Team** for the robust embedded database
- **All Contributors** who have helped improve this project
- **Educators** who provided feedback and feature requests

---

## 📞 Support

### Getting Help

- **Documentation**: Read this README and inline code documentation
- **Issues**: Report bugs on the [Issues page](https://github.com/yourusername/gradeappv3/issues)
- **Discussions**: Join conversations in [Discussions](https://github.com/yourusername/gradeappv3/discussions)

### Reporting Bugs

When reporting bugs, please include:
1. Your operating system and version
2. Java version (`java -version`)
3. Steps to reproduce the issue
4. Expected vs actual behavior
5. Relevant log file excerpts
6. Screenshots if applicable

### Feature Requests

We love hearing your ideas! Please submit feature requests with:
1. Clear description of the feature
2. Use case and benefits
3. Mockups or examples (if applicable)

---

## 🔮 Roadmap

Future enhancements under consideration:

- [ ] Web-based version for remote access
- [ ] Mobile companion app
- [ ] Cloud synchronization
- [ ] Advanced analytics and reporting
- [ ] Multi-language support
- [ ] Dark mode theme
- [ ] Plugin system for extensions
- [ ] Integration with learning management systems (LMS)
- [ ] Bulk grade import from external systems
- [ ] Email notifications for grade updates

---

## 📊 Project Statistics

- **Total Lines of Code**: ~10,000+
- **Controllers**: 17
- **Models**: 7
- **Utility Classes**: 5
- **FXML Views**: 16
- **Supported Platforms**: 3 (Windows, Linux, macOS)
- **Supported File Formats**: 2 (CSV, XLSX)

---

**Made with ❤️ for educators everywhere**

*MarkBook+ - Making grade management simple and efficient*
