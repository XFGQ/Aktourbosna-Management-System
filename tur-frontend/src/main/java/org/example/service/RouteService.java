package org.example.service;

import org.example.model.Route;

public class RouteService {

    public String getDisplayName(Route route) {
        if (route == null) return "";
        return route.getStartCity() + " → " + route.getEndCity();
    }

    public Double getBasePrice(Route route) {
        return route != null && route.getBasePrice() != null ? route.getBasePrice() : 0.0;
    }

    public Float getDistance(Route route) {
        return route != null && route.getDistance() != null ? route.getDistance() : 0.0f;
    }
}