package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Tour;
import org.example.service.ApiService;

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

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        colTourName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colGuide.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadData();
    }

    private void loadData() {
        try {
            List<Tour> tours = apiService.fetchTours();
            recentToursTable.getItems().setAll(tours);
            totalToursValue.setText(String.valueOf(tours.size()));
        } catch (Exception e) {
            loadMockData();
        }
    }

    private void loadMockData() {
        totalToursValue.setText("24");
        totalToursChange.setText("+12% this month");
        revenueValue.setText("€18,450");
        revenueChange.setText("+8% vs last month");
        guidesValue.setText("6");
        vehiclesValue.setText("9");
        vehiclesStatus.setText("In fleet");

        recentToursTable.getItems().setAll(
            makeTour("Sarajevo City Tour",    "Sarajevo, BiH",    "2025-05-02", "Marko Petrović", "Active"),
            makeTour("Mostar Heritage Walk",  "Mostar, BiH",      "2025-05-05", "Amir Hodžić",    "Upcoming"),
            makeTour("Balkan Mountains Trek", "Foča, BiH",        "2025-05-10", "Selma Kovač",    "Upcoming"),
            makeTour("Dubrovnik Day Trip",    "Dubrovnik, HR",    "2025-05-14", "Marko Petrović", "Upcoming"),
            makeTour("Banja Luka Cultural",   "Banja Luka, BiH",  "2025-04-20", "Amir Hodžić",    "Completed")
        );
    }

    private Tour makeTour(String name, String dest, String date, String guide, String status) {
        Tour t = new Tour(name, dest, date);
        t.setGuideName(guide);
        t.setStatus(status);
        return t;
    }

    @FXML
    private void onViewAllTours() {
        // Tour Management ekranına navigate et
    }
}