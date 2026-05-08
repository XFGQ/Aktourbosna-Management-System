package org.example.controller;

import org.example.application.dto.TourDTO;
import org.example.service.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public ResponseEntity<List<TourDTO>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourDTO> getTourById(@PathVariable Long id) {
        return ResponseEntity.ok(tourService.getTourById(id));
    }

    @PostMapping
    public ResponseEntity<TourDTO> createTour(@RequestBody TourDTO tourDTO) {
        return ResponseEntity.ok(tourService.createTour(tourDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourDTO> updateTour(@PathVariable Long id, @RequestBody TourDTO tourDTO) {
        return ResponseEntity.ok(tourService.updateTour(id, tourDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tourId}/waypoints/{waypointId}")
    public ResponseEntity<TourDTO> addWaypoint(@PathVariable Long tourId, @PathVariable Long waypointId) {
        return ResponseEntity.ok(tourService.addWaypoint(tourId, waypointId));
    }

    @DeleteMapping("/{tourId}/waypoints/{waypointId}")
    public ResponseEntity<TourDTO> removeWaypoint(@PathVariable Long tourId, @PathVariable Long waypointId) {
        return ResponseEntity.ok(tourService.removeWaypoint(tourId, waypointId));
    }
}
