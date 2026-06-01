package org.example.service;

import org.example.application.dto.WaypointDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.WaypointMapper;
import org.example.model.Waypoint;
import org.example.repository.WaypointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WaypointServiceTest {

    @Mock private WaypointRepository waypointRepository;
    @Mock private WaypointMapper waypointMapper;

    @InjectMocks private WaypointService waypointService;

    private Waypoint entity;
    private WaypointDTO dto;

    @BeforeEach
    void setUp() {
        entity = new Waypoint();
        dto = new WaypointDTO(1L, "Istanbul", "Turkey", 10.0);
    }

    @Test
    @DisplayName("getAllWaypoints - returns list of waypoint DTOs")
    void getAllWaypoints_returnsList() {
        when(waypointRepository.findAll()).thenReturn(List.of(entity));
        when(waypointMapper.toDto(entity)).thenReturn(dto);

        List<WaypointDTO> result = waypointService.getAllWaypoints();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Istanbul");
        verify(waypointRepository).findAll();
    }

    @Test
    @DisplayName("getWaypointById - existing ID returns DTO")
    void getWaypointById_existing_returnsDto() {
        when(waypointRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(waypointMapper.toDto(entity)).thenReturn(dto);

        WaypointDTO result = waypointService.getWaypointById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getWaypointById - non-existing ID throws ResourceNotFoundException")
    void getWaypointById_nonExisting_throws() {
        when(waypointRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> waypointService.getWaypointById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Waypoint not found");
    }

    @Test
    @DisplayName("createWaypoint - saves and returns DTO")
    void createWaypoint_savesAndReturnsDto() {
        when(waypointMapper.toEntity(dto)).thenReturn(entity);
        when(waypointRepository.save(entity)).thenReturn(entity);
        when(waypointMapper.toDto(entity)).thenReturn(dto);

        WaypointDTO result = waypointService.createWaypoint(dto);

        assertThat(result.getCity()).isEqualTo("Istanbul");
        verify(waypointRepository).save(entity);
    }

    @Test
    @DisplayName("updateWaypoint - non-existing ID throws ResourceNotFoundException")
    void updateWaypoint_nonExisting_throws() {
        when(waypointRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> waypointService.updateWaypoint(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Waypoint not found");
    }

    @Test
    @DisplayName("deleteWaypoint - non-existing ID throws and does not delete")
    void deleteWaypoint_nonExisting_throws() {
        when(waypointRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> waypointService.deleteWaypoint(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Waypoint not found");

        verify(waypointRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteWaypoint - existing ID deletes successfully")
    void deleteWaypoint_existing_deletes() {
        when(waypointRepository.existsById(1L)).thenReturn(true);

        waypointService.deleteWaypoint(1L);

        verify(waypointRepository).deleteById(1L);
    }
}
