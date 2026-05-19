package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import org.example.model.Guide;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddGuideDialog {

    public static Guide show() {
        return show(null);
    }

    public static Guide show(Guide existing) {
        Dialog<Guide> dialog = new Dialog<>();
        javafx.stage.Window owner = javafx.stage.Window.getWindows().stream()
                .filter(javafx.stage.Window::isShowing).findFirst().orElse(null);
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);

        boolean editMode = existing != null;
        dialog.setTitle(editMode ? "Edit Guide" : "Add New Guide");
        dialog.setHeaderText(editMode ? "Update guide details" : "Enter guide details");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField fullName   = new TextField();
        fullName.setPromptText("e.g. hasan_yilmaz");
        PasswordField password = new PasswordField();
        password.setPromptText("Min. 6 characters");
        TextField email      = new TextField();
        email.setPromptText("e.g. hasan@example.com");

        int row = 0;
        grid.add(new Label("Username *:"),   0, row); grid.add(fullName, 1, row++);
        if (!editMode) {
            grid.add(new Label("Password *:"), 0, row); grid.add(password, 1, row++);
        }
        grid.add(new Label("Email *:"),      0, row); grid.add(email,    1, row);

        if (editMode) {
            fullName.setText(existing.getUsername() != null ? existing.getUsername() : "");
            email.setText(existing.getEmail() != null ? existing.getEmail() : "");
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                if (fullName.getText().trim().isEmpty() || email.getText().trim().isEmpty()) {
                    Toast.error("Username and Email are required.");
                    return null;
                }
                if (!editMode && password.getText().trim().length() < 6) {
                    Toast.error("Password must be at least 6 characters.");
                    return null;
                }
                Guide g = new Guide();
                g.setUsername(fullName.getText().trim());
                g.setPassword(password.getText().trim());
                g.setEmail(email.getText().trim());
                return g;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static List<String> parseList(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        List<String> items = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        return items.isEmpty() ? null : items;
    }
}
