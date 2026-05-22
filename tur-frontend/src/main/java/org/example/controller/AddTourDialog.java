package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.example.model.Customer;
import org.example.model.Guide;
import org.example.model.Route;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.service.SessionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;

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
        routeBox.setPromptText("Select route");
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
        calculatedPriceField.setPromptText("Calculated price (€) — auto from route & vehicle");
        calculatedPriceField.setPrefWidth(260);
        calculatedPriceField.setEditable(false);

        Label vehicleFeeInfo = new Label();
        vehicleFeeInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        vehicleFeeInfo.setManaged(false);
        vehicleFeeInfo.setVisible(false);

        VBox calcBox = new VBox(2, calculatedPriceField, vehicleFeeInfo);

        TextField priceField = new TextField(isEdit && existing.getFinalPrice() != null && existing.getFinalPrice() > 0
                ? String.valueOf(existing.getFinalPrice().intValue()) : "");
        priceField.setPromptText("Final price (€)");
        priceField.setPrefWidth(260);

        Label priceInfo = new Label("Doldurulmaması halinde calculated price final price olacaktır.");
        priceInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        VBox priceBox = new VBox(2, priceField, priceInfo);



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

        Long initialVehicleId = isEdit ? existing.getVehicleId() : null;

        Runnable updatePrice = () -> {
            double routePrice = 0.0;
            Route selRoute = routeBox.getValue();
            if (selRoute != null && selRoute.getBasePrice() != null) {
                routePrice = selRoute.getBasePrice();
            }

            double vehicleTotal = 0.0;
            long days = 0;
            Vehicle selVehicle = vehicleBox.getValue();
            LocalDate start = startPicker.getValue();
            LocalDate end = endPicker.getValue();

            if (selVehicle != null && selVehicle.getDailyRentalFee() != null && start != null && end != null) {
                days = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
                if (days > 0) {
                    vehicleTotal = selVehicle.getDailyRentalFee() * days;
                }
            }

            double total = routePrice + vehicleTotal;
            if (total > 0) {
                calculatedPriceField.setText(String.valueOf(total));
            } else {
                calculatedPriceField.setText("");
            }

            if (selVehicle != null && selVehicle.getDailyRentalFee() != null && vehicleTotal > 0) {
                vehicleFeeInfo.setText("Günlük araç ücreti dahildir (" + days + " gün)");
                vehicleFeeInfo.setManaged(true);
                vehicleFeeInfo.setVisible(true);
            } else {
                vehicleFeeInfo.setManaged(false);
                vehicleFeeInfo.setVisible(false);
            }
        };

        routeBox.valueProperty().addListener((obs, oldR, newR) -> updatePrice.run());
        vehicleBox.valueProperty().addListener((obs, oldV, newV) -> updatePrice.run());

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

        startPicker.valueProperty().addListener((obs, o, n) -> { updateAvailability.run(); updatePrice.run(); });
        endPicker.valueProperty().addListener((obs, o, n)   -> { updateAvailability.run(); updatePrice.run(); });
        guideBox.valueProperty().addListener((obs, o, n)    -> updateAvailability.run());

        // Initial populate
        updateAvailability.run();
        updatePrice.run();

        // Restore vehicle selection in edit mode
        if (initialVehicleId != null) {
            vehicleBox.getItems().stream()
                    .filter(v -> initialVehicleId.equals(v.getId()))
                    .findFirst().ifPresent(vehicleBox::setValue);
        }
        updatePrice.run();

        // --- Layout ---
        boolean isGuideUser = SessionManager.getInstance().isGuide();
        grid.addRow(0, new Label("Tour Name *"),       nameField);
        grid.addRow(1, new Label("Start Date *"),      startPicker);
        grid.addRow(2, new Label("End Date *"),        endPicker);
        grid.addRow(3, new Label("Hotel *"),             hotelField);
        grid.addRow(4, new Label("Route *"),             routeBox);
        if (!isGuideUser) {
            grid.addRow(5, new Label("Guide *"),       guideBox);
            grid.add(new Label(), 0, 6);
            grid.add(guideWarning, 1, 6);
            grid.addRow(7, new Label("Vehicle *"),       vehicleBox);
            grid.addRow(8, new Label("Calc. Price (€)"), calcBox);
            grid.addRow(9, new Label("Final Price (€)"), priceBox);
        } else {
            grid.addRow(5, new Label("Vehicle *"),       vehicleBox);
            grid.addRow(6, new Label("Calc. Price (€)"), calcBox);
            grid.addRow(7, new Label("Final Price (€)"), priceBox);
        }

        // --- Customers Layout ---
        VBox customersContainer = new VBox(8);
        Button addCustomerBtn = new Button("+ Add Customer");
        addCustomerBtn.getStyleClass().add("btn-secondary");
        addCustomerBtn.setOnAction(e -> customersContainer.getChildren().add(createCustomerRow(customersContainer)));

        HBox customerHeader = new HBox(15, new Label("New Customers"), addCustomerBtn);
        customerHeader.setAlignment(Pos.CENTER_LEFT);
        customerHeader.setStyle("-fx-font-weight: bold; -fx-padding: 10 28 5 28;");
        
        customersContainer.setPadding(new Insets(0, 28, 0, 28));

        VBox contentBox = new VBox(10, grid, customerHeader, customersContainer);
        contentBox.setPadding(new Insets(0, 0, 20, 0));

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: white; -fx-border-color: transparent;");
        scrollPane.setPrefSize(500, 500);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setPrefWidth(520);

        final Button btSave = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        btSave.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            boolean valid = true;
            nameField.setStyle("");
            guideBox.setStyle("");
            startPicker.setStyle("");
            endPicker.setStyle("");
            hotelField.setStyle("");
            routeBox.setStyle("");
            vehicleBox.setStyle("");
            priceField.setStyle("");

            if (nameField.getText().trim().isEmpty()) {
                nameField.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (hotelField.getText().trim().isEmpty()) {
                hotelField.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (routeBox.getValue() == null) {
                routeBox.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (vehicleBox.getValue() == null) {
                vehicleBox.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (!SessionManager.getInstance().isGuide() && guideBox.getValue() == null) {
                guideBox.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            LocalDate start = startPicker.getValue();
            LocalDate end   = endPicker.getValue();
            if (start == null) {
                startPicker.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (end == null || (start != null && end.isBefore(start))) {
                endPicker.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                valid = false;
            }
            if (!priceField.getText().trim().isEmpty()) {
                try {
                    Double.parseDouble(priceField.getText().trim());
                } catch (NumberFormatException e) {
                    priceField.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px;");
                    valid = false;
                }
            }

            if (!valid) {
                event.consume();
                Toast.error("Lütfen kırmızı ile işaretli alanları kontrol ediniz.");
            }
        });

        // --- Result converter ---
        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;

            LocalDate start = startPicker.getValue();
            LocalDate end   = endPicker.getValue();

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
                catch (NumberFormatException ignored) {}
            }
            if (!priceField.getText().trim().isEmpty()) {
                try { tour.setFinalPrice(Double.parseDouble(priceField.getText().trim())); }
                catch (NumberFormatException ignored) {}
            } else {
                if (!calculatedPriceField.getText().trim().isEmpty()) {
                    try { tour.setFinalPrice(Double.parseDouble(calculatedPriceField.getText().trim())); }
                    catch (NumberFormatException ignored) {}
                }
            }

            List<Customer> newCustomers = new ArrayList<>();
            for (javafx.scene.Node node : customersContainer.getChildren()) {
                if (node instanceof HBox row) {
                    TextField nameF = (TextField) row.getChildren().get(0);
                    TextField passF = (TextField) row.getChildren().get(1);
                    TextField phoneF = (TextField) row.getChildren().get(2);
                    TextField natF = (TextField) row.getChildren().get(3);
                    
                    if (!nameF.getText().trim().isEmpty()) {
                        Customer c = new Customer();
                        c.setFullName(nameF.getText().trim());
                        c.setPassportNumber(passF.getText().trim());
                        c.setPhone(phoneF.getText().trim());
                        c.setNationality(natF.getText().trim());
                        newCustomers.add(c);
                    }
                }
            }
            tour.setCustomers(newCustomers);

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

    private static HBox createCustomerRow(VBox container) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 8; -fx-background-radius: 4; -fx-border-color: #e0e0e0; -fx-border-radius: 4;");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        HBox.setHgrow(nameField, Priority.ALWAYS);

        TextField passportField = new TextField();
        passportField.setPromptText("Passport");
        HBox.setHgrow(passportField, Priority.ALWAYS);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        phoneField.setPrefWidth(100);

        TextField natField = new TextField();
        natField.setPromptText("Nationality");
        natField.setPrefWidth(90);

        Button removeBtn = new Button("✕");
        removeBtn.setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold; -fx-cursor: hand;");
        removeBtn.setOnAction(e -> container.getChildren().remove(row));

        row.getChildren().addAll(nameField, passportField, phoneField, natField, removeBtn);
        return row;
    }
}
