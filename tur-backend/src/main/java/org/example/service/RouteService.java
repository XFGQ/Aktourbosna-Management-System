package org.example.service;

import org.example.application.dto.TollDTO;
import org.example.application.dto.WaypointDTO;
import org.example.application.dto.route.RouteCreateDTO;
import org.example.application.dto.route.RouteResponseDTO;
import org.example.application.dto.route.RouteSummaryDTO;
import org.example.application.dto.route.RouteUpdateDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.RouteMapper;
import org.example.application.mapper.TollMapper;
import org.example.application.mapper.WaypointMapper;
import org.example.model.Route;
import org.example.model.Toll;
import org.example.model.Waypoint;
import org.example.repository.RouteRepository;
import org.example.repository.TollRepository;
import org.example.repository.WaypointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final WaypointRepository waypointRepository;
    private final TollRepository tollRepository;
    private final RouteMapper routeMapper;
    private final WaypointMapper waypointMapper;
    private final TollMapper tollMapper;

    public RouteService(RouteRepository routeRepository, WaypointRepository waypointRepository,
                        TollRepository tollRepository, RouteMapper routeMapper,
                        WaypointMapper waypointMapper, TollMapper tollMapper) {
        this.routeRepository = routeRepository;
        this.waypointRepository = waypointRepository;
        this.tollRepository = tollRepository;
        this.routeMapper = routeMapper;
        this.waypointMapper = waypointMapper;
        this.tollMapper = tollMapper;
    }

    @Transactional(readOnly = true)
    public List<RouteSummaryDTO> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(routeMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RouteResponseDTO getRouteById(Long id) {
        return routeMapper.toResponse(routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + id)));
    }

    @Transactional
    public RouteResponseDTO createRoute(RouteCreateDTO dto) {
        Route route = routeMapper.toEntity(dto);
        resolveRelations(route, dto.getWaypointIds(), dto.getTollIds(),
                dto.getDefaultWaypoints(), dto.getTolls());
        return routeMapper.toResponse(routeRepository.save(route));
    }

    @Transactional
    public RouteResponseDTO updateRoute(Long id, RouteUpdateDTO dto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + id));
        routeMapper.updateEntityFromDto(dto, route);
        resolveRelations(route, dto.getWaypointIds(), dto.getTollIds(),
                dto.getDefaultWaypoints(), dto.getTolls());
        return routeMapper.toResponse(routeRepository.save(route));
    }

    @Transactional
    public RouteResponseDTO addWaypointToRoute(Long routeId, Long waypointId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + routeId));
        Waypoint waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new ResourceNotFoundException("Waypoint not found: " + waypointId));
        if (!route.getDefaultWaypoints().contains(waypoint)) {
            route.getDefaultWaypoints().add(waypoint);
            routeRepository.save(route);
        }
        return routeMapper.toResponse(route);
    }

    @Transactional
    public RouteResponseDTO removeWaypointFromRoute(Long routeId, Long waypointId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + routeId));
        Waypoint waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new ResourceNotFoundException("Waypoint not found: " + waypointId));
        route.getDefaultWaypoints().remove(waypoint);
        routeRepository.save(route);
        return routeMapper.toResponse(route);
    }

    private void resolveRelations(Route route, List<Long> waypointIds, List<Long> tollIds,
                                   List<WaypointDTO> inlineWaypoints, List<TollDTO> inlineTolls) {
        if (waypointIds != null && !waypointIds.isEmpty()) {
            List<Waypoint> waypoints = waypointIds.stream()
                    .map(wId -> waypointRepository.findById(wId)
                            .orElseThrow(() -> new ResourceNotFoundException("Waypoint not found: " + wId)))
                    .collect(Collectors.toList());
            route.setDefaultWaypoints(waypoints);
        } else if (inlineWaypoints != null && !inlineWaypoints.isEmpty()) {
            List<Waypoint> waypoints = inlineWaypoints.stream()
                    .map(waypointMapper::toEntity)
                    .map(waypointRepository::save)
                    .collect(Collectors.toList());
            route.setDefaultWaypoints(waypoints);
        }

        if (tollIds != null && !tollIds.isEmpty()) {
            List<Toll> tolls = tollIds.stream()
                    .map(tId -> tollRepository.findById(tId)
                            .orElseThrow(() -> new ResourceNotFoundException("Toll not found: " + tId)))
                    .collect(Collectors.toList());
            route.setTolls(tolls);
        } else if (inlineTolls != null && !inlineTolls.isEmpty()) {
            List<Toll> tolls = inlineTolls.stream()
                    .map(tollMapper::toEntity)
                    .map(tollRepository::save)
                    .collect(Collectors.toList());
            route.setTolls(tolls);
        }
    }
}
