package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Tour;
import org.example.service.TourService;

import java.util.List;
import java.util.stream.Collectors;

public class GuideTourManagementController {

    @FXML private Label totalEarningsValue;
    @FXML private Label monthlyEarningsValue;
    @FXML private Label completedToursValue;

    @FXML private TableView<Tour> recentToursTable;
    @FXML private TableColumn<Tour, String> colRecentName;
    @FXML private TableColumn<Tour, String> colRecentDeparture;
    @FXML private TableColumn<Tour, String> colRecentDestination;
    @FXML private TableColumn<Tour, String> colRecentDate;
    @FXML private TableColumn<Tour, String> colRecentHotel;
    @FXML private TableColumn<Tour, String> colRecentVehicle;
    @FXML private TableColumn<Tour, String> colRecentGroup;
    @FXML private TableColumn<Tour, String> colRecentStatus;

    @FXML private TableView<Tour> upcomingToursTable;
    @FXML private TableColumn<Tour, String> colUpcomingName;
    @FXML private TableColumn<Tour, String> colUpcomingDeparture;
    @FXML private TableColumn<Tour, String> colUpcomingDestination;
    @FXML private TableColumn<Tour, String> colUpcomingDate;
    @FXML private TableColumn<Tour, String> colUpcomingHotel;
    @FXML private TableColumn<Tour, String> colUpcomingVehicle;
    @FXML private TableColumn<Tour, String> colUpcomingGroup;
    @FXML private TableColumn<Tour, String> colUpcomingStatus;

    private final TourService tourService = new TourService();

    @FXML
    public void initialize() {
        bindColumns(colRecentName, colRecentDeparture, colRecentDestination, colRecentDate,
                colRecentHotel, colRecentVehicle, colRecentGroup, colRecentStatus);

        bindColumns(colUpcomingName, colUpcomingDeparture, colUpcomingDestination, colUpcomingDate,
                colUpcomingHotel, colUpcomingVehicle, colUpcomingGroup, colUpcomingStatus);

        loadData();
    }

    private void bindColumns(TableColumn<Tour, String> name, TableColumn<Tour, String> departure,
                             TableColumn<Tour, String> destination, TableColumn<Tour, String> date,
                             TableColumn<Tour, String> hotel, TableColumn<Tour, String> vehicle,
                             TableColumn<Tour, String> group, TableColumn<Tour, String> status) {
        name.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        departure.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        destination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        date.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        hotel.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        group.setCellValueFactory(new PropertyValueFactory<>("groupSize"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        vehicle.setCellValueFactory(cd ->
                new SimpleStringProperty(tourService.getVehicleDisplayName(cd.getValue())));
    }

    private void loadData() {
        try {
            List<Tour> tours = tourService.getAllTours();

            List<Tour> recent = tours.stream()
                    .filter(t -> "Completed".equals(t.getStatus()) || "Active".equals(t.getStatus()))
                    .collect(Collectors.toList());

            List<Tour> upcoming = tours.stream()
                    .filter(t -> "Upcoming".equals(t.getStatus()))
                    .collect(Collectors.toList());

            recentToursTable.getItems().setAll(recent);
            upcomingToursTable.getItems().setAll(upcoming);

            double totalEarnings = tourService.calculateTotalRevenue(tours);
            totalEarningsValue.setText("€" + String.format("%,.0f", totalEarnings));

            double monthlyEarnings = tourService.calculateTotalRevenue(recent);
            monthlyEarningsValue.setText("€" + String.format("%,.0f", monthlyEarnings));

            completedToursValue.setText(String.valueOf(recent.size()));

        } catch (Exception e) {
            e.printStackTrace();
            totalEarningsValue.setText("—");
            monthlyEarningsValue.setText("—");
            completedToursValue.setText("—");
        }
    }

    @FXML
    private void onAddTour() {
        // Add Tour dialog - ileride
    }
}