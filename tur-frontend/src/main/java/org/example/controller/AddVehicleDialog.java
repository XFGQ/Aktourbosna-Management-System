package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.model.Vehicle;

public class AddVehicleDialog {

    public static Vehicle show() {
        return show(null);
    }

    public static Vehicle show(Vehicle existing) {
        Dialog<Vehicle> dialog = new Dialog<>();
        javafx.stage.Window owner = javafx.stage.Window.getWindows().stream()
                .filter(javafx.stage.Window::isShowing).findFirst().orElse(null);
        dialog.initOwner(owner);
        dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
        boolean editMode = existing != null;
        dialog.setTitle(editMode ? "Edit Vehicle" : "Add New Vehicle");
        dialog.setHeaderText(editMode ? "Update vehicle details" : "Enter vehicle details");

        ButtonType saveButton = new ButtonType(editMode ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField brand = new TextField();
        brand.setPromptText("e.g. Volkswagen");
        TextField model = new TextField();
        model.setPromptText("e.g. Passat");
        TextField year = new TextField();
        year.setPromptText("e.g. 2020");
        TextField color = new TextField();
        color.setPromptText("e.g. Black");
        TextField plate = new TextField();
        plate.setPromptText("e.g. A24-T-122");
        TextField seats = new TextField();
        seats.setPromptText("e.g. 5");
        ComboBox<String> fuel = new ComboBox<>();
        fuel.getItems().addAll("Diesel", "Gasoline", "Electric", "Hybrid");
        fuel.setValue("Diesel");
        TextField mileage = new TextField();
        mileage.setPromptText("e.g. 50000");
        TextField fuelConsumption = new TextField();
        fuelConsumption.setPromptText("e.g. 7.5");
        CheckBox available = new CheckBox("Available");
        available.setSelected(true);

        // Edit modunda mevcut değerleri doldur
        if (editMode) {
            brand.setText(existing.getBrand() != null ? existing.getBrand() : "");
            model.setText(existing.getModel() != null ? existing.getModel() : "");
            year.setText(existing.getYear() != null ? existing.getYear().toString() : "");
            color.setText(existing.getColor() != null ? existing.getColor() : "");
            plate.setText(existing.getPlateNumber() != null ? existing.getPlateNumber() : "");
            seats.setText(existing.getSeatCapacity() != null ? existing.getSeatCapacity().toString() : "");
            if (existing.getFuelType() != null) fuel.setValue(existing.getFuelType());
            mileage.setText(existing.getCurrentMileage() != null ? existing.getCurrentMileage().toString() : "");
            fuelConsumption.setText(existing.getAvgFuelConsumption() != null ? existing.getAvgFuelConsumption().toString() : "");
            available.setSelected(Boolean.TRUE.equals(existing.getAvailable()));
        }

        grid.add(new Label("Brand:"), 0, 0);          grid.add(brand, 1, 0);
        grid.add(new Label("Model:"), 0, 1);          grid.add(model, 1, 1);
        grid.add(new Label("Year:"), 0, 2);           grid.add(year, 1, 2);
        grid.add(new Label("Color:"), 0, 3);          grid.add(color, 1, 3);
        grid.add(new Label("Plate Number:"), 0, 4);   grid.add(plate, 1, 4);
        grid.add(new Label("Seat Capacity:"), 0, 5);  grid.add(seats, 1, 5);
        grid.add(new Label("Fuel Type:"), 0, 6);      grid.add(fuel, 1, 6);
        grid.add(new Label("Mileage (km):"), 0, 7);   grid.add(mileage, 1, 7);
        grid.add(new Label("Fuel cons. (L/100km):"), 0, 8); grid.add(fuelConsumption, 1, 8);
        grid.add(available, 1, 9);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                // Boş alan kontrolü
                if (brand.getText().trim().isEmpty() || model.getText().trim().isEmpty()
                        || year.getText().trim().isEmpty() || plate.getText().trim().isEmpty()
                        || seats.getText().trim().isEmpty()) {
                    Toast.error("Brand, Model, Year, Plate Number and Seats are required.");
                    return null;
                }
                try {
                    Vehicle v = editMode ? existing : new Vehicle();
                    v.setBrand(brand.getText().trim());
                    v.setModel(model.getText().trim());
                    v.setYear(Integer.parseInt(year.getText().trim()));
                    v.setColor(color.getText().trim());
                    v.setPlateNumber(plate.getText().trim());
                    v.setSeatCapacity(Integer.parseInt(seats.getText().trim()));
                    v.setFuelType(fuel.getValue());
                    v.setCurrentMileage(mileage.getText().isEmpty() ? 0f : Float.parseFloat(mileage.getText().trim()));
                    v.setAvgFuelConsumption(fuelConsumption.getText().isEmpty() ? 0f : Float.parseFloat(fuelConsumption.getText().trim()));
                    v.setAvailable(available.isSelected());
                    return v;
                } catch (NumberFormatException e) {
                    Toast.error("Year, seats, mileage and fuel consumption must be numbers.");
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }
}