package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.example.model.Guide;
import org.example.model.Route;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.service.SessionManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AddTourDialog {

    public static Tour show(List<Guide> guides, List<Vehicle> vehicles, List<Tour> allTours, List<Route> routes) {
        return show(null, guides, vehicles, allTours, routes);
    }

    public static Tour show(Tour existing, List<Guide> guides, List<Vehicle> vehicles, List<Tour> allTours, List<Route> routes) {
        boolean isEdit  = existing != null;
        Long  excludeId = isEdit ? existing.getTourId() : null;

        Dialog<Tour> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Tour" : "Add Tour");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);
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

        // --- Route combo ---
        ComboBox<Route> routeBox = new ComboBox<>();
        if (routes != null) routeBox.getItems().addAll(routes);
        routeBox.setPromptText("Select route (optional)");
        routeBox.setPrefWidth(260);
        routeBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Route r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null
                        : (r.getRouteName() != null ? r.getRouteName() + " (" : "(")
                          + r.getStartCity() + " → " + r.getEndCity() + ")");
            }
        });
        routeBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Route r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null
                        : (r.getRouteName() != null ? r.getRouteName() + " (" : "(")
                          + r.getStartCity() + " → " + r.getEndCity() + ")");
            }
        });
        if (isEdit && existing.getRouteId() != null && routes != null) {
            routes.stream().filter(r -> existing.getRouteId().equals(r.getRouteId())).findFirst()
                    .ifPresent(routeBox::setValue);
        }

        TextField calculatedPriceField = new TextField(isEdit && existing.getCalculatedPrice() != null && existing.getCalculatedPrice() > 0
                ? String.valueOf(existing.getCalculatedPrice().intValue()) : "");
        calculatedPriceField.setPromptText("Calculated price (€) — auto from route");
        calculatedPriceField.setPrefWidth(260);

        TextField priceField = new TextField(isEdit && existing.getFinalPrice() != null && existing.getFinalPrice() > 0
                ? String.valueOf(existing.getFinalPrice().intValue()) : "");
        priceField.setPromptText("Final price (€)");
        priceField.setPrefWidth(260);

        // Auto-suggest calculatedPrice from route basePrice when field is empty
        routeBox.valueProperty().addListener((obs, oldR, newR) -> {
            if (calculatedPriceField.getText().trim().isEmpty() && newR != null
                    && newR.getBasePrice() != null && newR.getBasePrice() > 0) {
                calculatedPriceField.setText(String.valueOf(newR.getBasePrice().intValue()));
            }
        });

        // --- Guide combo ---
        ComboBox<Guide> guideBox = new ComboBox<>();
        guideBox.getItems().addAll(guides);
        guideBox.setPromptText("Select guide");
        guideBox.setPrefWidth(260);
        guideBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Guide g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? null : g.getUsername());
            }
        });
        guideBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Guide g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? null : g.getUsername());
            }
        });
        if (isEdit && existing.getGuideId() != null) {
            guides.stream().filter(g -> existing.getGuideId().equals(g.getId())).findFirst()
                    .ifPresent(guideBox::setValue);
        }

        Label guideWarning = new Label();
        guideWarning.setStyle("-fx-text-fill: #E65100; -fx-font-size: 11px; -fx-font-weight: bold;");
        guideWarning.setWrapText(true);
        guideWarning.setMaxWidth(260);
        guideWarning.setMinHeight(14);

        // --- Vehicle combo ---
        ComboBox<Vehicle> vehicleBox = new ComboBox<>();
        vehicleBox.setPromptText("Select vehicle (optional)");
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

        Long initialVehicleId = isEdit ? existing.getVehicleId() : null;

        // --- Availability updater ---
        Runnable updateAvailability = () -> {
            LocalDate start = startPicker.getValue();
            LocalDate end   = endPicker.getValue();
            if (start == null || end == null) return;

            // Guide conflict warning
            Guide sel = guideBox.getValue();
            if (sel != null) {
                String conflict = findGuideConflict(sel.getId(), start, end, allTours, excludeId);
                guideWarning.setText(conflict != null
                        ? "⚠ Bu rehber \"" + conflict + "\" turuna bu tarihlerde atanmış"
                        : "");
            } else {
                guideWarning.setText("");
            }

            // Vehicle: only available vehicles with no date conflict
            Long currentVehId = vehicleBox.getValue() != null ? vehicleBox.getValue().getId() : null;
            List<Vehicle> avail = vehicles.stream()
                    .filter(v -> !Boolean.FALSE.equals(v.getAvailable()))
                    .filter(v -> v.getId() == null || !vehicleConflicts(v.getId(), start, end, allTours, excludeId))
                    .collect(Collectors.toList());
            vehicleBox.getItems().setAll(avail);
            if (currentVehId != null) {
                avail.stream().filter(v -> currentVehId.equals(v.getId()))
                        .findFirst().ifPresent(vehicleBox::setValue);
            }
        };

        startPicker.valueProperty().addListener((obs, o, n) -> updateAvailability.run());
        endPicker.valueProperty().addListener((obs, o, n)   -> updateAvailability.run());
        guideBox.valueProperty().addListener((obs, o, n)    -> updateAvailability.run());

        // Initial populate
        updateAvailability.run();

        // Restore vehicle selection in edit mode
        if (initialVehicleId != null) {
            vehicleBox.getItems().stream()
                    .filter(v -> initialVehicleId.equals(v.getId()))
                    .findFirst().ifPresent(vehicleBox::setValue);
        }

        // --- Layout ---
        boolean isGuideUser = SessionManager.getInstance().isGuide();
        grid.addRow(0, new Label("Tour Name *"),       nameField);
        grid.addRow(1, new Label("Start Date *"),      startPicker);
        grid.addRow(2, new Label("End Date *"),        endPicker);
        grid.addRow(3, new Label("Hotel"),             hotelField);
        grid.addRow(4, new Label("Route"),             routeBox);
        if (!isGuideUser) {
            grid.addRow(5, new Label("Guide *"),       guideBox);
            grid.add(new Label(), 0, 6);
            grid.add(guideWarning, 1, 6);
            grid.addRow(7, new Label("Vehicle"),       vehicleBox);
            grid.addRow(8, new Label("Calc. Price (€)"), calculatedPriceField);
            grid.addRow(9, new Label("Final Price (€)"), priceField);
        } else {
            grid.addRow(5, new Label("Vehicle"),       vehicleBox);
            grid.addRow(6, new Label("Calc. Price (€)"), calculatedPriceField);
            grid.addRow(7, new Label("Final Price (€)"), priceField);
        }

        dialog.getDialogPane().setContent(new VBox(grid));
        dialog.getDialogPane().setPrefWidth(460);

        // --- Result converter ---
        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;

            if (nameField.getText().trim().isEmpty()) {
                Toast.error("Tour name is required.");
                return null;
            }
            if (!SessionManager.getInstance().isGuide() && guideBox.getValue() == null) {
                Toast.error("Please select a Guide.");
                return null;
            }
            LocalDate start = startPicker.getValue();
            LocalDate end   = endPicker.getValue();
            if (start != null && end != null && end.isBefore(start)) {
                Toast.error("End date cannot be before start date.");
                return null;
            }

            Tour tour = isEdit ? existing : new Tour();
            tour.setTourName(nameField.getText().trim());
            tour.setStartDate(start);
            tour.setEndDate(end);
            tour.setHotelName(hotelField.getText().trim().isEmpty() ? null : hotelField.getText().trim());
            if (SessionManager.getInstance().isGuide()) {
                tour.setGuideId(SessionManager.getInstance().getGuideId());
            } else {
                tour.setGuideId(guideBox.getValue().getId());
            }
            tour.setVehicleId(vehicleBox.getValue() != null ? vehicleBox.getValue().getId() : null);
            tour.setRouteId(routeBox.getValue() != null ? routeBox.getValue().getRouteId() : null);
            if (!calculatedPriceField.getText().trim().isEmpty()) {
                try { tour.setCalculatedPrice(Double.parseDouble(calculatedPriceField.getText().trim())); }
                catch (NumberFormatException e) { Toast.error("Calculated price must be a number."); return null; }
            }
            if (!priceField.getText().trim().isEmpty()) {
                try { tour.setFinalPrice(Double.parseDouble(priceField.getText().trim())); }
                catch (NumberFormatException e) { Toast.error("Final price must be a number."); return null; }
            }
            return tour;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static String findGuideConflict(Long guideId, LocalDate start, LocalDate end,
                                             List<Tour> tours, Long excludeId) {
        if (guideId == null || start == null || end == null || tours == null) return null;
        return tours.stream()
                .filter(t -> excludeId == null || !excludeId.equals(t.getTourId()))
                .filter(t -> guideId.equals(t.getGuideId()))
                .filter(t -> t.getStartDate() != null && t.getEndDate() != null)
                .filter(t -> !end.isBefore(t.getStartDate()) && !start.isAfter(t.getEndDate()))
                .map(Tour::getTourName)
                .findFirst()
                .orElse(null);
    }

    private static boolean vehicleConflicts(Long vehicleId, LocalDate start, LocalDate end,
                                             List<Tour> tours, Long excludeId) {
        if (vehicleId == null || start == null || end == null || tours == null) return false;
        return tours.stream()
                .filter(t -> excludeId == null || !excludeId.equals(t.getTourId()))
                .filter(t -> vehicleId.equals(t.getVehicleId()))
                .filter(t -> t.getStartDate() != null && t.getEndDate() != null)
                .anyMatch(t -> !end.isBefore(t.getStartDate()) && !start.isAfter(t.getEndDate()));
    }
}
