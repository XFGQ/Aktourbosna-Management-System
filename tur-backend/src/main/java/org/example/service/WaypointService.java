package org.example.service;

import org.example.application.dto.WaypointDTO;
import org.example.application.mapper.WaypointMapper;
import org.example.model.Waypoint;
import org.example.repository.WaypointRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WaypointService {
    private final WaypointRepository waypointRepository;
    private final WaypointMapper waypointMapper;

    public WaypointService(WaypointRepository waypointRepository, WaypointMapper waypointMapper) {
        this.waypointRepository = waypointRepository;
        this.waypointMapper = waypointMapper;
    }

    public List<WaypointDTO> getAllWaypoints() {
        return waypointRepository.findAll().stream()
                .map(waypointMapper::toDto)
                .collect(Collectors.toList());
    }

    public WaypointDTO createWaypoint(WaypointDTO waypointDTO) {
        Waypoint waypoint = waypointMapper.toEntity(waypointDTO);
        return waypointMapper.toDto(waypointRepository.save(waypoint));
    }
}