package org.example.service;

import org.example.application.dto.route.RouteCreateDTO;
import org.example.application.dto.route.RouteResponseDTO;
import org.example.application.dto.route.RouteSummaryDTO;
import org.example.application.dto.route.RouteUpdateDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.RouteMapper;
import org.example.application.mapper.TollMapper;
import org.example.application.mapper.WaypointMapper;
import org.example.model.Route;
import org.example.model.Waypoint;
import org.example.repository.RouteRepository;
import org.example.repository.TollRepository;
import org.example.repository.WaypointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock RouteRepository routeRepository;
    @Mock WaypointRepository waypointRepository;
    @Mock TollRepository tollRepository;
    @Mock RouteMapper routeMapper;
    @Mock WaypointMapper waypointMapper;
    @Mock TollMapper tollMapper;
    @InjectMocks RouteService routeService;

    private Route route;
    private RouteResponseDTO responseDTO;
    private RouteSummaryDTO summaryDTO;

    @BeforeEach
    void setup() {
        route = new Route();
        route.setRouteId(1L);
        route.setRouteName("Sarajevo - Mostar");
        route.setStartCity("Sarajevo");
        route.setEndCity("Mostar");
        route.setDefaultWaypoints(new ArrayList<>());
        route.setTolls(new ArrayList<>());

        responseDTO = new RouteResponseDTO();
        responseDTO.setRouteId(1L);
        responseDTO.setRouteName("Sarajevo - Mostar");
        responseDTO.setStartCity("Sarajevo");
        responseDTO.setEndCity("Mostar");

        summaryDTO = new RouteSummaryDTO();
        summaryDTO.setRouteId(1L);
        summaryDTO.setRouteName("Sarajevo - Mostar");
    }

    @Test
    void getAllRoutes_returnsMappedSummaryList() {
        when(routeRepository.findAll()).thenReturn(List.of(route));
        when(routeMapper.toSummary(route)).thenReturn(summaryDTO);

        List<RouteSummaryDTO> result = routeService.getAllRoutes();

        assertThat(result).hasSize(1).containsExactly(summaryDTO);
        verify(routeRepository).findAll();
    }

    @Test
    void getAllRoutes_emptyRepository_returnsEmptyList() {
        when(routeRepository.findAll()).thenReturn(List.of());

        List<RouteSummaryDTO> result = routeService.getAllRoutes();

        assertThat(result).isEmpty();
    }

    @Test
    void getRouteById_found_returnsDTO() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeMapper.toResponse(route)).thenReturn(responseDTO);

        RouteResponseDTO result = routeService.getRouteById(1L);

        assertThat(result.getRouteId()).isEqualTo(1L);
        assertThat(result.getRouteName()).isEqualTo("Sarajevo - Mostar");
    }

    @Test
    void getRouteById_notFound_throwsResourceNotFoundException() {
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.getRouteById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createRoute_noRelations_savesAndReturnsDTO() {
        RouteCreateDTO dto = new RouteCreateDTO();
        dto.setRouteName("Sarajevo - Mostar");
        dto.setStartCity("Sarajevo");
        dto.setEndCity("Mostar");

        when(routeMapper.toEntity(dto)).thenReturn(route);
        when(routeRepository.save(route)).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(responseDTO);

        RouteResponseDTO result = routeService.createRoute(dto);

        assertThat(result.getRouteName()).isEqualTo("Sarajevo - Mostar");
        verify(routeRepository).save(route);
    }

    @Test
    void createRoute_withWaypointIds_resolvesWaypoints() {
        RouteCreateDTO dto = new RouteCreateDTO();
        dto.setRouteName("Sarajevo - Mostar");
        dto.setStartCity("Sarajevo");
        dto.setEndCity("Mostar");
        dto.setWaypointIds(List.of(10L));

        Waypoint waypoint = new Waypoint();
        waypoint.setId(10L);

        when(routeMapper.toEntity(dto)).thenReturn(route);
        when(waypointRepository.findById(10L)).thenReturn(Optional.of(waypoint));
        when(routeRepository.save(route)).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(responseDTO);

        routeService.createRoute(dto);

        assertThat(route.getDefaultWaypoints()).containsExactly(waypoint);
        verify(routeRepository).save(route);
    }

    @Test
    void updateRoute_found_updatesAndReturns() {
        RouteUpdateDTO dto = new RouteUpdateDTO();
        dto.setRouteName("Sarajevo - Banja Luka");

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeRepository.save(route)).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(responseDTO);

        RouteResponseDTO result = routeService.updateRoute(1L, dto);

        assertThat(result).isEqualTo(responseDTO);
        verify(routeMapper).updateEntityFromDto(dto, route);
        verify(routeRepository).save(route);
    }

    @Test
    void updateRoute_notFound_throwsResourceNotFoundException() {
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.updateRoute(99L, new RouteUpdateDTO()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteRoute_found_deletesSuccessfully() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        routeService.deleteRoute(1L);

        verify(routeRepository).delete(route);
    }

    @Test
    void deleteRoute_notFound_throwsResourceNotFoundException() {
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.deleteRoute(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void addWaypointToRoute_notAlreadyPresent_addsAndSaves() {
        Waypoint waypoint = new Waypoint();
        waypoint.setId(10L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(waypointRepository.findById(10L)).thenReturn(Optional.of(waypoint));
        when(routeMapper.toResponse(route)).thenReturn(responseDTO);

        routeService.addWaypointToRoute(1L, 10L);

        assertThat(route.getDefaultWaypoints()).contains(waypoint);
        verify(routeRepository).save(route);
    }

    @Test
    void addWaypointToRoute_alreadyPresent_doesNotDuplicate() {
        Waypoint waypoint = new Waypoint();
        waypoint.setId(10L);
        route.getDefaultWaypoints().add(waypoint);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(waypointRepository.findById(10L)).thenReturn(Optional.of(waypoint));
        when(routeMapper.toResponse(route)).thenReturn(responseDTO);

        routeService.addWaypointToRoute(1L, 10L);

        assertThat(route.getDefaultWaypoints()).hasSize(1);
    }

    @Test
    void removeWaypointFromRoute_present_removesAndSaves() {
        Waypoint waypoint = new Waypoint();
        waypoint.setId(10L);
        route.getDefaultWaypoints().add(waypoint);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(waypointRepository.findById(10L)).thenReturn(Optional.of(waypoint));
        when(routeMapper.toResponse(route)).thenReturn(responseDTO);

        routeService.removeWaypointFromRoute(1L, 10L);

        assertThat(route.getDefaultWaypoints()).doesNotContain(waypoint);
        verify(routeRepository).save(route);
    }
}
