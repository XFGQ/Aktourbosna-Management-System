package org.example.service;

import org.example.model.Waypoint;

import java.util.List;

public class WaypointService {

    public String getDisplayName(Waypoint waypoint) {
        if (waypoint == null) return "";
        return waypoint.getCity() + ", " + waypoint.getCountry();
    }

    public Double calculateTotalExtraFee(List<Waypoint> waypoints) {
        return waypoints.stream()
                .mapToDouble(w -> w.getExtraFee() != null ? w.getExtraFee() : 0.0)
                .sum();
    }
}