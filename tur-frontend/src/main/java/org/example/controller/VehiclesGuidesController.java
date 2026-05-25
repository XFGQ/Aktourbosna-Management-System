package org.example.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.Guide;
import org.example.model.Vehicle;
import org.example.service.GuideService;
import org.example.service.VehicleService;
import org.example.service.SessionManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class VehiclesGuidesController {

    @FXML private VBox guidesStatCard;
    @FXML private VBox guidesPanel;
    @FXML private Button addVehicleBtn;

    @FXML private Label pageTitle;
    @FXML private Label pageSubtitle;

    @FXML private Label totalVehiclesValue;
    @FXML private Label vehiclesAvailable;
    @FXML private Label totalGuidesValue;

    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableColumn<Vehicle, Long> colVehId;
    @FXML private TableColumn<Vehicle, String> colVehBrand;
    @FXML private TableColumn<Vehicle, String> colVehModel;
    @FXML private TableColumn<Vehicle, Integer> colVehYear;
    @FXML private TableColumn<Vehicle, String> colVehColor;
    @FXML private TableColumn<Vehicle, String> colVehPlate;
    @FXML private TableColumn<Vehicle, Integer> colVehSeats;
    @FXML private TableColumn<Vehicle, String> colVehFuel;
    @FXML private TableColumn<Vehicle, String> colVehPrice;
    @FXML private TableColumn<Vehicle, String> colVehStatus;
    @FXML private TableColumn<Vehicle, Vehicle> colVehActions;

    @FXML private TableView<Guide> guidesTable;
    @FXML private TableColumn<Guide, String> colGuideName;
    @FXML private TableColumn<Guide, String> colGuideEmail;
    @FXML private TableColumn<Guide, String> colGuidePhone;
    @FXML private TableColumn<Guide, String> colGuideCity;
    @FXML private TableColumn<Guide, String> colGuideLicense;
    @FXML private TableColumn<Guide, Integer> colGuideExp;
    @FXML private TableColumn<Guide, String> colGuideFee;
    @FXML private TableColumn<Guide, Guide>  colGuideActions;

    private final VehicleService vehicleService = new VehicleService();
    private final GuideService guideService = new GuideService();

    @FXML
    public void initialize() {
        boolean isGuide = SessionManager.getInstance().isGuide();

        if (isGuide) {
            if (pageTitle != null) pageTitle.setText("Vehicles");
            if (pageSubtitle != null) pageSubtitle.setText("Overview of all vehicles");

            if (addVehicleBtn != null) {
                addVehicleBtn.setVisible(false);
                addVehicleBtn.setManaged(false);
            }
            colVehActions.setVisible(false);

            if (guidesPanel != null) {
                guidesPanel.setVisible(false);
                guidesPanel.setManaged(false);
            }
            if (guidesStatCard != null) {
                guidesStatCard.setVisible(false);
                guidesStatCard.setManaged(false);
            }
        }

        colVehId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVehBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colVehModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colVehYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colVehColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colVehPlate.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        colVehSeats.setCellValueFactory(new PropertyValueFactory<>("seatCapacity"));
        colVehFuel.setCellValueFactory(new PropertyValueFactory<>("fuelType"));

        colVehPrice.setCellValueFactory(cd -> {
            Double price = cd.getValue().getDailyRentalFee();
            return new SimpleStringProperty(price != null ? "€" + String.format("%,.0f", price) : "—");
        });

        colVehStatus.setCellValueFactory(cd ->
                new SimpleStringProperty(Boolean.TRUE.equals(cd.getValue().getAvailable()) ? "Available" : "Not Available"));

        if (!isGuide) {
            colVehActions.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
            colVehActions.setCellFactory(col -> new TableCell<>() {
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");
                private final HBox box = new HBox(6, editBtn, deleteBtn);
                {
                    box.setAlignment(Pos.CENTER);
                    editBtn.getStyleClass().add("btn-secondary");
                    deleteBtn.getStyleClass().add("btn-danger");
                    editBtn.setOnAction(e -> {
                        Vehicle v = getTableView().getItems().get(getIndex());
                        onEditVehicle(v);
                    });
                    deleteBtn.setOnAction(e -> {
                        Vehicle v = getTableView().getItems().get(getIndex());
                        onDeleteVehicle(v);
                    });
                }
                @Override
                protected void updateItem(Vehicle item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty || item == null ? null : box);
                }
            });

            colGuideName.setCellValueFactory(cd ->
                    new SimpleStringProperty(guideService.getDisplayName(cd.getValue())));
            colGuideEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colGuidePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            colGuideCity.setCellValueFactory(new PropertyValueFactory<>("baseCity"));
            colGuideLicense.setCellValueFactory(new PropertyValueFactory<>("licenseNo"));
            colGuideExp.setCellValueFactory(new PropertyValueFactory<>("experience"));
            colGuideFee.setCellValueFactory(cd -> {
                Double fee = cd.getValue().getDailyFee();
                return new SimpleStringProperty(fee != null ? "€" + String.format("%,.0f", fee) : "—");
            });

            colGuideActions.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
            colGuideActions.setCellFactory(col -> new TableCell<>() {
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");
                private final HBox box = new HBox(6, editBtn, deleteBtn);
                {
                    box.setAlignment(Pos.CENTER);
                    editBtn.getStyleClass().add("btn-secondary");
                    deleteBtn.getStyleClass().add("btn-danger");
                    editBtn.setOnAction(e -> onEditGuide(getTableView().getItems().get(getIndex())));
                    deleteBtn.setOnAction(e -> onDeleteGuide(getTableView().getItems().get(getIndex())));
                }
                @Override
                protected void updateItem(Guide item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty || item == null ? null : box);
                }
            });
        }

        setupVehicleContextMenu(vehiclesTable, isGuide);
        setupGuideContextMenu(guidesTable, isGuide);

        loadData();
    }

    @FXML
    public void refresh() {
        loadData();
    }

    private void setupVehicleContextMenu(TableView<Vehicle> table, boolean isGuide) {
        if (isGuide) return;
        table.setRowFactory(tv -> {
            TableRow<Vehicle> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem editBtn = new MenuItem("Edit");
            editBtn.setOnAction(e -> onEditVehicle(row.getItem()));
            MenuItem deleteBtn = new MenuItem("Delete");
            deleteBtn.setOnAction(e -> onDeleteVehicle(row.getItem()));
            menu.getItems().addAll(editBtn, deleteBtn);
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );
            return row;
        });
    }

    private void setupGuideContextMenu(TableView<Guide> table, boolean isGuide) {
        if (isGuide) return;
        table.setRowFactory(tv -> {
            TableRow<Guide> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem editBtn = new MenuItem("Edit");
            editBtn.setOnAction(e -> onEditGuide(row.getItem()));
            MenuItem deleteBtn = new MenuItem("Delete");
            deleteBtn.setOnAction(e -> onDeleteGuide(row.getItem()));
            menu.getItems().addAll(editBtn, deleteBtn);
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );
            return row;
        });
    }

    private void loadData() {
        boolean isGuide = SessionManager.getInstance().isGuide();

        CompletableFuture<List<Vehicle>> vehiclesFut = CompletableFuture.supplyAsync(() -> {
            try { return vehicleService.getAllVehicles(); }
            catch (Exception e) { throw new CompletionException(e); }
        });

        CompletableFuture<List<Guide>> guidesFut = isGuide ?
                CompletableFuture.completedFuture(null) :
                CompletableFuture.supplyAsync(() -> {
                    try { return guideService.getAllGuides(); }
                    catch (Exception e) { throw new CompletionException(e); }
                });

        CompletableFuture.allOf(vehiclesFut, guidesFut).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    totalVehiclesValue.setText("—");
                    vehiclesAvailable.setText("");
                    if (!isGuide) totalGuidesValue.setText("—");
                });
                return;
            }
            try {
                List<Vehicle> vehicles = vehiclesFut.join();
                List<Guide>   guides   = guidesFut.join();
                Platform.runLater(() -> {
                    vehiclesTable.getItems().setAll(vehicles);
                    totalVehiclesValue.setText(String.valueOf(vehicles.size()));
                    vehiclesAvailable.setText(vehicleService.countAvailable(vehicles) + " available");
                    if (!isGuide && guides != null) {
                        guidesTable.getItems().setAll(guides);
                        totalGuidesValue.setText(String.valueOf(guides.size()));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    totalVehiclesValue.setText("—");
                    vehiclesAvailable.setText("");
                    if (!isGuide) totalGuidesValue.setText("—");
                });
            }
        });
    }

    @FXML
    private void onAddVehicle() {
        Vehicle newVehicle = AddVehicleDialog.show();
        if (newVehicle == null) return;
        runInBackground(
                () -> vehicleService.addVehicle(newVehicle),
                "Adding vehicle...",
                "Vehicle added successfully.",
                "Failed to add vehicle"
        );
    }

    private void onEditVehicle(Vehicle vehicle) {
        Vehicle updated = AddVehicleDialog.show(vehicle);
        if (updated == null) return;
        runInBackground(
                () -> vehicleService.updateVehicle(vehicle.getId(), updated),
                "Updating vehicle...",
                "Vehicle updated successfully.",
                "Failed to update vehicle"
        );
    }

    private void onDeleteVehicle(Vehicle vehicle) {
        if (!ConfirmDialog.show("Confirm deletion",
                "Delete vehicle \"" + vehicle.getBrand() + " " + vehicle.getModel()
                        + " (" + vehicle.getPlateNumber() + ")\"?")) return;

        runInBackground(
                () -> vehicleService.deleteVehicle(vehicle.getId()),
                "Deleting vehicle...",
                "Vehicle deleted.",
                "Failed to delete vehicle"
        );
    }

    @FXML
    private void onAddGuide() {
        Guide newGuide = AddGuideDialog.show();
        if (newGuide == null) return;
        runInBackground(
                () -> guideService.addGuide(newGuide),
                "Adding guide...",
                "Guide added successfully.",
                "Failed to add guide"
        );
    }

    private void onEditGuide(Guide guide) {
        Guide updated = AddGuideDialog.show(guide);
        if (updated == null) return;
        runInBackground(
                () -> guideService.updateGuide(guide.getId(), updated),
                "Updating guide...",
                "Guide updated successfully.",
                "Failed to update guide"
        );
    }

    private void onDeleteGuide(Guide guide) {
        if (!ConfirmDialog.show("Confirm deletion",
                "Delete guide \"" + guide.getUsername() + "\"?")) return;

        runInBackground(
                () -> guideService.deleteGuide(guide.getId()),
                "Deleting guide...",
                "Guide deleted.",
                "Failed to delete guide"
        );
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

        Task<RefreshedData> task = new Task<>() {
            @Override
            protected RefreshedData call() throws Exception {
                boolean isGuide = SessionManager.getInstance().isGuide();
                op.run();
                CompletableFuture<List<Vehicle>> vFut = CompletableFuture.supplyAsync(() -> {
                    try { return vehicleService.getAllVehicles(); }
                    catch (Exception e) { return null; }
                });
                CompletableFuture<List<Guide>> gFut = isGuide ?
                        CompletableFuture.completedFuture(null) :
                        CompletableFuture.supplyAsync(() -> {
                            try { return guideService.getAllGuides(); }
                            catch (Exception e) { return null; }
                        });
                CompletableFuture.allOf(vFut, gFut).join();
                RefreshedData data = new RefreshedData();
                data.vehicles = vFut.join();
                data.guides   = gFut.join();
                return data;
            }
        };

        task.setOnSucceeded(e -> {
            boolean isGuide = SessionManager.getInstance().isGuide();
            RefreshedData data = task.getValue();
            if (data.vehicles != null) {
                vehiclesTable.getItems().setAll(data.vehicles);
                totalVehiclesValue.setText(String.valueOf(data.vehicles.size()));
                vehiclesAvailable.setText(vehicleService.countAvailable(data.vehicles) + " available");
            }
            if (!isGuide && data.guides != null) {
                guidesTable.getItems().setAll(data.guides);
                totalGuidesValue.setText(String.valueOf(data.guides.size()));
            }
            loadingStage.close();
            AppController app = AppController.getInstance();
            if (app != null) app.invalidateOtherViews("vehiclesGuides.fxml");
            Toast.success(successMsg);
        });

        task.setOnFailed(e -> {
            loadingStage.close();
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
            String errDetail = ex != null && ex.getMessage() != null ? ex.getMessage() : "Unknown error";
            if (errDetail.contains("500") && errorTitle.startsWith("Failed to delete vehicle")) {
                Toast.error("Bu araç bir veya daha fazla tura atanmış olduğu için silinemez. Önce turlardan kaldırın.");
            } else if (errDetail.contains("500") && errorTitle.startsWith("Failed to delete guide")) {
                Toast.error("Bu rehber bir veya daha fazla tura atanmış olduğu için silinemez. Önce turlardan kaldırın.");
            } else {
                Toast.error(errorTitle + ": " + errDetail);
            }
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private static class RefreshedData {
        List<Vehicle> vehicles;
        List<Guide> guides;
    }

    @FunctionalInterface
    private interface BackgroundOp {
        void run() throws Exception;
    }
}