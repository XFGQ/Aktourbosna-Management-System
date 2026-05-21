package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.service.SessionManager;
import org.example.service.TourService;
import org.example.service.VehicleService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GuideDashboardController {

    @FXML private Label pageTitle;
    @FXML private Label toursThisWeekValue;
    @FXML private Label customersValue;
    @FXML private Label completedToursValue;
    @FXML private Label nextTourValue;

    @FXML private TableView<Tour> todayTourTable;
    @FXML private TableColumn<Tour, String> colTodayDate;
    @FXML private TableColumn<Tour, String> colTodayTourName;
    @FXML private TableColumn<Tour, String> colTodayDestination;
    @FXML private TableColumn<Tour, String> colTodayHotel;
    @FXML private TableColumn<Tour, String> colTodayGroupSize;
    @FXML private TableColumn<Tour, String> colTodayVehicle;

    @FXML private TableView<Tour> upcomingToursTable;
    @FXML private TableColumn<Tour, String> colDate;
    @FXML private TableColumn<Tour, String> colTourName;
    @FXML private TableColumn<Tour, String> colDestination;
    @FXML private TableColumn<Tour, String> colHotel;
    @FXML private TableColumn<Tour, String> colGroupSize;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TourService tourService = new TourService();
    private final VehicleService vehicleService = new VehicleService();

    private final Map<Long, String> vehicleNames = new HashMap<>();

    @FXML
    public void initialize() {
        if (pageTitle != null) {
            String username = SessionManager.getInstance().getUsername();
            pageTitle.setText("Welcome back, " + (username != null ? username : "Guide"));
        }
        colTourName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTourName()));
        colDestination.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
        colHotel.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
        colDate.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getStartDate() != null ? cd.getValue().getStartDate().toString() : "—"));
        colGroupSize.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCustomerCount() > 0 ? String.valueOf(cd.getValue().getCustomerCount()) : "—"));

        if (todayTourTable != null) {
            colTodayTourName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTourName()));
            colTodayDestination.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getRouteName() != null ? cd.getValue().getRouteName()
                            : (cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—")));
            colTodayHotel.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
            colTodayDate.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getStartDate() != null ? cd.getValue().getStartDate().format(DATE_FMT) : "—"));
            colTodayGroupSize.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getCustomerCount() > 0 ? cd.getValue().getCustomerCount() + " people" : "—"));
            colTodayVehicle.setCellValueFactory(cd -> {
                String plate = cd.getValue().getVehiclePlate();
                String vName = vehicleNames.getOrDefault(cd.getValue().getVehicleId(), null);
                if (vName != null && plate != null && !plate.isEmpty()) return new SimpleStringProperty(vName + " · " + plate);
                else if (plate != null && !plate.isEmpty()) return new SimpleStringProperty(plate);
                else return new SimpleStringProperty(vName != null ? vName : "—");
            });
        }
        loadData();
    }

    @FXML
    public void refresh() {
        loadData();
    }

    private void loadData() {
        CompletableFuture<List<Tour>>    toursFut    = CompletableFuture.supplyAsync(() -> {
            try { return tourService.getAllTours(); }
            catch (Exception e) { throw new CompletionException(e); }
        });
        CompletableFuture<List<Vehicle>> vehiclesFut = CompletableFuture.supplyAsync(() -> {
            try { return vehicleService.getAllVehicles(); }
            catch (Exception e) { throw new CompletionException(e); }
        });

        CompletableFuture.allOf(toursFut, vehiclesFut).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    toursThisWeekValue.setText("—"); customersValue.setText("—");
                    completedToursValue.setText("—"); nextTourValue.setText("—");
                });
                return;
            }
            try {
                List<Tour>    allTours = toursFut.join();
                List<Vehicle> vehicles = vehiclesFut.join();

                Long myGuideId = SessionManager.getInstance().getGuideId();
                List<Tour> tours = (SessionManager.getInstance().isGuide() && myGuideId != null)
                        ? allTours.stream().filter(t -> myGuideId.equals(t.getGuideId())).collect(java.util.stream.Collectors.toList())
                        : allTours;

                Map<Long, String> vNames = new HashMap<>();
                for (Vehicle v : vehicles)
                    if (v.getId() != null) vNames.put(v.getId(), v.getBrand() + " " + v.getModel());

                List<Tour> upcoming = tourService.getUpcomingTours(tours);
                List<Tour> recent   = tourService.getRecentTours(tours);

                javafx.application.Platform.runLater(() -> {
                    vehicleNames.clear(); vehicleNames.putAll(vNames);
                    upcomingToursTable.getItems().setAll(upcoming);
                    toursThisWeekValue.setText(String.valueOf(upcoming.size()));
                    customersValue.setText(String.valueOf(tours.stream().mapToInt(Tour::getCustomerCount).sum()));
                    completedToursValue.setText(String.valueOf(recent.size()));
                    nextTourValue.setText(upcoming.isEmpty() ? "—" : upcoming.get(0).getTourName());

                    if (!upcoming.isEmpty()) {
                        todayTourTable.getItems().setAll(upcoming.get(0));
                    } else {
                        todayTourTable.getItems().clear();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    toursThisWeekValue.setText("—"); customersValue.setText("—");
                    completedToursValue.setText("—"); nextTourValue.setText("—");
                });
            }
        });
    }
}
