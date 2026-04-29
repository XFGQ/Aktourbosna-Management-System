package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    @Column(name = "waypoint_id")
    private Long waypointId;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "base_cost")
    private Float baseCost = 0.0f;

    @Column(name = "additional_cost")
    private Float additionalCost = 0.0f;

    @Column(name = "is_paid")
    private Boolean isPaid = false;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = true;

    @Column(name = "is_optional", nullable = false)
    private boolean isOptional = false;

    @Column(name = "is_removed")
    private boolean isRemoved = false;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
}