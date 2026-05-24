package org.example.controller;

import jakarta.validation.Valid;
import org.example.application.dto.WaypointDTO;
import org.example.service.WaypointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waypoints")
public class WaypointController {

    private final WaypointService waypointService;

    public WaypointController(WaypointService waypointService) {
        this.waypointService = waypointService;
    }

    @GetMapping
    public ResponseEntity<List<WaypointDTO>> getAllWaypoints() {
        return ResponseEntity.ok(waypointService.getAllWaypoints());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaypointDTO> getWaypointById(@PathVariable Long id) {
        return ResponseEntity.ok(waypointService.getWaypointById(id));
    }

    @PostMapping
    public ResponseEntity<WaypointDTO> createWaypoint(@Valid @RequestBody WaypointDTO waypointDTO) {
        return ResponseEntity.ok(waypointService.createWaypoint(waypointDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaypointDTO> updateWaypoint(@PathVariable Long id,
                                                      @Valid @RequestBody WaypointDTO waypointDTO) {
        return ResponseEntity.ok(waypointService.updateWaypoint(id, waypointDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaypoint(@PathVariable Long id) {
        waypointService.deleteWaypoint(id);
        return ResponseEntity.noContent().build();
    }
}