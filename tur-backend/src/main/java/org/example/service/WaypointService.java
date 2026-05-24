package org.example.service;

import org.example.application.dto.WaypointDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.WaypointMapper;
import org.example.model.Waypoint;
import org.example.repository.WaypointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public WaypointDTO getWaypointById(Long id) {
        Waypoint waypoint = waypointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waypoint not found: " + id));
        return waypointMapper.toDto(waypoint);
    }

    public WaypointDTO createWaypoint(WaypointDTO waypointDTO) {
        Waypoint waypoint = waypointMapper.toEntity(waypointDTO);
        return waypointMapper.toDto(waypointRepository.save(waypoint));
    }

    @Transactional
    public WaypointDTO updateWaypoint(Long id, WaypointDTO waypointDTO) {
        Waypoint waypoint = waypointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waypoint not found: " + id));
        waypointMapper.updateEntityFromDto(waypointDTO, waypoint);
        return waypointMapper.toDto(waypointRepository.save(waypoint));
    }

    public void deleteWaypoint(Long id) {
        if (!waypointRepository.existsById(id)) {
            throw new ResourceNotFoundException("Waypoint not found: " + id);
        }
        waypointRepository.deleteById(id);
    }
}