package org.example.service;

import org.example.application.dto.TourDTO;
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

    public List<TourDTO> getAllTours() {
        return tourRepository.findAll().stream()
                .map(tourMapper::toDto)
                .collect(Collectors.toList());
    }

    public TourDTO getTourById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı. ID: " + id));
        return tourMapper.toDto(tour);
    }

    public TourDTO createTour(TourDTO dto) {
        Tour tour = tourMapper.toEntity(dto);
        resolveRelations(tour, dto);
        return tourMapper.toDto(tourRepository.save(tour));
    }

    public TourDTO updateTour(Long id, TourDTO dto) {
        Tour existing = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek tur bulunamadı. ID: " + id));
        tourMapper.updateEntityFromDto(dto, existing);
        resolveRelations(existing, dto);
        return tourMapper.toDto(tourRepository.save(existing));
    }

    public void deleteTour(Long id) {
        if (!tourRepository.existsById(id)) {
            throw new RuntimeException("Silinecek tur bulunamadı. ID: " + id);
        }
        tourRepository.deleteById(id);
    }

    public TourDTO addWaypoint(Long tourId, Long waypointId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı. ID: " + tourId));
        Waypoint waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new RuntimeException("Durak bulunamadı. ID: " + waypointId));
        if (!tour.getExtraWaypoints().contains(waypoint)) {
            tour.getExtraWaypoints().add(waypoint);
        }
        return tourMapper.toDto(tourRepository.save(tour));
    }

    public TourDTO removeWaypoint(Long tourId, Long waypointId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı. ID: " + tourId));
        tour.getExtraWaypoints().removeIf(w -> w.getId().equals(waypointId));
        return tourMapper.toDto(tourRepository.save(tour));
    }

    private void resolveRelations(Tour tour, TourDTO dto) {
        if (dto.getGuideId() != null) {
            tour.setGuide(guideRepository.findById(dto.getGuideId())
                    .orElseThrow(() -> new RuntimeException("Rehber bulunamadı. ID: " + dto.getGuideId())));
        }
        if (dto.getVehicleId() != null) {
            tour.setVehicle(vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Araç bulunamadı. ID: " + dto.getVehicleId())));
        }
        if (dto.getRouteId() != null) {
            tour.setBaseRoute(routeRepository.findById(dto.getRouteId())
                    .orElseThrow(() -> new RuntimeException("Rota bulunamadı. ID: " + dto.getRouteId())));
        }
        if (dto.getExtraWaypointIds() != null) {
            List<Waypoint> waypoints = dto.getExtraWaypointIds().stream()
                    .map(wId -> waypointRepository.findById(wId)
                            .orElseThrow(() -> new RuntimeException("Durak bulunamadı. ID: " + wId)))
                    .collect(Collectors.toList());
            tour.setExtraWaypoints(waypoints);
        }
    }
}
