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

        loadData();
    }

    private void loadData() {
        try {
            List<Tour> tours = apiService.fetchTours();
            recentToursTable.getItems().setAll(tours);
            totalToursValue.setText(String.valueOf(tours.size()));
        } catch (Exception e) {
            // Backend not running — load mock data for UI development
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
            new Tour("Sarajevo City Tour",     "Sarajevo, BiH",    null),
            new Tour("Mostar Heritage Walk",   "Mostar, BiH",      null),
            new Tour("Balkan Mountains Trek",  "Foča, BiH",        null),
            new Tour("Dubrovnik Day Trip",     "Dubrovnik, HR",    null),
            new Tour("Banja Luka Cultural",    "Banja Luka, BiH",  null)
        );
    }

    @FXML
    private void onViewAllTours() {
        // Will navigate to Tour Management when that view is implemented
    }
}
