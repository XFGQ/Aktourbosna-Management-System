package org.example.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Tour;
import org.example.service.ExpenseService;
import org.example.service.TourService;

import java.util.List;
import java.util.stream.Collectors;

public class TourManagementController {

    @FXML private Label totalToursValue;
    @FXML private Label revenueValue;
    @FXML private Label expensesValue;
    @FXML private Label netProfitValue;

    @FXML private TableView<Tour> recentToursTable;
    @FXML private TableColumn<Tour, String> colRecentName;
    @FXML private TableColumn<Tour, String> colRecentDeparture;
    @FXML private TableColumn<Tour, String> colRecentDestination;
    @FXML private TableColumn<Tour, String> colRecentDate;
    @FXML private TableColumn<Tour, String> colRecentHotel;
    @FXML private TableColumn<Tour, String> colRecentVehicle;
    @FXML private TableColumn<Tour, String> colRecentGuide;
    @FXML private TableColumn<Tour, Number> colRecentPrice;
    @FXML private TableColumn<Tour, String> colRecentStatus;

    @FXML private TableView<Tour> upcomingToursTable;
    @FXML private TableColumn<Tour, String> colUpcomingName;
    @FXML private TableColumn<Tour, String> colUpcomingDeparture;
    @FXML private TableColumn<Tour, String> colUpcomingDestination;
    @FXML private TableColumn<Tour, String> colUpcomingDate;
    @FXML private TableColumn<Tour, String> colUpcomingHotel;
    @FXML private TableColumn<Tour, String> colUpcomingVehicle;
    @FXML private TableColumn<Tour, String> colUpcomingGuide;
    @FXML private TableColumn<Tour, Number> colUpcomingPrice;
    @FXML private TableColumn<Tour, String> colUpcomingStatus;

    private final TourService tourService = new TourService();
    private final ExpenseService expenseService = new ExpenseService();

    @FXML
    public void initialize() {
        bindColumns(colRecentName, colRecentDeparture, colRecentDestination, colRecentDate,
                colRecentHotel, colRecentVehicle, colRecentGuide, colRecentPrice, colRecentStatus);

        bindColumns(colUpcomingName, colUpcomingDeparture, colUpcomingDestination, colUpcomingDate,
                colUpcomingHotel, colUpcomingVehicle, colUpcomingGuide, colUpcomingPrice, colUpcomingStatus);

        loadData();
    }

    @FXML
    public void refresh() {
        loadData();
    }

    private void bindColumns(TableColumn<Tour, String> name, TableColumn<Tour, String> departure,
                             TableColumn<Tour, String> destination, TableColumn<Tour, String> date,
                             TableColumn<Tour, String> hotel, TableColumn<Tour, String> vehicle,
                             TableColumn<Tour, String> guide, TableColumn<Tour, Number> price,
                             TableColumn<Tour, String> status) {
        name.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        departure.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        destination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        date.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        hotel.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        guide.setCellValueFactory(cd ->
                new SimpleStringProperty(tourService.getGuideName(cd.getValue())));
        vehicle.setCellValueFactory(cd ->
                new SimpleStringProperty(tourService.getVehicleDisplayName(cd.getValue())));
        price.setCellValueFactory(cd ->
                new SimpleObjectProperty<>(cd.getValue().getFinalPrice()));
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Tour> tours = tourService.getAllTours();
                List<Tour> recent = tours.stream()
                        .filter(t -> "Completed".equals(t.getStatus()) || "Active".equals(t.getStatus()))
                        .collect(Collectors.toList());
                List<Tour> upcoming = tours.stream()
                        .filter(t -> "Upcoming".equals(t.getStatus()))
                        .collect(Collectors.toList());
                double revenue = tourService.calculateTotalRevenue(tours);
                double totalExpenses = tours.stream()
                        .mapToDouble(t -> expenseService.calculateTotal(expenseService.getExpensesForTour(t)))
                        .sum();
                javafx.application.Platform.runLater(() -> {
                    recentToursTable.getItems().setAll(recent);
                    upcomingToursTable.getItems().setAll(upcoming);
                    totalToursValue.setText(String.valueOf(tours.size()));
                    revenueValue.setText("€" + String.format("%,.0f", revenue));
                    expensesValue.setText("€" + String.format("%,.0f", totalExpenses));
                    netProfitValue.setText("€" + String.format("%,.0f", revenue - totalExpenses));
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalToursValue.setText("—");
                    revenueValue.setText("—");
                    expensesValue.setText("—");
                    netProfitValue.setText("—");
                });
            }
        }).start();
    }

    @FXML
    private void onAddTour() {

    }
}