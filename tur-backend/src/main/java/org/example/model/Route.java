package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "route_name", nullable = false, length = 200)
    private String routeName;

    @Column(name = "start_city", nullable = false, length = 100)
    private String startCity;

    @Column(name = "end_city", nullable = false, length = 100)
    private String endCity;

    @Column(name = "country", length = 100)
    private String country;

    private Float distance;

    @Column(name = "base_price")
    @Builder.Default
    private Double basePrice = 0.0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "route_waypoints_mapping",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "waypoint_id")
    )
    @Builder.Default
    private List<Waypoint> defaultWaypoints = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "route_tolls",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "toll_id")
    )
    @Builder.Default
    private List<Toll> tolls = new ArrayList<>();
}