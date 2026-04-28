package org.example.application.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleDTO {
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
    private boolean isAvailable;

}