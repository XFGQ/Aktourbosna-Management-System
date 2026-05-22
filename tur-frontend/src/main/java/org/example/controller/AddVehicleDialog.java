package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.model.Vehicle;

import java.time.LocalDate;

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
        TextField dailyFee = new TextField();
        dailyFee.setPromptText("e.g. 100.0");
        DatePicker lastService = new DatePicker();
        lastService.setPromptText("Last minor service date");
        CheckBox available = new CheckBox("Available");
        available.setSelected(true);

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
            dailyFee.setText(existing.getDailyRentalFee() != null ? existing.getDailyRentalFee().toString() : "");
            lastService.setValue(existing.getLastMinorService());
            available.setSelected(Boolean.TRUE.equals(existing.getAvailable()));
        }

        grid.add(new Label("Brand *:"),             0, 0);  grid.add(brand,           1, 0);
        grid.add(new Label("Model *:"),             0, 1);  grid.add(model,           1, 1);
        grid.add(new Label("Year *:"),              0, 2);  grid.add(year,            1, 2);
        grid.add(new Label("Color:"),               0, 3);  grid.add(color,           1, 3);
        grid.add(new Label("Plate Number *:"),      0, 4);  grid.add(plate,           1, 4);
        grid.add(new Label("Seat Capacity:"),       0, 5);  grid.add(seats,           1, 5);
        grid.add(new Label("Fuel Type *:"),         0, 6);  grid.add(fuel,            1, 6);
        grid.add(new Label("Mileage (km):"),        0, 7);  grid.add(mileage,         1, 7);
        grid.add(new Label("Fuel cons. (L/100km):"),0, 8);  grid.add(fuelConsumption, 1, 8);
        grid.add(new Label("Daily Fee (€) *:"),     0, 9);  grid.add(dailyFee,        1, 9);
        grid.add(new Label("Last Service:"),        0, 10); grid.add(lastService,     1, 10);
        grid.add(available,                            1, 11);

        dialog.getDialogPane().setContent(grid);

        final Button btSave = (Button) dialog.getDialogPane().lookupButton(saveButton);
        btSave.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            boolean valid = true;
            brand.setStyle("");
            model.setStyle("");
            year.setStyle("");
            plate.setStyle("");
            fuel.setStyle("");
            dailyFee.setStyle("");
            seats.setStyle("");
            mileage.setStyle("");
            fuelConsumption.setStyle("");

            if (brand.getText().trim().isEmpty()) {
                brand.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (model.getText().trim().isEmpty()) {
                model.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (year.getText().trim().isEmpty()) {
                year.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            } else {
                try { Integer.parseInt(year.getText().trim()); } catch (NumberFormatException e) {
                    year.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                    valid = false;
                }
            }
            if (plate.getText().trim().isEmpty()) {
                plate.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (fuel.getValue() == null) {
                fuel.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (dailyFee.getText().trim().isEmpty()) {
                dailyFee.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            } else {
                try { Double.parseDouble(dailyFee.getText().trim()); } catch (NumberFormatException e) {
                    dailyFee.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                    valid = false;
                }
            }
            
            if (!seats.getText().trim().isEmpty()) {
                try { Integer.parseInt(seats.getText().trim()); } catch (NumberFormatException e) {
                    seats.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                    valid = false;
                }
            }
            if (!mileage.getText().trim().isEmpty()) {
                try { Float.parseFloat(mileage.getText().trim()); } catch (NumberFormatException e) {
                    mileage.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                    valid = false;
                }
            }
            if (!fuelConsumption.getText().trim().isEmpty()) {
                try { Float.parseFloat(fuelConsumption.getText().trim()); } catch (NumberFormatException e) {
                    fuelConsumption.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                    valid = false;
                }
            }

            if (!valid) {
                event.consume();
                Toast.error("Lütfen kırmızı ile işaretli alanları kontrol ediniz.");
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                Vehicle v = editMode ? existing : new Vehicle();
                v.setBrand(brand.getText().trim());
                v.setModel(model.getText().trim());
                v.setYear(Integer.parseInt(year.getText().trim()));
                v.setColor(color.getText().trim().isEmpty() ? null : color.getText().trim());
                v.setPlateNumber(plate.getText().trim());
                v.setSeatCapacity(seats.getText().trim().isEmpty() ? null : Integer.parseInt(seats.getText().trim()));
                v.setFuelType(fuel.getValue());
                v.setCurrentMileage(mileage.getText().isEmpty() ? null : Float.parseFloat(mileage.getText().trim()));
                v.setAvgFuelConsumption(fuelConsumption.getText().isEmpty() ? null : Float.parseFloat(fuelConsumption.getText().trim()));
                v.setDailyRentalFee(Double.parseDouble(dailyFee.getText().trim()));
                v.setLastMinorService(lastService.getValue());
                v.setAvailable(available.isSelected());
                return v;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }
}
