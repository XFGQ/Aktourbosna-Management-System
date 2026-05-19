package org.example.service;

import org.example.model.Vehicle;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleService {

    private final ApiService apiService = new ApiService();

    // --- Static cache shared across all controller instances ---
    private static volatile List<Vehicle> cachedVehicles = null;
    private static volatile long cacheTs = 0;
    private static final long TTL_MS = 300_000;
    private static final Object LOCK = new Object();

    public List<Vehicle> getAllVehicles() throws Exception {
        if (cachedVehicles != null && System.currentTimeMillis() - cacheTs < TTL_MS) {
            return cachedVehicles;
        }
        synchronized (LOCK) {
            if (cachedVehicles != null && System.currentTimeMillis() - cacheTs < TTL_MS) {
                return cachedVehicles;
            }
            List<Vehicle> fresh = apiService.fetchVehicles();
            cachedVehicles = fresh;
            cacheTs = System.currentTimeMillis();
            return fresh;
        }
    }

    public static void invalidateCache() {
        cachedVehicles = null;
    }

    public Vehicle addVehicle(Vehicle vehicle) throws Exception {
        Vehicle result = apiService.createVehicle(vehicle);
        invalidateCache();
        return result;
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicle) throws Exception {
        Vehicle result = apiService.updateVehicle(id, vehicle);
        invalidateCache();
        return result;
    }

    public void deleteVehicle(Long id) throws Exception {
        apiService.deleteVehicle(id);
        invalidateCache();
    }

    public List<Vehicle> getAvailableVehicles(List<Vehicle> vehicles) {
        return vehicles.stream()
                .filter(v -> Boolean.TRUE.equals(v.getAvailable()))
                .collect(Collectors.toList());
    }

    public String getDisplayName(Vehicle vehicle) {
        if (vehicle == null) return "";
        return vehicle.getBrand() + " " + vehicle.getModel()
                + " · " + vehicle.getPlateNumber();
    }

    public long countAvailable(List<Vehicle> vehicles) {
        return vehicles.stream()
                .filter(v -> Boolean.TRUE.equals(v.getAvailable()))
                .count();
    }
}
