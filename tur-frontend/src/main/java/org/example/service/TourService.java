package org.example.service;

import org.example.model.Tour;

import java.util.List;
import java.util.stream.Collectors;

public class TourService {

    private final ApiService apiService = new ApiService();

    public List<Tour> getAllTours() throws Exception {
        return apiService.fetchTours();
    }

    public List<Tour> getToursByGuide(List<Tour> tours, String guideName) {
        return tours.stream()
                .filter(t -> t.getGuide() != null
                        && guideName.equals(t.getGuide().getFullName()))
                .collect(Collectors.toList());
    }

    public String getGuideName(Tour tour) {
        return tour.getGuide() != null ? tour.getGuide().getFullName() : "";
    }

    public String getVehicleDisplayName(Tour tour) {
        if (tour.getVehicle() == null) return "";
        return tour.getVehicle().getBrand() + " "
                + tour.getVehicle().getModel() + " · "
                + tour.getVehicle().getPlateNumber();
    }

    public Double calculateTotalRevenue(List<Tour> tours) {
        return tours.stream()
                .mapToDouble(t -> t.getTotalCost() != null ? t.getTotalCost() : 0.0)
                .sum();
    }

    public long countTours(List<Tour> tours) {
        return tours.size();
    }
}