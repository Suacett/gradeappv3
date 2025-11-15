# MarkBook+ Quick Start Guide

## First Time Setup

### Step 1: Install Java 21

**Check if Java is installed:**
```bash
java -version
```

If Java 21 or higher is installed, you'll see something like:
```
openjdk version "21.0.x"
```

**If Java is not installed:**
- Download from: https://adoptium.net/
- Install Java 21 or higher

### Step 2: Install Maven (Required for building)

**Check if Maven is installed:**
```bash
mvn -version
```

**If Maven is not installed:**

**Linux (Fedora/RHEL/CentOS):**
```bash
sudo dnf install maven
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt install maven
```

**macOS:**
```bash
brew install maven
```

**Windows:**
- Download from: https://maven.apache.org/download.cgi
- Extract and add to PATH

### Step 3: Build the Application

Navigate to the project directory and build:

```bash
cd gradeappv3
mvn clean package
```

This will:
- Download all required dependencies
- Compile the application
- Create the executable JAR file
- Take 2-5 minutes on first run

**Wait for:** `BUILD SUCCESS`

### Step 4: Run the Application

**Linux/macOS:**
```bash
./run-markbook.sh
```

**Windows:**
```batch
run-markbook.bat
```

**Or run directly with Maven:**
```bash
mvn javafx:run
```

**Or run the JAR file:**
```bash
java -jar target/demo3-1.0-SNAPSHOT.jar
```

## Troubleshooting First Run

### "No plugin found for prefix 'javafx'"

**Cause:** Project hasn't been built yet.

**Solution:** Run the build first:
```bash
mvn clean package
```

Then run the application.

### Build Fails - Network Issues

**Cause:** Maven can't download dependencies.

**Solution:**
1. Check internet connection
2. Try again (Maven will resume download)
3. If behind a proxy, configure Maven proxy settings

### Build Fails - Java Version

**Error:** `Source option X is no longer supported`

**Cause:** Java version is too old.

**Solution:** Install Java 21 or higher.

### JavaFX Native Library Errors (Linux)

**Error:** `Error initializing QuantumRenderer`

**Solution:** Install OpenJFX:

**Fedora:**
```bash
sudo dnf install java-21-openjfx
```

**Ubuntu:**
```bash
sudo apt install openjfx
```

## After First Build

Once you've successfully built the project once, you can simply run:

```bash
./run-markbook.sh    # Linux/macOS
run-markbook.bat      # Windows
```

The launcher scripts will automatically rebuild if needed.

## Quick Command Reference

```bash
# Build the application
mvn clean package

# Run with Maven
mvn javafx:run

# Run with launcher (auto-builds if needed)
./run-markbook.sh

# Build and create distribution
./build.sh

# Clean build artifacts
mvn clean
```

## Data Location

Your database and settings will be stored at:

- **Windows:** `%APPDATA%\MarkBookPlus`
- **Linux:** `~/.markbookplus`
- **macOS:** `~/Library/Application Support/MarkBookPlus`

## Need Help?

- Check the main README.md for detailed documentation
- Log files are at: `~/.markbookplus/markbook.log` (Linux/Mac)
- Report issues on the project GitHub page

---

**You're all set! Welcome to MarkBook+** 🎓
