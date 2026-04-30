package org.example.service;

import org.example.model.Tour;
import org.example.model.TourWaypoint;

import java.util.List;
import java.util.stream.Collectors;

public class TourWaypointService {

    public List<TourWaypoint> getTourWaypoints(Tour tour) {
        return tour.getTourWaypoints();
    }

    public List<TourWaypoint> getPaidWaypoints(List<TourWaypoint> tourWaypoints) {
        return tourWaypoints.stream()
                .filter(tw -> Boolean.TRUE.equals(tw.getIsPaid()))
                .collect(Collectors.toList());
    }

    public Double calculateTotalAppliedCost(List<TourWaypoint> tourWaypoints) {
        return tourWaypoints.stream()
                .mapToDouble(tw -> tw.getAppliedCost() != null ? tw.getAppliedCost() : 0.0)
                .sum();
    }
}