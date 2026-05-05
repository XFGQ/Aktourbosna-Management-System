package org.example.controller;

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

    @PostMapping
    public ResponseEntity<WaypointDTO> createWaypoint(@RequestBody WaypointDTO waypointDTO) {
        return ResponseEntity.ok(waypointService.createWaypoint(waypointDTO));
    }
}