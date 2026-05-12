package org.example.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
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
    @SerializedName("available")
    private Boolean available;
    private Double dailyRentalFee;
}