package org.example.controller;

import jakarta.validation.Valid;
import org.example.application.dto.route.RouteCreateDTO;
import org.example.application.dto.route.RouteResponseDTO;
import org.example.application.dto.route.RouteSummaryDTO;
import org.example.application.dto.route.RouteUpdateDTO;
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
    public ResponseEntity<List<RouteSummaryDTO>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponseDTO> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @PostMapping
    public ResponseEntity<RouteResponseDTO> createRoute(@Valid @RequestBody RouteCreateDTO dto) {
        return ResponseEntity.ok(routeService.createRoute(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteResponseDTO> updateRoute(@PathVariable Long id,
                                                        @RequestBody RouteUpdateDTO dto) {
        return ResponseEntity.ok(routeService.updateRoute(id, dto));
    }

    @PostMapping("/{routeId}/waypoints/{waypointId}")
    public ResponseEntity<RouteResponseDTO> addWaypointToRoute(@PathVariable Long routeId,
                                                               @PathVariable Long waypointId) {
        return ResponseEntity.ok(routeService.addWaypointToRoute(routeId, waypointId));
    }

    @DeleteMapping("/{routeId}/waypoints/{waypointId}")
    public ResponseEntity<RouteResponseDTO> removeWaypointFromRoute(@PathVariable Long routeId,
                                                                    @PathVariable Long waypointId) {
        return ResponseEntity.ok(routeService.removeWaypointFromRoute(routeId, waypointId));
    }
}
