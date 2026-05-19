package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import org.example.model.Guide;

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

        int row = 0;

        TextField usernameField = new TextField();
        TextField passwordField = new TextField();
        TextField emailField    = new TextField();

        if (!editMode) {
            usernameField.setPromptText("e.g. hasan.yilmaz");
            passwordField.setPromptText("min. 6 characters");
            emailField.setPromptText("e.g. hasan@example.com");
            grid.add(new Label("Username *:"), 0, row); grid.add(usernameField, 1, row++);
            grid.add(new Label("Password *:"), 0, row); grid.add(passwordField, 1, row++);
            grid.add(new Label("Email *:"),    0, row); grid.add(emailField,    1, row++);
        } else {
            Label infoLabel = new Label(existing.getUsername() != null ? existing.getUsername() : "");
            infoLabel.setStyle("-fx-font-weight: bold;");
            grid.add(new Label("Guide:"), 0, row); grid.add(infoLabel, 1, row++);
        }

        TextField phone      = new TextField();
        phone.setPromptText("e.g. +387601234567");
        TextField baseCity   = new TextField();
        baseCity.setPromptText("e.g. Sarajevo");
        TextField licenseNo  = new TextField();
        licenseNo.setPromptText("e.g. LIC-2024-001");
        TextField experience = new TextField();
        experience.setPromptText("Years, e.g. 5");
        TextField dailyFee   = new TextField();
        dailyFee.setPromptText("e.g. 150");

        if (editMode) {
            phone.setText(existing.getPhone() != null ? existing.getPhone() : "");
            baseCity.setText(existing.getBaseCity() != null ? existing.getBaseCity() : "");
            licenseNo.setText(existing.getLicenseNo() != null ? existing.getLicenseNo() : "");
            experience.setText(existing.getExperience() != null ? String.valueOf(existing.getExperience()) : "");
            dailyFee.setText(existing.getDailyFee() != null ? String.valueOf(existing.getDailyFee().intValue()) : "");
        }

        grid.add(new Label("Phone:"),            0, row); grid.add(phone,      1, row++);
        grid.add(new Label("Base City:"),        0, row); grid.add(baseCity,   1, row++);
        grid.add(new Label("License No:"),       0, row); grid.add(licenseNo,  1, row++);
        grid.add(new Label("Experience (yrs):"), 0, row); grid.add(experience, 1, row++);
        grid.add(new Label("Daily Fee (€):"),    0, row); grid.add(dailyFee,   1, row);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                try {
                    Guide g = new Guide();
                    if (!editMode) {
                        if (usernameField.getText().trim().isEmpty()) { Toast.error("Username is required."); return null; }
                        if (passwordField.getText().trim().isEmpty()) { Toast.error("Password is required."); return null; }
                        if (emailField.getText().trim().isEmpty())    { Toast.error("Email is required.");    return null; }
                        g.setUsername(usernameField.getText().trim());
                        g.setPassword(passwordField.getText().trim());
                        g.setEmail(emailField.getText().trim());
                    }
                    g.setPhone(phone.getText().trim().isEmpty() ? null : phone.getText().trim());
                    g.setBaseCity(baseCity.getText().trim().isEmpty() ? null : baseCity.getText().trim());
                    g.setLicenseNo(licenseNo.getText().trim().isEmpty() ? null : licenseNo.getText().trim());
                    g.setExperience(experience.getText().trim().isEmpty() ? null : Integer.parseInt(experience.getText().trim()));
                    g.setDailyFee(dailyFee.getText().trim().isEmpty() ? null : Double.parseDouble(dailyFee.getText().trim()));
                    return g;
                } catch (NumberFormatException e) {
                    Toast.error("Experience and Daily Fee must be numbers.");
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }
}
