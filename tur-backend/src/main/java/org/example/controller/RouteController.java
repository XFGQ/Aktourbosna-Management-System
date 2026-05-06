package org.example.controller;

import org.example.application.dto.RouteDTO;
import org.example.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @PostMapping
    public ResponseEntity<RouteDTO> createRoute(@RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok(routeService.createRoute(routeDTO));
    }

    @PostMapping("/{routeId}/waypoints/{waypointId}")
    public ResponseEntity<RouteDTO> addWaypointToRoute(@PathVariable Long routeId, @PathVariable Long waypointId) {
        return ResponseEntity.ok(routeService.addWaypointToRoute(routeId, waypointId));
    }

    @DeleteMapping("/{routeId}/waypoints/{waypointId}")
    public ResponseEntity<RouteDTO> removeWaypointFromRoute(@PathVariable Long routeId, @PathVariable Long waypointId) {
        return ResponseEntity.ok(routeService.removeWaypointFromRoute(routeId, waypointId));
    }
}