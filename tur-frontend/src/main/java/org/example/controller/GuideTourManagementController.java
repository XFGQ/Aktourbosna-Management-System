package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.Guide;
import org.example.model.Route;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.service.GuideService;
import org.example.service.RouteService;
import org.example.service.SessionManager;
import org.example.service.TourService;
import org.example.service.VehicleService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class GuideTourManagementController {

    @FXML private Label totalEarningsValue;
    @FXML private Label monthlyEarningsValue;
    @FXML private Label completedToursValue;

    @FXML private TableView<Tour> recentToursTable;
    @FXML private TableColumn<Tour, String> colRecentName;
    @FXML private TableColumn<Tour, String> colRecentDate;
    @FXML private TableColumn<Tour, String> colRecentHotel;
    @FXML private TableColumn<Tour, String> colRecentVehicle;
    @FXML private TableColumn<Tour, String> colRecentStatus;

    @FXML private TableView<Tour> upcomingToursTable;
    @FXML private TableColumn<Tour, String> colUpcomingName;
    @FXML private TableColumn<Tour, String> colUpcomingDate;
    @FXML private TableColumn<Tour, String> colUpcomingHotel;
    @FXML private TableColumn<Tour, String> colUpcomingVehicle;
    @FXML private TableColumn<Tour, String> colUpcomingStatus;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TourService    tourService    = new TourService();
    private final VehicleService vehicleService = new VehicleService();
    private final GuideService   guideService   = new GuideService();
    private final RouteService   routeService   = new RouteService();

    private final Map<Long, String> vehicleNames = new HashMap<>();

    private List<Guide>   cachedGuides   = new ArrayList<>();
    private List<Vehicle> cachedVehicles = new ArrayList<>();
    private List<Tour>    cachedTours    = new ArrayList<>();
    private List<Route>   cachedRoutes   = new ArrayList<>();

    @FXML
    public void initialize() {
        bindColumns(colRecentName, colRecentDate, colRecentHotel, colRecentVehicle, colRecentStatus);
        bindColumns(colUpcomingName, colUpcomingDate, colUpcomingHotel, colUpcomingVehicle, colUpcomingStatus);
        loadData();
    }

    @FXML
    public void refresh() { loadData(); }

    private void bindColumns(TableColumn<Tour, String> name, TableColumn<Tour, String> date,
                             TableColumn<Tour, String> hotel, TableColumn<Tour, String> vehicle,
                             TableColumn<Tour, String> status) {
        name.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTourName()));
        name.setCellFactory(col -> tooltipCell());

        date.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getStartDate() != null ? cd.getValue().getStartDate().format(DATE_FMT) : "—"));
        date.setCellFactory(col -> tooltipCell());

        hotel.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
        hotel.setCellFactory(col -> tooltipCell());

        vehicle.setCellValueFactory(cd -> {
            String vPlate = cd.getValue().getVehiclePlate();
            String vName  = vehicleNames.getOrDefault(cd.getValue().getVehicleId(), null);
            if (vName != null && vPlate != null && !vPlate.isEmpty())
                return new SimpleStringProperty(vName + " · " + vPlate);
            if (vPlate != null && !vPlate.isEmpty()) return new SimpleStringProperty(vPlate);
            if (vName != null) return new SimpleStringProperty(vName);
            return new SimpleStringProperty("—");
        });
        vehicle.setCellFactory(col -> tooltipCell());

        status.setCellValueFactory(cd -> new SimpleStringProperty(tourService.deriveStatus(cd.getValue())));
        status.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
                Label badge = new Label(s);
                badge.getStyleClass().add(switch (s) {
                    case "Active"    -> "badge-active";
                    case "Upcoming"  -> "badge-upcoming";
                    case "Completed" -> "badge-completed";
                    default          -> "badge-default";
                });
                setGraphic(badge);
                setText(null);
            }
        });
    }

    private void loadData() {
        CompletableFuture<List<Tour>> toursFut = CompletableFuture.supplyAsync(() -> {
            try {
                List<Tour> allTours = tourService.getAllTours();
                Long myGuideId = SessionManager.getInstance().getGuideId();
                if (SessionManager.getInstance().isGuide() && myGuideId != null) {
                    return allTours.stream()
                            .filter(t -> myGuideId.equals(t.getGuideId()))
                            .collect(Collectors.toList());
                }
                return allTours;
            }
            catch (Exception e) { throw new CompletionException(e); }
        });

        CompletableFuture<List<Vehicle>> vehiclesFut = CompletableFuture.supplyAsync(() -> {
            try { return vehicleService.getAllVehicles(); }
            catch (Exception e) { throw new CompletionException(e); }
        });

        CompletableFuture<List<Guide>> guidesFut = CompletableFuture.supplyAsync(() -> {
            try { return guideService.getAllGuides(); }
            catch (Exception e) { throw new CompletionException(e); }
        });

        CompletableFuture<List<Route>> routesFut = CompletableFuture.supplyAsync(() -> {
            try { return routeService.getAllRoutes(); }
            catch (Exception e) { throw new CompletionException(e); }
        });

        CompletableFuture.allOf(toursFut, vehiclesFut, guidesFut, routesFut).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalEarningsValue.setText("—");
                    monthlyEarningsValue.setText("—");
                    completedToursValue.setText("—");
                });
                return;
            }
            try {
                List<Tour>    tours    = toursFut.join();
                List<Vehicle> vehicles = vehiclesFut.join();
                List<Guide>   guides   = guidesFut.join();
                List<Route>   routes   = routesFut.join();

                Map<Long, String> vNames = new HashMap<>();
                for (Vehicle v : vehicles)
                    if (v.getId() != null) vNames.put(v.getId(), v.getBrand() + " " + v.getModel());

                List<Tour> recent   = tourService.getRecentTours(tours);
                List<Tour> upcoming = tourService.getUpcomingTours(tours);
                double totalEarnings   = tourService.calculateTotalRevenue(tours);
                double monthlyEarnings = tourService.calculateTotalRevenue(recent);

                javafx.application.Platform.runLater(() -> {
                    vehicleNames.clear(); vehicleNames.putAll(vNames);
                    cachedVehicles = vehicles;
                    cachedGuides   = guides;
                    cachedTours    = tours;
                    cachedRoutes   = routes;
                    recentToursTable.getItems().setAll(recent);
                    upcomingToursTable.getItems().setAll(upcoming);
                    totalEarningsValue.setText("€" + String.format("%,.0f", totalEarnings));
                    monthlyEarningsValue.setText("€" + String.format("%,.0f", monthlyEarnings));
                    completedToursValue.setText(String.valueOf(recent.size()));
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalEarningsValue.setText("—");
                    monthlyEarningsValue.setText("—");
                    completedToursValue.setText("—");
                });
            }
        });
    }

    @FXML
    private void onAddTour() {
        if (cachedGuides.isEmpty()) {
            Toast.info("Data is still loading, please wait a moment.");
            loadData();
            return;
        }
        Tour newTour = AddTourDialog.show(cachedGuides, cachedVehicles, cachedTours, cachedRoutes);
        if (newTour == null) return;
        Long myGuideId = SessionManager.getInstance().getGuideId();
        if (myGuideId != null) newTour.setGuideId(myGuideId);
        runInBackground(
                () -> tourService.addTour(newTour),
                "Adding tour...", "Tour added successfully.", "Failed to add tour");
    }

    private void runInBackground(BackgroundOp op, String loadingMsg, String successMsg, String errorTitle) {
        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.initStyle(StageStyle.UNDECORATED);
        loadingStage.setResizable(false);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(40, 40);
        Label msg = new Label(loadingMsg);
        msg.setStyle("-fx-font-size: 14px;");
        VBox box = new VBox(12, spinner, msg);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        box.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; -fx-border-width: 1px;");
        loadingStage.setScene(new javafx.scene.Scene(box));
        loadingStage.show();

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception { op.run(); return null; }
        };
        task.setOnSucceeded(e -> {
            loadingStage.close();
            loadData();
            AppController app = AppController.getInstance();
            if (app != null) app.invalidateOtherViews("guideTourManagement.fxml");
            Toast.success(successMsg);
        });
        task.setOnFailed(e -> {
            loadingStage.close();
            Throwable ex = task.getException();
            String detail = ex != null && ex.getMessage() != null ? ex.getMessage() : "Unknown error";
            Toast.error(errorTitle + ": " + detail);
        });
        new Thread(task).start();
    }

    private static TableCell<Tour, String> tooltipCell() {
        return new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setTooltip(null); }
                else { setText(s); setTooltip(new Tooltip(s)); }
            }
        };
    }

    @FunctionalInterface
    private interface BackgroundOp { void run() throws Exception; }
}
