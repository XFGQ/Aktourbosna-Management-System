package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

        colGuide.setCellValueFactory(cd ->
                new SimpleStringProperty(tourService.getGuideName(cd.getValue())));

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
                double revenue = tourService.calculateTotalRevenue(tours);
                long guides = guideService.countGuides(guideService.getAllGuides());
                var vehicles = vehicleService.getAllVehicles();
                long available = vehicleService.countAvailable(vehicles);
                javafx.application.Platform.runLater(() -> {
                    recentToursTable.getItems().setAll(tours);
                    totalToursValue.setText(String.valueOf(tours.size()));
                    totalToursChange.setText("");
                    revenueValue.setText("€" + String.format("%,.0f", revenue));
                    revenueChange.setText("");
                    guidesValue.setText(String.valueOf(guides));
                    vehiclesValue.setText(String.valueOf(vehicles.size()));
                    vehiclesStatus.setText(available + " available");
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalToursValue.setText("—");
                    revenueValue.setText("—");
                    guidesValue.setText("—");
                    vehiclesValue.setText("—");
                    vehiclesStatus.setText("");
                });
            }
        }).start();
    }

    @FXML
    private void onViewAllTours() {
        if (recentToursTable.getScene() != null) {
            Node btn = recentToursTable.getScene().lookup("#btnTourManagement");
            if (btn instanceof Button) {
                ((Button) btn).fire();
            }
        }
    }
}