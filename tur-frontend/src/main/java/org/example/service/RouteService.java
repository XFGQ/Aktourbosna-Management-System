package org.example.service;

import org.example.model.Route;

import java.util.List;

public class RouteService {

    private final ApiService apiService = new ApiService();

    private static volatile List<Route> cachedRoutes = null;
    private static volatile long cacheTs = 0;
    private static final long TTL_MS = 300_000;
    private static final Object LOCK = new Object();

    public List<Route> getAllRoutes() throws Exception {
        if (cachedRoutes != null && System.currentTimeMillis() - cacheTs < TTL_MS) {
            return cachedRoutes;
        }
        synchronized (LOCK) {
            if (cachedRoutes != null && System.currentTimeMillis() - cacheTs < TTL_MS) {
                return cachedRoutes;
            }
            List<Route> fresh = apiService.fetchRoutes();
            cachedRoutes = fresh;
            cacheTs = System.currentTimeMillis();
            return fresh;
        }
    }

    public static void invalidateCache() {
        cachedRoutes = null;
    }

    public Route addRoute(Route route) throws Exception {
        Route result = apiService.createRoute(route);
        invalidateCache();
        return result;
    }

    public Route updateRoute(Long id, Route route) throws Exception {
        Route result = apiService.updateRoute(id, route);
        invalidateCache();
        return result;
    }

    public void deleteRoute(Long id) throws Exception {
        apiService.deleteRoute(id);
        invalidateCache();
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
