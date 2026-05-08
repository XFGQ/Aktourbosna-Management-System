package org.example.service;

import org.example.application.dto.RouteDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.RouteMapper;
import org.example.model.Route;
import org.example.model.Waypoint;
import org.example.repository.RouteRepository;
import org.example.repository.WaypointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final WaypointRepository waypointRepository;
    private final RouteMapper routeMapper;

    public RouteService(RouteRepository routeRepository, WaypointRepository waypointRepository, RouteMapper routeMapper) {
        this.routeRepository = routeRepository;
        this.waypointRepository = waypointRepository;
        this.routeMapper = routeMapper;
    }

    public List<RouteDTO> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(routeMapper::toDto)
                .collect(Collectors.toList());
    }

    public RouteDTO getRouteById(Long id) {
        return routeMapper.toDto(routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + id)));
    }

    public RouteDTO createRoute(RouteDTO routeDTO) {
        return routeMapper.toDto(routeRepository.save(routeMapper.toEntity(routeDTO)));
    }

    public RouteDTO updateRoute(Long id, RouteDTO routeDTO) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + id));
        routeMapper.updateEntityFromDto(routeDTO, route);
        return routeMapper.toDto(routeRepository.save(route));
    }

    @Transactional
    public RouteDTO addWaypointToRoute(Long routeId, Long waypointId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + routeId));
        Waypoint waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new ResourceNotFoundException("Waypoint not found: " + waypointId));
        if (!route.getDefaultWaypoints().contains(waypoint)) {
            route.getDefaultWaypoints().add(waypoint);
            routeRepository.save(route);
        }
        return routeMapper.toDto(route);
    }

    @Transactional
    public RouteDTO removeWaypointFromRoute(Long routeId, Long waypointId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + routeId));
        Waypoint waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new ResourceNotFoundException("Waypoint not found: " + waypointId));
        route.getDefaultWaypoints().remove(waypoint);
        routeRepository.save(route);
        return routeMapper.toDto(route);
    }
}
