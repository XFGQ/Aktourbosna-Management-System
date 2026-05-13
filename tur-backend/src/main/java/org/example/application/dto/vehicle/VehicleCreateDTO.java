package org.example.application.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleCreateDTO {

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String plateNumber;

    private Integer year;
    private String color;

    @NotNull
    private Integer seatCapacity;

    private String fuelType;
    private Float avgFuelConsumption;
    private Float currentMileage;
    private LocalDate lastMinorService;
    private Boolean available;
    private Double dailyRentalFee;
}
