# MarkBook+ UI Usage Examples

## Quick Integration Guide

This document shows you exactly how to use the new UI improvements in your existing code.

---

## 🚀 Running the UI Test

To verify everything works correctly:

```bash
# Compile and run the UI test
mvn clean compile
mvn exec:java -Dexec.mainClass="com.gradeapp.util.UITest"
```

This will open a test window showing all UI components.

---

## 📝 Common Use Cases

### 1. Showing Alerts

#### Before (Old Way):
```java
Alert alert = new Alert(Alert.AlertType.ERROR);
alert.setTitle("Error");
alert.setHeaderText(null);
alert.setContentText("Failed to save student.");
alert.showAndWait();
```

#### After (New Way):
```java
UIHelper.showErrorAlert("Error", "Failed to save student.");
```

#### All Alert Types:
```java
// Success message
UIHelper.showSuccessAlert("Success", "Student added successfully!");

// Error message
UIHelper.showErrorAlert("Error", "Failed to save data.");

// Warning message
UIHelper.showWarningAlert("Warning", "This will delete all data.");

// Information message
UIHelper.showInfoAlert("Info", "Database is up to date.");
```

---

### 2. Getting User Confirmation

#### Before (Old Way):
```java
Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
alert.setTitle("Delete Student");
alert.setHeaderText(null);
alert.setContentText("Are you sure you want to delete this student?");
Optional<ButtonType> result = alert.showAndWait();
if (result.isPresent() && result.get() == ButtonType.OK) {
    // Delete student
}
```

#### After (New Way):
```java
if (UIHelper.showConfirmDialog("Delete Student", "Are you sure?")) {
    // Delete student
}
```

#### Yes/No Dialog:
```java
if (UIHelper.showYesNoDialog("Save Changes", "Save before closing?")) {
    // Save changes
} else {
    // Discard changes
}
```

---

### 3. Getting Input from User

```java
// Get text input
String studentName = UIHelper.showInputDialog(
    "Add Student",
    "Enter student name:",
    "" // default value
);

if (studentName != null && !studentName.isEmpty()) {
    // Use the name
}
```

```java
// Get selection from list
List<String> courses = Arrays.asList("Math", "Science", "English");
String selected = UIHelper.showChoiceDialog(
    "Select Course",
    "Choose a course:",
    courses,
    "Math" // default selection
);

if (selected != null) {
    // Use the selection
}
```

---

### 4. Creating Styled Buttons

#### In FXML:
```xml
<Button text="Save" styleClass="primary-button" />
<Button text="Delete" styleClass="delete-button" />
<Button text="Complete" styleClass="success-button" />
```

#### In Java Code:
```java
// Primary action button (blue, filled)
Button saveBtn = UIHelper.createPrimaryButton("Save");

// Success button (green, filled)
Button completeBtn = UIHelper.createSuccessButton("Complete");

// Warning button (orange, filled)
Button reviewBtn = UIHelper.createWarningButton("Review");

// Delete button (red outline, fills on hover)
Button deleteBtn = UIHelper.createDeleteButton("Delete");

// Add to layout
buttonBox.getChildren().addAll(deleteBtn, saveBtn);
```

---

### 5. Adding Tooltips

```java
// Simple tooltip
Button btn = new Button("Export");
UIHelper.addTooltip(btn, "Export data to Excel");

// Detailed tooltip with title and description
Button importBtn = new Button("Import");
UIHelper.addDetailedTooltip(
    importBtn,
    "Import Students",
    "Import student data from CSV or Excel files. Supported formats: .csv, .xlsx"
);

// Add tooltip to any control
TextField nameField = new TextField();
UIHelper.addTooltip(nameField, "Enter the student's full name");
```

---

### 6. Form Validation

```java
@FXML
private TextField nameField;

@FXML
private TextField gradeField;

public void handleSave() {
    // Validate name is not empty
    if (!UIHelper.validateNotEmpty(nameField, "Student Name")) {
        return; // Shows error alert automatically
    }

    // Parse and validate grade
    Double grade = UIHelper.parseDouble(gradeField, "Grade");
    if (grade == null) {
        return; // Shows error alert if invalid number
    }

    // Validate range
    if (!UIHelper.validateRange(grade, 0, 100, "Grade")) {
        return; // Shows error if out of range
    }

    // All valid - save data
    saveStudent(nameField.getText(), grade);
    UIHelper.showSuccessAlert("Success", "Student saved!");
}
```

---

### 7. Creating Styled Labels

```java
// Headings
Label mainTitle = UIHelper.createHeading1("Student Management");
Label sectionTitle = UIHelper.createHeading2("Add New Student");
Label subsectionTitle = UIHelper.createHeading3("Student Details");

// Status labels
Label success = UIHelper.createSuccessLabel("✓ Saved successfully");
Label warning = UIHelper.createWarningLabel("⚠ Pending review");
Label error = UIHelper.createErrorLabel("✗ Failed to save");

// Add to layout
container.getChildren().addAll(mainTitle, sectionTitle, success);
```

---

### 8. Applying Style Classes

```java
// Apply a style class to any node
VBox card = new VBox(12);
UIHelper.applyStyleClass(card, "card");
UIHelper.applyStyleClass(card, "padding-medium");

// Remove a style class
UIHelper.removeStyleClass(card, "card");

// Directly use style classes
container.getStyleClass().addAll("section-style", "spacing-large");
```

---

### 9. Show/Hide and Enable/Disable

```java
// Hide a control
UIHelper.setVisible(advancedOptions, false);

// Show a control
UIHelper.setVisible(advancedOptions, true);

// Disable a button
UIHelper.setEnabled(saveButton, false);

// Enable a button
UIHelper.setEnabled(saveButton, true);
```

---

### 10. Progress Dialog

```java
// Show progress dialog
Alert progress = UIHelper.showProgressDialog("Loading", "Please wait...");

// Do work in background
new Thread(() -> {
    try {
        // Load data...
        Thread.sleep(2000);

        // Close progress on JavaFX thread
        Platform.runLater(() -> {
            progress.close();
            UIHelper.showSuccessAlert("Complete", "Data loaded!");
        });
    } catch (Exception e) {
        Platform.runLater(() -> {
            progress.close();
            UIHelper.showErrorAlert("Error", e.getMessage());
        });
    }
}).start();
```

---

## 🎨 Complete Example: Adding a Student

```java
public class AddStudentController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField idField;

    @FXML
    private ComboBox<String> classCombo;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    public void initialize() {
        // Apply modern button styles
        UIHelper.applyStyleClass(saveBtn, "primary-button");

        // Add helpful tooltips
        UIHelper.addTooltip(nameField, "Enter the student's full name");
        UIHelper.addTooltip(idField, "Enter a unique student ID");
        UIHelper.addDetailedTooltip(
            classCombo,
            "Class Selection",
            "Select which class this student belongs to. You can change this later."
        );
    }

    @FXML
    public void handleSave() {
        // Validate inputs
        if (!UIHelper.validateNotEmpty(nameField, "Student Name")) {
            return;
        }

        if (!UIHelper.validateNotEmpty(idField, "Student ID")) {
            return;
        }

        if (classCombo.getValue() == null) {
            UIHelper.showErrorAlert("Validation Error", "Please select a class.");
            return;
        }

        // Confirm action
        if (!UIHelper.showConfirmDialog(
            "Add Student",
            "Add " + nameField.getText() + " to the class?"
        )) {
            return;
        }

        // Save student
        try {
            Student student = new Student(nameField.getText(), idField.getText());
            database.addStudent(student);

            UIHelper.showSuccessAlert("Success", "Student added successfully!");

            // Clear form
            nameField.clear();
            idField.clear();
            classCombo.setValue(null);

        } catch (Exception e) {
            UIHelper.showErrorAlert("Error", "Failed to add student: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        if (UIHelper.showYesNoDialog("Cancel", "Discard changes?")) {
            // Close dialog or clear form
            nameField.clear();
            idField.clear();
        }
    }
}
```

---

## 🎨 Complete Example: Student List with Actions

```java
public class StudentListController {

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> nameColumn;

    @FXML
    private TableColumn<Student, String> idColumn;

    @FXML
    private TableColumn<Student, Void> actionsColumn;

    @FXML
    public void initialize() {
        // Set up columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        // Add action buttons column
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = UIHelper.createDeleteButton("Delete");
            private final HBox buttons = new HBox(8, editBtn, deleteBtn);

            {
                // Make buttons small
                editBtn.getStyleClass().add("small-button");
                deleteBtn.getStyleClass().add("small-button");

                // Add tooltips
                UIHelper.addTooltip(editBtn, "Edit student details");
                UIHelper.addTooltip(deleteBtn, "Delete this student");

                buttons.setAlignment(Pos.CENTER);

                // Edit action
                editBtn.setOnAction(e -> {
                    Student student = getTableRow().getItem();
                    if (student != null) {
                        handleEdit(student);
                    }
                });

                // Delete action
                deleteBtn.setOnAction(e -> {
                    Student student = getTableRow().getItem();
                    if (student != null) {
                        handleDelete(student);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        // Load students
        loadStudents();
    }

    private void handleEdit(Student student) {
        String newName = UIHelper.showInputDialog(
            "Edit Student",
            "Edit student name:",
            student.getName()
        );

        if (newName != null && !newName.isEmpty()) {
            student.setName(newName);
            database.updateStudent(student);
            UIHelper.showSuccessAlert("Updated", "Student updated successfully!");
            studentTable.refresh();
        }
    }

    private void handleDelete(Student student) {
        if (UIHelper.showYesNoDialog(
            "Delete Student",
            "Delete " + student.getName() + "? This cannot be undone."
        )) {
            try {
                database.deleteStudent(student.getStudentId());
                studentTable.getItems().remove(student);
                UIHelper.showSuccessAlert("Deleted", "Student deleted successfully!");
            } catch (Exception e) {
                UIHelper.showErrorAlert("Error", "Failed to delete student: " + e.getMessage());
            }
        }
    }

    private void loadStudents() {
        try {
            List<Student> students = database.getAllStudents();
            studentTable.getItems().setAll(students);
        } catch (Exception e) {
            UIHelper.showErrorAlert("Error", "Failed to load students: " + e.getMessage());
        }
    }
}
```

---

## 🎨 Creating a Modern Dashboard Card

```java
private VBox createStatisticsCard() {
    VBox card = new VBox(12);
    card.getStyleClass().addAll("card", "padding-medium");

    // Title
    Label title = UIHelper.createHeading3("Student Statistics");

    // Stats
    Label totalStudents = new Label("Total Students: " + database.getStudentCount());
    totalStudents.getStyleClass().add("card-text");

    Label avgGrade = UIHelper.createSuccessLabel("Average Grade: 85.5%");

    Label pendingGrades = UIHelper.createWarningLabel("Pending Grades: 12");

    // Add to card
    card.getChildren().addAll(title, totalStudents, avgGrade, pendingGrades);

    return card;
}
```

---

## 📋 Style Class Reference

### Buttons
- `primary-button` - Blue filled button
- `success-button` - Green filled button
- `warning-button` - Orange filled button
- `delete-button` - Red outline button
- `small-button` - Smaller padding
- `large-button` - Larger padding
- `icon-button` - Minimal, transparent

### Text
- `heading-1` - Large heading (28px)
- `heading-2` - Medium heading (22px)
- `heading-3` - Small heading (18px)
- `success-text` - Green text
- `warning-text` - Orange text
- `error-text` - Red text
- `muted-text` - Gray text
- `card-text` - Card body text

### Containers
- `card` - Card with shadow
- `section-style` - Section container
- `assessments-section-style` - Assessment section with shadow

### Utility
- `spacing-small` - 8px spacing
- `spacing-medium` - 16px spacing
- `spacing-large` - 24px spacing
- `padding-small` - 8px padding
- `padding-medium` - 16px padding
- `padding-large` - 24px padding
- `rounded` - 8px border radius
- `rounded-large` - 16px border radius
- `shadow` - Card shadow
- `clickable` - Hand cursor

---

## ✅ Testing Checklist

Before deploying, verify:

- [ ] Run `UITest` to see all components
- [ ] All alerts show with correct styling
- [ ] Buttons have hover effects
- [ ] Tooltips appear on hover
- [ ] Form validation works
- [ ] Tables have alternating row colors
- [ ] Cards have shadows
- [ ] Keyboard navigation works (Tab key)
- [ ] Focus indicators are visible
- [ ] Works on Windows, Linux, and macOS

---

## 🔧 Troubleshooting

### Styles Not Applying
1. Check that `styles.css` is loaded in your Scene:
   ```java
   scene.getStylesheets().add(getClass().getResource("/org/example/demo3/styles.css").toExternalForm());
   ```

2. Verify the resource path is correct

3. Check for CSS syntax errors

### UIHelper Methods Not Found
1. Ensure you have the latest code
2. Rebuild the project: `mvn clean compile`
3. Check import statement: `import com.gradeapp.util.UIHelper;`

### Tooltips Not Showing
1. Ensure you're using `UIHelper.addTooltip()` not `setTooltip()`
2. Wait 500ms for tooltip to appear
3. Check that the control is enabled

---

## 📚 Additional Resources

- **UI_GUIDE.md** - Complete style guide
- **UI_IMPROVEMENTS_SUMMARY.md** - Visual improvements overview
- **UIHelper.java** - Source code with full documentation
- **UITest.java** - Complete test application

---

**Happy coding with the improved MarkBook+ UI!** 🎨
