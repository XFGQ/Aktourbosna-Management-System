package org.example.service;

import org.example.application.dto.vehicle.VehicleCreateDTO;
import org.example.application.dto.vehicle.VehicleResponseDTO;
import org.example.application.dto.vehicle.VehicleSummaryDTO;
import org.example.application.dto.vehicle.VehicleUpdateDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.VehicleMapper;
import org.example.model.Vehicle;
import org.example.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    public List<VehicleSummaryDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toSummary)
                .collect(Collectors.toList());
    }

    public VehicleResponseDTO getVehicleById(Long id) {
        return vehicleMapper.toResponse(vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + id)));
    }

    public VehicleResponseDTO createVehicle(VehicleCreateDTO dto) {
        return vehicleMapper.toResponse(vehicleRepository.save(vehicleMapper.toEntity(dto)));
    }

    public VehicleResponseDTO updateVehicle(Long id, VehicleUpdateDTO dto) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + id));
        vehicleMapper.updateEntityFromDto(dto, existing);
        return vehicleMapper.toResponse(vehicleRepository.save(existing));
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}
