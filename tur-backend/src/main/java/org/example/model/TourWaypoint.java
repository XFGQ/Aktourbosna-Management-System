package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tour_waypoints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourWaypoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_waypoint_id")
    private Long tourWaypointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waypoint_id", nullable = false)
    private Waypoint waypoint;

    @Column(name = "applied_cost")
    private Float appliedCost;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "is_optional_override")
    private boolean isOptionalOverride = false;

    @Column(name = "is_paid")
    private Boolean isPaid = false;
}