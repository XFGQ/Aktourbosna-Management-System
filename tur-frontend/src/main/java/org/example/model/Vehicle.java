package org.example.model;

import java.util.Date;
import java.util.List;

public class Vehicle {

    private int vehicleId;
    private String brand;
    private String model;
    private int year;
    private String color;
    private String plateNumber;
    private int capacity;
    private String fuelType;
    private float avgFuelConsumption;
    private float currentMileage;
    private Date lastMaintenanceService;
    private List<String> serviceHistory;

    public Vehicle() {}

    public Vehicle(int vehicleId, String brand, String model, int year,
                   String color, String plateNumber, int capacity,
                   String fuelType, float avgFuelConsumption,
                   float currentMileage, Date lastMaintenanceService,
                   List<String> serviceHistory) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.plateNumber = plateNumber;
        this.capacity = capacity;
        this.fuelType = fuelType;
        this.avgFuelConsumption = avgFuelConsumption;
        this.currentMileage = currentMileage;
        this.lastMaintenanceService = lastMaintenanceService;
        this.serviceHistory = serviceHistory;
    }

    public boolean isAvailable() { return true; }

    public void addServiceRecord() {}

    public String getDetails() {
        return year + " " + brand + " " + model
                + " | Plate: " + plateNumber
                + " | Capacity: " + capacity
                + " | Fuel: " + fuelType;
    }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public float getAvgFuelConsumption() { return avgFuelConsumption; }
    public void setAvgFuelConsumption(float avgFuelConsumption) { this.avgFuelConsumption = avgFuelConsumption; }

    public float getCurrentMileage() { return currentMileage; }
    public void setCurrentMileage(float currentMileage) { this.currentMileage = currentMileage; }

    public Date getLastMaintenanceService() { return lastMaintenanceService; }
    public void setLastMaintenanceService(Date lastMaintenanceService) { this.lastMaintenanceService = lastMaintenanceService; }

    public List<String> getServiceHistory() { return serviceHistory; }
    public void setServiceHistory(List<String> serviceHistory) { this.serviceHistory = serviceHistory; }
}
