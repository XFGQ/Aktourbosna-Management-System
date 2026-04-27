package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "waypoints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Waypoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "extra_fee")
    private Double extraFee = 0.0;
}