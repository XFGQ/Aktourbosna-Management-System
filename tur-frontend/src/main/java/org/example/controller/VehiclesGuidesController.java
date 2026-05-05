package org.example.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML private TableColumn<Vehicle, Number> colVehYear;
    @FXML private TableColumn<Vehicle, String> colVehColor;
    @FXML private TableColumn<Vehicle, String> colVehPlate;
    @FXML private TableColumn<Vehicle, Number> colVehSeats;
    @FXML private TableColumn<Vehicle, String> colVehFuel;
    @FXML private TableColumn<Vehicle, String> colVehStatus;

    @FXML private TableView<Guide> guidesTable;
    @FXML private TableColumn<Guide, String> colGuideName;
    @FXML private TableColumn<Guide, String> colGuideEmail;
    @FXML private TableColumn<Guide, String> colGuidePhone;
    @FXML private TableColumn<Guide, String> colGuideCity;
    @FXML private TableColumn<Guide, String> colGuideLicense;
    @FXML private TableColumn<Guide, Number> colGuideExp;
    @FXML private TableColumn<Guide, Number> colGuideFee;

    private final VehicleService vehicleService = new VehicleService();
    private final GuideService guideService = new GuideService();

    @FXML
    public void initialize() {
        
        colVehBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colVehModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colVehYear.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getYear()));
        colVehColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colVehPlate.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        colVehSeats.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getSeatCapacity()));
        colVehFuel.setCellValueFactory(new PropertyValueFactory<>("fuelType"));
        colVehStatus.setCellValueFactory(cd -> {
            Boolean av = cd.getValue().getIsAvailable();
            return new SimpleStringProperty(av != null && av ? "Available" : "Unavailable");
        });

        // Guide table sütunları
        colGuideName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colGuideEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGuidePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colGuideCity.setCellValueFactory(new PropertyValueFactory<>("baseCity"));
        colGuideLicense.setCellValueFactory(new PropertyValueFactory<>("licenseNo"));
        colGuideExp.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getExperience()));
        colGuideFee.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getDailyFee()));

        loadData();
    }

    private void loadData() {
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            vehiclesTable.getItems().setAll(vehicles);
            totalVehiclesValue.setText(String.valueOf(vehicles.size()));
            vehiclesAvailable.setText(vehicleService.countAvailable(vehicles) + " available");

            List<Guide> guides = guideService.getAllGuides();
            guidesTable.getItems().setAll(guides);
            totalGuidesValue.setText(String.valueOf(guides.size()));
        } catch (Exception e) {
            e.printStackTrace();
            totalVehiclesValue.setText("—");
            totalGuidesValue.setText("—");
        }
    }
}