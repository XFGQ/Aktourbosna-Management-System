package org.example.service;

import org.example.model.Route;
import org.example.model.Tour;

import java.util.List;

public class RouteService {

    public List<Route> getRoutesForTour(Tour tour) {
        return tour.getRoutes();
    }

    public Double calculateTotalBasePrice(List<Route> routes) {
        return routes.stream()
                .mapToDouble(r -> r.getBasePrice() != null ? r.getBasePrice() : 0.0)
                .sum();
    }

    public String getDisplayName(Route route) {
        if (route == null) return "";
        return route.getStartCity() + " → " + route.getEndCity();
    }

    public Double calculateTotalDistance(List<Route> routes) {
        return routes.stream()
                .mapToDouble(r -> r.getDistance() != null ? r.getDistance() : 0.0)
                .sum();
    }
}