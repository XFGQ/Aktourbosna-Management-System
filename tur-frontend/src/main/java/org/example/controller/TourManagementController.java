package org.example.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.Expense;
import org.example.model.Guide;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.service.ExpenseService;
import org.example.service.GuideService;
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

public class TourManagementController {

    @FXML private Label totalToursValue;
    @FXML private Label revenueValue;
    @FXML private Label expensesValue;
    @FXML private Label netProfitValue;

    @FXML private TableView<Tour> recentToursTable;
    @FXML private TableColumn<Tour, String>  colRecentName;
    @FXML private TableColumn<Tour, String>  colRecentDate;
    @FXML private TableColumn<Tour, String>  colRecentHotel;
    @FXML private TableColumn<Tour, String>  colRecentVehicle;
    @FXML private TableColumn<Tour, String>  colRecentGuide;
    @FXML private TableColumn<Tour, Number>  colRecentPrice;
    @FXML private TableColumn<Tour, String>  colRecentStatus;
    @FXML private TableColumn<Tour, Tour>    colRecentActions;

    @FXML private TableView<Tour> upcomingToursTable;
    @FXML private TableColumn<Tour, String>  colUpcomingName;
    @FXML private TableColumn<Tour, String>  colUpcomingDate;
    @FXML private TableColumn<Tour, String>  colUpcomingHotel;
    @FXML private TableColumn<Tour, String>  colUpcomingVehicle;
    @FXML private TableColumn<Tour, String>  colUpcomingGuide;
    @FXML private TableColumn<Tour, Number>  colUpcomingPrice;
    @FXML private TableColumn<Tour, String>  colUpcomingStatus;
    @FXML private TableColumn<Tour, Tour>    colUpcomingActions;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TourService    tourService    = new TourService();
    private final GuideService   guideService   = new GuideService();
    private final VehicleService vehicleService = new VehicleService();
    private final ExpenseService expenseService = new ExpenseService();

    private final Map<Long, String> guideNames   = new HashMap<>();
    private final Map<Long, String> vehicleNames = new HashMap<>();
    private List<Guide>   cachedGuides   = new ArrayList<>();
    private List<Vehicle> cachedVehicles = new ArrayList<>();
    private List<Tour>    cachedTours    = new ArrayList<>();

    @FXML
    public void initialize() {
        bindColumns(recentToursTable,
                colRecentName, colRecentDate, colRecentHotel,
                colRecentVehicle, colRecentGuide, colRecentPrice, colRecentStatus, colRecentActions);
        bindColumns(upcomingToursTable,
                colUpcomingName, colUpcomingDate, colUpcomingHotel,
                colUpcomingVehicle, colUpcomingGuide, colUpcomingPrice, colUpcomingStatus, colUpcomingActions);
        loadData();
    }

    @FXML
    public void refresh() { loadData(); }

    private void bindColumns(TableView<Tour> table,
                             TableColumn<Tour, String> name,
                             TableColumn<Tour, String> date,
                             TableColumn<Tour, String> hotel,
                             TableColumn<Tour, String> vehicle,
                             TableColumn<Tour, String> guide,
                             TableColumn<Tour, Number> price,
                             TableColumn<Tour, String> status,
                             TableColumn<Tour, Tour>   actions) {

        name.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTourName()));
        name.setCellFactory(col -> tooltipCell());
        date.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getStartDate() != null ? cd.getValue().getStartDate().format(DATE_FMT) : "—"));
        date.setCellFactory(col -> tooltipCell());
        hotel.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHotelName() != null ? cd.getValue().getHotelName() : "—"));
        hotel.setCellFactory(col -> tooltipCell());
        guide.setCellValueFactory(cd -> new SimpleStringProperty(
                guideNames.getOrDefault(cd.getValue().getGuideId(), "—")));
        guide.setCellFactory(col -> tooltipCell());
        vehicle.setCellValueFactory(cd -> new SimpleStringProperty(
                vehicleNames.getOrDefault(cd.getValue().getVehicleId(), "—")));
        vehicle.setCellFactory(col -> tooltipCell());
        price.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getFinalPrice()));

        // Status badge
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

        // Actions
        actions.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
        actions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox   box       = new HBox(6, editBtn, deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                editBtn.getStyleClass().add("btn-secondary");
                deleteBtn.getStyleClass().add("btn-danger");
                editBtn.setOnAction(e -> onEditTour(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> onDeleteTour(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Tour t, boolean empty) {
                super.updateItem(t, empty);
                setGraphic(empty || t == null ? null : box);
            }
        });
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

        CompletableFuture.allOf(toursFut, guidesFut, vehiclesFut).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalToursValue.setText("—"); revenueValue.setText("—");
                    expensesValue.setText("—");   netProfitValue.setText("—");
                });
                return;
            }
            try {
                List<Tour>    tours    = toursFut.join();
                List<Guide>   guides   = guidesFut.join();
                List<Vehicle> vehicles = vehiclesFut.join();

                List<CompletableFuture<List<Expense>>> expFuts = tours.stream()
                        .filter(t -> t.getTourId() != null)
                        .map(t -> CompletableFuture.supplyAsync(() -> {
                            try { return expenseService.getExpensesByTourId(t.getTourId()); }
                            catch (Exception e) { return List.<Expense>of(); }
                        }))
                        .collect(Collectors.toList());

                List<Expense> allExpenses = CompletableFuture
                        .allOf(expFuts.toArray(new CompletableFuture[0]))
                        .thenApply(_void -> expFuts.stream()
                                .flatMap(f -> f.join().stream())
                                .collect(Collectors.toList()))
                        .join();

                Map<Long, String> gNames = new HashMap<>();
                for (Guide g : guides)
                    if (g.getId() != null && g.getFullName() != null) gNames.put(g.getId(), g.getFullName());
                Map<Long, String> vNames = new HashMap<>();
                for (Vehicle v : vehicles)
                    if (v.getId() != null) vNames.put(v.getId(), v.getBrand() + " " + v.getModel());

                List<Tour> recent   = tourService.getRecentTours(tours);
                List<Tour> upcoming = tourService.getUpcomingTours(tours);
                double revenue      = tourService.calculateTotalRevenue(tours);
                double totalExpense = expenseService.calculateTotal(allExpenses);

                javafx.application.Platform.runLater(() -> {
                    guideNames.clear();   guideNames.putAll(gNames);
                    vehicleNames.clear(); vehicleNames.putAll(vNames);
                    cachedGuides   = guides;
                    cachedVehicles = vehicles;
                    cachedTours    = tours;
                    recentToursTable.getItems().setAll(recent);
                    upcomingToursTable.getItems().setAll(upcoming);
                    totalToursValue.setText(String.valueOf(tours.size()));
                    revenueValue.setText("€" + String.format("%,.0f", revenue));
                    expensesValue.setText("€" + String.format("%,.0f", totalExpense));
                    netProfitValue.setText("€" + String.format("%,.0f", revenue - totalExpense));
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalToursValue.setText("—"); revenueValue.setText("—");
                    expensesValue.setText("—");   netProfitValue.setText("—");
                });
            }
        });
    }

    @FXML
    private void onAddTour() {
        Tour newTour = AddTourDialog.show(cachedGuides, cachedVehicles, cachedTours);
        if (newTour == null) return;
        runInBackground(
                () -> tourService.addTour(newTour),
                "Adding tour...", "Tour added successfully.", "Failed to add tour");
    }

    private void onEditTour(Tour tour) {
        Tour updated = AddTourDialog.show(tour, cachedGuides, cachedVehicles, cachedTours);
        if (updated == null) return;
        runInBackground(
                () -> tourService.updateTour(tour.getTourId(), updated),
                "Updating tour...", "Tour updated successfully.", "Failed to update tour");
    }

    private void onDeleteTour(Tour tour) {
        if (!ConfirmDialog.show("Confirm deletion",
                "Delete tour \"" + tour.getTourName() + "\"?")) return;
        runInBackground(
                () -> tourService.deleteTour(tour.getTourId()),
                "Deleting tour...", "Tour deleted.", "Failed to delete tour");
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
            if (app != null) app.invalidateOtherViews("tourManagement.fxml");
            Toast.success(successMsg);
        });
        task.setOnFailed(e -> {
            loadingStage.close();
            Toast.error(errorTitle + ": " + task.getException().getMessage());
        });
        new Thread(task).start();
    }

    private static TableCell<Tour, String> tooltipCell() {
        return new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setTooltip(null); }
                else { setText(s); setTooltip(new javafx.scene.control.Tooltip(s)); }
            }
        };
    }

    @FunctionalInterface
    private interface BackgroundOp { void run() throws Exception; }
}
