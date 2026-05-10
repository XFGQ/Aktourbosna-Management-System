package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.service.TourService;
import org.example.service.VehicleService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuideDashboardController {

    @FXML private Label toursThisWeekValue;
    @FXML private Label customersValue;
    @FXML private Label completedToursValue;
    @FXML private Label nextTourValue;

    @FXML private Label todayTourName;
    @FXML private Label todayTourTime;
    @FXML private Label todayHotel;
    @FXML private Label todayDestination;
    @FXML private Label todayGroupSize;
    @FXML private Label todayVehicle;

    @FXML private TableView<Tour> upcomingToursTable;
    @FXML private TableColumn<Tour, String> colDate;
    @FXML private TableColumn<Tour, String> colTourName;
    @FXML private TableColumn<Tour, String> colDestination;
    @FXML private TableColumn<Tour, String> colHotel;
    @FXML private TableColumn<Tour, String> colGroupSize;

    private final TourService tourService = new TourService();
    private final VehicleService vehicleService = new VehicleService();

    private final Map<Long, String> vehicleNames = new HashMap<>();

    @FXML
    public void initialize() {
        colTourName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTourName()));
        colDestination.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
        colHotel.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
        colDate.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getStartDate() != null ? cd.getValue().getStartDate().toString() : "—"));
        colGroupSize.setCellValueFactory(cd -> new SimpleStringProperty("—"));
        loadData();
    }

    @FXML
    public void refresh() {
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Tour> tours = tourService.getAllTours();
                List<Vehicle> vehicles = vehicleService.getAllVehicles();

                Map<Long, String> vNames = new HashMap<>();
                for (Vehicle v : vehicles) {
                    if (v.getId() != null) vNames.put(v.getId(), v.getBrand() + " " + v.getModel());
                }

                List<Tour> upcoming = tourService.getUpcomingTours(tours);
                List<Tour> recent = tourService.getRecentTours(tours);

                javafx.application.Platform.runLater(() -> {
                    vehicleNames.clear(); vehicleNames.putAll(vNames);
                    upcomingToursTable.getItems().setAll(upcoming);
                    toursThisWeekValue.setText(String.valueOf(upcoming.size()));
                    customersValue.setText("—");
                    completedToursValue.setText(String.valueOf(recent.size()));
                    nextTourValue.setText(upcoming.isEmpty() ? "—" : upcoming.get(0).getTourName());

                    if (!upcoming.isEmpty()) {
                        Tour next = upcoming.get(0);
                        todayTourName.setText(next.getTourName() != null ? next.getTourName() : "—");
                        todayHotel.setText(next.getHotelName() != null ? next.getHotelName() : "—");
                        todayDestination.setText("—");
                        todayGroupSize.setText("—");
                        todayVehicle.setText(vNames.getOrDefault(next.getVehicleId(), "—"));
                        todayTourTime.setText(next.getStartDate() != null ? next.getStartDate().toString() : "—");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    toursThisWeekValue.setText("—");
                    customersValue.setText("—");
                    completedToursValue.setText("—");
                    nextTourValue.setText("—");
                });
            }
        }).start();
    }
}
