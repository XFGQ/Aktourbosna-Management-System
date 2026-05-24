package org.example.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.model.Route;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.service.RouteService;
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
    @FXML private Label earningsValue;
    @FXML private Label expensesValue;
    @FXML private Label nextTourValue;

    @FXML private TableView<Tour> todayTourTable;
    @FXML private TableColumn<Tour, String> colTodayDate;
    @FXML private TableColumn<Tour, String> colTodayTourName;
    @FXML private TableColumn<Tour, String> colTodayDestination;
    @FXML private TableColumn<Tour, String> colTodayHotel;
    @FXML private TableColumn<Tour, String> colTodayOrigin;
    @FXML private TableColumn<Tour, Number> colTodayPrice;
    @FXML private TableColumn<Tour, String> colTodayVehicle;

    @FXML private TableView<Tour> upcomingToursTable;
    @FXML private TableColumn<Tour, String> colDate;
    @FXML private TableColumn<Tour, String> colTourName;
    @FXML private TableColumn<Tour, String> colDestination;
    @FXML private TableColumn<Tour, String> colHotel;
    @FXML private TableColumn<Tour, String> colOrigin;
    @FXML private TableColumn<Tour, Number> colPrice;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TourService tourService = new TourService();
    private final VehicleService vehicleService = new VehicleService();
    private final RouteService routeService = new RouteService();

    private final Map<Long, String> vehicleNames = new HashMap<>();
    private final Map<Long, String> routeStartCities = new HashMap<>();

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
        colOrigin.setCellValueFactory(cd -> {
            String origin = routeStartCities.get(cd.getValue().getRouteId());
            return new SimpleStringProperty(origin != null ? origin : "—");
        });
        colPrice.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getFinalPrice()));
        colPrice.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number val, boolean empty) {
                super.updateItem(val, empty);
                if (empty) setText(null);
                else if (val == null || val.doubleValue() == 0) setText("—");
                else setText("€" + String.format("%,.0f", val.doubleValue()));
            }
        });

        if (todayTourTable != null) {
            colTodayTourName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTourName()));
            colTodayDestination.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getRouteName() != null ? cd.getValue().getRouteName()
                            : (cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—")));
            colTodayHotel.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
            colTodayDate.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getStartDate() != null ? cd.getValue().getStartDate().format(DATE_FMT) : "—"));
            colTodayOrigin.setCellValueFactory(cd -> {
                String origin = routeStartCities.get(cd.getValue().getRouteId());
                return new SimpleStringProperty(origin != null ? origin : "—");
            });
            colTodayPrice.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getFinalPrice()));
            colTodayPrice.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Number val, boolean empty) {
                    super.updateItem(val, empty);
                    if (empty) setText(null);
                    else if (val == null || val.doubleValue() == 0) setText("—");
                    else setText("€" + String.format("%,.0f", val.doubleValue()));
                }
            });
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
        CompletableFuture<List<Route>> routesFut = CompletableFuture.supplyAsync(() -> {
            try { return routeService.getAllRoutes(); }
            catch (Exception e) { throw new CompletionException(e); }
        });

        CompletableFuture.allOf(toursFut, vehiclesFut, routesFut).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    toursThisWeekValue.setText("—"); earningsValue.setText("—");
                    expensesValue.setText("—"); nextTourValue.setText("—");
                });
                return;
            }
            try {
                List<Tour>    allTours = toursFut.join();
                List<Vehicle> vehicles = vehiclesFut.join();
                List<Route>   routes   = routesFut.join();

                Long myGuideId = SessionManager.getInstance().getGuideId();
                List<Tour> tours = (SessionManager.getInstance().isGuide() && myGuideId != null)
                        ? allTours.stream().filter(t -> myGuideId.equals(t.getGuideId())).collect(java.util.stream.Collectors.toList())
                        : allTours;

                Map<Long, String> vNames = new HashMap<>();
                for (Vehicle v : vehicles)
                    if (v.getId() != null) vNames.put(v.getId(), v.getBrand() + " " + v.getModel());

                Map<Long, String> rCities = new HashMap<>();
                for (Route r : routes)
                    if (r.getRouteId() != null) rCities.put(r.getRouteId(), r.getStartCity());

                List<Tour> upcoming = tourService.getUpcomingTours(tours);
                List<Tour> recent   = tourService.getRecentTours(tours);

                double monthlyEarnings = tourService.calculateTotalRevenue(recent);
                double totalExpense = tours.stream()
                        .mapToDouble(t -> t.getTotalExpense() != null ? t.getTotalExpense() : 0.0)
                        .sum();

                javafx.application.Platform.runLater(() -> {
                    vehicleNames.clear(); vehicleNames.putAll(vNames);
                    routeStartCities.clear(); routeStartCities.putAll(rCities);
                    upcomingToursTable.getItems().setAll(upcoming);
                    toursThisWeekValue.setText(String.valueOf(upcoming.size()));
                    earningsValue.setText("€" + String.format("%,.0f", monthlyEarnings));
                    expensesValue.setText("€" + String.format("%,.0f", totalExpense));
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
                    toursThisWeekValue.setText("—"); earningsValue.setText("—");
                    expensesValue.setText("—"); nextTourValue.setText("—");
                });
            }
        });
    }
}
