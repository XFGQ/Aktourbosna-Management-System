package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Tour;

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

    @FXML
    public void initialize() {
        colTourName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colHotel.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        loadMockData();
    }

    private void loadMockData() {
        toursThisWeekValue.setText("4");
        customersValue.setText("52");
        completedToursValue.setText("18");
        nextTourValue.setText("Tomorrow 09:00");

        todayTourName.setText("Sarajevo City Tour");
        todayTourTime.setText("10:00 – 14:00");
        todayHotel.setText("Hotel Europe");
        todayDestination.setText("Sarajevo, BiH");
        todayGroupSize.setText("12 guests");
        todayVehicle.setText("Minibus — BA 201-K-451");

        upcomingToursTable.getItems().setAll(
            new Tour("Mostar Heritage Walk",  "Mostar, BiH",     null, "Hotel Mepas"),
            new Tour("Balkan Mountains Trek", "Foča, BiH",       null, "Hotel & Spa & Resort & Grad"),
            new Tour("Dubrovnik Day Trip",    "Dubrovnik, HR",   null, "Hotel Rixos"),
            new Tour("Banja Luka Cultural",   "Banja Luka, BiH", null, "Hotel Jelena")
        );
    }
}
