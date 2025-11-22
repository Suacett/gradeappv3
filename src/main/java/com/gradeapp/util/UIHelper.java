package com.gradeapp.util;

import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.Optional;

/**
 * UI Helper utility class for common UI operations.
 * Provides consistent, accessible, and user-friendly UI components.
 */
public class UIHelper {

    /**
     * Shows a success alert dialog.
     *
     * @param title The dialog title
     * @param message The success message
     */
    public static void showSuccessAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message, "success-text");
    }

    /**
     * Shows an error alert dialog.
     *
     * @param title The dialog title
     * @param message The error message
     */
    public static void showErrorAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message, "error-text");
    }

    /**
     * Shows a warning alert dialog.
     *
     * @param title The dialog title
     * @param message The warning message
     */
    public static void showWarningAlert(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message, "warning-text");
    }

    /**
     * Shows an information alert dialog.
     *
     * @param title The dialog title
     * @param message The information message
     */
    public static void showInfoAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message, null);
    }

    /**
     * Shows a custom alert dialog.
     *
     * @param type The alert type
     * @param title The dialog title
     * @param message The alert message
     * @param styleClass Optional CSS style class
     */
    private static void showAlert(Alert.AlertType type, String title, String message, String styleClass) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Make dialog resizable
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        // Apply custom style if provided
        if (styleClass != null) {
            alert.getDialogPane().getStyleClass().add(styleClass);
        }

        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog.
     *
     * @param title The dialog title
     * @param message The confirmation message
     * @return true if user clicked OK, false otherwise
     */
    public static boolean showConfirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Shows a yes/no confirmation dialog.
     *
     * @param title The dialog title
     * @param message The confirmation message
     * @return true if user clicked Yes, false if clicked No
     */
    public static boolean showYesNoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    /**
     * Shows a text input dialog.
     *
     * @param title The dialog title
     * @param header The header text
     * @param defaultValue The default input value
     * @return The user's input, or null if canceled
     */
    public static String showInputDialog(String title, String header, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText("Please enter:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Shows a choice dialog.
     *
     * @param title The dialog title
     * @param header The header text
     * @param choices The list of choices
     * @param defaultChoice The default choice
     * @param <T> The type of choices
     * @return The user's choice, or null if canceled
     */
    public static <T> T showChoiceDialog(String title, String header, java.util.List<T> choices, T defaultChoice) {
        ChoiceDialog<T> dialog = new ChoiceDialog<>(defaultChoice, choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText("Please select:");

        Optional<T> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Adds a tooltip to a node.
     *
     * @param node The node to add tooltip to
     * @param text The tooltip text
     */
    public static void addTooltip(Node node, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(500));
        tooltip.setHideDelay(Duration.millis(200));
        Tooltip.install(node, tooltip);
    }

    /**
     * Adds a detailed tooltip with title and description.
     *
     * @param node The node to add tooltip to
     * @param title The tooltip title
     * @param description The tooltip description
     */
    public static void addDetailedTooltip(Node node, String title, String description) {
        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11px;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);

        content.getChildren().addAll(titleLabel, descLabel);

        Tooltip tooltip = new Tooltip();
        tooltip.setGraphic(content);
        tooltip.setShowDelay(Duration.millis(500));
        tooltip.setHideDelay(Duration.millis(200));
        Tooltip.install(node, tooltip);
    }

    /**
     * Applies a style class to a node.
     *
     * @param node The node to style
     * @param styleClass The CSS style class to apply
     */
    public static void applyStyleClass(Node node, String styleClass) {
        if (node != null && styleClass != null) {
            node.getStyleClass().add(styleClass);
        }
    }

    /**
     * Removes a style class from a node.
     *
     * @param node The node to modify
     * @param styleClass The CSS style class to remove
     */
    public static void removeStyleClass(Node node, String styleClass) {
        if (node != null && styleClass != null) {
            node.getStyleClass().remove(styleClass);
        }
    }

    /**
     * Creates a styled button with the specified text and style class.
     *
     * @param text The button text
     * @param styleClass The CSS style class (e.g., "primary-button", "delete-button")
     * @return The styled button
     */
    public static Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        if (styleClass != null) {
            button.getStyleClass().add(styleClass);
        }
        return button;
    }

    /**
     * Creates a primary action button.
     *
     * @param text The button text
     * @return The styled button
     */
    public static Button createPrimaryButton(String text) {
        return createStyledButton(text, "primary-button");
    }

    /**
     * Creates a success action button.
     *
     * @param text The button text
     * @return The styled button
     */
    public static Button createSuccessButton(String text) {
        return createStyledButton(text, "success-button");
    }

    /**
     * Creates a warning action button.
     *
     * @param text The button text
     * @return The styled button
     */
    public static Button createWarningButton(String text) {
        return createStyledButton(text, "warning-button");
    }

    /**
     * Creates a delete/danger action button.
     *
     * @param text The button text
     * @return The styled button
     */
    public static Button createDeleteButton(String text) {
        return createStyledButton(text, "delete-button");
    }

    /**
     * Creates a styled label with the specified text and style class.
     *
     * @param text The label text
     * @param styleClass The CSS style class (e.g., "heading-1", "success-text")
     * @return The styled label
     */
    public static Label createStyledLabel(String text, String styleClass) {
        Label label = new Label(text);
        if (styleClass != null) {
            label.getStyleClass().add(styleClass);
        }
        return label;
    }

    /**
     * Creates a heading label (h1).
     *
     * @param text The heading text
     * @return The styled label
     */
    public static Label createHeading1(String text) {
        return createStyledLabel(text, "heading-1");
    }

    /**
     * Creates a heading label (h2).
     *
     * @param text The heading text
     * @return The styled label
     */
    public static Label createHeading2(String text) {
        return createStyledLabel(text, "heading-2");
    }

    /**
     * Creates a heading label (h3).
     *
     * @param text The heading text
     * @return The styled label
     */
    public static Label createHeading3(String text) {
        return createStyledLabel(text, "heading-3");
    }

    /**
     * Creates a success text label.
     *
     * @param text The text
     * @return The styled label
     */
    public static Label createSuccessLabel(String text) {
        return createStyledLabel(text, "success-text");
    }

    /**
     * Creates a warning text label.
     *
     * @param text The text
     * @return The styled label
     */
    public static Label createWarningLabel(String text) {
        return createStyledLabel(text, "warning-text");
    }

    /**
     * Creates an error text label.
     *
     * @param text The text
     * @return The styled label
     */
    public static Label createErrorLabel(String text) {
        return createStyledLabel(text, "error-text");
    }

    /**
     * Shows a progress dialog with a message.
     *
     * @param title The dialog title
     * @param message The progress message
     * @return The progress dialog (caller should close it when done)
     */
    public static Alert showProgressDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(null);

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);

        Label messageLabel = new Label(message);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxWidth(50);
        progressIndicator.setMaxHeight(50);

        content.getChildren().addAll(progressIndicator, messageLabel);
        alert.getDialogPane().setContent(content);

        // Remove buttons
        alert.getDialogPane().getButtonTypes().clear();

        alert.show();
        return alert;
    }

    /**
     * Enables or disables a node.
     *
     * @param node The node to enable/disable
     * @param enabled True to enable, false to disable
     */
    public static void setEnabled(Node node, boolean enabled) {
        if (node != null) {
            node.setDisable(!enabled);
        }
    }

    /**
     * Sets the visibility of a node.
     *
     * @param node The node
     * @param visible True to show, false to hide
     */
    public static void setVisible(Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }

    /**
     * Validates that a text field is not empty.
     *
     * @param textField The text field to validate
     * @param fieldName The name of the field (for error message)
     * @return True if valid, false if empty
     */
    public static boolean validateNotEmpty(TextField textField, String fieldName) {
        String text = textField.getText();
        if (text == null || text.trim().isEmpty()) {
            textField.getStyleClass().add("error");
            showErrorAlert("Validation Error", fieldName + " cannot be empty.");
            return false;
        }
        textField.getStyleClass().remove("error");
        return true;
    }

    /**
     * Validates that a number is within a range.
     *
     * @param value The value to validate
     * @param min The minimum value
     * @param max The maximum value
     * @param fieldName The name of the field (for error message)
     * @return True if valid, false otherwise
     */
    public static boolean validateRange(double value, double min, double max, String fieldName) {
        if (value < min || value > max) {
            showErrorAlert("Validation Error",
                    fieldName + " must be between " + min + " and " + max + ".");
            return false;
        }
        return true;
    }

    /**
     * Tries to parse a double from a text field.
     *
     * @param textField The text field
     * @param fieldName The name of the field (for error message)
     * @return The parsed double, or null if invalid
     */
    public static Double parseDouble(TextField textField, String fieldName) {
        try {
            return Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            textField.getStyleClass().add("error");
            showErrorAlert("Validation Error",
                    fieldName + " must be a valid number.");
            return null;
        }
    }

    /**
     * Tries to parse an integer from a text field.
     *
     * @param textField The text field
     * @param fieldName The name of the field (for error message)
     * @return The parsed integer, or null if invalid
     */
    public static Integer parseInt(TextField textField, String fieldName) {
        try {
            return Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            textField.getStyleClass().add("error");
            showErrorAlert("Validation Error",
                    fieldName + " must be a valid integer.");
            return null;
        }
    }

    /**
     * Gets the primary stage from a node.
     *
     * @param node The node
     * @return The primary stage, or null if not found
     */
    public static Stage getStage(Node node) {
        if (node != null && node.getScene() != null && node.getScene().getWindow() instanceof Stage) {
            return (Stage) node.getScene().getWindow();
        }
        return null;
    }

    /**
     * Centers a dialog on its owner.
     *
     * @param dialog The dialog to center
     */
    public static void centerDialog(Dialog<?> dialog) {
        dialog.setOnShown(e -> {
            dialog.setX((dialog.getOwner().getX() + dialog.getOwner().getWidth() / 2) - (dialog.getWidth() / 2));
            dialog.setY((dialog.getOwner().getY() + dialog.getOwner().getHeight() / 2) - (dialog.getHeight() / 2));
        });
    }
}
