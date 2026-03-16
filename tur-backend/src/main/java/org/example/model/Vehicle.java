package org.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plateNumber; // Plaka
    private String model;
    private String litreHundred;
    private Integer seatCapacity;
    private Boolean isAvailable = true;
}