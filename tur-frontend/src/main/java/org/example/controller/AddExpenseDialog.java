package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.example.model.Expense;
import org.example.model.Tour;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class AddExpenseDialog {

    private static final String[] CATEGORIES = {
        "Accommodation", "Transport", "Food & Beverage", "Guide Fee",
        "Entrance Fee", "Fuel", "Toll", "Insurance", "Other"
    };

    /** Add mode — returns Object[]{tourId, expense} */
    public static Object[] show(List<Tour> tours) {
        return showInternal(null, null, tours);
    }

    /** Edit mode — tourId is the existing tour, returns Object[]{tourId, expense} */
    public static Object[] show(Expense existing, Long tourId, List<Tour> tours) {
        return showInternal(existing, tourId, tours);
    }

    private static Object[] showInternal(Expense existing, Long existingTourId, List<Tour> tours) {
        boolean editMode = existing != null;

        Dialog<Object[]> dialog = new Dialog<>();
        dialog.setTitle(editMode ? "Edit Expense" : "Add Expense");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 28, 10, 28));

        int row = 0;

        // Tour — selectable in add mode, read-only label in edit mode
        Long[] resolvedTourId = {existingTourId};
        if (editMode) {
            String tourName = tours.stream()
                    .filter(t -> existingTourId != null && existingTourId.equals(t.getTourId()))
                    .map(Tour::getTourName)
                    .findFirst().orElse("—");
            Label tourLabel = new Label(tourName);
            tourLabel.setStyle("-fx-font-weight: bold;");
            grid.addRow(row++, new Label("Tour"), tourLabel);
        }
        ComboBox<Tour> tourBox = new ComboBox<>();
        if (!editMode) {
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
            tourBox.valueProperty().addListener((obs, o, n) -> resolvedTourId[0] = n != null ? n.getTourId() : null);
            grid.addRow(row++, new Label("Tour *"), tourBox);
        }

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

        TextField receiptField = new TextField();
        receiptField.setPromptText("Select or type receipt path");
        receiptField.setPrefWidth(200);
        Button browseBtn = new Button("Browse…");
        File[] selectedFile = new File[1];
        browseBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Receipt File");
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images / PDF", "*.png", "*.jpg", "*.jpeg", "*.pdf"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fc.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                receiptField.setText(file.getAbsolutePath());
                selectedFile[0] = file;
            }
        });
        HBox receiptBox = new HBox(6, receiptField, browseBtn);

        if (editMode) {
            categoryBox.setValue(existing.getCategory());
            amountField.setText(existing.getAmount() != null ? String.valueOf(existing.getAmount().intValue()) : "");
            if (existing.getDate() != null) datePicker.setValue(existing.getDate());
            if (existing.getReceiptPath() != null) receiptField.setText(existing.getReceiptPath());
        }

        grid.addRow(row++, new Label("Category *"),   categoryBox);
        grid.addRow(row++, new Label("Amount (€) *"), amountField);
        grid.addRow(row++, new Label("Date"),         datePicker);
        grid.addRow(row,   new Label("Receipt"),      receiptBox);

        dialog.getDialogPane().setContent(new VBox(grid));
        dialog.getDialogPane().setPrefWidth(480);

        final Button btSave = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        btSave.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            boolean valid = true;
            if (!editMode) tourBox.setStyle("");
            categoryBox.setStyle("");
            amountField.setStyle("");

            if (!editMode && resolvedTourId[0] == null) {
                tourBox.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (categoryBox.getValue() == null || categoryBox.getValue().trim().isEmpty()) {
                categoryBox.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (amountField.getText().trim().isEmpty()) {
                amountField.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            } else {
                try {
                    Float.parseFloat(amountField.getText().trim());
                } catch (NumberFormatException e) {
                    amountField.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                    valid = false;
                }
            }

            if (!valid) {
                event.consume();
                Toast.error("Lütfen kırmızı ile işaretli alanları kontrol ediniz.");
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            
            float amount = Float.parseFloat(amountField.getText().trim());
            Expense expense = editMode ? existing : new Expense();
            expense.setCategory(categoryBox.getValue().trim());
            expense.setAmount(amount);
            expense.setDate(datePicker.getValue());
            
            File fileToUpload = null;
            String receiptText = receiptField.getText().trim();
            if (selectedFile[0] != null && selectedFile[0].getAbsolutePath().equals(receiptText)) {
                fileToUpload = selectedFile[0];
            } else if (!receiptText.isEmpty() && new File(receiptText).exists()) {
                fileToUpload = new File(receiptText);
            } else {
                expense.setReceiptPath(receiptText.isEmpty() ? null : receiptText);
            }
            return new Object[]{resolvedTourId[0], expense, fileToUpload};
        });

        return dialog.showAndWait().orElse(null);
    }
}
