package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Route {
    private Long routeId;
    private String routeName;
    private String startCity;
    private String endCity;
    private String country;
    private Float distance;
    private Double basePrice = 0.0;
    private List<Waypoint> defaultWaypoints = new ArrayList<>();
    private List<Toll> tolls = new ArrayList<>();
}
