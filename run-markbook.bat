@echo off
REM MarkBook+ Launcher Script for Windows
REM This script runs the MarkBook+ educational grading application

setlocal enabledelayedexpansion

:: Script configuration
set "APP_NAME=MarkBook+"
set "MAIN_CLASS=org.example.demo3.HelloApplication"
set "MIN_JAVA_VERSION=21"

:: Function to print colored messages (Windows 10+)
set "INFO=[INFO]"
set "WARNING=[WARNING]"
set "ERROR=[ERROR]"

:main
echo.
echo ========================================
echo %APP_NAME% Launcher
echo ========================================
echo.

:: Check Java installation
call :check_java
if errorlevel 1 goto :error

:: Parse command line arguments
if "%~1"=="" goto :run_default
if /i "%~1"=="-h" goto :show_help
if /i "%~1"=="--help" goto :show_help
if /i "%~1"=="-b" goto :build
if /i "%~1"=="--build" goto :build
if /i "%~1"=="-m" goto :run_maven
if /i "%~1"=="--maven" goto :run_maven
if /i "%~1"=="-j" goto :run_jar
if /i "%~1"=="--jar" goto :run_jar
if /i "%~1"=="-v" goto :show_version
if /i "%~1"=="--version" goto :show_version

echo %ERROR% Unknown option: %~1
echo Use --help for usage information
goto :error

:run_default
echo %INFO% Attempting to run %APP_NAME%...

:: Try Maven first
where mvn >nul 2>&1
if %errorlevel%==0 (
    call :run_maven
    goto :end
)

:: Try JAR file
if exist "target\demo3-1.0-SNAPSHOT.jar" (
    call :run_jar
    goto :end
)

echo %ERROR% Cannot run application. Try building first with: %~nx0 --build
goto :error

:check_java
echo %INFO% Checking Java installation...

where java >nul 2>&1
if errorlevel 1 (
    echo %ERROR% Java is not installed or not in PATH
    echo %INFO% Please install Java %MIN_JAVA_VERSION% or higher
    echo %INFO% Visit: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
    exit /b 1
)

:: Get Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION_STR=%%g
)

:: Remove quotes and extract major version
set JAVA_VERSION_STR=%JAVA_VERSION_STR:"=%
for /f "delims=." %%a in ("%JAVA_VERSION_STR%") do set JAVA_VERSION=%%a

echo %INFO% Detected Java version: %JAVA_VERSION%

if %JAVA_VERSION% LSS %MIN_JAVA_VERSION% (
    echo %ERROR% Java version %MIN_JAVA_VERSION% or higher is required
    echo %ERROR% Current version: %JAVA_VERSION%
    exit /b 1
)

echo %INFO% Java version check passed
exit /b 0

:check_maven
where mvn >nul 2>&1
if errorlevel 1 (
    echo %WARNING% Maven is not installed. Required for building from source.
    echo %INFO% Download from: https://maven.apache.org/download.cgi
    exit /b 1
)
exit /b 0

:build
echo %INFO% Building %APP_NAME% with Maven...

call :check_maven
if errorlevel 1 goto :error

call mvn clean package -DskipTests

if errorlevel 1 (
    echo %ERROR% Build failed
    goto :error
)

echo %INFO% Build completed successfully
goto :end

:run_maven
echo %INFO% Running %APP_NAME% using Maven...

call :check_maven
if errorlevel 1 goto :error

call mvn javafx:run
goto :end

:run_jar
set "JAR_FILE=target\demo3-1.0-SNAPSHOT.jar"

if not exist "%JAR_FILE%" (
    echo %ERROR% JAR file not found at %JAR_FILE%
    echo %INFO% Build the application first with: %~nx0 --build
    goto :error
)

echo %INFO% Running %APP_NAME% from JAR...
java -jar "%JAR_FILE%"
goto :end

:show_help
echo.
echo %APP_NAME% - Educational Grading and Assessment Management
echo.
echo Usage: %~nx0 [OPTION]
echo.
echo Options:
echo   (no option)   Run the application (build if necessary)
echo   -b, --build   Build the application using Maven
echo   -m, --maven   Run using Maven (mvn javafx:run)
echo   -j, --jar     Run from JAR file
echo   -h, --help    Display this help message
echo   -v, --version Show application version
echo.
echo Examples:
echo   %~nx0              # Run the application
echo   %~nx0 --build      # Build the application
echo   %~nx0 --maven      # Run using Maven
echo.
echo Requirements:
echo   - Java %MIN_JAVA_VERSION% or higher
echo   - Maven (for building)
echo   - JavaFX %MIN_JAVA_VERSION%
echo.
echo For more information, visit the project repository or README.md
echo.
goto :end

:show_version
echo %APP_NAME% version 1.0
goto :end

:error
echo.
echo %ERROR% Script execution failed
pause
exit /b 1

:end
endlocal
exit /b 0
