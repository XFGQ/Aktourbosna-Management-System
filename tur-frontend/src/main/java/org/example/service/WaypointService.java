package org.example.service;

import org.example.model.Route;
import org.example.model.Waypoint;

import java.util.List;
import java.util.stream.Collectors;

public class WaypointService {

    public List<Waypoint> getWaypointsForRoute(Route route) {
        return route.getWaypoints();
    }

    public List<Waypoint> getDefaultWaypoints(List<Waypoint> waypoints) {
        return waypoints.stream()
                .filter(Waypoint::isDefault)
                .collect(Collectors.toList());
    }

    public List<Waypoint> getOptionalWaypoints(List<Waypoint> waypoints) {
        return waypoints.stream()
                .filter(Waypoint::isOptional)
                .collect(Collectors.toList());
    }

    public Double calculateTotalAdditionalCost(List<Waypoint> waypoints) {
        return waypoints.stream()
                .mapToDouble(w -> w.getAdditionalCost() != null ? w.getAdditionalCost() : 0.0)
                .sum();
    }
}