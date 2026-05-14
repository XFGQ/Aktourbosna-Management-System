package org.example.application.dto.vehicle;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleUpdateDTO {

    private String brand;
    private String model;
    private String plateNumber;
    private Integer year;
    private String color;
    private Integer seatCapacity;
    private String fuelType;
    private Float avgFuelConsumption;
    private Float currentMileage;
    private LocalDate lastMinorService;
    private Boolean available;
    private Double dailyRentalFee;
}
