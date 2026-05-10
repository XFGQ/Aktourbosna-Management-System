package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.service.TourService;
import org.example.service.VehicleService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuideTourManagementController {

    @FXML private Label totalEarningsValue;
    @FXML private Label monthlyEarningsValue;
    @FXML private Label completedToursValue;

    @FXML private TableView<Tour> recentToursTable;
    @FXML private TableColumn<Tour, String> colRecentName;
    @FXML private TableColumn<Tour, String> colRecentDate;
    @FXML private TableColumn<Tour, String> colRecentHotel;
    @FXML private TableColumn<Tour, String> colRecentVehicle;
    @FXML private TableColumn<Tour, String> colRecentStatus;

    @FXML private TableView<Tour> upcomingToursTable;
    @FXML private TableColumn<Tour, String> colUpcomingName;
    @FXML private TableColumn<Tour, String> colUpcomingDate;
    @FXML private TableColumn<Tour, String> colUpcomingHotel;
    @FXML private TableColumn<Tour, String> colUpcomingVehicle;
    @FXML private TableColumn<Tour, String> colUpcomingStatus;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TourService    tourService    = new TourService();
    private final VehicleService vehicleService = new VehicleService();

    private final Map<Long, String> vehicleNames = new HashMap<>();

    @FXML
    public void initialize() {
        bindColumns(colRecentName, colRecentDate, colRecentHotel, colRecentVehicle, colRecentStatus);
        bindColumns(colUpcomingName, colUpcomingDate, colUpcomingHotel, colUpcomingVehicle, colUpcomingStatus);
        loadData();
    }

    @FXML
    public void refresh() { loadData(); }

    private void bindColumns(TableColumn<Tour, String> name, TableColumn<Tour, String> date,
                             TableColumn<Tour, String> hotel, TableColumn<Tour, String> vehicle,
                             TableColumn<Tour, String> status) {
        name.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTourName()));
        name.setCellFactory(col -> tooltipCell());

        date.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getStartDate() != null ? cd.getValue().getStartDate().format(DATE_FMT) : "—"));
        date.setCellFactory(col -> tooltipCell());

        hotel.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
        hotel.setCellFactory(col -> tooltipCell());

        vehicle.setCellValueFactory(cd -> new SimpleStringProperty(
                vehicleNames.getOrDefault(cd.getValue().getVehicleId(), "—")));
        vehicle.setCellFactory(col -> tooltipCell());

        status.setCellValueFactory(cd -> new SimpleStringProperty(tourService.deriveStatus(cd.getValue())));
        status.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
                Label badge = new Label(s);
                badge.getStyleClass().add(switch (s) {
                    case "Active"    -> "badge-active";
                    case "Upcoming"  -> "badge-upcoming";
                    case "Completed" -> "badge-completed";
                    default          -> "badge-default";
                });
                setGraphic(badge);
                setText(null);
            }
        });
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Tour>    tours    = tourService.getAllTours();
                List<Vehicle> vehicles = vehicleService.getAllVehicles();

                Map<Long, String> vNames = new HashMap<>();
                for (Vehicle v : vehicles)
                    if (v.getId() != null) vNames.put(v.getId(), v.getBrand() + " " + v.getModel());

                List<Tour> recent   = tourService.getRecentTours(tours);
                List<Tour> upcoming = tourService.getUpcomingTours(tours);
                double totalEarnings   = tourService.calculateTotalRevenue(tours);
                double monthlyEarnings = tourService.calculateTotalRevenue(recent);

                javafx.application.Platform.runLater(() -> {
                    vehicleNames.clear(); vehicleNames.putAll(vNames);
                    recentToursTable.getItems().setAll(recent);
                    upcomingToursTable.getItems().setAll(upcoming);
                    totalEarningsValue.setText("€" + String.format("%,.0f", totalEarnings));
                    monthlyEarningsValue.setText("€" + String.format("%,.0f", monthlyEarnings));
                    completedToursValue.setText(String.valueOf(recent.size()));
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalEarningsValue.setText("—");
                    monthlyEarningsValue.setText("—");
                    completedToursValue.setText("—");
                });
            }
        }).start();
    }

    private static TableCell<Tour, String> tooltipCell() {
        return new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setTooltip(null); }
                else { setText(s); setTooltip(new Tooltip(s)); }
            }
        };
    }

    @FXML
    private void onAddTour() {}
}
