# MarkBook+ UI Style Guide

## Overview

MarkBook+ features a modern, accessible, and cross-platform UI design. This guide documents the UI components, styling system, and best practices for maintaining a consistent user experience.

---

## 📐 Design Principles

### 1. **Consistency**
- Uniform styling across all views
- Predictable interactions
- Cohesive color palette

### 2. **Accessibility**
- High contrast ratios
- Keyboard navigation support
- Clear focus indicators
- Screen reader friendly

### 3. **Usability**
- Clear visual hierarchy
- Informative feedback
- Error prevention and recovery
- Efficient workflows

### 4. **Cross-Platform**
- Works on Windows, Linux, and macOS
- Adapts to different screen sizes
- System font fallbacks

---

## 🎨 Color Palette

### Primary Colors
```css
Primary Blue:  #0071E3  /* Main actions, links */
Primary Hover: #005BB5  /* Hover states */
Primary Light: #E3F2FD  /* Backgrounds, selected items */
```

### Status Colors
```css
Success: #28C841  /* Successful operations */
Warning: #FF9500  /* Warnings, cautions */
Danger:  #FF3B30  /* Errors, destructive actions */
Neutral: #8E8E93  /* Secondary text, disabled states */
```

### Neutral Colors
```css
Border:   #D1D1D6  /* Borders, dividers */
Hover:    #F0F0F0  /* Hover backgrounds */
Selected: #E3F2FD  /* Selected items */
```

---

## 🔧 CSS Style Classes

### Buttons

#### Basic Button Styles
```java
// Default button (white with blue border)
Button btn = new Button("Click Me");

// Primary button (filled blue)
btn.getStyleClass().add("primary-button");

// Success button (filled green)
btn.getStyleClass().add("success-button");

// Warning button (filled orange)
btn.getStyleClass().add("warning-button");

// Delete/Danger button (red outline, fills on hover)
btn.getStyleClass().add("delete-button");
```

#### Button Sizes
```java
// Small button
btn.getStyleClass().add("small-button");

// Large button
btn.getStyleClass().add("large-button");

// Icon button (minimal, transparent)
btn.getStyleClass().add("icon-button");
```

#### Using UIHelper
```java
Button primaryBtn = UIHelper.createPrimaryButton("Save");
Button deleteBtn = UIHelper.createDeleteButton("Delete");
Button successBtn = UIHelper.createSuccessButton("Complete");
```

### Text Styles

#### Headings
```java
// Heading 1 (28px, bold)
Label h1 = UIHelper.createHeading1("Main Title");

// Heading 2 (22px, semibold)
Label h2 = UIHelper.createHeading2("Section Title");

// Heading 3 (18px, semibold)
Label h3 = UIHelper.createHeading3("Subsection");
```

#### Status Text
```java
// Success text (green, bold)
Label success = UIHelper.createSuccessLabel("Operation successful!");

// Warning text (orange, bold)
Label warning = UIHelper.createWarningLabel("Please review before continuing");

// Error text (red, bold)
Label error = UIHelper.createErrorLabel("An error occurred");

// Muted text (gray)
Label muted = new Label("Additional information");
muted.getStyleClass().add("muted-text");
```

### Cards & Sections

#### Card Components
```java
// Create a card container
VBox card = new VBox(12);
card.getStyleClass().add("card");

// Card with hover effect
card.getStyleClass().addAll("card", "shadow-hover");
```

#### Section Containers
```java
// Standard section
VBox section = new VBox();
section.getStyleClass().add("section-style");

// Assessment section (with shadow)
VBox assessmentSection = new VBox();
assessmentSection.getStyleClass().add("assessments-section-style");
```

### Tables & Lists

Tables and lists automatically have:
- Rounded corners
- Soft shadows
- Alternating row colors
- Hover effects
- Professional column headers

```java
TableView<Student> table = new TableView<>();
// Automatically styled with enhanced appearance

// Highlight a specific row
tableRow.getStyleClass().add("highlighted-row");
```

### Form Controls

#### Text Fields
```java
TextField nameField = new TextField();
nameField.setPromptText("Enter name...");

// Add error state
nameField.getStyleClass().add("error");

// Validate with UIHelper
if (UIHelper.validateNotEmpty(nameField, "Name")) {
    // Valid
}
```

#### ComboBoxes
```java
ComboBox<String> courseBox = new ComboBox<>();
// Automatically styled with modern appearance
```

### Badges & Chips

```java
// Create a badge
Label badge = new Label("New");
badge.getStyleClass().add("badge");

// Success badge
badge.getStyleClass().addAll("badge", "success");

// Warning badge
badge.getStyleClass().addAll("badge", "warning");

// Danger badge
badge.getStyleClass().addAll("badge", "danger");
```

### Utility Classes

#### Spacing
```java
// Small spacing (8px)
container.getStyleClass().add("spacing-small");

// Medium spacing (16px)
container.getStyleClass().add("spacing-medium");

// Large spacing (24px)
container.getStyleClass().add("spacing-large");
```

#### Padding
```java
// Small padding (8px)
container.getStyleClass().add("padding-small");

// Medium padding (16px)
container.getStyleClass().add("padding-medium");

// Large padding (24px)
container.getStyleClass().add("padding-large");
```

#### Effects
```java
// Rounded corners
container.getStyleClass().add("rounded");

// Large rounded corners
container.getStyleClass().add("rounded-large");

// Card shadow
container.getStyleClass().add("shadow");

// Hover shadow
container.getStyleClass().add("shadow-hover");

// Clickable cursor
element.getStyleClass().add("clickable");

// Full width
element.getStyleClass().add("full-width");
```

---

## 💬 Dialogs & Alerts

### Success Alert
```java
UIHelper.showSuccessAlert("Success", "Student added successfully!");
```

### Error Alert
```java
UIHelper.showErrorAlert("Error", "Failed to save data.");
```

### Warning Alert
```java
UIHelper.showWarningAlert("Warning", "This action cannot be undone.");
```

### Confirmation Dialog
```java
if (UIHelper.showConfirmDialog("Confirm", "Are you sure?")) {
    // User clicked OK
}
```

### Yes/No Dialog
```java
if (UIHelper.showYesNoDialog("Delete", "Delete this student?")) {
    // User clicked Yes
}
```

### Input Dialog
```java
String name = UIHelper.showInputDialog("Add Student", "Enter student name:", "");
if (name != null) {
    // User entered a name
}
```

### Choice Dialog
```java
List<String> courses = Arrays.asList("Math", "Science", "English");
String selected = UIHelper.showChoiceDialog(
    "Select Course",
    "Choose a course:",
    courses,
    "Math"
);
```

### Progress Dialog
```java
Alert progress = UIHelper.showProgressDialog("Loading", "Please wait...");
// Do work...
progress.close();
```

---

## 🔍 Tooltips

### Simple Tooltip
```java
Button btn = new Button("Save");
UIHelper.addTooltip(btn, "Save your changes");
```

### Detailed Tooltip
```java
Button btn = new Button("Export");
UIHelper.addDetailedTooltip(
    btn,
    "Export Data",
    "Export student data to Excel or CSV format. This will include all grades and assessments."
);
```

---

## ✅ Form Validation

### Validate Not Empty
```java
TextField nameField = new TextField();
if (!UIHelper.validateNotEmpty(nameField, "Student Name")) {
    return; // Validation failed
}
```

### Parse Numbers
```java
TextField gradeField = new TextField();
Double grade = UIHelper.parseDouble(gradeField, "Grade");
if (grade == null) {
    return; // Invalid number
}
```

### Validate Range
```java
double grade = 85.0;
if (!UIHelper.validateRange(grade, 0, 100, "Grade")) {
    return; // Out of range
}
```

---

## 🎯 Navigation

### Navigation Items

Navigation items automatically support:
- Hover effects
- Active/selected states
- Visual indicators
- Smooth transitions

```java
HBox navItem = new HBox();
navItem.getStyleClass().add("nav-item");

// Mark as selected
navItem.getStyleClass().add("selected");

// Remove selection
navItem.getStyleClass().remove("selected");
```

---

## 🎨 Custom Styling Examples

### Create a Styled Card
```java
VBox card = new VBox(12);
card.getStyleClass().addAll("card", "padding-medium");

Label title = UIHelper.createHeading3("Student Information");
Label detail = new Label("John Doe - Grade: 95%");
detail.getStyleClass().add("card-text");

card.getChildren().addAll(title, detail);
```

### Create an Action Button Row
```java
HBox buttonRow = new HBox(12);
buttonRow.setAlignment(Pos.CENTER_RIGHT);

Button save = UIHelper.createPrimaryButton("Save");
Button cancel = new Button("Cancel");
Button delete = UIHelper.createDeleteButton("Delete");

buttonRow.getChildren().addAll(delete, cancel, save);
```

### Create a Form Section
```java
VBox form = new VBox();
form.getStyleClass().addAll("section-style", "spacing-medium", "padding-medium");

Label heading = UIHelper.createHeading2("Add Student");

TextField nameField = new TextField();
nameField.setPromptText("Student Name");

TextField idField = new TextField();
idField.setPromptText("Student ID");

Button submit = UIHelper.createSuccessButton("Submit");

form.getChildren().addAll(heading, nameField, idField, submit);
```

---

## 📱 Responsive Design

### Adapting to Window Size
```java
// Make elements full width
textField.setMaxWidth(Double.MAX_VALUE);
textField.getStyleClass().add("full-width");

// Responsive spacing
VBox container = new VBox();
container.getStyleClass().add("spacing-medium");
container.setPadding(new Insets(16));
```

---

## ♿ Accessibility Features

### Keyboard Navigation
- All buttons and controls are keyboard accessible
- Tab key navigation works properly
- Enter key activates default buttons
- Escape key closes dialogs

### Focus Indicators
- Clear focus outlines on all interactive elements
- High contrast focus states
- Visible keyboard focus

### Screen Reader Support
- Proper ARIA labels
- Descriptive text for all controls
- Meaningful error messages

---

## 🔧 Best Practices

### Do's ✅
- Use UIHelper methods for consistency
- Add tooltips to non-obvious controls
- Provide clear error messages
- Use appropriate button styles for actions
- Test on multiple platforms
- Maintain consistent spacing
- Use semantic style classes

### Don'ts ❌
- Don't use inline styles (use CSS classes)
- Don't create custom alert dialogs (use UIHelper)
- Don't skip validation
- Don't use too many colors
- Don't forget tooltips on icon buttons
- Don't hardcode sizes (use CSS)

---

## 🎓 Examples

### Complete Student Form
```java
public class StudentForm extends VBox {
    public StudentForm() {
        getStyleClass().addAll("section-style", "spacing-medium", "padding-large");

        // Title
        Label title = UIHelper.createHeading2("Add New Student");

        // Name field
        VBox nameBox = new VBox(6);
        Label nameLabel = new Label("Student Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter full name");
        UIHelper.addTooltip(nameField, "Enter the student's full name");
        nameBox.getChildren().addAll(nameLabel, nameField);

        // ID field
        VBox idBox = new VBox(6);
        Label idLabel = new Label("Student ID:");
        TextField idField = new TextField();
        idField.setPromptText("Enter student ID");
        UIHelper.addTooltip(idField, "Enter the unique student ID");
        idBox.getChildren().addAll(idLabel, idField);

        // Buttons
        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        Button save = UIHelper.createSuccessButton("Save Student");
        Button cancel = new Button("Cancel");

        save.setOnAction(e -> {
            if (validateAndSave(nameField, idField)) {
                UIHelper.showSuccessAlert("Success", "Student added successfully!");
            }
        });

        buttonRow.getChildren().addAll(cancel, save);

        getChildren().addAll(title, nameBox, idBox, buttonRow);
    }

    private boolean validateAndSave(TextField nameField, TextField idField) {
        if (!UIHelper.validateNotEmpty(nameField, "Name")) return false;
        if (!UIHelper.validateNotEmpty(idField, "Student ID")) return false;

        // Save logic here
        return true;
    }
}
```

### Data Table with Actions
```java
TableView<Student> table = new TableView<>();
table.setItems(students);

// Name column
TableColumn<Student, String> nameCol = new TableColumn<>("Name");
nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

// Actions column
TableColumn<Student, Void> actionCol = new TableColumn<>("Actions");
actionCol.setCellFactory(param -> new TableCell<>() {
    private final Button editBtn = new Button("Edit");
    private final Button deleteBtn = UIHelper.createDeleteButton("Delete");

    {
        editBtn.getStyleClass().add("small-button");
        deleteBtn.getStyleClass().add("small-button");

        UIHelper.addTooltip(editBtn, "Edit student details");
        UIHelper.addTooltip(deleteBtn, "Delete this student");

        HBox buttons = new HBox(6, editBtn, deleteBtn);
        buttons.setAlignment(Pos.CENTER);

        deleteBtn.setOnAction(e -> {
            Student student = getTableRow().getItem();
            if (UIHelper.showYesNoDialog("Confirm", "Delete " + student.getName() + "?")) {
                // Delete student
            }
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : buttons);
    }
});

table.getColumns().addAll(nameCol, actionCol);
```

---

## 🔄 Migration Guide

### Updating Existing Code

**Before:**
```java
Button btn = new Button("Save");
btn.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
```

**After:**
```java
Button btn = UIHelper.createPrimaryButton("Save");
```

**Before:**
```java
Alert alert = new Alert(Alert.AlertType.ERROR);
alert.setTitle("Error");
alert.setContentText("Something went wrong");
alert.showAndWait();
```

**After:**
```java
UIHelper.showErrorAlert("Error", "Something went wrong");
```

---

## 📚 Additional Resources

- **JavaFX CSS Reference**: https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/cssref.html
- **Material Design Guidelines**: https://material.io/design
- **Accessibility Guidelines**: https://www.w3.org/WAI/WCAG21/quickref/

---

## 🤝 Contributing

When adding new UI components:

1. Follow the existing color palette
2. Use UIHelper when possible
3. Add appropriate tooltips
4. Test keyboard navigation
5. Verify on multiple platforms
6. Document new style classes
7. Update this guide

---

**Enjoy building beautiful, accessible interfaces with MarkBook+!** 🎨
