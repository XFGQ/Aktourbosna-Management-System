package org.example.service;

import org.example.model.Vehicle;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleService {

    private final ApiService apiService = new ApiService();

    public List<Vehicle> getAllVehicles() throws Exception {
        return apiService.fetchVehicles();
    }

    public List<Vehicle> getAvailableVehicles(List<Vehicle> vehicles) {
        return vehicles.stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsAvailable()))
                .collect(Collectors.toList());
    }

    public String getDisplayName(Vehicle vehicle) {
        if (vehicle == null) return "";
        return vehicle.getBrand() + " " + vehicle.getModel()
                + " · " + vehicle.getPlateNumber();
    }

    public long countAvailable(List<Vehicle> vehicles) {
        return vehicles.stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsAvailable()))
                .count();
    }
}