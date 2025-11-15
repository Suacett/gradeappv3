#!/bin/bash
# MarkBook+ Launcher Script for Linux/macOS
# This script runs the MarkBook+ educational grading application

set -e  # Exit on error

# Script configuration
APP_NAME="MarkBook+"
MAIN_CLASS="org.example.demo3.HelloApplication"
MIN_JAVA_VERSION=21

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check Java version
check_java() {
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        print_info "Please install Java $MIN_JAVA_VERSION or higher"
        print_info "Visit: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/"
        exit 1
    fi

    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)

    if [ -z "$JAVA_VERSION" ]; then
        # Try alternative version detection
        JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[0-9]+')
    fi

    print_info "Detected Java version: $JAVA_VERSION"

    if [ "$JAVA_VERSION" -lt "$MIN_JAVA_VERSION" ]; then
        print_error "Java version $MIN_JAVA_VERSION or higher is required"
        print_error "Current version: $JAVA_VERSION"
        exit 1
    fi

    print_info "Java version check passed ✓"
}

# Function to check if Maven is available
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_warning "Maven is not installed. Required for building from source."
        return 1
    fi
    return 0
}

# Function to build the application
build_app() {
    print_info "Building $APP_NAME with Maven..."

    if ! check_maven; then
        print_error "Cannot build: Maven is not installed"
        print_info "Install Maven: sudo apt install maven (Ubuntu/Debian) or brew install maven (macOS)"
        return 1
    fi

    mvn clean package -DskipTests

    if [ $? -eq 0 ]; then
        print_info "Build completed successfully ✓"
        return 0
    else
        print_error "Build failed"
        return 1
    fi
}

# Function to run using Maven
run_with_maven() {
    print_info "Running $APP_NAME using Maven..."
    mvn javafx:run
}

# Function to run using JAR file
run_with_jar() {
    local JAR_FILE="target/demo3-1.0-SNAPSHOT.jar"

    if [ ! -f "$JAR_FILE" ]; then
        print_warning "JAR file not found at $JAR_FILE"
        return 1
    fi

    print_info "Running $APP_NAME from JAR..."
    java -jar "$JAR_FILE"
}

# Function to display help
show_help() {
    cat << EOF
$APP_NAME - Educational Grading and Assessment Management

Usage: $0 [OPTION]

Options:
  (no option)   Run the application (build if necessary)
  -b, --build   Build the application using Maven
  -m, --maven   Run using Maven (mvn javafx:run)
  -j, --jar     Run from JAR file
  -h, --help    Display this help message
  -v, --version Show application version

Examples:
  $0              # Run the application
  $0 --build      # Build the application
  $0 --maven      # Run using Maven

Requirements:
  - Java $MIN_JAVA_VERSION or higher
  - Maven (for building)
  - JavaFX $MIN_JAVA_VERSION

For more information, visit the project repository or README.md
EOF
}

# Main script logic
main() {
    print_info "Starting $APP_NAME..."
    print_info "Platform: $(uname -s) $(uname -m)"

    # Check Java installation
    check_java

    # Parse command line arguments
    case "${1:-}" in
        -h|--help)
            show_help
            exit 0
            ;;
        -b|--build)
            build_app
            exit $?
            ;;
        -m|--maven)
            check_maven || exit 1
            run_with_maven
            exit $?
            ;;
        -j|--jar)
            run_with_jar || exit 1
            exit $?
            ;;
        -v|--version)
            print_info "$APP_NAME version 1.0"
            exit 0
            ;;
        "")
            # Default: Try Maven first, then JAR
            if check_maven; then
                run_with_maven
            elif run_with_jar; then
                :  # Success
            else
                print_error "Cannot run application. Try building first with: $0 --build"
                exit 1
            fi
            ;;
        *)
            print_error "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
}

# Run main function
main "$@"
