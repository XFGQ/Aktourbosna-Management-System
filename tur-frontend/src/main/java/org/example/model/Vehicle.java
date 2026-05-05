package org.example.model;

import java.time.LocalDate;

public class Vehicle {

    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String plateNumber;
    private Integer seatCapacity;
    private String fuelType;
    private Float avgFuelConsumption;
    private Float currentMileage;
    private LocalDate lastMinorService;
    private Boolean isAvailable;

    public Vehicle() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public Integer getSeatCapacity() { return seatCapacity; }
    public void setSeatCapacity(Integer seatCapacity) { this.seatCapacity = seatCapacity; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public Float getAvgFuelConsumption() { return avgFuelConsumption; }
    public void setAvgFuelConsumption(Float avgFuelConsumption) { this.avgFuelConsumption = avgFuelConsumption; }
    public Float getCurrentMileage() { return currentMileage; }
    public void setCurrentMileage(Float currentMileage) { this.currentMileage = currentMileage; }
    public LocalDate getLastMinorService() { return lastMinorService; }
    public void setLastMinorService(LocalDate lastMinorService) { this.lastMinorService = lastMinorService; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}