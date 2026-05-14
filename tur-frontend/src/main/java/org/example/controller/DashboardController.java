package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import org.example.model.Guide;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.service.GuideService;
import org.example.service.TourService;
import org.example.service.VehicleService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final Map<Long, String> guideNames = new HashMap<>();

    @FXML
    public void initialize() {
        colTourName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTourName()));
        colTourName.setCellFactory(col -> tooltipCell());

        colDestination.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
        colDestination.setCellFactory(col -> tooltipCell());

        colDate.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getStartDate() != null ? cd.getValue().getStartDate().format(DATE_FMT) : "—"));
        colDate.setCellFactory(col -> tooltipCell());

        colGuide.setCellValueFactory(cd -> new SimpleStringProperty(
                guideNames.getOrDefault(cd.getValue().getGuideId(), "—")));
        colGuide.setCellFactory(col -> tooltipCell());

        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(tourService.deriveStatus(cd.getValue())));
        colStatus.setCellFactory(col -> new TableCell<>() {
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
                setGraphic(badge); setText(null);
            }
        });

        loadData();
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

    @FXML
    public void refresh() {
        loadData();
    }

    private void loadData() {
        CompletableFuture<List<Tour>>    toursFut    = CompletableFuture.supplyAsync(() -> {
            try { return tourService.getAllTours(); }
            catch (Exception e) { throw new CompletionException(e); }
        });
        CompletableFuture<List<Guide>>   guidesFut   = CompletableFuture.supplyAsync(() -> {
            try { return guideService.getAllGuides(); }
            catch (Exception e) { throw new CompletionException(e); }
        });
        CompletableFuture<List<Vehicle>> vehiclesFut = CompletableFuture.supplyAsync(() -> {
            try { return vehicleService.getAllVehicles(); }
            catch (Exception e) { throw new CompletionException(e); }
        });

        CompletableFuture.allOf(toursFut, guidesFut, vehiclesFut).whenComplete((v, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalToursValue.setText("—"); revenueValue.setText("—");
                    guidesValue.setText("—"); vehiclesValue.setText("—"); vehiclesStatus.setText("");
                });
                return;
            }
            try {
                List<Tour>    tours    = toursFut.join();
                List<Guide>   guides   = guidesFut.join();
                List<Vehicle> vehicles = vehiclesFut.join();

                Map<Long, String> nameMap = new HashMap<>();
                for (Guide g : guides)
                    if (g.getId() != null && g.getFullName() != null) nameMap.put(g.getId(), g.getFullName());

                double revenue = tourService.calculateTotalRevenue(tours);
                long available = vehicleService.countAvailable(vehicles);

                javafx.application.Platform.runLater(() -> {
                    guideNames.clear(); guideNames.putAll(nameMap);
                    recentToursTable.getItems().setAll(tours);
                    totalToursValue.setText(String.valueOf(tours.size()));
                    totalToursChange.setText("");
                    revenueValue.setText("€" + String.format("%,.0f", revenue));
                    revenueChange.setText("");
                    guidesValue.setText(String.valueOf(guides.size()));
                    vehiclesValue.setText(String.valueOf(vehicles.size()));
                    vehiclesStatus.setText(available + " available");
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalToursValue.setText("—"); revenueValue.setText("—");
                    guidesValue.setText("—"); vehiclesValue.setText("—"); vehiclesStatus.setText("");
                });
            }
        });
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
