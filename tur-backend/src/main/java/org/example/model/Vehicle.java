package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @Column(name = "vehicle_id", length = 50)
    private String vehicleId;

    @Column(name = "brand", nullable = false, length = 100)
    private String brand;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "seat_capacity")
    private Integer seatCapacity;

    @Column(name = "fuel_type", length = 50)
    private String fuelType;

    @Column(name = "avg_fuel_consumption")
    private Float avgFuelConsumption;

    @Column(name = "current_mileage")
    private Float currentMileage;

    @Column(name = "last_minor_service")
    private LocalDate lastMinorService;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @ElementCollection
    @CollectionTable(name = "vehicle_service_history", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "service_record", length = 500)
    private List<String> serviceHistory = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle")
    private List<Tour> tours = new ArrayList<>();
}