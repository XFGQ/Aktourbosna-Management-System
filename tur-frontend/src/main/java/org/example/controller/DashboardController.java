package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Tour;
import org.example.service.GuideService;
import org.example.service.TourService;
import org.example.service.VehicleService;

import java.util.List;

public class DashboardController {

    @FXML private Label totalToursValue;
    @FXML private Label totalToursChange;
    @FXML private Label revenueValue;
    @FXML private Label revenueChange;
    @FXML private Label guidesValue;
    @FXML private Label vehiclesValue;
    @FXML private Label vehiclesStatus;

    @FXML private TableView<Tour> recentToursTable;
    @FXML private TableColumn<Tour, String> colTourName;
    @FXML private TableColumn<Tour, String> colDestination;
    @FXML private TableColumn<Tour, String> colDate;
    @FXML private TableColumn<Tour, String> colGuide;
    @FXML private TableColumn<Tour, String> colStatus;

    private final TourService tourService = new TourService();
    private final GuideService guideService = new GuideService();
    private final VehicleService vehicleService = new VehicleService();

    @FXML
    public void initialize() {
        colTourName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Guide objesinden fullName çekmek için custom cell value factory
        colGuide.setCellValueFactory(cellData ->
                new SimpleStringProperty(tourService.getGuideName(cellData.getValue())));

        loadData();
    }

    private void loadData() {
        try {
            List<Tour> tours = tourService.getAllTours();
            recentToursTable.getItems().setAll(tours);

            totalToursValue.setText(String.valueOf(tourService.countTours(tours)));
            totalToursChange.setText("");

            revenueValue.setText("€" + String.format("%,.0f", tourService.calculateTotalRevenue(tours)));
            revenueChange.setText("");

            guidesValue.setText(String.valueOf(guideService.countGuides(guideService.getAllGuides())));

            var vehicles = vehicleService.getAllVehicles();
            vehiclesValue.setText(String.valueOf(vehicles.size()));
            vehiclesStatus.setText(vehicleService.countAvailable(vehicles) + " available");
        } catch (Exception e) {
            e.printStackTrace();
            totalToursValue.setText("—");
            revenueValue.setText("—");
            guidesValue.setText("—");
            vehiclesValue.setText("—");
        }
    }

    @FXML
    private void onViewAllTours() {
        // Tour Management'a yönlendir
    }
}