package org.example.controller;

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

import java.util.List;

public class VehiclesGuidesController {

    @FXML private Label totalVehiclesValue;
    @FXML private Label vehiclesAvailable;
    @FXML private Label totalGuidesValue;

    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableColumn<Vehicle, String> colVehBrand;
    @FXML private TableColumn<Vehicle, String> colVehModel;
    @FXML private TableColumn<Vehicle, Integer> colVehYear;
    @FXML private TableColumn<Vehicle, String> colVehColor;
    @FXML private TableColumn<Vehicle, String> colVehPlate;
    @FXML private TableColumn<Vehicle, Integer> colVehSeats;
    @FXML private TableColumn<Vehicle, String> colVehFuel;
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
    @FXML private TableColumn<Guide, Guide> colGuideActions;

    private final VehicleService vehicleService = new VehicleService();
    private final GuideService guideService = new GuideService();

    @FXML
    public void initialize() {
        colVehBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colVehModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colVehYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colVehColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colVehPlate.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        colVehSeats.setCellValueFactory(new PropertyValueFactory<>("seatCapacity"));
        colVehFuel.setCellValueFactory(new PropertyValueFactory<>("fuelType"));
        colVehStatus.setCellValueFactory(cd ->
                new SimpleStringProperty(Boolean.TRUE.equals(cd.getValue().getIsAvailable()) ? "Available" : "Not Available"));

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
                editBtn.setOnAction(e -> {
                    Guide g = getTableView().getItems().get(getIndex());
                    onEditGuide(g);
                });
                deleteBtn.setOnAction(e -> {
                    Guide g = getTableView().getItems().get(getIndex());
                    onDeleteGuide(g);
                });
            }
            @Override
            protected void updateItem(Guide item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });

        loadData();
    }

    @FXML
    public void refresh() {
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Vehicle> vehicles = vehicleService.getAllVehicles();
                List<Guide> guides = guideService.getAllGuides();
                long available = vehicleService.countAvailable(vehicles);
                javafx.application.Platform.runLater(() -> {
                    vehiclesTable.getItems().setAll(vehicles);
                    totalVehiclesValue.setText(String.valueOf(vehicles.size()));
                    vehiclesAvailable.setText(available + " available");
                    guidesTable.getItems().setAll(guides);
                    totalGuidesValue.setText(String.valueOf(guides.size()));
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalVehiclesValue.setText("—");
                    vehiclesAvailable.setText("");
                    totalGuidesValue.setText("—");
                });
            }
        }).start();
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
                "Delete guide \"" + guide.getFullName() + "\"?")) return;

        runInBackground(
                () -> guideService.deleteGuide(guide.getId()),
                "Deleting guide...",
                "Guide deleted.",
                "Failed to delete guide"
        );
    }

    /**
     * 1) Loading penceresi göster
     * 2) Arka planda HTTP işlemi + yeni veri çekme
     * 3) Loading kapanınca UI güncelle, başarı mesajı göster
     * 4) Diğer ekranları "kirli" işaretle - kullanıcı geçince yenilenecekler
     */
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
                op.run();
                RefreshedData data = new RefreshedData();
                try {
                    data.vehicles = vehicleService.getAllVehicles();
                } catch (Exception ignored) {}
                try {
                    data.guides = guideService.getAllGuides();
                } catch (Exception ignored) {}
                return data;
            }
        };

        task.setOnSucceeded(e -> {
            RefreshedData data = task.getValue();
            if (data.vehicles != null) {
                vehiclesTable.getItems().setAll(data.vehicles);
                totalVehiclesValue.setText(String.valueOf(data.vehicles.size()));
                vehiclesAvailable.setText(vehicleService.countAvailable(data.vehicles) + " available");
            }
            if (data.guides != null) {
                guidesTable.getItems().setAll(data.guides);
                totalGuidesValue.setText(String.valueOf(data.guides.size()));
            }
            loadingStage.close();
            AppController app = AppController.getInstance();
            if (app != null) app.refreshAllCached();
            Toast.success(successMsg);
        });

        task.setOnFailed(e -> {
            loadingStage.close();
            Throwable ex = task.getException();
            ex.printStackTrace();
            Toast.error(errorTitle + ": " + ex.getMessage());
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