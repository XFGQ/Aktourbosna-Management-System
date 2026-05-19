package org.example.service;

import org.example.model.Tour;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TourService {

    private final ApiService apiService = new ApiService();

    // --- Static cache shared across all controller instances ---
    private static volatile List<Tour> cachedTours = null;
    private static volatile long cacheTs = 0;
    private static final long TTL_MS = 300_000;
    private static final Object LOCK = new Object();

    public List<Tour> getAllTours() throws Exception {
        if (cachedTours != null && System.currentTimeMillis() - cacheTs < TTL_MS) {
            return cachedTours;
        }
        synchronized (LOCK) {
            if (cachedTours != null && System.currentTimeMillis() - cacheTs < TTL_MS) {
                return cachedTours;
            }
            List<Tour> fresh = apiService.fetchTours();
            cachedTours = fresh;
            cacheTs = System.currentTimeMillis();
            return fresh;
        }
    }

    public static void invalidateCache() {
        cachedTours = null;
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
        Tour result = apiService.createTour(tour);
        invalidateCache();
        return result;
    }

    public Tour updateTour(Long id, Tour tour) throws Exception {
        Tour result = apiService.updateTour(id, tour);
        invalidateCache();
        return result;
    }

    public void deleteTour(Long id) throws Exception {
        apiService.deleteTour(id);
        invalidateCache();
    }
}
