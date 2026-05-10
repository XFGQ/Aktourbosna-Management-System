package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
        dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
        boolean editMode = existing != null;
        dialog.setTitle(editMode ? "Edit Guide" : "Add New Guide");
        dialog.setHeaderText(editMode ? "Update guide details" : "Enter guide details");

        ButtonType saveButton = new ButtonType(editMode ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField fullName = new TextField();
        fullName.setPromptText("e.g. Hasan Yilmaz");
        TextField email = new TextField();
        email.setPromptText("e.g. hasan@example.com");
        TextField phone = new TextField();
        phone.setPromptText("e.g. +387601234567");
        TextField baseCity = new TextField();
        baseCity.setPromptText("e.g. Sarajevo");
        TextField licenseNo = new TextField();
        licenseNo.setPromptText("e.g. LIC-2024-001");
        TextField experience = new TextField();
        experience.setPromptText("Years, e.g. 5");
        TextField dailyFee = new TextField();
        dailyFee.setPromptText("e.g. 150");

        if (editMode) {
            fullName.setText(existing.getFullName() != null ? existing.getFullName() : "");
            email.setText(existing.getEmail() != null ? existing.getEmail() : "");
            phone.setText(existing.getPhone() != null ? existing.getPhone() : "");
            baseCity.setText(existing.getBaseCity() != null ? existing.getBaseCity() : "");
            licenseNo.setText(existing.getLicenseNo() != null ? existing.getLicenseNo() : "");
            experience.setText(existing.getExperience() != null ? existing.getExperience().toString() : "");
            dailyFee.setText(existing.getDailyFee() != null ? existing.getDailyFee().toString() : "");
        }

        grid.add(new Label("Full Name:"), 0, 0);         grid.add(fullName, 1, 0);
        grid.add(new Label("Email:"), 0, 1);             grid.add(email, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);             grid.add(phone, 1, 2);
        grid.add(new Label("Base City:"), 0, 3);         grid.add(baseCity, 1, 3);
        grid.add(new Label("License No:"), 0, 4);        grid.add(licenseNo, 1, 4);
        grid.add(new Label("Experience (yrs):"), 0, 5);  grid.add(experience, 1, 5);
        grid.add(new Label("Daily Fee (€):"), 0, 6);     grid.add(dailyFee, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                try {
                    Guide g = editMode ? existing : new Guide();
                    g.setFullName(fullName.getText().trim());
                    g.setEmail(email.getText().trim());
                    g.setPhone(phone.getText().trim());
                    g.setBaseCity(baseCity.getText().trim());
                    g.setLicenseNo(licenseNo.getText().trim());
                    g.setExperience(experience.getText().isEmpty() ? 0 : Integer.parseInt(experience.getText().trim()));
                    g.setDailyFee(dailyFee.getText().isEmpty() ? 0.0 : Double.parseDouble(dailyFee.getText().trim()));
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
