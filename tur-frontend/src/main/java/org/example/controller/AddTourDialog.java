package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.example.model.Guide;
import org.example.model.Tour;
import org.example.model.Vehicle;

import java.time.LocalDate;
import java.util.List;

public class AddTourDialog {

    public static Tour show(List<Guide> guides, List<Vehicle> vehicles) {
        return show(null, guides, vehicles);
    }

    public static Tour show(Tour existing, List<Guide> guides, List<Vehicle> vehicles) {
        boolean isEdit = existing != null;
        Dialog<Tour> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Tour" : "Add Tour");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 28, 10, 28));

        TextField nameField = new TextField(isEdit ? existing.getTourName() : "");
        nameField.setPromptText("Tour name");
        nameField.setPrefWidth(260);

        DatePicker startPicker = new DatePicker(isEdit ? existing.getStartDate() : LocalDate.now());
        DatePicker endPicker   = new DatePicker(isEdit ? existing.getEndDate()   : LocalDate.now().plusDays(7));
        startPicker.setPrefWidth(260);
        endPicker.setPrefWidth(260);

        TextField hotelField = new TextField(isEdit && existing.getHotelName() != null ? existing.getHotelName() : "");
        hotelField.setPromptText("Hotel name");
        hotelField.setPrefWidth(260);

        TextField priceField = new TextField(isEdit && existing.getFinalPrice() != null ? String.valueOf(existing.getFinalPrice().intValue()) : "");
        priceField.setPromptText("Final price (€)");
        priceField.setPrefWidth(260);

        ComboBox<Guide> guideBox = new ComboBox<>();
        guideBox.getItems().addAll(guides);
        guideBox.setPromptText("Select guide");
        guideBox.setPrefWidth(260);
        guideBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Guide g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? null : g.getFullName());
            }
        });
        guideBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Guide g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? null : g.getFullName());
            }
        });
        if (isEdit && existing.getGuideId() != null) {
            guides.stream().filter(g -> existing.getGuideId().equals(g.getId())).findFirst()
                    .ifPresent(guideBox::setValue);
        }

        ComboBox<Vehicle> vehicleBox = new ComboBox<>();
        vehicleBox.getItems().addAll(vehicles);
        vehicleBox.setPromptText("Select vehicle");
        vehicleBox.setPrefWidth(260);
        vehicleBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Vehicle v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getBrand() + " " + v.getModel() + " · " + v.getPlateNumber());
            }
        });
        vehicleBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Vehicle v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getBrand() + " " + v.getModel() + " · " + v.getPlateNumber());
            }
        });
        if (isEdit && existing.getVehicleId() != null) {
            vehicles.stream().filter(v -> existing.getVehicleId().equals(v.getId())).findFirst()
                    .ifPresent(vehicleBox::setValue);
        }

        grid.addRow(0, new Label("Tour Name *"), nameField);
        grid.addRow(1, new Label("Start Date *"), startPicker);
        grid.addRow(2, new Label("End Date *"),   endPicker);
        grid.addRow(3, new Label("Hotel"),        hotelField);
        grid.addRow(4, new Label("Guide *"),      guideBox);
        grid.addRow(5, new Label("Vehicle"),      vehicleBox);
        grid.addRow(6, new Label("Price (€)"),    priceField);

        dialog.getDialogPane().setContent(new VBox(grid));
        dialog.getDialogPane().setPrefWidth(460);

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            if (nameField.getText().trim().isEmpty()) {
                Toast.error("Tour name is required.");
                return null;
            }
            if (guideBox.getValue() == null) {
                Toast.error("Please select a Guide.");
                return null;
            }
            Tour tour = isEdit ? existing : new Tour();
            tour.setTourName(nameField.getText().trim());
            tour.setStartDate(startPicker.getValue());
            tour.setEndDate(endPicker.getValue());
            tour.setHotelName(hotelField.getText().trim().isEmpty() ? null : hotelField.getText().trim());
            tour.setGuideId(guideBox.getValue().getId());
            tour.setVehicleId(vehicleBox.getValue() != null ? vehicleBox.getValue().getId() : null);
            if (!priceField.getText().trim().isEmpty()) {
                try { tour.setFinalPrice(Double.parseDouble(priceField.getText().trim())); }
                catch (NumberFormatException e) { Toast.error("Price must be a number."); return null; }
            }
            return tour;
        });

        return dialog.showAndWait().orElse(null);
    }
}
