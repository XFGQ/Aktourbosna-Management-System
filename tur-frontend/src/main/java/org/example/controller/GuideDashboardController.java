package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Tour;
import org.example.service.TourService;

import java.util.List;

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

    @FXML
    public void initialize() {
        colTourName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colHotel.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colGroupSize.setCellValueFactory(new PropertyValueFactory<>("groupSize"));

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Tour> tours = tourService.getAllTours();
                javafx.application.Platform.runLater(() -> {
                    upcomingToursTable.getItems().setAll(tours);
                    toursThisWeekValue.setText(String.valueOf(tours.size()));
                    customersValue.setText("—");
                    completedToursValue.setText("—");
                    nextTourValue.setText(tours.isEmpty() ? "—" : tours.get(0).getTourName());
                    if (!tours.isEmpty()) {
                        Tour today = tours.get(0);
                        todayTourName.setText(today.getTourName());
                        todayHotel.setText(today.getHotelName());
                        todayDestination.setText(today.getDestination());
                        todayGroupSize.setText(today.getGroupSize());
                        todayVehicle.setText(tourService.getVehicleDisplayName(today));
                        todayTourTime.setText(today.getStartDate() != null ? today.getStartDate().toString() : "—");
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