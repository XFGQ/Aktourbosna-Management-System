package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "start_city", nullable = false, length = 100)
    private String startCity;

    @Column(name = "end_city", nullable = false, length = 100)
    private String endCity;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "distance")
    private Float distance;

    @Column(name = "base_price")
    private Float basePrice = 0.0f;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Waypoint> waypoints = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "route_tolls",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "toll_id")
    )
    private List<Toll> tolls = new ArrayList<>();

    @ManyToMany(mappedBy = "routes")
    private List<Tour> tours = new ArrayList<>();

    public void addWaypoint(Waypoint waypoint) {
        if (this.waypoints == null) {
            this.waypoints = new ArrayList<>();
        }
        this.waypoints.add(waypoint);
        waypoint.setRoute(this);
    }
}