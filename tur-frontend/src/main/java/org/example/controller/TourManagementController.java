package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Tour;

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
    @FXML private TableColumn<Tour, Double> colRecentPrice;
    @FXML private TableColumn<Tour, String> colRecentStatus;

    @FXML private TableView<Tour> upcomingToursTable;
    @FXML private TableColumn<Tour, String> colUpcomingName;
    @FXML private TableColumn<Tour, String> colUpcomingDeparture;
    @FXML private TableColumn<Tour, String> colUpcomingDestination;
    @FXML private TableColumn<Tour, String> colUpcomingDate;
    @FXML private TableColumn<Tour, String> colUpcomingHotel;
    @FXML private TableColumn<Tour, String> colUpcomingVehicle;
    @FXML private TableColumn<Tour, String> colUpcomingGuide;
    @FXML private TableColumn<Tour, Double> colUpcomingPrice;
    @FXML private TableColumn<Tour, String> colUpcomingStatus;

    @FXML
    public void initialize() {
        colRecentName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colRecentDeparture.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        colRecentDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colRecentDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colRecentHotel.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        colRecentVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicle"));
        colRecentGuide.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        colRecentPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colRecentStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colUpcomingName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colUpcomingDeparture.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        colUpcomingDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colUpcomingDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colUpcomingHotel.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        colUpcomingVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicle"));
        colUpcomingGuide.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        colUpcomingPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colUpcomingStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadMockData();
    }

    private Tour tour(String name, String departure, String dest, String date, String hotel, String vehicle, String guide, Double price, String status) {
        Tour t = new Tour(name, dest, date, hotel, price, status);
        t.setDepartureCity(departure);
        t.setVehicle(vehicle);
        t.setGuideName(guide);
        return t;
    }

    private void loadMockData() {
        totalToursValue.setText("8");
        revenueValue.setText("€10,110");
        expensesValue.setText("€4,250");
        netProfitValue.setText("€5,860");

        recentToursTable.getItems().setAll(
            tour("Banja Luka Cultural", "Sarajevo Airport", "Banja Luka, BiH", "2025-04-20", "Hotel Jelena",  "Sprinter 2022 · BA-201-K", "Amir Hodžić",   870.0,  "Completed"),
            tour("Trebinje Wine Tour",  "Mostar Airport",   "Trebinje, BiH",   "2025-04-15", "Hotel Platani", "Transit 2023 · BA-112-A",  "Selma Kovač",   990.0,  "Completed"),
            tour("Sarajevo City Tour",  "Sarajevo Airport", "Sarajevo, BiH",   "2025-05-02", "Hotel Europe",  "Sprinter 2021 · BA-445-J", "Marko Petrović",1200.0, "Active")
        );

        upcomingToursTable.getItems().setAll(
            tour("Mostar Heritage Walk",  "Sarajevo Airport", "Mostar, BiH",   "2025-05-05", "Hotel Mepas",           "Sprinter 2022 · BA-338-L", "Amir Hodžić",    950.0,  "Upcoming"),
            tour("Balkan Mountains Trek", "Sarajevo Airport", "Foča, BiH",     "2025-05-10", "Hotel Zelengora",        "Transit 2023 · BA-112-A",  "Selma Kovač",   1500.0, "Upcoming"),
            tour("Dubrovnik Day Trip",    "Mostar Airport",   "Dubrovnik, HR", "2025-05-14", "Hotel Rixos",           "Sprinter 2022 · BA-201-K", "Marko Petrović",1800.0, "Upcoming"),
            tour("Tara Canyon Hike",     "Sarajevo Airport", "Tara, MNE",     "2025-05-18", "Hotel Tara",            "Sprinter 2021 · BA-445-J", "Amir Hodžić",   1650.0, "Upcoming"),
            tour("Belgrade Day Tour",    "Sarajevo Airport", "Belgrade, SRB", "2025-05-22", "Hotel Metropol Palace", "Sprinter 2022 · BA-338-L", "Selma Kovač",   2100.0, "Upcoming")
        );
    }

    @FXML
    private void onAddTour() {
        // Will open Add Tour dialog when implemented
    }
}
