package org.example.service;

import org.example.model.Tour;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TourService {

    private final ApiService apiService = new ApiService();

    public List<Tour> getAllTours() throws Exception {
        return apiService.fetchTours();
    }

    public List<Tour> getUpcomingTours(List<Tour> tours) {
        LocalDate today = LocalDate.now();
        return tours.stream()
                .filter(t -> t.getStartDate() != null && today.isBefore(t.getStartDate()))
                .collect(Collectors.toList());
    }

    public List<Tour> getRecentTours(List<Tour> tours) {
        LocalDate today = LocalDate.now();
        return tours.stream()
                .filter(t -> t.getStartDate() != null && !today.isBefore(t.getStartDate()))
                .collect(Collectors.toList());
    }

    public String deriveStatus(Tour t) {
        if (t.getStartDate() == null || t.getEndDate() == null) return "—";
        LocalDate today = LocalDate.now();
        if (today.isBefore(t.getStartDate())) return "Upcoming";
        if (today.isAfter(t.getEndDate())) return "Completed";
        return "Active";
    }

    public Double calculateTotalRevenue(List<Tour> tours) {
        return tours.stream()
                .mapToDouble(t -> t.getFinalPrice() != null ? t.getFinalPrice() : 0.0)
                .sum();
    }

    public long countTours(List<Tour> tours) {
        return tours.size();
    }

    public Tour addTour(Tour tour) throws Exception {
        return apiService.createTour(tour);
    }

    public Tour updateTour(Long id, Tour tour) throws Exception {
        return apiService.updateTour(id, tour);
    }

    public void deleteTour(Long id) throws Exception {
        apiService.deleteTour(id);
    }
}
