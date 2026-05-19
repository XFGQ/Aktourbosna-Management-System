package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.example.model.Customer;

public class AddCustomerDialog {

    public static Customer show() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Add Customer");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 28, 10, 28));

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. John Smith");
        nameField.setPrefWidth(240);

        TextField passportField = new TextField();
        passportField.setPromptText("e.g. TR123456");
        passportField.setPrefWidth(240);

        TextField phoneField = new TextField();
        phoneField.setPromptText("e.g. +38761234567");
        phoneField.setPrefWidth(240);

        TextField nationalityField = new TextField();
        nationalityField.setPromptText("e.g. Bosnian");
        nationalityField.setPrefWidth(240);

        grid.addRow(0, new Label("Full Name *"),    nameField);
        grid.addRow(1, new Label("Passport No"),    passportField);
        grid.addRow(2, new Label("Phone"),          phoneField);
        grid.addRow(3, new Label("Nationality"),    nationalityField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(420);

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            if (nameField.getText().trim().isEmpty()) {
                Toast.error("Full name is required.");
                return null;
            }
            Customer c = new Customer();
            c.setFullName(nameField.getText().trim());
            c.setPassportNumber(passportField.getText().trim().isEmpty() ? null : passportField.getText().trim());
            c.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
            c.setNationality(nationalityField.getText().trim().isEmpty() ? null : nationalityField.getText().trim());
            return c;
        });

        return dialog.showAndWait().orElse(null);
    }
}
