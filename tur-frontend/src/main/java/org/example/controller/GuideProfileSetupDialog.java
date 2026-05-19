package org.example.controller;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.example.model.Guide;
import org.example.service.GuideService;

public class GuideProfileSetupDialog {

    /** Returns true if the profile was saved, false if the dialog was dismissed without saving. */
    public static boolean show(Guide existing) {
        Dialog<Boolean> dialog = new Dialog<>();
        javafx.stage.Window owner = javafx.stage.Window.getWindows().stream()
                .filter(javafx.stage.Window::isShowing).findFirst().orElse(null);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Complete Your Profile");

        Label title = new Label("Welcome, " + (existing.getUsername() != null ? existing.getUsername() : "Guide") + "!");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        Label subtitle = new Label("Please complete your profile before continuing.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        VBox header = new VBox(4, title, subtitle);
        header.setPadding(new Insets(0, 0, 12, 0));

        TextField phoneField      = new TextField(nvl(existing.getPhone()));
        TextField baseCityField   = new TextField(nvl(existing.getBaseCity()));
        TextField licenseField    = new TextField(nvl(existing.getLicenseNo()));
        TextField experienceField = new TextField(existing.getExperience() != null ? String.valueOf(existing.getExperience()) : "");
        TextField dailyFeeField   = new TextField(existing.getDailyFee() != null ? String.valueOf(existing.getDailyFee().intValue()) : "");

        phoneField.setPromptText("e.g. +387 60 123 4567");
        baseCityField.setPromptText("e.g. Sarajevo");
        licenseField.setPromptText("e.g. LIC-2024-001");
        experienceField.setPromptText("Years, e.g. 5");
        dailyFeeField.setPromptText("e.g. 150");

        for (TextField f : new TextField[]{phoneField, baseCityField, licenseField, experienceField, dailyFeeField}) {
            f.setPrefWidth(240);
        }

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #E53935; -fx-font-size: 11px;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 0, 6, 0));
        grid.add(new Label("Phone *:"),           0, 0); grid.add(phoneField,      1, 0);
        grid.add(new Label("Base City *:"),       0, 1); grid.add(baseCityField,   1, 1);
        grid.add(new Label("License No:"),        0, 2); grid.add(licenseField,    1, 2);
        grid.add(new Label("Experience (yrs):"),  0, 3); grid.add(experienceField, 1, 3);
        grid.add(new Label("Daily Fee (€):"),     0, 4); grid.add(dailyFeeField,   1, 4);

        VBox content = new VBox(header, grid, errorLabel);
        content.setPadding(new Insets(24, 28, 12, 28));
        content.setSpacing(8);
        content.setAlignment(Pos.TOP_LEFT);
        content.setStyle("-fx-background-color: white;");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-border-color: #cfd8dc; -fx-border-width: 1;");

        ButtonType saveBtn = new ButtonType("Save & Continue", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(saveBtn);

        javafx.scene.Node saveBtnNode = dialog.getDialogPane().lookupButton(saveBtn);
        saveBtnNode.addEventFilter(ActionEvent.ACTION, event -> {
            String phone    = phoneField.getText().trim();
            String baseCity = baseCityField.getText().trim();

            if (phone.isEmpty() || baseCity.isEmpty()) {
                errorLabel.setText("Phone and Base City are required.");
                event.consume();
                return;
            }

            Integer experience = null;
            Double dailyFee = null;
            try {
                if (!experienceField.getText().trim().isEmpty())
                    experience = Integer.parseInt(experienceField.getText().trim());
            } catch (NumberFormatException e) {
                errorLabel.setText("Experience must be a whole number.");
                event.consume();
                return;
            }
            try {
                if (!dailyFeeField.getText().trim().isEmpty())
                    dailyFee = Double.parseDouble(dailyFeeField.getText().trim());
            } catch (NumberFormatException e) {
                errorLabel.setText("Daily Fee must be a number.");
                event.consume();
                return;
            }

            Guide updated = new Guide();
            updated.setId(existing.getId());
            updated.setUsername(existing.getUsername());
            updated.setEmail(existing.getEmail());
            updated.setPhone(phone);
            updated.setBaseCity(baseCity);
            updated.setLicenseNo(licenseField.getText().trim().isEmpty() ? null : licenseField.getText().trim());
            updated.setExperience(experience);
            updated.setDailyFee(dailyFee);

            try {
                new GuideService().updateMyProfile(updated);
            } catch (Exception ex) {
                errorLabel.setText("Failed to save profile. Please try again.");
                event.consume();
            }
        });

        dialog.setResultConverter(btn -> btn != null && btn.getButtonData() == ButtonBar.ButtonData.OK_DONE);
        return dialog.showAndWait().orElse(false);
    }

    private static String nvl(String s) {
        return s != null ? s : "";
    }
}
