#!/bin/bash
# MarkBook+ Build Script
# Comprehensive build script for cross-platform deployment

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="MarkBook+"
VERSION="1.0"
BUILD_DIR="build"
DIST_DIR="dist"

# Functions
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# Check prerequisites
check_prerequisites() {
    print_header "Checking Prerequisites"

    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed"
        exit 1
    fi
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    print_success "Java $JAVA_VERSION detected"

    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed"
        exit 1
    fi
    print_success "Maven detected"

    echo ""
}

# Clean previous builds
clean_build() {
    print_header "Cleaning Previous Builds"

    mvn clean
    rm -rf "$BUILD_DIR" "$DIST_DIR"
    mkdir -p "$BUILD_DIR" "$DIST_DIR"

    print_success "Cleaned successfully"
    echo ""
}

# Compile and package
build_package() {
    print_header "Building Application"

    mvn clean package -DskipTests

    if [ $? -eq 0 ]; then
        print_success "Build completed successfully"
    else
        print_error "Build failed"
        exit 1
    fi

    echo ""
}

# Run tests
run_tests() {
    print_header "Running Tests"

    mvn test

    if [ $? -eq 0 ]; then
        print_success "All tests passed"
    else
        print_warning "Some tests failed"
    fi

    echo ""
}

# Create distribution package
create_distribution() {
    print_header "Creating Distribution Package"

    # Copy JAR file
    if [ -f "target/demo3-1.0-SNAPSHOT.jar" ]; then
        cp target/demo3-1.0-SNAPSHOT.jar "$DIST_DIR/markbook-plus.jar"
        print_success "Copied JAR file"
    fi

    # Copy launcher scripts
    cp run-markbook.sh "$DIST_DIR/"
    cp run-markbook.bat "$DIST_DIR/"
    chmod +x "$DIST_DIR/run-markbook.sh"
    print_success "Copied launcher scripts"

    # Copy README
    if [ -f "README.md" ]; then
        cp README.md "$DIST_DIR/"
        print_success "Copied README"
    fi

    # Create installation guide
    cat > "$DIST_DIR/INSTALL.txt" << 'EOF'
MarkBook+ Installation Guide
=============================

System Requirements:
- Java 21 or higher
- 500MB free disk space
- Windows, macOS, or Linux

Installation Steps:

1. Ensure Java 21 is installed:
   - Check: java -version
   - Download from: https://adoptium.net/

2. Run the application:

   On Linux/macOS:
   ./run-markbook.sh

   On Windows:
   run-markbook.bat

   Or run directly with Java:
   java -jar markbook-plus.jar

3. First run will create application data in:
   - Windows: %APPDATA%\MarkBookPlus
   - macOS: ~/Library/Application Support/MarkBookPlus
   - Linux: ~/.markbookplus

Features:
- Student management
- Course and assessment creation
- Grade tracking and analysis
- Import/Export functionality
- Automatic database backups
- Cross-platform compatibility

For help and documentation, see README.md

EOF
    print_success "Created installation guide"

    # Create version info
    cat > "$DIST_DIR/VERSION.txt" << EOF
MarkBook+ Version $VERSION
Build Date: $(date)
Java Version: $(java -version 2>&1 | head -n 1)
Platform: $(uname -s) $(uname -m)
EOF
    print_success "Created version file"

    echo ""
}

# Create archive
create_archive() {
    print_header "Creating Archive"

    ARCHIVE_NAME="markbook-plus-v${VERSION}-$(uname -s | tr '[:upper:]' '[:lower:]').tar.gz"

    cd "$DIST_DIR"
    tar -czf "../${ARCHIVE_NAME}" ./*
    cd ..

    print_success "Created archive: ${ARCHIVE_NAME}"
    print_info "Archive size: $(du -h ${ARCHIVE_NAME} | cut -f1)"

    echo ""
}

# Print build summary
print_summary() {
    print_header "Build Summary"

    echo "Application: $APP_NAME v$VERSION"
    echo "Build date: $(date)"
    echo ""
    echo "Distribution files:"
    ls -lh "$DIST_DIR"
    echo ""

    if [ -f "markbook-plus-v${VERSION}"*.tar.gz ]; then
        echo "Archive created:"
        ls -lh markbook-plus-v${VERSION}*.tar.gz
        echo ""
    fi

    print_success "Build completed successfully!"
    print_info "Distribution ready in: $DIST_DIR/"
    print_info "Run with: cd $DIST_DIR && ./run-markbook.sh"

    echo ""
}

# Main build process
main() {
    print_header "$APP_NAME Build System v$VERSION"
    echo ""

    # Parse arguments
    SKIP_TESTS=false
    SKIP_ARCHIVE=false

    while [[ $# -gt 0 ]]; do
        case $1 in
            --skip-tests)
                SKIP_TESTS=true
                shift
                ;;
            --skip-archive)
                SKIP_ARCHIVE=true
                shift
                ;;
            --help)
                echo "Usage: $0 [OPTIONS]"
                echo ""
                echo "Options:"
                echo "  --skip-tests     Skip running tests"
                echo "  --skip-archive   Skip creating archive"
                echo "  --help           Show this help message"
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                echo "Use --help for usage information"
                exit 1
                ;;
        esac
    done

    check_prerequisites
    clean_build
    build_package

    if [ "$SKIP_TESTS" = false ]; then
        run_tests
    else
        print_warning "Skipping tests"
        echo ""
    fi

    create_distribution

    if [ "$SKIP_ARCHIVE" = false ]; then
        create_archive
    else
        print_warning "Skipping archive creation"
        echo ""
    fi

    print_summary
}

# Run main
main "$@"
