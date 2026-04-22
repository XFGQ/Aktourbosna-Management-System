package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Tour;

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

    @FXML
    public void initialize() {
        colRecentName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colRecentDeparture.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        colRecentDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colRecentDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colRecentHotel.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        colRecentVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicle"));
        colRecentGroup.setCellValueFactory(new PropertyValueFactory<>("groupSize"));
        colRecentStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colUpcomingName.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        colUpcomingDeparture.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        colUpcomingDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colUpcomingDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colUpcomingHotel.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        colUpcomingVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicle"));
        colUpcomingGroup.setCellValueFactory(new PropertyValueFactory<>("groupSize"));
        colUpcomingStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadMockData();
    }

    private Tour tour(String name, String departure, String dest, String date, String hotel, String vehicle, String group, String status) {
        Tour t = new Tour(name, dest, date, hotel, group, status);
        t.setDepartureCity(departure);
        t.setVehicle(vehicle);
        return t;
    }

    private void loadMockData() {
        totalEarningsValue.setText("€2,060");
        monthlyEarningsValue.setText("€870");
        completedToursValue.setText("2");

        recentToursTable.getItems().setAll(
            tour("Banja Luka Cultural", "Sarajevo Airport", "Banja Luka, BiH", "2025-04-20", "Hotel Jelena",  "Sprinter 2022 · BA-201-K", "9 guests",  "Completed"),
            tour("Trebinje Wine Tour",  "Mostar Airport",   "Trebinje, BiH",   "2025-04-15", "Hotel Platani", "Transit 2023 · BA-112-A",  "6 guests",  "Completed"),
            tour("Sarajevo City Tour",  "Sarajevo Airport", "Sarajevo, BiH",   "2025-05-02", "Hotel Europe",  "Sprinter 2021 · BA-445-J", "12 guests", "Active")
        );

        upcomingToursTable.getItems().setAll(
            tour("Mostar Heritage Walk",  "Sarajevo Airport", "Mostar, BiH",   "2025-05-05", "Hotel Mepas",           "Sprinter 2022 · BA-338-L", "8 guests",  "Upcoming"),
            tour("Balkan Mountains Trek", "Sarajevo Airport", "Foča, BiH",     "2025-05-10", "Hotel Zelengora",        "Transit 2023 · BA-112-A",  "15 guests", "Upcoming"),
            tour("Dubrovnik Day Trip",    "Mostar Airport",   "Dubrovnik, HR", "2025-05-14", "Hotel Rixos",           "Sprinter 2022 · BA-201-K", "10 guests", "Upcoming"),
            tour("Tara Canyon Hike",     "Sarajevo Airport", "Tara, MNE",     "2025-05-18", "Hotel Tara",            "Sprinter 2021 · BA-445-J", "15 guests", "Upcoming"),
            tour("Belgrade Day Tour",    "Sarajevo Airport", "Belgrade, SRB", "2025-05-22", "Hotel Metropol Palace", "Sprinter 2022 · BA-338-L", "11 guests", "Upcoming")
        );
    }

    @FXML
    private void onAddTour() {
        // Will open Add Tour dialog when implemented
    }
}
