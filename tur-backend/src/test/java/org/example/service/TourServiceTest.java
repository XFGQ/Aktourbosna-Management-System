package org.example.service;

import org.example.application.dto.tour.TourCreateDTO;
import org.example.application.dto.tour.TourResponseDTO;
import org.example.application.dto.tour.TourSummaryDTO;
import org.example.application.dto.tour.TourUpdateDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.TourMapper;
import org.example.model.Guide;
import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.model.Waypoint;
import org.example.repository.GuideRepository;
import org.example.repository.RouteRepository;
import org.example.repository.TourRepository;
import org.example.repository.VehicleRepository;
import org.example.repository.WaypointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock TourRepository tourRepository;
    @Mock TourMapper tourMapper;
    @Mock GuideRepository guideRepository;
    @Mock VehicleRepository vehicleRepository;
    @Mock RouteRepository routeRepository;
    @Mock WaypointRepository waypointRepository;
    @InjectMocks TourService tourService;

    private Tour tour;
    private TourSummaryDTO summaryDTO;
    private TourResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        tour = new Tour();
        tour.setTourId(1L);
        tour.setTourName("Balkan Tour");
        tour.setStartDate(LocalDate.of(2024, 6, 1));
        tour.setEndDate(LocalDate.of(2024, 6, 10));
        tour.setExtraWaypoints(new ArrayList<>());
        tour.setCustomers(new ArrayList<>());
        tour.setExpenses(new ArrayList<>());

        summaryDTO = new TourSummaryDTO();
        summaryDTO.setTourId(1L);
        summaryDTO.setTourName("Balkan Tour");

        responseDTO = new TourResponseDTO();
        responseDTO.setTourId(1L);
        responseDTO.setTourName("Balkan Tour");
    }

    @Test
    void getAllTours_returnsMappedList() {
        when(tourRepository.findAll()).thenReturn(List.of(tour));
        when(tourMapper.toSummary(tour)).thenReturn(summaryDTO);

        List<TourSummaryDTO> result = tourService.getAllTours();

        assertThat(result).hasSize(1).containsExactly(summaryDTO);
        verify(tourRepository).findAll();
    }

    @Test
    void getAllTours_emptyRepository_returnsEmptyList() {
        when(tourRepository.findAll()).thenReturn(List.of());

        List<TourSummaryDTO> result = tourService.getAllTours();

        assertThat(result).isEmpty();
    }

    @Test
    void getTourById_found_returnsDTO() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourMapper.toResponse(tour)).thenReturn(responseDTO);

        TourResponseDTO result = tourService.getTourById(1L);

        assertThat(result.getTourId()).isEqualTo(1L);
        assertThat(result.getTourName()).isEqualTo("Balkan Tour");
    }

    @Test
    void getTourById_notFound_throwsResourceNotFoundException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tourService.getTourById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createTour_withGuideOnly_savesAndReturnsDTO() {
        TourCreateDTO dto = new TourCreateDTO();
        dto.setTourName("Balkan Tour");
        dto.setStartDate(LocalDate.of(2024, 6, 1));
        dto.setEndDate(LocalDate.of(2024, 6, 10));
        dto.setGuideId(5L);

        Guide guide = new Guide();
        guide.setId(5L);

        when(tourMapper.toEntity(dto)).thenReturn(tour);
        when(guideRepository.findById(5L)).thenReturn(Optional.of(guide));
        when(tourRepository.save(tour)).thenReturn(tour);
        when(tourMapper.toResponse(tour)).thenReturn(responseDTO);

        TourResponseDTO result = tourService.createTour(dto);

        assertThat(result.getTourName()).isEqualTo("Balkan Tour");
        verify(tourRepository).save(tour);
    }

    @Test
    void createTour_withVehicle_resolvesVehicleRelation() {
        TourCreateDTO dto = new TourCreateDTO();
        dto.setTourName("Balkan Tour");
        dto.setStartDate(LocalDate.of(2024, 6, 1));
        dto.setEndDate(LocalDate.of(2024, 6, 10));
        dto.setGuideId(5L);
        dto.setVehicleId(3L);

        Guide guide = new Guide();
        guide.setId(5L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(3L);

        when(tourMapper.toEntity(dto)).thenReturn(tour);
        when(guideRepository.findById(5L)).thenReturn(Optional.of(guide));
        when(vehicleRepository.findById(3L)).thenReturn(Optional.of(vehicle));
        when(tourRepository.save(tour)).thenReturn(tour);
        when(tourMapper.toResponse(tour)).thenReturn(responseDTO);

        TourResponseDTO result = tourService.createTour(dto);

        assertThat(result).isEqualTo(responseDTO);
        assertThat(tour.getVehicle()).isEqualTo(vehicle);
    }

    @Test
    void updateTour_found_updatesAndReturns() {
        TourUpdateDTO dto = new TourUpdateDTO();
        dto.setTourName("Updated Tour");

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourRepository.save(tour)).thenReturn(tour);
        when(tourMapper.toResponse(tour)).thenReturn(responseDTO);

        TourResponseDTO result = tourService.updateTour(1L, dto);

        assertThat(result).isEqualTo(responseDTO);
        verify(tourMapper).updateEntityFromDto(dto, tour);
        verify(tourRepository).save(tour);
    }

    @Test
    void updateTour_notFound_throwsResourceNotFoundException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tourService.updateTour(99L, new TourUpdateDTO()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteTour_exists_deletesSuccessfully() {
        when(tourRepository.existsById(1L)).thenReturn(true);

        tourService.deleteTour(1L);

        verify(tourRepository).deleteById(1L);
    }

    @Test
    void deleteTour_notFound_throwsResourceNotFoundException() {
        when(tourRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> tourService.deleteTour(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void addWaypoint_tourAndWaypointExist_addsWaypointAndSaves() {
        Waypoint waypoint = new Waypoint();
        waypoint.setId(10L);
        waypoint.setCity("Sarajevo");
        waypoint.setCountry("BiH");

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(waypointRepository.findById(10L)).thenReturn(Optional.of(waypoint));
        when(tourRepository.save(tour)).thenReturn(tour);
        when(tourMapper.toResponse(tour)).thenReturn(responseDTO);

        TourResponseDTO result = tourService.addWaypoint(1L, 10L);

        assertThat(result).isEqualTo(responseDTO);
        assertThat(tour.getExtraWaypoints()).contains(waypoint);
    }

    @Test
    void addWaypoint_waypointAlreadyPresent_doesNotDuplicate() {
        Waypoint waypoint = new Waypoint();
        waypoint.setId(10L);
        tour.getExtraWaypoints().add(waypoint);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(waypointRepository.findById(10L)).thenReturn(Optional.of(waypoint));
        when(tourRepository.save(tour)).thenReturn(tour);
        when(tourMapper.toResponse(tour)).thenReturn(responseDTO);

        tourService.addWaypoint(1L, 10L);

        assertThat(tour.getExtraWaypoints()).hasSize(1);
    }

    @Test
    void removeWaypoint_tourExists_removesWaypointAndSaves() {
        Waypoint waypoint = new Waypoint();
        waypoint.setId(10L);
        tour.getExtraWaypoints().add(waypoint);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourRepository.save(tour)).thenReturn(tour);
        when(tourMapper.toResponse(tour)).thenReturn(responseDTO);

        tourService.removeWaypoint(1L, 10L);

        assertThat(tour.getExtraWaypoints()).doesNotContain(waypoint);
        verify(tourRepository).save(tour);
    }
}
