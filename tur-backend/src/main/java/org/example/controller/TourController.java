package org.example.controller;

import jakarta.validation.Valid;
import org.example.application.dto.tour.TourCreateDTO;
import org.example.application.dto.tour.TourResponseDTO;
import org.example.application.dto.tour.TourSummaryDTO;
import org.example.application.dto.tour.TourUpdateDTO;
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
    public ResponseEntity<List<TourSummaryDTO>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponseDTO> getTourById(@PathVariable Long id) {
        return ResponseEntity.ok(tourService.getTourById(id));
    }

    @PostMapping
    public ResponseEntity<TourResponseDTO> createTour(@Valid @RequestBody TourCreateDTO dto) {
        return ResponseEntity.ok(tourService.createTour(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourResponseDTO> updateTour(@PathVariable Long id,
                                                      @RequestBody TourUpdateDTO dto) {
        return ResponseEntity.ok(tourService.updateTour(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tourId}/waypoints/{waypointId}")
    public ResponseEntity<TourResponseDTO> addWaypoint(@PathVariable Long tourId,
                                                       @PathVariable Long waypointId) {
        return ResponseEntity.ok(tourService.addWaypoint(tourId, waypointId));
    }

    @DeleteMapping("/{tourId}/waypoints/{waypointId}")
    public ResponseEntity<TourResponseDTO> removeWaypoint(@PathVariable Long tourId,
                                                          @PathVariable Long waypointId) {
        return ResponseEntity.ok(tourService.removeWaypoint(tourId, waypointId));
    }
}
