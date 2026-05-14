package org.example.service;

import org.example.application.dto.tour.TourCreateDTO;
import org.example.application.dto.tour.TourResponseDTO;
import org.example.application.dto.tour.TourSummaryDTO;
import org.example.application.dto.tour.TourUpdateDTO;
import org.example.application.mapper.TourMapper;
import org.example.model.Tour;
import org.example.model.Waypoint;
import org.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourService {

    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final GuideRepository guideRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;
    private final WaypointRepository waypointRepository;

    public TourService(TourRepository tourRepository, TourMapper tourMapper,
                       GuideRepository guideRepository, VehicleRepository vehicleRepository,
                       RouteRepository routeRepository, WaypointRepository waypointRepository) {
        this.tourRepository = tourRepository;
        this.tourMapper = tourMapper;
        this.guideRepository = guideRepository;
        this.vehicleRepository = vehicleRepository;
        this.routeRepository = routeRepository;
        this.waypointRepository = waypointRepository;
    }

    public List<TourSummaryDTO> getAllTours() {
        return tourRepository.findAll().stream()
                .map(tourMapper::toSummary)
                .collect(Collectors.toList());
    }

    public TourResponseDTO getTourById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı. ID: " + id));
        return tourMapper.toResponse(tour);
    }

    public TourResponseDTO createTour(TourCreateDTO dto) {
        Tour tour = tourMapper.toEntity(dto);
        resolveRelations(tour, dto.getGuideId(), dto.getVehicleId(), dto.getRouteId(), dto.getExtraWaypointIds());
        return tourMapper.toResponse(tourRepository.save(tour));
    }

    public TourResponseDTO updateTour(Long id, TourUpdateDTO dto) {
        Tour existing = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek tur bulunamadı. ID: " + id));
        tourMapper.updateEntityFromDto(dto, existing);
        resolveRelations(existing, dto.getGuideId(), dto.getVehicleId(), dto.getRouteId(), dto.getExtraWaypointIds());
        return tourMapper.toResponse(tourRepository.save(existing));
    }

    public void deleteTour(Long id) {
        if (!tourRepository.existsById(id)) {
            throw new RuntimeException("Silinecek tur bulunamadı. ID: " + id);
        }
        tourRepository.deleteById(id);
    }

    public TourResponseDTO addWaypoint(Long tourId, Long waypointId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı. ID: " + tourId));
        Waypoint waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new RuntimeException("Durak bulunamadı. ID: " + waypointId));
        if (!tour.getExtraWaypoints().contains(waypoint)) {
            tour.getExtraWaypoints().add(waypoint);
        }
        return tourMapper.toResponse(tourRepository.save(tour));
    }

    public TourResponseDTO removeWaypoint(Long tourId, Long waypointId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı. ID: " + tourId));
        tour.getExtraWaypoints().removeIf(w -> w.getId().equals(waypointId));
        return tourMapper.toResponse(tourRepository.save(tour));
    }

    private void resolveRelations(Tour tour, Long guideId, Long vehicleId, Long routeId, List<Long> waypointIds) {
        if (guideId != null) {
            tour.setGuide(guideRepository.findById(guideId)
                    .orElseThrow(() -> new RuntimeException("Rehber bulunamadı. ID: " + guideId)));
        }
        if (vehicleId != null) {
            tour.setVehicle(vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Araç bulunamadı. ID: " + vehicleId)));
        }
        if (routeId != null) {
            tour.setBaseRoute(routeRepository.findById(routeId)
                    .orElseThrow(() -> new RuntimeException("Rota bulunamadı. ID: " + routeId)));
        }
        if (waypointIds != null) {
            List<Waypoint> waypoints = waypointIds.stream()
                    .map(wId -> waypointRepository.findById(wId)
                            .orElseThrow(() -> new RuntimeException("Durak bulunamadı. ID: " + wId)))
                    .collect(Collectors.toList());
            tour.setExtraWaypoints(waypoints);
        }
    }
}
