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

import java.util.List;

public class AddTourDialog {

    public static Tour show(List<Guide> guides, List<Vehicle> vehicles) {
        return show(null, guides, vehicles);
    }

    public static Tour show(Tour existing, List<Guide> guides, List<Vehicle> vehicles) {
        boolean editMode = existing != null;

        Dialog<Tour> dialog = new Dialog<>();
        dialog.setTitle(editMode ? "Edit Tour" : "Add Tour");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType saveBtn = new ButtonType(editMode ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 28, 10, 28));

        TextField tourName = new TextField();
        tourName.setPromptText("Tour name");
        tourName.setPrefWidth(260);

        TextField hotelName = new TextField();
        hotelName.setPromptText("Hotel name");

        DatePicker startDate = new DatePicker();
        startDate.setPromptText("Start date");
        startDate.setPrefWidth(260);

        DatePicker endDate = new DatePicker();
        endDate.setPromptText("End date");
        endDate.setPrefWidth(260);

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

        ComboBox<Vehicle> vehicleBox = new ComboBox<>();
        vehicleBox.getItems().addAll(vehicles);
        vehicleBox.setPromptText("Select vehicle");
        vehicleBox.setPrefWidth(260);
        vehicleBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Vehicle v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getBrand() + " " + v.getModel() + " (" + v.getPlateNumber() + ")");
            }
        });
        vehicleBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Vehicle v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getBrand() + " " + v.getModel() + " (" + v.getPlateNumber() + ")");
            }
        });

        TextField calcPrice = new TextField();
        calcPrice.setPromptText("Calculated price (€)");

        TextField finalPrice = new TextField();
        finalPrice.setPromptText("Final price (€)");

        // Fill existing values when editing
        if (editMode) {
            tourName.setText(existing.getTourName() != null ? existing.getTourName() : "");
            hotelName.setText(existing.getHotelName() != null ? existing.getHotelName() : "");
            startDate.setValue(existing.getStartDate());
            endDate.setValue(existing.getEndDate());
            calcPrice.setText(existing.getCalculatedPrice() != null ? String.valueOf(existing.getCalculatedPrice()) : "");
            finalPrice.setText(existing.getFinalPrice() != null ? String.valueOf(existing.getFinalPrice()) : "");
            if (existing.getGuideId() != null)
                guides.stream().filter(g -> existing.getGuideId().equals(g.getId())).findFirst().ifPresent(guideBox::setValue);
            if (existing.getVehicleId() != null)
                vehicles.stream().filter(v -> existing.getVehicleId().equals(v.getId())).findFirst().ifPresent(vehicleBox::setValue);
        }

        grid.addRow(0, new Label("Tour Name *"), tourName);
        grid.addRow(1, new Label("Hotel"), hotelName);
        grid.addRow(2, new Label("Start Date *"), startDate);
        grid.addRow(3, new Label("End Date *"), endDate);
        grid.addRow(4, new Label("Guide *"), guideBox);
        grid.addRow(5, new Label("Vehicle"), vehicleBox);
        grid.addRow(6, new Label("Calc. Price (€)"), calcPrice);
        grid.addRow(7, new Label("Final Price (€)"), finalPrice);

        VBox content = new VBox(grid);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(460);

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            if (tourName.getText().trim().isEmpty() || startDate.getValue() == null || endDate.getValue() == null) {
                Toast.error("Tour Name, Start Date and End Date are required.");
                return null;
            }
            if (guideBox.getValue() == null) {
                Toast.error("Please select a Guide.");
                return null;
            }
            Tour t = editMode ? existing : new Tour();
            t.setTourName(tourName.getText().trim());
            t.setHotelName(hotelName.getText().trim().isEmpty() ? null : hotelName.getText().trim());
            t.setStartDate(startDate.getValue());
            t.setEndDate(endDate.getValue());
            t.setGuideId(guideBox.getValue() != null ? guideBox.getValue().getId() : null);
            t.setVehicleId(vehicleBox.getValue() != null ? vehicleBox.getValue().getId() : null);
            try { t.setCalculatedPrice(calcPrice.getText().isEmpty() ? 0.0 : Double.parseDouble(calcPrice.getText())); }
            catch (NumberFormatException ignored) { t.setCalculatedPrice(0.0); }
            try { t.setFinalPrice(finalPrice.getText().isEmpty() ? 0.0 : Double.parseDouble(finalPrice.getText())); }
            catch (NumberFormatException ignored) { t.setFinalPrice(0.0); }
            return t;
        });

        return dialog.showAndWait().orElse(null);
    }
}
