package org.example.service;

import org.example.model.Route;
import java.util.List;

public class RouteService {

    private final ApiService apiService = new ApiService();

    public List<Route> getAllRoutes() throws Exception {
        return apiService.fetchRoutes();
    }

    public Route addRoute(Route route) throws Exception {
        return apiService.createRoute(route);
    }

    public Route updateRoute(Long id, Route route) throws Exception {
        return apiService.updateRoute(id, route);
    }

    public void deleteRoute(Long id) throws Exception {
        apiService.deleteRoute(id);
    }

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