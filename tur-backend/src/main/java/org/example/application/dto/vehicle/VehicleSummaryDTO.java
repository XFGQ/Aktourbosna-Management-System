package org.example.application.dto.vehicle;

import lombok.Data;

@Data
public class VehicleSummaryDTO {

    private Long id;
    private String brand;
    private String model;
    private String plateNumber;
    private Integer seatCapacity;
    private Boolean available;
}
