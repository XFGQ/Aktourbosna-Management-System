package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.example.model.Expense;
import org.example.model.Tour;

import java.time.LocalDate;
import java.util.List;

public class AddExpenseDialog {

    private static final String[] CATEGORIES = {
        "Accommodation", "Transport", "Food & Beverage", "Guide Fee",
        "Entrance Fee", "Fuel", "Toll", "Insurance", "Other"
    };

    public static Object[] show(List<Tour> tours) {
        Dialog<Object[]> dialog = new Dialog<>();
        dialog.setTitle("Add Expense");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 28, 10, 28));

        ComboBox<Tour> tourBox = new ComboBox<>();
        tourBox.getItems().addAll(tours);
        tourBox.setPromptText("Select tour");
        tourBox.setPrefWidth(260);
        tourBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Tour t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? null : t.getTourName());
            }
        });
        tourBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Tour t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? null : t.getTourName());
            }
        });

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(CATEGORIES);
        categoryBox.setPromptText("Select category");
        categoryBox.setPrefWidth(260);
        categoryBox.setEditable(true);

        TextField amountField = new TextField();
        amountField.setPromptText("Amount (€)");
        amountField.setPrefWidth(260);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(260);

        grid.addRow(0, new Label("Tour *"),        tourBox);
        grid.addRow(1, new Label("Category *"),    categoryBox);
        grid.addRow(2, new Label("Amount (€) *"),  amountField);
        grid.addRow(3, new Label("Date"),          datePicker);

        dialog.getDialogPane().setContent(new VBox(grid));
        dialog.getDialogPane().setPrefWidth(440);

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            if (tourBox.getValue() == null) {
                Toast.error("Please select a tour.");
                return null;
            }
            if (categoryBox.getValue() == null || categoryBox.getValue().trim().isEmpty()) {
                Toast.error("Category is required.");
                return null;
            }
            if (amountField.getText().trim().isEmpty()) {
                Toast.error("Amount is required.");
                return null;
            }
            float amount;
            try {
                amount = Float.parseFloat(amountField.getText().trim());
            } catch (NumberFormatException e) {
                Toast.error("Amount must be a number.");
                return null;
            }
            Expense expense = new Expense();
            expense.setCategory(categoryBox.getValue().trim());
            expense.setAmount(amount);
            expense.setDate(datePicker.getValue());
            return new Object[]{tourBox.getValue().getTourId(), expense};
        });

        return dialog.showAndWait().orElse(null);
    }
}
