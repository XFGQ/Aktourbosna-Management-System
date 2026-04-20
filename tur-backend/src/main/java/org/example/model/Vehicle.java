package org.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vehicles")
@Data //
public class Vehicle {

     @Id
    @Column(name = "vehicle_id", length = 50)
    private String vehicleId;

    private String brand;
    private String model;
    private Integer vehicleYear;
    private String color;
    private Integer currentKm;
    private String fuelType;
    private Double dailyRentalFee;
    private String currency;
    private String plateNumber;
    private int seatCapacity;
    private boolean isAvailable;
}