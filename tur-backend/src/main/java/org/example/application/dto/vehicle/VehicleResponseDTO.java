package org.example.application.dto.vehicle;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class VehicleResponseDTO {

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
    private Boolean available;
    private Double dailyRentalFee;
    private List<String> serviceHistory;
    private int tourCount;
}
